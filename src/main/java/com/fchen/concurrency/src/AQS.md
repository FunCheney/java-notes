### AbstractQueuedSynchronizer 
&ensp;&ensp; 我们知道AbstractQueuedSynchronizer是依靠一个volatile修饰的int state 和 一个FIFO的队列来实现的。下面先来看看这个变量与队列。

对同步状态的维护：
```
 // 同步状态
private volatile int state;  

// 获取当前同步器的状态，对volatile变量的读
protected final int getState() {
    return state;
}

// 设置当前同步容器的状态，对volatile变量的写
protected final void setState(int newState) {
    state = newState;
}

// 通过CAS的方式这只同步器状态的值
protected final boolean compareAndSetState(int expect, int update) {
    /**
     *  expect 旧的预期的值
     *  update 要更新的值
     *  当内存中存储的值与旧的期望的值相同时，更新新值成功，否则就不更新
     */
    return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
}
```
在AbstractQueuedSynchronizer中维护了一个Node类，用来构造FIFO的队列
```
 static final class Node {
        /** 等待队列中的节点类型，默认是共享的 */
        static final Node SHARED = new Node();
        /** 用来标识等待队列中的节点类型，这里表示是独占的 */
        static final Node EXCLUSIVE = null;

        /** 由于在同步队列中等待的线程等待超时或被中断，
          * 需要从同步队列中取消等待，节点进入该状态将不会变化
          */
        static final int CANCELLED =  1;
        
        /** 后继节点的状态处于等待状态，而当前节点的线程如果释放了同步状态或被取消，
         *  将会通知后继节点，使后继节点的线程得以运行
         */
        static final int SIGNAL = -1;
        
        /**
         * 节点在等待队列中，节点线程在Condition上，当其他线程对Condition调用了
         * signal() 方法后，该节点将会从等待队列中转移到同步队列中，加入到同步状
         * 态的获取中
         */
        static final int CONDITION = -2;
        
        /**
         * 表示下一次共享状式同步状态将会无条件传播下去
         */
        static final int PROPAGATE = -3;
        /** 
         * 初始状态值为0
         */
        volatile int waitStatus;
        /**
         * 前驱节点
         */
        volatile Node prev;

        /**
         * 后继节点
         */
        volatile Node next;

        /**
         * 获取同步状态的线程
         */
        volatile Thread thread;

        /**
         * 等待队列中的后继节点。如果当前节点是共享的，那么这个字段
         * 将是一个SHARED常量，也就是说节点类型(独占和共享)和等待队列中的后继节点公用一个字段
         */
        Node nextWaiter;

        /**
         * 如果节点是共享的返回true
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * 返回当前节点的前驱节点
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {    // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
```
对FIFO队列的维护：

&ensp;&ensp;首先在同步容器中定义了一个Head节点和Tail节点，用来标记FIFO队列的头结点和尾节点。其中每个节点Node又包含有它的前驱节点与后继节点。也就是说，在同步容器中的FIFO队列是通过双向链表的方式来构造的。

设置FIFO队列的头结点
```
private void setHead(Node node) {
    head = node;
    node.thread = null;
    node.prev = null;
}
```
//FIFO队列中节点的入队操作
```
private Node enq(final Node node) {
    for (;;) {
        // 节点t标记当前队列的尾节点
        Node t = tail;
        if (t == null) {
            /**
             * 尾节点为空，说明队列为空,创建一个空节点
             * 并将其使用CAS的方式设置为头结点
             */
            if (compareAndSetHead(new Node()))
                //设置成功后，将头结点指向尾节点，此时队列FIFO队列不为空
                tail = head;
        } else {
            /**
             * 尾节点不为空，让当前节点的前驱节点为，当前队列的尾节点
             * 队列添加节点在队尾
             */
            node.prev = t;
            /**
             * 使用CAS的方式将新添加的节点设置为尾节点
             * compareAndSetTail(t, node)只需要传递
             * 当前队列的尾节点和新加入的节点，
             */ 
            if (compareAndSetTail(t, node)) {
                // 设置成功后 维护节点之间的关系
                // 原先尾节点的下一结点为当前入队的节点
                t.next = node;
                return t;
            }
        }
    }
}
```
&ensp;&ensp;这里的for(;;) 是为了保证当前节点的入队成功。试想：

* 1.假设当前的同步队列为空(上述代码中尾节点为空)，new Node() 创建一个空的结点，通过compareAndSetHead(new Node())来将其设置为头结点。

    a. 设置成功，将同步队列的尾节点指向头结点，然后for循环再次进入，执行步骤2);
    
    b. 设置不成功，for循环再次进入，执行步骤1)。
    
* 2.若当前队列不为空，则将当前节点添加到队尾，并维护队列的关系。

    a. compareAndSetTail(t, node)添加到队尾成功，维护队列，退出for循环；
    
    b. 添加失败，一直for循环知道添加成功为止。

&ensp;&ensp;enq(final Node node)方法将并发添加结点的请求通过CAS变得“串行化了”。

同步器中的方法:

将结点添加到等待队列中：
```
private Node addWaiter(Node mode) {
    // 构造结点
    Node node = new Node(Thread.currentThread(), mode);
    // 快速尝试在尾部添加
    Node pred = tail;
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    enq(node);
    return node;
}
```
&ensp;&ensp;addWaiter(Node mode)方法保证了当前线程添加的节点在，正确的添加在FIFO队列的队尾。

&ensp;&ensp;节点进入同步队列之后，就进入了一个自旋的过程，每个节点(或者说每个线程)，都在自省的观察，当条件满足，并获取到同步状态，就可以从这个自旋的过程中退出，否则依旧保留在这个自旋过程中。
```
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            // 获取当前节点的前驱节点
            final Node p = node.predecessor();
            // 当前驱节点为头结点时，独占的获取同步状态
            if (p == head && tryAcquire(arg)) {
                // 当前节点设置为头结点
                setHead(node);
                // 将前驱节点的下一结点的引用置为null
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            /**
             * 前驱节点不是头结点
             * ===================
             */
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
&ensp;&ensp;为什么只有前驱节点是头结点才获取同步状态？

* 1.头结点是成功获取同步状态的节点，而头结点释放了同步状态之后，将会唤醒其后继节点，后继节点的线程被唤醒之后需要检查自己的前驱节点是不是头结点。

* 2.维护同步队列的FIFO原则。

```
private void doAcquireInterruptibly(int arg)
                        throws InterruptedException {
    final Node node = addWaiter(Node.EXCLUSIVE);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
```
private boolean doAcquireNanos(int arg, long nanosTimeout)
                                throws InterruptedException {
    if (nanosTimeout <= 0L)
        return false;
    final long deadline = System.nanoTime() + nanosTimeout;
    final Node node = addWaiter(Node.EXCLUSIVE);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return true;
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L)
                return false;
            if (shouldParkAfterFailedAcquire(p, node) &&
                nanosTimeout > spinForTimeoutThreshold)
                LockSupport.parkNanos(this, nanosTimeout);
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

```
private void doAcquireShared(int arg) {
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    if (interrupted)
                        selfInterrupt();
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}

private void doAcquireSharedInterruptibly(int arg)
                        throws InterruptedException {
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}

private boolean doAcquireSharedNanos(int arg, long nanosTimeout)
                                       throws InterruptedException {
    if (nanosTimeout <= 0L)
        return false;
    final long deadline = System.nanoTime() + nanosTimeout;
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
            }
            nanosTimeout = deadline - System.nanoTime();
            if (nanosTimeout <= 0L)
                return false;
            if (shouldParkAfterFailedAcquire(p, node) &&
                nanosTimeout > spinForTimeoutThreshold)
                LockSupport.parkNanos(this, nanosTimeout);
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
```
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    if (ws == Node.SIGNAL)
        /*
         * This node has already set status asking a release
         * to signal it, so it can safely park.
         */
        return true;
    if (ws > 0) {
        /*
         * Predecessor was cancelled. Skip over predecessors and
         * indicate retry.
         */
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
        /*
         * waitStatus must be 0 or PROPAGATE.  Indicate that we
         * need a signal, but don't park yet.  Caller will need to
         * retry to make sure it cannot acquire before parking.
         */
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
}
```
```
private final boolean parkAndCheckInterrupt() {
    LockSupport.park(this);
    return Thread.interrupted();
}
```
```
private void cancelAcquire(Node node) {
    // Ignore if node doesn't exist
    if (node == null)
        return;

    node.thread = null;

    // Skip cancelled predecessors
    Node pred = node.prev;
    while (pred.waitStatus > 0)
        node.prev = pred = pred.prev;

    // predNext is the apparent node to unsplice. CASes below will
    // fail if not, in which case, we lost race vs another cancel
    // or signal, so no further action is necessary.
    Node predNext = pred.next;

    // Can use unconditional write instead of CAS here.
    // After this atomic step, other Nodes can skip past us.
    // Before, we are free of interference from other threads.
    node.waitStatus = Node.CANCELLED;

    // If we are the tail, remove ourselves.
    if (node == tail && compareAndSetTail(node, pred)) {
        compareAndSetNext(pred, predNext, null);
    } else {
        // If successor needs signal, try to set pred's next-link
        // so it will get one. Otherwise wake it up to propagate.
        int ws;
        if (pred != head &&
            ((ws = pred.waitStatus) == Node.SIGNAL ||
             (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
            pred.thread != null) {
            Node next = node.next;
            if (next != null && next.waitStatus <= 0)
                compareAndSetNext(pred, predNext, next);
        } else {
            unparkSuccessor(node);
        }

        node.next = node; // help GC
    }
}
```
```
 private void unparkSuccessor(Node node) {
        /*
         * If status is negative (i.e., possibly needing signal) try
         * to clear in anticipation of signalling.  It is OK if this
         * fails or if status is changed by waiting thread.
         */
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        /*
         * Thread to unpark is held in successor, which is normally
         * just the next node.  But if cancelled or apparently null,
         * traverse backwards from tail to find the actual
         * non-cancelled successor.
         */
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);
    }

```

```
private void doReleaseShared() {
    for (;;) {
        Node h = head;
        if (h != null && h != tail) {
            int ws = h.waitStatus;
            if (ws == Node.SIGNAL) {
                if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                    continue;            // loop to recheck cases
                unparkSuccessor(h);
            }
            else if (ws == 0 &&
                     !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                continue;                // loop on failed CAS
        }
        if (h == head)                   // loop if head changed
            break;
    }
}
```
同步器提供的模板方法：

独占式同步状态获取，对中断不敏感
```
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```
&ensp;&ensp;上述代码主要完成了同步状态的获取、结点构造、加入同步队列以及在同步队列中自旋等待。
其主要逻辑是：

* 1.首先调用自定义同步器实现的tryAcquire(arg)方法，该方法保证线程安全的获取同步状态，如果同步状态获取失败，则构造同步节点(独占式Node.EXCLUSIVE,同一时刻只能有一个线程成功获取同步状态)；

* 2.通过addWaiter(Node node)方法将该节点加入到同步队列尾部；

* 3.最后调用acquireQueued(Node node,int arg)方法，使得该节点以“死循环”的方式获取同步状态，如果获取不到则阻塞节点中的线程，而被阻塞线程的唤醒主要依靠前驱节点的出队或阻塞线程被中断来实现。

独占式同步状态获取，对中断敏感
```
public final void acquireInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (!tryAcquire(arg))
        doAcquireInterruptibly(arg);
}
```
在acquireInterruptibly的基础上增加超时限制
```
public final boolean tryAcquireNanos(int arg, long nanosTimeout)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    return tryAcquire(arg) ||
        doAcquireNanos(arg, nanosTimeout);
}
```
共享式的获取同步状态：
```
public final void acquireShared(int arg) {
    if (tryAcquireShared(arg) < 0)
        doAcquireShared(arg);
}
```
共享式的获取同步状态响应中断：
```
 public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (tryAcquireShared(arg) < 0)
        doAcquireSharedInterruptibly(arg);
}
```
独占式的释放同步状态：
```
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```
&ensp;&ensp;当前线程获取同步状态并执行了相应的逻辑之后，就需要释放同步状态，使得后续的节点能够继续的获得同步状态。通过release(int arg)方法可以释放同步状态，该方法释放了同步状态之后，会唤醒其后继节点(进而使后继节点重新尝试获取同步状态)。

共享式的释放同步状态：
```
public final boolean releaseShared(int arg) {
    if (tryReleaseShared(arg)) {
        doReleaseShared();
        return true;
    }
    return false;
}
```

 同步器中可重写的方法
 ```
 /**
  * 独占式获取同步状态，实现该方法需要查询当前状态并判断
  * 同步状态是否符合预期值，然后再进行CAS设置同步状态
  */
 protected boolean tryAcquire(int arg) {
     throw new UnsupportedOperationException();
 }

 /**
  * 独占式的释放同步状态，等待获取同步状态的线程将有机会获取同步状态
  */
 protected boolean tryRelease(int arg) {
     throw new UnsupportedOperationException();
 }

 /**
  * 共享的方式获取同步状态，返回值大于0表示获取成功，反之，获取失败
  */
 protected int tryAcquireShared(int arg) {
     throw new UnsupportedOperationException();
 }

 /**
  * 共享式释放同步状态
  */
 protected boolean tryReleaseShared(int arg) {
     throw new UnsupportedOperationException();
 }

 /**
  * 是否被当前线程独占
  */
 protected boolean isHeldExclusively() {
     throw new UnsupportedOperationException();
 }
```