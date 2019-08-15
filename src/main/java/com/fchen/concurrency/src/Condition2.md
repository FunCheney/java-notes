### Condition实现原理

&ensp;&ensp;ConditionObject是同步器AbstractQueuedSynchronizer的内部类，Condition的操作要获取相关联的锁。每个Condition对象都包含者一个队列(等待队列)，该队列是实现等待/通知功能的关键。

#### 等待队列
&ensp;&ensp;经过前面的学习我们知道，在AQS中维护着一个同步的FIFO队列，用来完成同步状态的获取。同步队列的维护是通过head，tail节点构造的双向链表来实现的。等待队列与同步队列都是公用一个节点类(AQS中的Node类)来是实现的。

&ensp;&ensp;等待队列是一个FIFO队列，该队列的维护是通过firstWaiter，lastWaiter节点构造的单向链表来实现的。在队列中的每个节点都包含了一个线程的引用，该线程就是Condition对象上的等待线程，如果一个线程调用了Condition.await()方法，那么该线程释放锁、构造节点加入等待队列并进入等待状态。

等待队列模型：

