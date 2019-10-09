## ConcurrentHashMap (JDK 1.8)

&ensp;&ensp;上一篇文章中介绍了ConcurrentHashMap中的一些属性，与其底层实现中红黑树相关的部分。这一篇文章中将来看一下ConcurrentHashMap的具体实现。

### 构造方法

#### 无参构造方法
```
/**
 *  创建一个新的，空的Map;默认的初始表大小（16）。
 */
public ConcurrentHashMap() {
}
```
#### 带有初始容量的构造方法
```
/**
 * 创建一个Map，其初始表格大小适应指定数量的元素，而不需要动态调整大小。
 */
public ConcurrentHashMap(int initialCapacity) {
     if (initialCapacity < 0)
         //初始容量小于0 抛出异常
         throw new IllegalArgumentException();
     /**
      * 初始化容量大于最大容量 1 << 30 无符号右移1位
      *     初始化容量为最大容量
      *     否则，大于输入参数且最近的2的整数次幂的数
      */
     int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ?
                MAXIMUM_CAPACITY :
                tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
     /**
      * sizeCtl > 0,数值表示初始化或下一次进行扩容的大小
      * 这里表示初始化
      */
     this.sizeCtl = cap;
 }
```
#### 通过给定的Map创建相同映射的并发Map
```
public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
    this.sizeCtl = DEFAULT_CAPACITY;
    //putAll()方法后面见
    putAll(m);
}
```
#### 根据给定的元素数量（ initialCapacity ）和初始表密度（ loadFactor ）创建
```
public ConcurrentHashMap(int initialCapacity, float loadFactor) {
    this(initialCapacity, loadFactor, 1);
}
```
&ensp;&ensp;上述代码中，调用this(initialCapacity, loadFactor, 1)方法，对于该构造方法的解释如下。

```
public ConcurrentHashMap(int initialCapacity,
                         float loadFactor, int concurrencyLevel) {
    //参数判断，非法参数抛出异常
    if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
        throw new IllegalArgumentException();
    if (initialCapacity < concurrencyLevel)   // Use at least as many bins
        // 初始容量小于16时默认初始容量为16
        initialCapacity = concurrencyLevel;   // as estimated threads
    //初始容量大于16时重新计算初始化容量    
    long size = (long)(1.0 + (long)initialCapacity / loadFactor);
    int cap = (size >= (long)MAXIMUM_CAPACITY) ?
        MAXIMUM_CAPACITY : tableSizeFor((int)size);
    this.sizeCtl = cap;
}
```

#### tableSizeFor()方法
&ensp;&ensp;在上面的构造方法中有几个构造方法都调用到tableSizeFor()方法。该方法的主要作用是：**大于输入参数且最近的2的整数次幂的数。**

&ensp;&ensp;下面就来看看该方法的具体实现：
```
private static final int tableSizeFor(int c) {
    /**
     * 在使用位运算之前减1是必要的，
     * 否则对于c=16,最后就会得到32.实际需求应当返回16. 
     */
    int n = c - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```
&ensp;&ensp;为了更好的理解上述方法，下面以输入数字c = 25为例来看看；
  
     假设：c = 25；则 n = 24; 对应二进制表示为：    0001 1000;
          n >>> 1; ==> 0000 1100; n |= n >>> 1; 0000 1100; 得结果为：0001 1100;
          n >>> 2; ==> 0000 0111; n |= n >>> 2; 得结果为: 0001 1111;
          n >>> 4; ==> 0000 0001; n |= n >>> 4; 得结果为：0001 1111;
     至此：后面的两步运算结果之后都是同一个结果为：0001 1111;对应十进制数为：31；
     最后返回的数字为 32; 而大于25的最近的2的整数次幂恰好为32。
  

## ConcurrentHashMap的操作

### put()方法
```
public V put(K key, V value) {
    return putVal(key, value, false);
}
```
&ensp;&ensp;将指定的键映射到此表中的指定值,调用putVal(key, value, false);

#### putVal()方法
```
final V putVal(K key, V value, boolean onlyIfAbsent) {
    /**
     * 键或值中有一个为null，抛出异常
     * 说明ConcunrrentHashMap的Ke不能为null
     */
    if (key == null || value == null) throw new NullPointerException();
    //获取Key的Hash值
    int hash = spread(key.hashCode());
    //桶数量
    int binCount = 0;
    //采用自循环的方式添加数据
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            /**
             *  数据容器table为null或者长度为0
             *  调用initTable()方法初始化数据容器
             */
            tab = initTable();
        /**
         * 根据hash值计算出在table里面的位置
         * 获取对应位置的Node结点
         */
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            /**
             * 该位置的结点为 null
             * 利用CAS操作设置table数组中索引为i的元素
             */
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                break;                 
        }
        
         /**
          * 到这里说明该位置的结点不为null
          * 判断该位置结点类型是否为 forwardingNode 结点
          */
        else if ((fh = f.hash) == MOVED)
             // 是forwardingNode 结点类型，调用扩容方法
            tab = helpTransfer(tab, f);
        
        /**
         * 到这里说明结点不为null，
         * 且 不是forwardingNode 结点类型
         */    
        else {
            V oldVal = null;
            /**
             * 使用synchronized保证线程安全
             * 锁对象为当前结点
             */ 
            synchronized (f) {
                /**
                 * 重新获取当前位置结点，
                 * 判断当前位置的结点是否改变(有没被扩容)
                 * 保证当插入结点位置正确，因为扩容后，
                 * 容量变化会导致插入结点位置发生变化
                 */ 
                if (tabAt(tab, i) == f) {
                    // 结点的Hash值要大于等于0
                    if (fh >= 0) {
                        binCount = 1;
                        // 遍历链表中所有的结点
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            /**
                             * 链表中结点key的hash值和对应Key的Hash值相同
                             * 结点的key与put()元素的key相同或equals()方法相等
                             * 修改对应结点的value
                             */
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                // onlyIfAbsent 默认false
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            // 遍历到链表中的最后一个节点
                            if ((e = e.next) == null) {
                                /**
                                 * 此时，pred标记当前链表中的最后一个结点
                                 * 将当前结点链接到链表的最后一个结点上
                                 */
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;
                            }
                        }
                    }
                    //如果这个节点是树节点，就按照树的方式插入值
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                       value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            // 判断链表中节点数量是否等于0
            if (binCount != 0) {
                /**
                 * 如果链表长度已经达到临界值8，
                 * 就需要把链表转换为树结构（TREEIFY_THRESHOLD = 8）
                 */ 
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    //CAS 式更新baseCount，并判断是否需要扩容
    addCount(1L, binCount);
    return null;
}
```

#### initTable()方法
&ensp;&ensp;通过上面的构造方法可以看出，在调用ConcurrentHashMap的构造方法时，只是指定其初始化时的容量大小，并没有对Hash表(transient volatile Node<K,V>[] table)进行初始化，在上篇文章中提到table的初始化时懒加载方式初始化的，在这里可以更明确的看到。Hash表的初始化时在第一次插入元素的时候完成的。
```
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    // Hash表的数据容器为空或者长度为0 才会进入该方法
    while ((tab = table) == null || tab.length == 0) {
        /**
         * sizeCtl<0，意味着另外的线程执行CAS操作成功，
         * 当前线程只需要让出cpu时间片（放弃 CPU 的使用）
         */
        if ((sc = sizeCtl) < 0)
            Thread.yield(); // lost initialization race; just spin
          
        /**
         * 到这里说明未被初始化，继续初始化
         * compareAndSwapInt(Object var1, long offset, int expect, int update)
         * obj内的value和expect相等，就证明没有其他线程改变过这个变量，
         * 那么就更新它为update，如果这一步CAS没有成功，
         * 那就采用自旋的方式继续进行CAS操作。
         */    
        else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            try {
                if ((tab = table) == null || tab.length == 0) {
                    // 确定数据容器的初始化容量
                    int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                    @SuppressWarnings("unchecked")
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    table = tab = nt;
                    //计算阈值，建议使用这种方式，尽量不使用浮点值
                    sc = n - (n >>> 2);
                }
            } finally {
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```  
&ensp;&ensp;通过上述代码的学习，发现ConcurrentHashMap在初始化的时候只允许一个线程对其初始化。

#### helpTransfer()方法
&ensp;&ensp;在上述的putVal()方法中，有一段代码提到结点类型为ForwardingNode的时候会调用扩容方法，下面在调用扩容方法之前，先来学习一下ForwardingNode;

##### ForwardingNode结点类
```java
static final class ForwardingNode<K,V> extends Node<K,V> {
    final Node<K,V>[] nextTable;
    ForwardingNode(Node<K,V>[] tab) {
        super(MOVED, null, null, null);
        this.nextTable = tab;
    }

    Node<K,V> find(int h, Object k) {
        ...
    }
}
```
&ensp;&ensp;可以看出ForwardingNode 是 Node类的子类，并重写了父类中的find()方法,用来查找结点。看到在ForwardingNode类中有一个final Node<K,V>[] nextTable 属性，上篇文章中讲到nextTable只有在扩容时才会用到，其他时候都为null，用于指向下一张hash表。ForwardingNode节点的key、value、next指针全部为null，它的hash值为MOVED（static final int MOVED = -1）。它用于链接两个table的节点类。其中的find方法是用来在nextTable中查找结点。

&ensp;&ensp;上面对ForwardingNode结点类有了一定的了解之后，再来看看helpTransfer()方法的实现。

```
final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
    Node<K,V>[] nextTab; int sc;
    /**
     * 验证是否需要扩容
     * 原先的容器不为null，
     * 当前结点的类型为ForwardingNode，
     * 当前节点指向的nextTable 不为null
     */
    if (tab != null && (f instanceof ForwardingNode) &&
        (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
        int rs = resizeStamp(tab.length);
        while (nextTab == nextTable && table == tab &&
               (sc = sizeCtl) < 0) { //sizeCtl小于0 扩容状态
            if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                sc == rs + MAX_RESIZERS || transferIndex <= 0)
                break;
            if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                transfer(tab, nextTab);
                break;
            }
        }
        return nextTab;
    }
    return table;
}
```

##### transfer()方法
```java
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
    int n = tab.length, stride;
    if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
        stride = MIN_TRANSFER_STRIDE; // subdivide range
    if (nextTab == null) {            // initiating
        try {
            @SuppressWarnings("unchecked")
            Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
            nextTab = nt;
        } catch (Throwable ex) {      // try to cope with OOME
            sizeCtl = Integer.MAX_VALUE;
            return;
        }
        nextTable = nextTab;
        transferIndex = n;
    }
    int nextn = nextTab.length;
    ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
    boolean advance = true;
    boolean finishing = false; // to ensure sweep before committing nextTab
    for (int i = 0, bound = 0;;) {
        Node<K,V> f; int fh;
        while (advance) {
            int nextIndex, nextBound;
            if (--i >= bound || finishing)
                advance = false;
            else if ((nextIndex = transferIndex) <= 0) {
                i = -1;
                advance = false;
            }
            else if (U.compareAndSwapInt
                     (this, TRANSFERINDEX, nextIndex,
                      nextBound = (nextIndex > stride ?
                                   nextIndex - stride : 0))) {
                bound = nextBound;
                i = nextIndex - 1;
                advance = false;
            }
        }
        if (i < 0 || i >= n || i + n >= nextn) {
            int sc;
            if (finishing) {
                nextTable = null;
                table = nextTab;
                sizeCtl = (n << 1) - (n >>> 1);
                return;
            }
            if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                    return;
                finishing = advance = true;
                i = n; // recheck before commit
            }
        }
        else if ((f = tabAt(tab, i)) == null)
            advance = casTabAt(tab, i, null, fwd);
        else if ((fh = f.hash) == MOVED)
            advance = true; // already processed
        else {
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    Node<K,V> ln, hn;
                    if (fh >= 0) {
                        int runBit = fh & n;
                        Node<K,V> lastRun = f;
                        for (Node<K,V> p = f.next; p != null; p = p.next) {
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        }
                        else {
                            hn = lastRun;
                            ln = null;
                        }
                        for (Node<K,V> p = f; p != lastRun; p = p.next) {
                            int ph = p.hash; K pk = p.key; V pv = p.val;
                            if ((ph & n) == 0)
                                ln = new Node<K,V>(ph, pk, pv, ln);
                            else
                                hn = new Node<K,V>(ph, pk, pv, hn);
                        }
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                    else if (f instanceof TreeBin) {
                        TreeBin<K,V> t = (TreeBin<K,V>)f;
                        TreeNode<K,V> lo = null, loTail = null;
                        TreeNode<K,V> hi = null, hiTail = null;
                        int lc = 0, hc = 0;
                        for (Node<K,V> e = t.first; e != null; e = e.next) {
                            int h = e.hash;
                            TreeNode<K,V> p = new TreeNode<K,V>
                                (h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                if ((p.prev = loTail) == null)
                                    lo = p;
                                else
                                    loTail.next = p;
                                loTail = p;
                                ++lc;
                            }
                            else {
                                if ((p.prev = hiTail) == null)
                                    hi = p;
                                else
                                    hiTail.next = p;
                                hiTail = p;
                                ++hc;
                            }
                        }
                        ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                            (hc != 0) ? new TreeBin<K,V>(lo) : t;
                        hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                            (lc != 0) ? new TreeBin<K,V>(hi) : t;
                        setTabAt(nextTab, i, ln);
                        setTabAt(nextTab, i + n, hn);
                        setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }
}
```
#### putTreeVal()方法

```java
final TreeNode<K,V> putTreeVal(int h, K k, V v) {
    Class<?> kc = null;
    boolean searched = false;
    for (TreeNode<K,V> p = root;;) {
        int dir, ph; K pk;
        if (p == null) {
            first = root = new TreeNode<K,V>(h, k, v, null, null);
            break;
        }
        else if ((ph = p.hash) > h)
            dir = -1;
        else if (ph < h)
            dir = 1;
        else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
            return p;
        else if ((kc == null &&
                  (kc = comparableClassFor(k)) == null) ||
                 (dir = compareComparables(kc, k, pk)) == 0) {
            if (!searched) {
                TreeNode<K,V> q, ch;
                searched = true;
                if (((ch = p.left) != null &&
                     (q = ch.findTreeNode(h, k, kc)) != null) ||
                    ((ch = p.right) != null &&
                     (q = ch.findTreeNode(h, k, kc)) != null))
                    return q;
            }
            dir = tieBreakOrder(k, pk);
        }

        TreeNode<K,V> xp = p;
        if ((p = (dir <= 0) ? p.left : p.right) == null) {
            TreeNode<K,V> x, f = first;
            first = x = new TreeNode<K,V>(h, k, v, f, xp);
            if (f != null)
                f.prev = x;
            if (dir <= 0)
                xp.left = x;
            else
                xp.right = x;
            if (!xp.red)
                x.red = true;
            else {
                lockRoot();
                try {
                    root = balanceInsertion(root, x);
                } finally {
                    unlockRoot();
                }
            }
            break;
        }
    }
    assert checkInvariants(root);
    return null;
}
```
#### treeifyBin() 链表转红黑树

```java
private final void treeifyBin(Node<K,V>[] tab, int index) {
    Node<K,V> b; int n, sc;
    if (tab != null) {
        if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
            tryPresize(n << 1);
        else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
            synchronized (b) {
                if (tabAt(tab, index) == b) {
                    TreeNode<K,V> hd = null, tl = null;
                    for (Node<K,V> e = b; e != null; e = e.next) {
                        TreeNode<K,V> p =
                            new TreeNode<K,V>(e.hash, e.key, e.val,
                                              null, null);
                        if ((p.prev = tl) == null)
                            hd = p;
                        else
                            tl.next = p;
                        tl = p;
                    }
                    setTabAt(tab, index, new TreeBin<K,V>(hd));
                }
            }
        }
    }
}
```
   

### get()方法

```java
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    int h = spread(key.hashCode());
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```    
          
### 其他方法

#### spread()方法
```
static final int spread(int h) {
    return (h ^ (h >>> 16)) & HASH_BITS;
}
```

### 一些思考

**负载因子为什么是0.75而不是其他值？**

**链表转红黑树的阈值为什么是8？**
