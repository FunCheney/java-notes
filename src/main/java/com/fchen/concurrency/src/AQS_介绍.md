## AQS的介绍与使用

#### AQS(队列同步容器)
&ensp;&ensp;队列同步容器AbstractQueuedSynchronizer，是用来构建锁或者其他同步组件的基础框架。它使用了一个int成员的变量表示同
步状态，通过内置的FIFO队列来完成资源的获取线程的排队工作。

&ensp;&ensp;同步容器的主要使用方法是继承，子类通过继承同步容器并实现它的抽象方法来管理同步状态，在抽象方法的实现过程中避免不
了要对同步状态进行更改，这时就需要同步容器提供的3个方法(getState()、setState(int newState)和 
compareAndSetState(int expect,int update))来进行操作，因为他们能够保证状态的改变时线程安全的。子类推荐被定义为自定义同步组件
的静态内部类，同步器自身没有实现任何接口，它仅仅定义了若干个同步状态获取和释放的方法来供自定义同步组件的使用，同步器既可以支
持独占式地获同步状态，也可以支持共享式的获取锁的同步状态，这样就方便实现不同类型的同步组件。

&ensp;&ensp;同步器的设计是基于模板方法模式的，也就是说使用者需要继承同步器并重写指定的方法，随后将同步器组合在自定义同步组件
中实现，并调用同步器提供的模板方法，而这些模板方法将会调用使用者重写的方法。

&ensp;&ensp;重写同步器指定的方法时，需要使用同步器提供的如下3个方法来访问或者修改同步状态。
* getState(): 获取当前的同步状态
* setState(int newState): 设置当前的同步状态
* compareAndSetState(int expect, int update): 使用CAS设置当前状态，该方法能够保证状态设置的原子性。

**同步器可重写的方法及描述**：

方法名称 | 描述
---|---
protected boolean tryAcquire(int arg) | 独占式获同步状态，实现该方法需要查询当前状态并判断同步状态是否符合预期值，然后在进行CAS设置同步状态
protected boolean tryRelease(int arg) | 独占式的释放同步状态，等待获取同步状态的线程将有机会获取同步状态
protected int tryAcquireShared(int arg) | 共享式获取同步状态。返回大于等于0的值，表示获取成功，反之获取失败。
protected int tryReleaseShared(int arg) | 共享式释放同步状态
protected boolean isHeldExclusively() | 当前同步器是否在独占模式下被线程占用，一般该方法表示是否被当前线程锁独占

**同步器提供的模板方法**：

方法名称 | 描  述
 ---|---
 void acquire(int arg) | 独占式获取同步状态，如果当前线程获取同步状态成功，则由该方法返回，否则，将进入同步队列等待，该方法将会调用重写的tryAcquire(int arg)方法。
 void acquireInterruptibly(int arg) | 与acquire(int arg)相同，但是该方法响应中断，当前线程未获取到同步状态而进入到同步队列中，如果当前线程被中断，则该方法会抛出InterruptedException并返回。
 boolean tryAcquireNanos(int arg,long nanos) | 在acquireInterruptibly(int arg)基础上增加超时限制，如果当前线程在超时时间内没有获取到同步状态，那么将会返回false，如果获取到了返回true。
 void acquireShared(int arg) | 共享式的获取同步状态，如果当前线程未获取到同步状态，将会进入同步队列等待，与独占式获取的主要区别是在同一时刻可以有多个线程获取到同步状态。
 void acquireSharedInterruptibly(int arg) | 与acquireShared(int arg)相同，该方法响应中断
 boolean tryAcquireSharedNanos(int arg,long nanos) | 在acquireSharedInterruptibly(int args)的基础上加入了超时限制
 boolean release(int arg) | 独占式的释放同步状态，该方法会在释放同步状态之后，将同步队列中的第一个节点包含的线程唤醒
 boolean release(int arg) | 共享式的释放同步状态
 Collection<Thread> getQueuedThreads() | 获取等待在同步队列上的线程集合
 
 &ensp;&ensp;同步状态提供的模板方法基本分3类：独占式获取与释放同步状态，共享式获取与释放同步状态以及查询同步队列中等待线程的情况。
 &ensp;&ensp;同步状态提供的模板方法基本分3类：独占式获取与释放同步状态，共享式获取与释放同步状态以及查询同步队列中等待线程的情况。自定义同步组件将使用同步器提供的模板方法来实现自己的同步语义。
 
 &ensp;&ensp;我们使用上面介绍的方法实现一个锁的功能。主要步骤如下：
 
 * 1.将AbstractQueuedSynchronizer的子类定义为非公共的内部帮助器类，用来实现其封闭类的同步属性。
 * 2.在子类中根据锁的属性(独占/共享)来实现不同的方法，我在这里实现独占锁，因此实现tryAcquire(int arg)，tryRelease(int arg)方法。
 * 3.实现Lock接口，实现Lock接口中的方法。
 * 4.锁的实现，交给我们创建的同步容器的子类(Sync)去实现。
 * 5.最后完成Sync类中的tryAcquire(int arg)，tryRelease(int arg)方法。
 
实现代码如下：
```
public class MyLock implements Lock {

    private Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        // 这里暂时不用
        return null;
    }

    private class Sync extends AbstractQueuedSynchronizer{

        @Override
        protected boolean tryAcquire(int arg) {
            // 当状态为0的时候，进来的线程获取到锁
            if(compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            //释放锁，将锁的状态设置为0
            if(getState() == 0){
                throw new IllegalStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
    }
}
```
&ensp;&ensp;上述例子中MyLock是一个独占锁，是一个自定义的同步组件，它在同一时刻只允许一个线程占有。在使用锁的时候不会使用锁内部的同步容器，而是使用自定义同步组件的方法，比如其中的Lock()方法。