#### 安全发布对象
1.在静态初始化函数中初始化对象的引用

2.将对象的引用保存到volatile类型或者AtomicReference中

3.将对象的引用保存到某个正确构造对象的final类型域中

4.将对象的引用保存到一个由锁保护的域中