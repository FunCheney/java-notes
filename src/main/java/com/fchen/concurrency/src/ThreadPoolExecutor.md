## 线程池

### 线程池的好处
* 重用存在的线程，减少创建对象，消亡开销，性能好

* 可有效的控制最大并发数，提高资源的利用率，同时可以避免过多资源竞争，避免阻塞

* 提供定时执行，定期执行、单线程、并发数控制等功能

### ThreadPoolExecutor

#### 参数
```
int corePoolSize       核心线程数
int maximumPoolSize    线程最大线程数
long keepAliveTime     线程池维护线程所允许的空闲时间
TimeUnit unit          时间单位
BlockingQueue<Runnable> workQueue 阻塞队列，存储等待执行的任务
ThreadFactory threadFactory       线程工程，创建线程，有默认的线程工厂
RejectedExecutionHandler handler  拒绝测略
```

workQueue：

 &ensp;&ensp;当提交一个新的任务到线程池以后，线程池会根据当前线程池中的正在运行着的线程数量提决定该任务的理方式。处理方式有如下几种
 
 * 1.直接切换
   
     使用SynchronousQueue
 
 * 2.使用有界队列
 
     使用ArrayBlockingQueue 使用这种方式可以将线程池的最大线程数量限制为maximumPoolSize，这样能够降低资源的消耗，这种方式使得线程池对线程的调度变得更加困难，因为线程数和任务存储量都是有限的了。
 
 * 3.使用无界队列
 
    使用基于链表的阻塞队列 LinkedBlockingDeque；使用这种方式线程池中能够创建的最大线程数就是corePoolSize；maximumPoolSize就不会起作用了。
    
#### 参数设置方案

* 1.想降低资源的消耗，cpu的使用率，系统的资源消耗，可设置一个教的对列容量和较小的线程池容量。这样会降低线程处理任务的吞吐量。

* 2.如果提交的任务经常发生阻塞，调用设置线程最大数的方法，从新设定线程池的容量

* 3.如果队列的容量设置较小，通常需要设置较大的线程池荣容量，提高CPU的使用率。但是线程池的容量设置的过大，在提交任务数量太多的情况下，并发量会增加，线程之间的调度就是要考虑的问题，这样反而会降低处理任务的吞吐量。

#### 线程池的拒绝策略

* 1. 默认直接抛出异常

* 2. 用调用者所用的线程来执行任务

* 3. 丢弃队列中最靠前的任务并执行当前的任务

* 4.直接丢弃该任务


### 线程池实例的状态

RUNNING

SHUTDOWN

STOP

TIDYING

TERMINATED


### execute()方法
&ensp;&ensp;提交任务，交给线程池执行


### submit()方法
&ensp;&ensp;提交任务，返回能够执行的结果 execute + Future

 
 



