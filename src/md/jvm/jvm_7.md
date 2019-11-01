### 内存分配策略

#### 优先分配到eden
```java
public class Demo {
    public static void main(String[] args) {
        byte[] b = new byte[4 * 1024 * 1024];
    }
}
```
JVM参数如下配置：-verbose:gc -XX:+PrintGCDetails

![image](https://github.com/FunCheney/concurrency/blob/master/src/md/jvm/image/jvm_001.pgn "JVM参数配置")

打印GC日志：
![image](https://github.com/FunCheney/concurrency/blob/master/src/md/jvm/image/jvm_002.png "内存分配策略打印_001")

#### 大对象直接分配到老年代


#### 长期存活的对象分配到老年代
#### 空间分配担保
#### 动态对象年龄判断