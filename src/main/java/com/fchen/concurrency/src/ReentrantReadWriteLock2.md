### ReentrantReadWriteLock(读写锁)源码学习之同步容器的实现
&ensp;&ensp;上一篇文章中我们学习了ReentrantReadWriteLock的两个内部类ReadLock以及WriteLock是如何创建的。以及他们各自对同步状态的获取与释放
调用方法的过程。

&ensp;&ensp;读写锁对同步状态的获取和释放到底是如何实现的。其实现方式大体上与ReentrantLock相似。都是通过继承
之定义同步容器Sync继承AbstractQueuedSynchronizer实现AQS中的方法来完成。下面我们就来看一下在ReentrantReadWriteLock中的同步容器的实现方式。

 ##### 读写状态的设计
 读写锁需要保存的状态：
 * 写锁重入的次数
 * 读锁的个数
 * 每个读锁重入的次数
 
 &ensp;&ensp;ReentrantReadWriteLock是可重入的，所以要记录重入的次数。而且读锁是共享的，多个线程可以同时获取到同一个读锁，所以需要记录读锁的个数。
 但是写锁是独占的，也就是锁只有一个写锁，所以不需要记录写锁的个数。
 
 &ensp;&ensp;再同步容器的设计中使用一个整型的变量来维护锁的状态。要想在一个整形变量上维护多种状态，就需要“按位切割使用”这个变量。读写锁将变量
 切割成两个部分(int 类型的变量占4个字节，共32位)，高16位表示读，低16位表示写。
 
 在ReentrantReadWriteLock的内部类Sync中关于锁的状态的表示：
 ```
static final int SHARED_SHIFT   = 16;
static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
/** 锁重入的最大值*/
static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

/** 获取共享锁的数量  */
static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
/** 获取独占锁的数量 */
static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }

```
 
 #### 独占的获取与释放同步状态
 获取：
 &ensp;&ensp;tryAcquire()方法是独占的获取同步状态，因此是在ReentrantReadWriteLock#WriteLock中的lock()方法调用是用到。来看看他是如何实现的：
 ```
protected final boolean tryAcquire(int acquires) {
      // 拿到当前线程
     Thread current = Thread.currentThread();
     // 获取同步状态
     int c = getState();
     // 获取独占锁的数量
     int w = exclusiveCount(c);
     if (c != 0) {
         // 同步状态c值不为0，不是第一次获取锁
         /**
          * 若 w == 0(为true)；说明是获取到读锁的线程来获取写锁
          *   current != getExclusiveOwnerThread() (为true)
          *      说明当前线程不是独占锁的持有线程
          * 返回，获取当前同步状态失败
          */
         if (w == 0 || current != getExclusiveOwnerThread())
             return false;
             
         /**
          * 若 写锁重入的次数 w + 当前重入的次数(1)
          *     大于最大值65535 抛出异常
          * 说明写锁重入的次数最多是65535次；
          */
         if (w + exclusiveCount(acquires) > MAX_COUNT)
             throw new Error("Maximum lock count exceeded");
         // 设置获取同步状态的次数
         setState(c + acquires);
         return true;
     }
     // 第一次获取写锁的状态
     /**
      * 在默认状态非公平的获取同步状态时
      * writerShouldBlock()默认返回false；
      * 然后CAS的方式设置同步状态 c为0;c + acquires 为1。
      */
     if (writerShouldBlock() ||
         !compareAndSetState(c, c + acquires))
         // 默认状态下，如果CAS设置失败，锁获取失败
         return false;
     // CAS设置成功，设置当前锁的持有线程
     setExclusiveOwnerThread(current);
     // 返回获取同步状态成功
     return true;
 }
```
&ensp;&ensp;综上，如果当前线程已经获取到写锁，则增加写状态，如果当前线程在获取写锁时，读锁已经被获取或者该线程不是已经获取到
写锁的线程，则当前线程进如等待状态。

再看一下上述代码中的writerShouldBlock()调用过程(默认是非公平)；

在 ReentrantReadWriteLock中：
```
abstract boolean writerShouldBlock();
```
找到其实现类中的方法：

在FairSync中：
```
final boolean writerShouldBlock() {
    return hasQueuedPredecessors();
}
```
在NonfairSync中：
```
final boolean writerShouldBlock() {
    return false; // writers can always barge
}
```

 释放：
 ```
protected final boolean tryRelease(int releases) {
     
     // 首先判断是否是独占锁，不是抛出异常
     if (!isHeldExclusively())
         throw new IllegalMonitorStateException();
     // 独占锁 状态获取的次数 -1
     int nextc = getState() - releases;
     // 判断独占锁是否释放完毕
     boolean free = exclusiveCount(nextc) == 0;
     if (free)
         // 释放完毕，设置拥有线程为null
         setExclusiveOwnerThread(null);
     // 设置独占锁同步状态的获取次数
     setState(nextc);
     return free;
}
```
 ##### 共享的获取与释放同步状态
 
获取：
```
protected final int tryAcquireShared(int unused) {
    // 获取当前的线程
    Thread current = Thread.currentThread();
    // 获取当前同步状态的值
    int c = getState();
    /**
     * 若exclusiveCount(c) != 0 为 true，说明写锁被获取
     *   getExclusiveOwnerThread() != current 为 true 
     *             获取到写锁的线程不是当前线程
     *  返回获取读锁失败
     */
    if (exclusiveCount(c) != 0 &&
        getExclusiveOwnerThread() != current)
        return -1;
    
    // 拿到读锁的次数
    int r = sharedCount(c);
    
    if (!readerShouldBlock() &&
        r < MAX_COUNT &&
        compareAndSetState(c, c + SHARED_UNIT)) {
        /**
         * r = 0; 第一次获取共享锁
         *  记录第一次获取读锁锁的线程
         *  设置读锁的获取次数为1
         */
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;
        } else if (firstReader == current) {
            /**
             * r > 0;且当前线程时第一次获取锁的线程
             * 这里说明是锁的重入
             *    锁的获取次数++
             */
            firstReaderHoldCount++;
        } else {
            /**
             * 这里说明是其他的线程获取读锁
             *  记录当前线程重入的次数 使用HoldCounter来记录
             */
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0)
                readHolds.set(rh);
            // 重入的值++
            rh.count++;
        }
        //返回获取同步状态成功
        return 1;
    }
    // 没有获取到同步状态，使用for循环的方式重复上述过程
    return fullTryAcquireShared(current);
}
```
&ensp;&ensp;读锁的获取，是可重入的共享的。在没有其他写线程访问时，读锁总会被成功的获取，所做的只是增加读的状态。如果当前线程已经
获取到读锁，则增加读状态。

readerShouldBlock() 方法,同样在ReentrantReadWriteLock中时抽象方法：
```
abstract boolean readerShouldBlock();
```
找到其实现类中的方法：

在FairSync中：
```
final boolean readerShouldBlock() {
    // AQS中的方法
    return hasQueuedPredecessors();
}
```
在NonfairSync中：
```
final boolean readerShouldBlock() {
    // AQS中的方法
    return apparentlyFirstQueuedIsExclusive();
}
```



记录当前线程重入的次数，并保存当前线程id。记录每个读锁重入的次数，通过ThreadLocal来保存，对于ThreadLocal相关的知识点，我们后面在来学习。
```
static final class HoldCounter {
    int count = 0;
    // Use id, not reference, to avoid garbage retention
    final long tid = getThreadId(Thread.currentThread());
}

static final class ThreadLocalHoldCounter
    extends ThreadLocal<HoldCounter> {
    public HoldCounter initialValue() {
        return new HoldCounter();
    }
}
```
 释放：
  ```
 protected final boolean tryReleaseShared(int unused) {
     //获取到当前线程
     Thread current = Thread.currentThread();
     // 判断当前线程是否为第一个的得到锁的线程
     if (firstReader == current) {
      // 第一个获取锁的线程获取到锁的次数为1
      if (firstReaderHoldCount == 1)
          // 置空第一个获取锁的记录
          firstReader = null;
      else
          // 获取到锁的次数--
          firstReaderHoldCount--;
     } else {
      HoldCounter rh = cachedHoldCounter;
      if (rh == null || rh.tid != getThreadId(current))
          rh = readHolds.get();
      int count = rh.count;
      if (count <= 1) {
          readHolds.remove();
          if (count <= 0)
              throw unmatchedUnlockException();
      }
      --rh.count;
     }
     for (;;) {
      int c = getState();
      int nextc = c - SHARED_UNIT;
      if (compareAndSetState(c, nextc))
          // Releasing the read lock has no effect on readers,
          // but it may allow waiting writers to proceed if
          // both read and write locks are now free.
          return nextc == 0;
     }
 }     
 ```
 ##### 锁降级
 &ensp;&ensp;锁的降级是指写锁降级为读锁。如果当前线程拥有写锁，然后将其释放，最后再获取读锁，这种分段完成的过程不能称之为锁降级。
 锁降级是指把持住(当前拥有的)写锁，再获取到读锁，随后释放(先前拥有的)写锁的过程。
 
&ensp;&ensp;锁降级主要时为了保证数据的可见性，如果当前线程不获取读锁而是直接释放写锁，假设此刻另一个线程(记作线程T)获取了写锁并修改了数据，
那么当前线程无法感知线程T的数据更新。如果当前线程获取读锁，即遵循锁降级的步骤，则线程T将会被阻塞，直到当前线程使用数据并释放读锁之后，线程
T才能获得写锁并进行数据的更新。

&ensp;&ensp;ReentrantReadWriteLock不支持锁升级(把持读锁、获取写锁、最后在释放读锁的过程)。目的也是保证数据的可见性，如果读锁已被多个线程
获取，其中任意线程成功获取了写锁并更新了数据，则其更新对其他获取到读锁的线程时不可见的。
  