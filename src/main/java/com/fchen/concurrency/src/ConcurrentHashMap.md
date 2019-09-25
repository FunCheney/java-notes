## ConcurrentHashMap
&ensp;&ensp;ConcurrentHashMap是线程安全的HashMap；在并发的情况下使用HashMap可能会导致死循环，在进行put操作时导致CPU利用率接近100%。是因为在多线程会导致HashMap的Entry链表形成环形数据结构，一旦形成环形数据结构，Entry的next结点永远不能为空，就会产生死循环获取Entry。

&ensp;&ensp;在JDk1.8中ConcurrentHashMap采用Node + CAS + Synchronized来保证并发情况下的更新不会出现问题。其底层的数据结构是：数组 + 链表 + 红黑树 的方式来实现的。

注：[点击了解红黑树]。

### ConcurrentHashMap中的成员

#### 关键常量
```
/** 
  * 最大容量，32位的Hash值的最高两位用作控制的目的，
  * 这个值必须恰好是1<<30(2的30次方)，这样分配的java数组
  * 在索引范围内(2的整数次幂)。
  */
private static final int MAXIMUM_CAPACITY = 1 << 30;

/**
 * Hash表默认的初始容量。必须是2的整数次幂，
 * 最小为1，最大为MAXIMUM_CAPACITY.
 */
private static final int DEFAULT_CAPACITY = 16;

/**
 * 最大的数组大小(非2次幂)。
 * toArray 和 related方法使用。
 */
static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

/**
 * 表默认的并发级别，未使用。为与该类的以前版本兼容而定义
 */
private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

/**
 * T表的默认加载因子，在构造函数中重写此值只影响初始表容量。
 * 浮点值通常不被使用，
 * 当前表中的容量 = 初始化容量 - (初始化容量无符号右移2位)时扩容
 */
private static final float LOAD_FACTOR = 0.75f;

/**
 * 链表转红黑树阀值,该值必须大于2，并且应该至少为8。
 * 以便与树移除中关于收缩后转换回普通Bin的假设相吻合。
 */
static final int TREEIFY_THRESHOLD = 8;

/**
 * 用于在调整大小操作期间反树化(拆分)bin的bin计数阈值，
 * 应该小于TREEIFY_THRESHOLD, 最多为6.
 */
static final int UNTREEIFY_THRESHOLD = 6;

static final int MIN_TREEIFY_CAPACITY = 64;

private static final int MIN_TRANSFER_STRIDE = 16;

/**
 * 用于生成戳记的位的数目，单位为sizeCtl。
 * 32位数组必须至少为6.
 */
private static int RESIZE_STAMP_BITS = 16;

/**
 * 2^15-1，help resize的最大线程数
 */
private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

/**
 * 32-16=16，sizeCtl中记录size大小的偏移量
 */
private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

/* forwarding nodes的hash值*/
static final int MOVED     = -1; 

/* 树根节点的hash值*/
static final int TREEBIN   = -2; 

/* ReservationNode的hash值*/
static final int RESERVED  = -3; 

/* 普通节点哈希的可用位*/
static final int HASH_BITS = 0x7fffffff;
```

#### 关键属性
```
/**
 * 装载Node的数组，作为ConcurrentHashMap的数据容器，
 * 采用懒加载的方式，直到第一次插入数据的时候才会进行初始化操作，
 * 数组的大小总是为2的幂次方。
 */
transient volatile Node<K,V>[] table;

/**
 * 扩容时使用，只有在扩容的时候才为非null
 */
private transient volatile Node<K,V>[] nextTable;


/**
 * 控制Table的初始化与扩容。
 *   当值为负数时table正在被初始化或扩容
 *     -1表示正在初始化
 *     -N则表示当前正有N-1个线程进行扩容操作
 *   正数或0代表hash表还没有被初始化，这个数值表示初始化或下一次进行扩容的大小
 */
private transient volatile int sizeCtl;
```

#### 内部类

##### Node 类
&ensp;&ensp; Node是最核心的内部类，它包装了key-value键值对，所有插入ConcurrentHashMap的数据都包装在这里面。Node类实现了Map.Entry<K,V>接口，Node类中包含有属性有key，value以及下一节点的引用，其中value和next属性使用volatile关键字修饰，保证其在多线程下的可见性。不允许调用setValue方法直接改变Node的value域，它增加了find方法辅助map.get()方法。

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    volatile V val;
    volatile Node<K,V> next;
    /**
    * Node结点的构造方法
    */
    Node(int hash, K key, V val, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.val = val;
        this.next = next;
    }

    public final K getKey()       { return key; }
    public final V getValue()     { return val; }
    public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
    public final String toString(){ return key + "=" + val; }
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public final boolean equals(Object o) {
        Object k, v, u; Map.Entry<?,?> e;
        return ((o instanceof Map.Entry) &&
                (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                (v = e.getValue()) != null &&
                (k == key || k.equals(key)) &&
                (v == (u = val) || v.equals(u)));
    }
    
    /**
     * Node结点中提供的find方法，在子类中可重写 
     */
    Node<K,V> find(int h, Object k) {
        Node<K,V> e = this;
        if (k != null) {
            do {
                K ek;
                if (e.hash == h &&
                    ((ek = e.key) == k || (ek != null && k.equals(ek))))
                    return e;
            } while ((e = e.next) != null);
        }
        return null;
    }
}
```

##### TreeNode类
&ensp;&ensp;树节点类，另外一个核心的数据结构，包含父接点，左链接的结点，右链接的结点，前驱结点的引用，以及结点的颜色(默认红色)。当链表长度过长的时候，会转换为TreeNode在TreeBins中使用。TreeNode是上述Node类的子类。
```java

static final class TreeNode<K,V> extends Node<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;

    
    TreeNode(int hash, K key, V val, Node<K,V> next,
             TreeNode<K,V> parent) {
        super(hash, key, val, next);
        this.parent = parent;
    }

    Node<K,V> find(int h, Object k) {
        return findTreeNode(h, k, null);
    }

    /**
     * 通过给定的key从指定的根节点开始(在其子树)查找
     * 对应的TreeNode结点，没有返回null
     * h 表示当前可以的Hash值
     * k 要查找的键(key)
     * kc k的Class对象，该Class应该是实现了Comparable<K>的，否则应该是null
     */
    final TreeNode<K,V> findTreeNode(int h, Object k, Class<?> kc) {
        // 判断对应的键是否为null
        if (k != null) {
            //获取当前结点
            TreeNode<K,V> p = this;
            do  { //循环
                int ph, dir; K pk; TreeNode<K,V> q;
                TreeNode<K,V> pl = p.left, pr = p.right;
                if ((ph = p.hash) > h)
                    /**
                    *  当前结点的Hash值大于要查找的Key的Hash值H
                    *  在当前节点的左子树中查找，反之在右子树中
                    *  进行下一轮循环
                    */
                    p = pl;
                else if (ph < h)
                    p = pr;
                else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                    /**
                    *  当前结点的key等于要查找的key，
                    *  或当前结点的key不为null且equals()方法为true
                    *  返回当前的结点
                    */
                    return p;
                
                    
                /**
                * 执行到这里说明 hash比对相同，
                * 但当前节点的key与要查找的k不相等
                */ 
                else if (pl == null)
                    /**
                    * 左孩子为空，指向当前节点右孩子，继续循环
                    */
                    p = pr;
                else if (pr == null)
                    /**
                     * 右孩子为空，指向当前节点左孩子，继续循环
                     */
                    p = pl;
                /**
                 * 左右孩子都不为空，再次进行比较，
                 * 确定在左子树还是右子树中查找 
                 */    
                else if ((kc != null ||
                          (kc = comparableClassFor(k)) != null) &&
                         (dir = compareComparables(kc, k, pk)) != 0)
                    /**
                     * comparable方法来比较pk和k的大小
                     * dir小于0，p指向左孩子，否则指向右孩子
                     */
                    p = (dir < 0) ? pl : pr;
                    
                /**
                 *  无法通过上一步骤确定是在左/右子树中查找
                 *  从右子树中递归调用findTreeNode()方法查找
                 */    
                else if ((q = pr.findTreeNode(h, k, kc)) != null)
                    return q;
                else
                    //在右子树中没有找到，到左子树中查找
                    p = pl;
            } while (p != null);
        }
        return null;
    }
}
```

##### TreeBin类
&ensp;&ensp;红黑树结构。该类并不包装key-value键值对，而是TreeNode的列表和它们的根节点。它代替了TreeNode的根节点，也就是说在实际的ConcurrentHashMap“数组”中，存放的是TreeBin对象，而不是TreeNode对象。这个类含有读写锁。
这里我们先看红黑树相关操作的方法。

```java
static final class TreeBin<K,V> extends Node<K,V> {
        TreeNode<K,V> root;
        volatile TreeNode<K,V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        static final int WRITER = 1; // set while holding write lock
        static final int WAITER = 2; // set when waiting for write lock
        static final int READER = 4; // increment value for setting read lock

        /**
         * 通过结点b构造红黑树
         */
        TreeBin(TreeNode<K,V> b) {
            super(TREEBIN, null, null, null);
            this.first = b;
            TreeNode<K,V> r = null;
            for (TreeNode<K,V> x = b, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (TreeNode<K,V> p = r;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);
                            TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            r = balanceInsertion(r, x);
                            break;
                        }
                    }
                }
            }
            this.root = r;
            assert checkInvariants(root);
        }
        
        /**
         *  左旋转过程
         */
        static <K,V> TreeNode<K,V> rotateLeft(TreeNode<K,V> root,
                                              TreeNode<K,V> p) {
            TreeNode<K,V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        /**
         *  右旋转 
         */
        static <K,V> TreeNode<K,V> rotateRight(TreeNode<K,V> root,
                                               TreeNode<K,V> p) {
            TreeNode<K,V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }
        
        /**
         * 红黑树中插入结点后会打破红黑树性质需要平衡 
         * TreeNode<K,V> root 根结点
         * TreeNode<K,V> x 要插入的结点
         */
        static <K,V> TreeNode<K,V> balanceInsertion(TreeNode<K,V> root,
                                                    TreeNode<K,V> x) {
            //默认插入结点为红色
            x.red = true;
            for (TreeNode<K,V> xp, xpp, xppl, xppr;;) {
                // xp为当前节点的父结点
                if ((xp = x.parent) == null) {
                    /**
                     * 当前结点的父结点为空，说明红黑树中只有一个结点
                     * 当前结点即为根结点，颜色为黑色
                     */
                    x.red = false;
                    return x;
                }
                /**
                 * 当前结点的父结点（xp）不为null
                 *   父结点为黑色，没有打破红黑树的平衡性(着色可能有问题)
                 *   父结点的的父结点(xpp)为null，红黑树中只有两个节点
                 *   上述两种情况直接返回root结点
                 */
                else if (!xp.red || (xpp = xp.parent) == null)
                    return root;
                
                /**
                * 当前结点的父结点(xp) 为 其父节点(xpp)的左孩子 
                */
                if (xp == (xppl = xpp.left)) {
                   
                    
                    if ((xppr = xpp.right) != null && xppr.red) {
                        /**
                         *  当前结点(x)得父结点(xp)的父结点(xpp)的右孩子(xppr)
                         *  不为null 且 颜色为红色(此时颜色的性质不满足)
                         *  变换颜色
                         */
                        xppr.red = false; // 将xppr变为黑色
                        xp.red = false;   // 将xp变为黑色  
                        xpp.red = true;   // 将xpp变为红色 
                        x = xpp; // 将xpp指向x 继续循环
                    }
                    /**
                     * 当前结点的父结点的父结点右孩子为null或颜色为黑色
                     */
                    else {
                        // 如果(当前结点)x为父结点的右孩子
                        if (x == xp.right) {
                            //左旋转
                            root = rotateLeft(root, x = xp);
                            // 重新指定xpp
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        // 如果当前结点的父结点不为null
                        if (xp != null) {
                            // 将xp的颜色置为黑色
                            xp.red = false;
                            // 父结点的父结点(xpp)不为null
                            if (xpp != null) {
                                //将xpp颜色置为红色
                                xpp.red = true;
                                // 有旋转
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                
                /**
                 * 当前结点的父结点(xp) 为 其父节点(xpp)的右孩子 
                 */
                else {
                    
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }
        
        /**
         *  
         */
        static <K,V> TreeNode<K,V> balanceDeletion(TreeNode<K,V> root,
                                                   TreeNode<K,V> x) {
            for (TreeNode<K,V> xp, xpl, xpr;;)  {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (x.red) {
                    x.red = false;
                    return root;
                }
                else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                            (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                    null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                sr.red = false;
                        }
                        if (xp != null) {
                            xp.red = false;
                            root = rotateLeft(root, xp);
                        }
                        x = root;
                    }
                }
            }
            else { // symmetric
                if (xpl != null && xpl.red) {
                    xpl.red = false;
                    xp.red = true;
                    root = rotateRight(root, xp);
                    xpl = (xp = x.parent) == null ? null : xp.left;
                }
                if (xpl == null)
                    x = xp;
                else {
                    TreeNode<K,V> sl = xpl.left, sr = xpl.right;
                    if ((sl == null || !sl.red) &&
                        (sr == null || !sr.red)) {
                        xpl.red = true;
                        x = xp;
                    }
                    else {
                        if (sl == null || !sl.red) {
                            if (sr != null)
                                sr.red = false;
                            xpl.red = true;
                            root = rotateLeft(root, xpl);
                            xpl = (xp = x.parent) == null ?
                                null : xp.left;
                        }
                        if (xpl != null) {
                            xpl.red = (xp == null) ? false : xp.red;
                            if ((sl = xpl.left) != null)
                                sl.red = false;
                        }
                        if (xp != null) {
                            xp.red = false;
                            root = rotateRight(root, xp);
                        }
                        x = root;
                    }
                }
            }
        }
    }

    
}
```






 [点击了解红黑树]:https://mp.weixin.qq.com/s/FzNbESz6FWdCayVyRD3YFA