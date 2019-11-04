### 内存分配策略


#### 优先分配到eden
&ensp;&ensp;大多数情况下，对象在新生代Eden区中分配。当Eden区中没有足够的空间进行分配时，虚拟机将发起一次Minor GC。
```java
public class Demo {
    public static void main(String[] args) {
        byte[] b = new byte[4 * 1024 * 1024];
    }
}
```
JVM参数如下配置：-verbose:gc -XX:+PrintGCDetails，这个收集器日志的参数，告诉虚拟机在发生垃圾收集行为时打印内存回收日志，并且在进程退出的时候输出当前的内存各个区域的分配情况。

![image](https://github.com/FunCheney/concurrency/blob/master/src/md/jvm/image/jvm_001.pgn "JVM参数配置")

打印GC日志：
![image](https://github.com/FunCheney/concurrency/blob/master/src/md/jvm/image/jvm_002.png "内存分配策略打印_001")

#### 大对象直接分配到老年代
&ensp;&ensp;所谓的大对象是指，需要大量连续的内存空间的Java对象，最典型的大对象就是那种很长的字符串以及数组。大对象对虚拟机的内存分配来说就是一个坏消息，经常出现大对象容易导致内存还有不少空间时就提前触发垃圾收集以获取足够的连续空间来“安置”它们。

&ensp;&ensp;虚拟机提供了一个-XX: PretenureSizeThreshold参数，令这个设置值的对象直接在老年代分配。这样做的目的是避免在Eden区及两个Survivor区之间发生大量的内存复制。

#### 长期存活的对象分配到老年代
&ensp;&ensp;虚拟机采用分代收集的思想来管理内存，那么内存回收时就必须能识别到哪些对象应该放在新生代，哪些对象应该放在老年代中。为了做到这一点，虚拟机给每个对象定义了一个对象年龄(Age)计数器。如果对象在Eden出生并经过第一次Minor GC后仍然存活，并且被Survivor容乃的话，将被移动到Survivor空间中，并且对象的年龄设置为1。对象在Survivor中每熬过一次Minor GC，年龄就增加1岁，当他的年龄增加到一定程度(默认为15岁)，就将会被晋升到老年代中。对象晋升老年代年龄的年龄阈值，可以通过参数 -XX:MaxTenuringThreshold设置。


#### 空间分配担保
#### 动态对象年龄判断
&ensp;&ensp;为了能更好的适应不同程序的内存情况，虚拟机并不是永远地要求对象的年龄必须达到了MaxTenuringThreshold才能晋升到老年代，如果Survivor空间中相同年龄所有对象大小的总和大于Survivor空间的一半，年龄大于或等于该年龄的对象就可以直接进入老年代，无须等到MaxTenuringTheshold中要求的年龄。