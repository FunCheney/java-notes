### ReentrantReadWriteLock(读写锁)源码学习
&ensp;&ensp;读写锁，分为读锁和写锁两种。读锁可以允许多个线程同时访问，但是当线程访问写锁的时候，所有的读线程和其他的写线程
都被阻塞。读写锁通过读写分离的方式提高了并发性能。

&ensp;&ensp;读写锁中读锁是共享锁，写锁是排他锁。

读写锁的特性：

特 性 | 说 明
 ---|---
 公平选择 | 支持费公平(默认)和公平的锁获取方式，吞吐量还是非公平优于公平
 重进入 | 该锁支持重进入，以读写线程为例：读线程在获取读锁之后，能够再次获取读锁。而写线程在获取了写锁之后能够再次获取写锁，同时也可以获取读锁
 锁降级 | 遵循获取写锁、获取读锁再释放写锁的次序，写锁能够降级为读锁
 
 在ReadWriteLock接口中仅定义了获取读锁和写锁的两个方法，即ReadLock()和writeLock()方法。
 
 在ReentrantReadWriteLock中还提供了一些便于外界监控其内部工作状态的方法
 
 方法名称 | 描述
  ---|---
  int getReadLockCount() | 返回当前的读锁被获取的次数，该次数不等于获取读锁的线程数，例如，仅一个线程它连续获取n次锁，那么占据读锁的线程数是1，但该方法返回n
  int getReadHoldCount() | 返回当前线程获取读锁的次数
  boolean isWriteLocked() | 判断写锁是被获取
  int getWriteHoldCount() | 返回当前写锁获取的次数
  
  
 ####  首先来看一下读写锁的使用
```
public class MyDemo {
    private Map<String,Object> map = new HashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Lock r = readWriteLock.readLock();

    private Lock w = readWriteLock.writeLock();

    public Object get(String key){
        r.lock();
        System.out.println(Thread.currentThread().getName()+ "读操作执行");
        try {
            Thread.sleep(3000);
            return map.get(key);
        } catch (Exception e){
            throw new RuntimeException();
        }finally {
            r.unlock();
            System.out.println(Thread.currentThread().getName()+ "写操作执行完毕");
        }
    }

    public void put(String key,Object value){
        w.lock();
        System.out.println(Thread.currentThread().getName()+ "写操作执行");
        try {
            Thread.sleep(3000);
            map.put(key,value);
        }catch (Exception e){
            throw new RuntimeException();
        }finally {
            w.unlock();
            System.out.println(Thread.currentThread().getName()+ "写操作执行完毕");
        }
    }
}
```
&ensp;&ensp;上述示例中使用对HashMap的读写来进行操作，同时使用读写锁来保证线程的安全。在读操作get(String key)方法中，需要获取读锁，可是使多个线程同时访问而不被阻塞。写操作put(String key, Object value);在操作时必须先获取写锁，在获取到写锁之后，其他线程对于读锁和写锁的获取均被阻塞，只有写锁被释放之后，其他的读写操作才可以继续。

 ####  读写锁的实现
 &ensp;&ensp;从上述例子来分析锁的调用过程：
 
 首先，通过readLock()和writeLock()方法拿到锁：
 ```
 private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
 private Lock r = readWriteLock.readLock();
 private Lock w = readWriteLock.writeLock();
```
在ReadWriteLock中这两个方法返回的是Lock接口
```
public interface ReadWriteLock {
    Lock readLock();

    Lock writeLock();
}
```
然后找到其实现类ReentrantReadWriteLock中的实现：
```
public class ReentrantReadWriteLock{ 
    /** 通过内部类实现readerLock*/  
    private final ReentrantReadWriteLock.ReadLock readerLock;
    /** 通过内部类实现writerLock*/
    private final ReentrantReadWriteLock.WriteLock writerLock;
    /** 定义同步容器*/
    final Sync sync;
    
   public ReentrantReadWriteLock(boolean fair) {
           sync = fair ? new FairSync() : new NonfairSync();
           /** 创建ReadLock */
           readerLock = new ReadLock(this);
           /** 创建WriteLock */
           writerLock = new WriteLock(this);
       }
   
    /** 实现ReadWriteLock中的writeLock()方法 */
    public ReentrantReadWriteLock.WriteLock writeLock() { return writerLock; }
    /** 实现ReadWriteLock中的readLock方法 */
    public ReentrantReadWriteLock.ReadLock  readLock()  { return readerLock; }
}
```
&ensp;&ensp;这里先简单解释一下ReentrantReadWriteLock(boolean fair)构造方法，在这个方法中，首先根据fair的状态来创建公平或者非公平的同步器。
然后通过this关键字将ReentrantReadWriteLock的实例对象传进去，这里之所以这样做，是为了能够在ReadLock和WriteLock内部类中使用外部类的同步容器。
这里以ReadLock为例：
```
protected ReadLock(ReentrantReadWriteLock lock) {
        sync = lock.sync;
    }
```

在结合类图来看一下：

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantReadWriteLock1.png "ReentrantReadWriteLock")

 其中ReadLock()和WriteLock() 是ReentrantReadWriteLock的内部类，并且实现了Lock()接口。
 
 ##### ReadLock的实现
 ```
public static class ReadLock implements Lock, java.io.Serializable {
      private static final long serialVersionUID = -5992448646407690164L;
      //定义同步容器
      private final Sync sync;
      /**
       * ReadLock的构造方法
       */
      protected ReadLock(ReentrantReadWriteLock lock) {
          // 调用外部内的同步容器
          sync = lock.sync;
      }
      /*=====下面的方法是Lock接口中的方法====*/
      public void lock() {
          sync.acquireShared(1);
      }

      public void lockInterruptibly() throws InterruptedException {
          sync.acquireSharedInterruptibly(1);
      }

      public boolean tryLock() {
          return sync.tryReadLock();
      }

      public boolean tryLock(long timeout, TimeUnit unit)
              throws InterruptedException {
          return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
      }

      public void unlock() {
          sync.releaseShared(1);
      }

      public Condition newCondition() {
          throw new UnsupportedOperationException();
      }

      public String toString() {
          int r = sync.getReadLockCount();
          return super.toString() +
              "[Read locks = " + r + "]";
      }
  }
```
 ###### 读锁的获取与释放
 读锁是共享锁，所以这里调用acquireShared()共享获取同步状态的方法
 ```
public void lock() {
       sync.acquireShared(1);
   }
```
同理，读锁的释放
```
public void unlock() {
      sync.releaseShared(1);
  }
```
 ##### WriteLock的实现
 ```
public static class WriteLock implements Lock, java.io.Serializable {
     private static final long serialVersionUID = -4992448646407690164L;
     /**==这部分与ReadLock类似==*/
     private final Sync sync;
     protected WriteLock(ReentrantReadWriteLock lock) {
         sync = lock.sync;
     }
     /**===这部分是Lock接口中方法的实现==*/
     public void lock() {
         sync.acquire(1);
     }
    
     public void lockInterruptibly() throws InterruptedException {
         sync.acquireInterruptibly(1);
     }
    
     public boolean tryLock( ) {
         return sync.tryWriteLock();
     }
    
     public boolean tryLock(long timeout, TimeUnit unit)
             throws InterruptedException {
         return sync.tryAcquireNanos(1, unit.toNanos(timeout));
     }
    
     public void unlock() {
         sync.release(1);
     }
    
     public Condition newCondition() {
         return sync.newCondition();
     }
    
     public String toString() {
         Thread o = sync.getOwner();
         return super.toString() + ((o == null) ?
                                    "[Unlocked]" :
                                    "[Locked by thread " + o.getName() + "]");
     }
    
     /**==这部分是写锁中特有==*/
     /**
      * 判断锁是否被当前线程锁持有
      */
     public boolean isHeldByCurrentThread() {
         return sync.isHeldExclusively();
     }
    /**
     * 当前写锁被获取的次数
     */
     public int getHoldCount() {
         return sync.getWriteHoldCount();
     }
}
```
 
 ###### 写锁的获取与释放
  写锁是排他锁，所以这里调用acquire()独占获取同步状态的方法
  ```
 public void lock() {
        sync.acquire(1);
    }
 ```
 同理，写锁的释放
 ```
 public void unlock() {
       sync.release(1);
   }
 ```
  