# concurrency
并发编程学习
## 1. 线程的生命周期
 start 初始状态
 ready-to-run 就绪状态 竞争cpu资源
 Running 运行状态
 Dead 死亡状态
 Sleeping 睡眠状态 超时等待 当一定时间过了之后，重新竞争cpu资源
 Waiting 等待状态 需要唤醒 才可以竞争cpu资源
 Blocked 阻塞状态
 ![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/ThreadState1.png "Thread Status")
 
