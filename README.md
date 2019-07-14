# concurrency
## :mag_right:  1. 概要
#### :key: 并发是什么
&ensp;&ensp;并发是指在某个时间段内，多任务交替处理的能力。
#### :key: 多线程能干什么
* 1.提高资源的利用
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
#### :bookmark_tabs:  顺序一致性

#### :bookmark_tabs:  Java内存模型
* 1.设计的初衷
* 2.解决的问题

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
* 2.创建线程
* 3.线程的状态及状态之间转换
* 4.线程间通信

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

&ensp;&ensp;1）CAS

* 3.无同步方案

&ensp;&ensp;在不涉及到操作共享数据的情况下，这样的代码就是线程安全的。不需要在通过同步措施来保证正确性。
## :pencil2:  8. 可见性、有序性、原子性
#### :memo: 原子性

#### :memo: 可见性

#### :memo: 有序性

##  :notebook: 9.多线程的一些知识总结
####  :open_book: final
#### :open_book: 单例模式与多线程
* 1.多线程下的单例模式_01
* 2.多线程下的单例模式_02


## :lock: 10.锁

## :hammer_and_wrench: 11.J.U.C



## :link:  高并发处理思路与手段
![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/way.jpg "处理方式与手段")



 
 
 
 
 [volatile关键字_01]:https://mp.weixin.qq.com/s?__biz=MzU3ODY5ODQwMw==&tempkey=MTAxN19sM0xlSnpUS1AxTmJZaXROV2xPVnFJamtOVmlLTER3bENIb0xOQUFXNEJrVF9PYVpLTnFvY05nR2VQYm1mNU5yQzdIcm1mVDg1SFpTSXdPa0RucHNVZWM2dGNUTEhpeTIxcWtUVkNfUy1GbnNjcU53Z2xFQXJtcDZVZlpOSXdBb1VhNHFFZW4ybkNydll1R1hINlZvaEdnTjZuWXFCcWlXYlBnTUZBfn4%3D&chksm=7d702c334a07a5253892024e466362908aa8ed4f66fffeb652e5c78d35ea813ba4a102667213#rd
 [volatile关键字_02]:https://mp.weixin.qq.com/s?__biz=MzU3ODY5ODQwMw==&tempkey=MTAxN18vMWhHMkh0TThmRHhCSW9YV2xPVnFJamtOVmlLTER3bENIb0xOQUFXNEJrVF9PYVpLTnFvY05nR2VQYVpfWXRhT21pV0dnanBqM1J1azY5Ykhfb1lCVzdaZDVWVkdiTTFQcUJqdVdRTkFrLTAzcVpnUmhTUmRLYXVTVm02bS1LWVZieGtDdVdpUnpFQ3AxQUczeDNfSVV6aHZHSndubTgxWS1CQzNRfn4%3D&chksm=7d702fc84a07a6de13caafa0cc11c719ef6288dd30dfdd9d9e61ae2e95575c11086f6a5c758b#rd
 [synchronized关键字_01]:https://mp.weixin.qq.com/s?__biz=MzU3ODY5ODQwMw==&tempkey=MTAxN19BbU5vcnMwTTlXaXJCSG10V2xPVnFJamtOVmlLTER3bENIb0xOQUFXNEJrVF9PYVpLTnFvY05nR2VQWmkyWXEyRU9KT3lnaTJRb2JLbmFEZDVQcWdYeklIZmo0bDB6OVJhYXVMNGd3Q3VEN0FZdXVfaXItN3JpYmZtR3UzTmZrY1RHZHVnanNQWDdDZDRQMDFHVVZjRnBJeVNUNGpJZmFQS0Y4T3lRfn4%3D&chksm=7d702cde4a07a5c8ee52cc76667e0e498edcf1c4c538c6c13586a01efe5a1568f432aa38313f#rd
 [synchronized关键字_02]:https://mp.weixin.qq.com/s?__biz=MzU3ODY5ODQwMw==&tempkey=MTAxN184azdHNHNTbzRWZENJNDNkV2xPVnFJamtOVmlLTER3bENIb0xOQUFXNEJrVF9PYVpLTnFvY05nR2VQWjN2SlltLUJOS0FEajE2dEloMEtBWW5nXzNPdnJnV0FVNjFqazdiUEZpRTBKZUlzalE5WkJqYlpDVzlxc21OOU1iZF9QaXZGZTBtQ1RfYXRUZUg3ekNnbE93Y0lIejltZkNIQXVYOUFhbk9Rfn4%3D&chksm=7d702c2e4a07a538b8c75948f900b71cc99c5a47214b4a61b574c850f75b5aa4ff579c1e18bc#rd
 [synchronized关键字_03]:https://mp.weixin.qq.com/s?__biz=MzU3ODY5ODQwMw==&tempkey=MTAxN19xOURVU0N3UHg0VDZDcjFPV2xPVnFJamtOVmlLTER3bENIb0xOQUFXNEJrVF9PYVpLTnFvY05nR2VQYkc0LXFZOFRLUGV2Q3hfZ3V5Y3phVW9ieEt0cU5LVUQ0bjNfSHRUMWpna3ltTFZmajgwUW55V01KVUFsc3g2Z09xblRlT3FnYjZXcU1mdF9qOUJ0cWhPS18wMjRwV0pqd3dyeG9ZcU5sbTRRfn4%3D&chksm=7d702c2a4a07a53c42d54d34f0e1a54f06c197666cee816ab36a08388cff86a7ac7af5c7a47c#rd

 
