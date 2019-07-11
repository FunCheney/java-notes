# concurrency
## :lock:  1. 概要
#### :key: 并发是什么
&ensp;&ensp;并发是指在某个时间段内，多任务交替处理的能力。
#### :key: 多线程能干什么
* 提高资源的利用
* 公平(对cpu的占用)
* 方便(某些应用场景下，不同的线程执行不同的任务，并进行必要的协调，要比写一个程序执行所有的任务更合理)
#### :key: 为什么要学习并发与多线程
* 硬件技术的发展，高并发是未来的趋势，要不要了解一下?
* Java语言天生支持多线程，要不要了解一下?
* Java程序员进阶必备，要不要了解一下?
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
* 缓存一致性问题
* 乱序执行优化与问题
#### :bookmark_tabs:  顺序一致性

#### :bookmark_tabs:  Java内存模型
* 设计的初衷
* 解决的问题
## :eyes:  5. 线程安全
#### :memo: 何为线程安全
#### :memo: Java中操作共享数据安全程度
#### :memo: 如何保证线程安全

## :pencil2:  6. 可见性、有序性、原子性
#### :memo: 原子性

#### :memo: 可见性

#### :memo: 有序性

## :crossed_swords:  7. Java并发机制的底层实现
#### :notebook: 原子操作即实现原理
#### :notebook: synchronized
#### :notebook: volatile
#### :notebook: final



## :link:  高并发处理思路与手段
![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/way.jpg "处理方式与手段")



 
 
