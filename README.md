# concurrency
## :mag_right:  1. 概要
#### :key: 并发是什么
&ensp;&ensp;并发是指在某个时间段内，多任务交替处理的能力。
#### :key: 多线程能干什么
* 1.提高资源的利用、提高任务的平均执行速度
* 2.公平(对cpu的占用)
* 3.方便(某些应用场景下，不同的线程执行不同的任务，并进行必要的协调，要比写一个程序执行所有的任务更合理)
#### :key: 为什么要学习并发与多线程
* 1.硬件技术的发展，高并发是未来的趋势，要不要了解一下?
* 2.Java语言天生支持多线程，要不要了解一下?
* 3.Java程序员进阶必备，要不要了解一下?
## :interrobang:  2. 多线程带来的优点与风险

#### :o: 2.1 优点
* 1.使用多处理器

* 2.模型简化

* 3.对异步事件的简单处理

* 4.用户界面的更佳响应

#### :x: 2.2 风险
* 1.线程安全

* 2.活跃性问题
    1. 死锁
    2. 饥饿
    
        a. 高优先级吞噬低优先级的CPU时间片
        
        b. 线程被永久堵塞在一个等待进入同步块的状态
        
        c. 等待的线程永远不被唤醒
    3. 活锁
* 3.性能问题
## :computer:  3. 如何学习
![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/abstract_01.jpg "abstract")

## :eyeglasses:  4. 抽象的概念
#### :bookmark_tabs:  计算机内存模型
* 1.缓存一致性问题
* 2.乱序执行优化与问题

 :link: [计算机内存模型]
#### :bookmark_tabs:  顺序一致性

#### :bookmark_tabs:  Java内存模型
* 1.设计的初衷
* 2.解决的问题

 :link: [Java内存模型]

## :crossed_swords:  5. Java并发机制的底层实现
#### :notebook: volatile
* [volatile关键字_01]
* [volatile关键字_02]
#### :notebook: synchronized
* [synchronized关键字_01]
* [synchronized关键字_02]
* [synchronized关键字_03]
#### :notebook: 原子操作即实现原理

## :eyeglasses:  6. Java并发编程的基础
* 1.进程、线程

&ensp;&ensp;&ensp;&ensp;进程：进程是操作系统结构的基础，是一次程序的执行；是一个程序机器数据在处理机上顺序执行时发生的活动；是程序在一个数据集合上
所运行的过程，它是系统进行资源分配和调度的独立单位。

&ensp;&ensp;&ensp;&ensp;线程：线程是CPU调度和分派的基本单位，为了更充分地利用cpu资源，一般都会是用多线程进行处理。

&ensp;&ensp;&ensp;&ensp;线程拥有自己的操作栈、程序技术器、局部变量表等资源，它与同一进程内的其他线程共享该进程的资源。

* 2.创建线程
* 3.[线程的状态及状态之间转换]
* 4.线程间通信
   
   &ensp;&ensp;&ensp;&ensp;a.[wait_notify]
   
   &ensp;&ensp;&ensp;&ensp;b.[join]
   
   &ensp;&ensp;&ensp;&ensp;c.[CountDownLatch]
   
* 5.线程池

## :eyes:  7. 线程安全
#### :memo: 何为线程安全

#### :memo: Java中操作共享数据安全程度
* 1.不可变

* 2.绝对线程安全

* 3.相对线程安全

* 4.线程兼容

* 5.线程对立
#### :memo: 如何保证线程安全
* 1.互斥同步

&ensp;&ensp;互斥同步是常见的一种并发正确的手段。**同步**是指在多个线程并发访问共享数据时，保证共享数据
在同一时刻只被一个线程使用。

&ensp;&ensp;互斥是实现同步的一种手段，临界区、互斥量和信号量都是主要的互斥实现方式。

   &ensp;&ensp;1）. synchronize 是实现互斥的主要手段。

   &ensp;&ensp;2）. ReentrantLocak 实现同步。

* 2.非阻塞同步

&ensp;&ensp;基于冲突检测的乐观并发策略。就是先进行操作，如果没有其他线程争用共享数据，那操作就成功了；如果有共享数据的争用，产生了冲突，在采取其他的补偿措施(最常见的补偿措施就是不断重试，知道成功为止)，这种乐观的并发策略的许多实现都不需要把线程挂起，因此这种操作称为非阻塞同步(Non-Blocking Synchronization).

&ensp;&ensp;1）. CAS

* 3.无同步方案

&ensp;&ensp;在不涉及到操作共享数据的情况下，这样的代码就是线程安全的。不需要在通过同步措施来保证正确性。

&ensp;&ensp;使用线程局部变量[ThreadLocal]



## :pencil2:  8. 可见性、有序性、原子性
#### :memo: 原子性

#### :memo: 可见性

#### :memo: 有序性

##  :notebook: 9.多线程的一些知识总结
####  :open_book: final
#### :open_book: 单例模式与多线程
* 1.[多线程下的单例模式_01]
* 2.[多线程下的单例模式_02]


## :lock: 10.锁
&ensp;&ensp;在JUC包中Lock是顶层接口。

&ensp;&ensp;[并发编程中锁的分类总结]。

####   内部锁
&ensp;&ensp;利用synchronized实现。使用synchronized关键字将会隐式的获取锁，但是它将锁的获取与释放固化了，也就是先获取的再释放。
这种方式虽然简化了同步管理，但是可扩展性相较于显示的获取锁变差了。
####   显示锁
&ensp;&ensp;利用volatile实现。定义了一个volatile int state变量作为共享资源。如果线程获取资源失败，则进入同步FIFO队列中等待。
如果获取成功，就执行临界区域代码。执行完释放资源时，会通知同步队列中的等待线程来获取资源后出队并执行。

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


## :hammer_and_wrench: 11.J.U.C
J.U.C 中源码相关阅读笔记

### locks包下面
* [AQS]
* [ReentrantLock]
* ReentrantReadWriteLock

&ensp;&ensp;&ensp;&ensp; 1. [ReentrantReadWriteLock的源码学习之实现]

&ensp;&ensp;&ensp;&ensp; 2. [ReentrantReadWriteLock的源码学习之同步容器的实现]
* LockSupport
* Condition

&ensp;&ensp;&ensp;&ensp; 1. [Condition使用示例]
 
&ensp;&ensp;&ensp;&ensp; 2. [Condition实现原理及源码分析] 
   
### Java并发容器和框架
* [ConcurrentHashMap]
* ConcurrentLinkedQueue
* 阻塞队列
### atomic包下面
* AtomicInteger
* AtomicIntegerFieldUpdater
* AtomicReference

Queue相关

### 线程池
* [初识线程池]




## :link:  高并发处理思路与手段

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/way.jpg "处理方式与手段")


### 缓存

缓存的作用？什么是缓存？

#### redis

*[Redis介绍]





### 队列

* [AMQP]

* [RabbitMQ]

* [RabbitMq的一些核心概念]

* [RabbitMq高级特性]

* [RabbitMq集群架构模式]

* [RabbitMq消息发送模式]

* [RabbitMq 问题]




### RPC
[ThreadLocal]:https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/ThreadLocal.md
[ConcurrentHashMap]:https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/ConcurrentHashMap.md

[Condition实现原理及源码分析]:https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/Condition2.md
[Condition使用示例]:https://mp.weixin.qq.com/s/3LBExINGMipnBaKj27eRSA
[ReentrantReadWriteLock的源码学习之同步容器的实现]:https://mp.weixin.qq.com/s/A6d940S3InLynoOqkvknJw
[ReentrantReadWriteLock的源码学习之实现]:https://mp.weixin.qq.com/s/TGhnvw_70etoG9nGn0LDUw
[ReentrantLock]:https://mp.weixin.qq.com/s/qInMguens_3Vun0YxGGYOA
[AQS]:https://mp.weixin.qq.com/s/7meggIhX8waD5DsMGizs0Q
[并发编程中锁的分类总结]:https://mp.weixin.qq.com/s/vYuadfkQJytuPgNees16cQ
[volatile关键字_01]:https://mp.weixin.qq.com/s/DFdImZ1srF-6OI_8ilRi8A
[volatile关键字_02]:https://mp.weixin.qq.com/s/Z99y3oUYhqvi7uoaet5tZw
[synchronized关键字_01]:https://mp.weixin.qq.com/s/6PVCZYnStHbbyLo8tY50zQ
[synchronized关键字_02]:https://mp.weixin.qq.com/s/mSCnIgpQJM7dRh5Tm75dUQ
[synchronized关键字_03]:https://mp.weixin.qq.com/s/oqhlqJEc1dQWY8yVqeGfEQ
[计算机内存模型]:https://mp.weixin.qq.com/s/_IyRKfNrZAjNhgFFgl2F6A
[Java内存模型]:https://mp.weixin.qq.com/s/et8fuuCSDZ19nYlssqWqEQ
[多线程下的单例模式_01]:https://mp.weixin.qq.com/s/57B7I7zjruOPN_8YVUP4LA
[多线程下的单例模式_02]:https://mp.weixin.qq.com/s/UQPR42fAPB3UfNwvto7MuA
[wait_notify]:https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/example/waitnotify/wait_notify.md
[线程的状态及状态之间转换]:https://github.com/FunCheney/concurrency/blob/master/src/md/ThreadSafe.md#computer1-线程的生命周期
[初识线程池]:https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/ThreadPoolExecutor.md
 
 [AMQP]:https://github.com/FunCheney/concurrency/blob/master/src/md/AQMP.md
 [RabbitMQ]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_0.md
 [RabbitMq的一些核心概念]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_Exchange.md
 [RabbitMq高级特性]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_1.md
 [RabbitMq集群架构模式]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_2.md
 [RabbitMq消息发送模式]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_3.md
 [RabbitMq 问题]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_4.md
 
 