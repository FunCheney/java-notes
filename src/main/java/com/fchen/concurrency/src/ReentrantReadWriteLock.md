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
 
 ##### 读写状态的设计
 
 ##### 写锁的获取与释放
 
 ##### 读锁的获取与释放
 
 ##### 锁降级
  