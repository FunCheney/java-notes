## ReentrantLock(重入锁)源码学习
&ensp;&ensp;重入锁，就是支持同一个线程多次获得锁，其释放也要经过相同的次数后其他线程才可以获取到该锁。此外该锁还支持获取
该锁时的公平和非公平选择。

&ensp;&ensp;通过之前的学习，在Java中synchronized关键字是隐式的支持锁的重入的，这个是通过JVM的底层自己是现代的。ReentrantLook虽
然没有像synchronized一样支持隐式的重进入，但是在调用Lock方法的时，已经获得锁的线程，再次调用lock()方法获取锁而不被阻塞。

&ensp;&ensp;同时上述文字中有提到，锁的获取时的公平性问题。ReentrantLock中也提供了锁获取的是否公平的方法。

### 类图
![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantLock1.png "ReentrantLock1")

&ensp;&ensp;通过这张类图，ReentrantLock实现了Lock接口，通过重写Lock接口中的方法来实现对应的锁的属性。通过ReentrantLock可以创建
公平锁(FairSync)和非公平锁(NonfairSync)。

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantLock2.png "ReentrantLock2")

&ensp;&ensp;通过这张类图，可以看到在ReentrantLock中，包含有一个抽象的内部类(Sync)，通过Sync来实现FairSync和NonfairSync。

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantLock3.png "ReentrantLock3")

&ensp;&ensp;最后这张图可以看到，sync是AbstractQueuedSynchronizer的子类，这里和上篇文章中使用队列同步容器实现锁的使用方式一样。
说明ReentrantLock的功能也是通过同步队列容器来实现的。只不过这里提供了公平锁与非公平锁两种形式。下面我们就分别来看看这两种锁。

#### ReentrantLock中的Sync的实现
```
abstract static class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = -5179523762034025860L;

    /**
     * Performs {@link Lock#lock}. The main reason for subclassing
     * is to allow fast path for nonfair version.
     */
    abstract void lock();

    /**
     * Performs non-fair tryLock.  tryAcquire is implemented in
     * subclasses, but both need nonfair try for trylock method.
     */
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }

    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }

    protected final boolean isHeldExclusively() {
        // While we must in general read state before owner,
        // we don't need to do so to check if current thread is owner
        return getExclusiveOwnerThread() == Thread.currentThread();
    }

    final ConditionObject newCondition() {
        return new ConditionObject();
    }

    // Methods relayed from outer class

    final Thread getOwner() {
        return getState() == 0 ? null : getExclusiveOwnerThread();
    }

    final int getHoldCount() {
        return isHeldExclusively() ? getState() : 0;
    }

    final boolean isLocked() {
        return getState() != 0;
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        setState(0); // reset to unlocked state
    }
}
```

### 非公平锁(NonfairSync)

```
static final class NonfairSync extends Sync {
    private static final long serialVersionUID = 7316153563782823691L;

    /**
     * Performs lock.  Try immediate barge, backing up to normal
     * acquire on failure.
     */
    final void lock() {
        if (compareAndSetState(0, 1))
            setExclusiveOwnerThread(Thread.currentThread());
        else
            acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
}
```

### ReentrantLock 中关于锁的方法
```
public ReentrantLock() {
    sync = new NonfairSync();
}
```

```
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```

```
public void lock() {
    sync.lock();
}
```

```
public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
}
```

```
public boolean tryLock() {
    return sync.nonfairTryAcquire(1);
}
```

```
public boolean tryLock(long timeout, TimeUnit unit)
        throws InterruptedException {
    return sync.tryAcquireNanos(1, unit.toNanos(timeout));
}
```

```   
public void unlock() {
    sync.release(1);
}
```

