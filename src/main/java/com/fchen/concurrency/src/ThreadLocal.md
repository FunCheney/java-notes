## ThreadLocal
&ensp;&ensp;ThreadLocal是线程变量，是一个ThreadLocal对象为键、任意对象为值的存储结构。这个结构被附带在线程上，也就是说一个线程可以根据一个ThreadLocal对象查询到绑定在这个线程上的一个值。

### 用途
* 保存线程上下文信息，在任意需要的地方获取；

* 线程私有变量，是线程安全的。
