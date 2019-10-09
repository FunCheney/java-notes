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
long keepAliveTime
TimeUnit unit
BlockingQueue<Runnable> workQueue 阻塞队列，存储等待执行的任务
ThreadFactory threadFactory  
RejectedExecutionHandler handler
```

workQueue：

 &ensp;&ensp;当提交一个新的任务到线程池以后，线程池会根据当前线程池中的正在运行着的线程数量提决定该任务的理方式。处理方式有如下几种
 
 *1.直接切换
   
     使用SynchronousQueue
 
 *2.使用有界队列
 
     使用ArrayBlockingQueue 使用这种方式可以将线程池的最大线程数量限制为maximumPoolSize，这样能够降低资源的消耗
 
 *3.使用无界队列
 
    使用基于链表的阻塞队列 LinkedBlockingDeque；使用这种方式线程池中能够创建的最大线程数就是corePoolSize；maximumPoolSize就不会起作用了。
 
 



