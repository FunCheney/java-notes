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
#### 大对象直接分配到老年代


#### 长期存活的对象分配到老年代
#### 空间分配担保
#### 动态对象年龄判断