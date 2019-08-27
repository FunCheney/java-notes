### Condition实现原理

&ensp;&ensp;ConditionObject是同步器AbstractQueuedSynchronizer的内部类，Condition的操作要获取相关联的锁,通过使用Lock接口中的newCondition()方法获取Condition对象。每个Condition对象都包含者一个队列(等待队列)，该队列是实现等待/通知功能的关键。

#### 等待队列
&ensp;&ensp;经过前面的学习我们知道，在AQS中维护着一个同步的FIFO队列，用来完成同步状态的获取。同步队列的维护是通过head，tail节点构造的双向链表来实现的。等待队列与同步队列都是公用一个节点类(AQS中的Node类)来是实现的。

&ensp;&ensp;等待队列是一个FIFO队列，该队列的维护是通过firstWaiter，lastWaiter节点构造的单向链表来实现的。在队列中的每个节点都包含了一个线程的引用，该线程就是Condition对象上的等待线程，如果一个线程调用了Condition.await()方法，那么该线程释放锁、构造节点加入等待队列并进入等待状态。


![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/waitQueue.jpg "waitQueue")

#### ConditionObject实现类中的方法

&ensp;&ensp;通过上一篇文章中的例子，使用到Condition的await()方法，和signal()方法。来看看这两个方法的具体实现

**await()等待方法：**

```
public final void await() throws InterruptedException {
    long nanosTimeout = unit.toNanos(time);
    if (Thread.interrupted())
        // 线程被中断，返回线程中断异常
        throw new InterruptedException();
    // 将调用Condition中await()方法的线程构造成节点，加入等待队列中
    Node node = addConditionWaiter();
    // 释放锁的同步状态
    int savedState = fullyRelease(node);
    int interruptMode = 0;
    // 判断node是否在同步队列中
    int interruptMode = 0;
    while (!isOnSyncQueue(node)) {
        // 不在同步队列中，一直阻塞等待唤醒
        LockSupport.park(this);
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    //当等待队列中的节点被唤醒，当前节点被放入同步队列中
    
    // acquireQueued方法加入到获取同步状态的竞争中，抢占cpu资源
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    if (node.nextWaiter != null)
        // 如果同步队列中还有其他节点，剔除等待队列中状态不为-3的节点
        unlinkCancelledWaiters();
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
}
```

&ensp;&ensp;调用该方法的线程成功获取了锁的线程(也就是同步队列中的首节点)，该方法会将当前线程构造成节点并加入到等待队列中，然后释放同步状态，唤醒同步队列中的后继节点，然后当前线程会进入等待状态。

&ensp;&ensp;当等待队列中的节点被唤醒，则唤醒节点的线程开始尝试获取同步状态。如果不是通过其他线程调用Condition的signal()方法唤醒，而是对线程中断，则会抛出InterceptedException。

await()方法加入等待队列图示：

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/await.jpg "waitQueue")


**addConditionWaiter()方法**

&ensp;&ensp;该方法的作用是，添加节点到等待队列中。

```
private Node addConditionWaiter() {
    //创建一个节点为等待队列的尾节点
    Node t = lastWaiter;
    // 队列的为节点不为空，或者节点的状态不为-3
    if (t != null && t.waitStatus != Node.CONDITION) {
        // 剔除等待队列中节点状态为取消状态的节点
        unlinkCancelledWaiters();
        // 将等待队列中的最后一个节点赋值给节点t
        t = lastWaiter;
    }
    // 根据当前线程构造状态为-3的节点
    Node node = new Node(Thread.currentThread(), Node.CONDITION);
    if (t == null)
        // 等待队列的尾节点为空，将node节点指向等待队列的第一个节点
        firstWaiter = node;
    else
        // 将node节点添加到队尾
        t.nextWaiter = node;
    // 维护等待队列
    lastWaiter = node;
    // 返回新加入的节点
    return node;
}
```

**unlinkCancelledWaiters()方法**

&ensp;&ensp;该方法的作用就是，剔除掉等待队列中，节点状态为取消状态的节点。

```
private void unlinkCancelledWaiters() {
    // 创建一个节点为等待队列的第一个节点
    Node t = firstWaiter;
    Node trail = null;
    
    while (t != null) {
        // 获取到节点t的下一节点
        Node next = t.nextWaiter;
        
        if (t.waitStatus != Node.CONDITION) {
            // 节点t的状态不为等待状态
            
            // 将t节点在等待队列中的应用关系置为null
            t.nextWaiter = null;
            if (trail == null)
                //trial为null，等待队列首节点t的下一节点
                firstWaiter = next;
            else
                //trail不为null，将next节点链为trail的下一节点，维护链表
                trail.nextWaiter = next;
                
            if (next == null)
            // next 节点为null，将trail指向队列的最后一个节点
                lastWaiter = trail;
        }
        else
            /**
              * 节点t的状态为等待状态,
              * 将t节点赋值给trail节点
              */
            trail = t;
            
        // 控制链表的遍历    
        t = next;
    }
}
```
**fullyRelease()方法**

&ensp;&ensp;当线程调用Condition的await()方法，会释放掉当前获取到的锁。调用该方法主要用来释放锁的同步状态。

```
final int fullyRelease(Node node) {
    boolean failed = true;
    try {
        // 获取当前的同步装状态
        int savedState = getState();
        // 释放同步状态
        if (release(savedState)) {
            failed = false;
            return savedState;
        } else {
            throw new IllegalMonitorStateException();
        }
    } finally {
        if (failed)
            node.waitStatus = Node.CANCELLED;
    }
}
```

**signal()通知方法：**

```
public final void signal() {
    // 检查是否为独占状态
    if (!isHeldExclusively())
        // 不是独占状态抛出异常
        throw new IllegalMonitorStateException();
    // 拿到等待队列的第一个节点    
    Node first = firstWaiter;
    if (first != null)
        // 等待队列中的第一个节点不为空，将其放入同步对列中
        doSignal(first);
}
```
&ensp;&ensp;执行signal()的线程必须是获取了锁的线程。并且按照队列先进先出的特点，将等待队列中的首节点移动到同步队列中。

signal()方法加入同步队列图示：

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/signal.jpg "signal")


**doSignal()方法：**
```
private void doSignal(Node first) {
    do {
        // 首节点指向首节点的下一节点 并判断其是否为null
        if ( (firstWaiter = first.nextWaiter) == null)
            /**
              * 首节点的下一节点为null，说明当前等待队列中没有等待的线程
              * 因此，将等待队列中的最后一个节点置为null
              */
            lastWaiter = null;
         // 若首节点的下一节点不为null，将首节点从等待队列中拿出来
        first.nextWaiter = null;
        /**
          * 等待队列中的节点加入同步队列失败，
          * 且等待队列中首节点不为null，则一直循环
          */
    } while (!transferForSignal(first) && (first = firstWaiter) != null);
}
```

**transferForSignal()方法：**

&ensp;&ensp;该方法的主要作用是将等待队列中首节点加入到同步队列中，加入成功返回true，否则返回false。

```
final boolean transferForSignal(Node node) {
    // CAS 设置当前线程构造的节点中的同步状态为0
    if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
        return false;
    /**
      * 节点的状态设置成功，调用enq(node)方法，
      * 将其加入到同步队列中
      */    
    Node p = enq(node);
    // 获取当前节点的等待状态
    int ws = p.waitStatus;
    if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
        // 唤醒该节点的线程
        LockSupport.unpark(node.thread);
    return true;
}
```

&ensp;&ensp;被唤醒后的线程，将从await()方法中的while循环中退出(isOnSyncQueue(node)方法返回true，节点已经进入到同步队列中)，进而调用同步器的acquireQueued()方法加入到获取同步状态的竞争中。


&ensp;&ensp;成功获取同步状态(锁)之后，被唤醒的线程将从先前调用的await()返回，此时该线程已经成功获取了锁。

