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

```
//入队操作
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}

// 构造节点并将其加入等待队列
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    // Try the fast path of enq; backup to full enq on failure
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

//设置头结点
private void setHead(Node node) {
    head = node;
    node.thread = null;
    node.prev = null;
}
```