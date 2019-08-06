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
     * 定义抽象的lock()方法供子类实现
     */
    abstract void lock();

    /**
     * 非公平的获取同步状态
     */
    final boolean nonfairTryAcquire(int acquires) {
        // 当前获取锁的线程
        final Thread current = Thread.currentThread();
        // 当前的同步状态的值
        int c = getState();
        if (c == 0) {
            /**
             * 还没有线程获取到当前的同步状态
             * 使用CAS的方式设置同步状态的值
             */
            if (compareAndSetState(0, acquires)) {
                // 同步状态值设置成功，设置获取到同步状态的线程
                setExclusiveOwnerThread(current);
                // 同步状态已被获取
                return true;
            }
        }
          // 若状态不为0 判断拥有同步状态的线程是不是当前的线程
        else if (current == getExclusiveOwnerThread()) {
            // 是。则支持锁的重入，使当前线程拥有锁的同步状态值+1
            int nextc = c + acquires;
            // 判断获取锁的次数是不是超过int值的最大值
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            // 设置获取到同步状态的次数
            setState(nextc);
            return true;
        }
        return false;
    }

    /**
     * 同步状态的释放
     */
    protected final boolean tryRelease(int releases) {
        // 记录获取同步状态的次数
        int c = getState() - releases;
        // 判断当前线程是不是获取同步状态的线程
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        // 标记同步状态是否释放完成
        boolean free = false;
        if (c == 0) {
            // 同步状态为0 
            free = true;
            // 将占有线程设置为null 表示释放成功
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }

    /**
     * 是否被当前线程锁持有
     */
    protected final boolean isHeldExclusively() {
        return getExclusiveOwnerThread() == Thread.currentThread();
    }

    final ConditionObject newCondition() {
        return new ConditionObject();
    }

    /**
     * 获取拥有同步状态的线程
     */
    final Thread getOwner() {
        return getState() == 0 ? null : getExclusiveOwnerThread();
    }

    /**
     * 某一线程持有同步状态的次数
     */
    final int getHoldCount() {
        return isHeldExclusively() ? getState() : 0;
    }

    /**
     * 是否获取到锁
     */
    final boolean isLocked() {
        return getState() != 0;
    }

    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        setState(0); // reset to unlocked state
    }
}
```
&ensp;&ensp;从上面我们看到，在ReentrantLock中提供的同步容器的实现类Sync中，实现了非公平获取锁同步状态的
方法nonfairTryAcquire(int acquires),在该方法中只要当前线程通过CAS的方式设置同步状态成功，则表示当前线程获取到锁。

### 非公平锁(NonfairSync)的实现

```
static final class NonfairSync extends Sync {
    private static final long serialVersionUID = 7316153563782823691L;

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
&ensp;&ensp;上述代码是ReentrantLock中lock()方法的实现，当调用ReentrantLock()的lock()方法是会调用NonfairSync类中的lock()方法(默认实现)，在改方法中，首先快速的使用CAS设置同步状态，设置成功说明当前线程已经获取到锁。设置失败，则调用AbstractOwnableSynchronizer中的acquire()方法独占式的获取同步状态。

非公平锁lock()方法的调用过程：


### 公平锁(FairSync)的实现
```
static final class FairSync extends Sync {
    private static final long serialVersionUID = -3000897897090466540L;

    final void lock() {
        acquire(1);
    }

    
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
}
```
&ensp;&ensp;上述代码是FairSync(公平锁)的实现，公平锁中同步状态的获取要按照获取时间的先后顺序来获得，即满足FIFO。

&ensp;&ensp;这里的tryAcquire(int acquires)与之前看到的nonfairTryAcquire(int acquires)比较，唯一不同的位置就是判断条件多了hasQueuedPredecessors()方法。及加入了同步队列中当前节点是否有前驱节点的判断，如果该方法返回true，则表示有线程比当前线程更早地请求获取锁，因此需要等待前驱线程获取并释放锁之后才能继续获取锁。

### ReentrantLock 中关于锁的方法
ReentrantLock中的构造方法，可以看出，ReentrantLock中默认是非公平锁的实现方式。
```
public ReentrantLock() {
    sync = new NonfairSync();
}
```
可以通过在ReentrantLock的构造器中传入参数的方式，来确定当前使用的锁是公平的还是非公平的。
```
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```
lock方法就是调用同步容器中的lock()方法，然后在分发到不同的子类当中去调用响应的lock方法。
```
public void lock() {
    sync.lock();
}
```
获取同步状态(对中断铭感)
```
public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
}
```
非公平的获取同步状态
```
public boolean tryLock() {
    return sync.nonfairTryAcquire(1);
}
```
超时获取同步状态
```
public boolean tryLock(long timeout, TimeUnit unit)
        throws InterruptedException {
    return sync.tryAcquireNanos(1, unit.toNanos(timeout));
}
```
锁的释放
```   
public void unlock() {
    sync.release(1);
}
```

