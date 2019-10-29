### 虚拟机分析工具

 #### JPS(Java process status)
 &ensp;&ensp;输入jps命令，查看Java进程本地虚拟机唯一id(lvmid) local virtual machine id;
 
* jps -l：显示进程所执行的主类或者Jar文件。
* jps -m：标识运行的主类所接收的参数。
* jps -v：输出JVM参数

#### Jstat
&ensp;&ensp;类装载、内存，垃圾收集，jit编译的信息。

* jstat -gcutil 进程id：显示gc的摘要信息。

#### Jinfo
&ensp;&ensp;用来实时查看和调整虚拟机的各项参数。使用方式：jinfo -[option] -[pid]

* jinfo -flag UseSerialGC [pid]:查看某一进程是否使用SerialGC；

虚拟机参数：

* -XX:[+/-]option: 启动或者禁用某一个参数
* -XX:option=[value]: 启动某一个参数

#### Jmap (Memory Map for Java)
&ensp;&ensp;Java内存映射工具，jmap命令用于生成堆转储快照(一般称为heapDump或dump文件)；使用方式：jmap [option] vmid 

* jamp -dump:format=b,file=[文件路径] [vmid]; 转储存堆的快照信息。

还可使用-XX:+HeadDumpOnOutOfMemoryError参数，可以让虚拟机在OOM异常出现之后自动生成dump文件。

* jamp -histo vmid；显示堆中对象的统计信息，包括类、实例数量、合计容量。

#### Jhat (JVM Heap Analysis Tool)
&ensp;&ensp;虚拟机啊堆转存储快照分析工具与jmap搭配使用，来分析jmap生成的的堆转储快照。







