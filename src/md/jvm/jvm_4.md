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
&ensp;&ensp;虚拟机啊堆转存储快照分析工具与jmap搭配使用，来分析jmap生成的的堆转储快照。jhat内置了一个微型的HTTP/HTML服务器，生成dump文件的分析结果后，可以在浏览器中查看。在实际的工作中，一般不使用jhat命令来分析dump文件，原因有二：一是一般不会在部署应用程序的服务器上直接分析dump文件，即使可以这样做，也会尽量将dunp文件复制到其他机器上进行分析，因为分析工作是一个耗时而且消耗硬件资源的过程，既然都要在其他机器进行，就没有必要受命令行工具的限制了；另一个原因是jhat的分析功能相对来说比较简陋。

#### Jstack(Stack Trace for Java)
&ensp;&ensp;jstack命令用于生成虚拟机当前时刻的线程快照(一般称为threaddump或javacore文件)。线程快照就是当前虚拟机内每一条线程正在执行的方法堆栈集合，生成线程快照的主要目的是定位线程出现长时间停顿的原因，如线程间死锁、死循环、请求外部资源导致长时间等待等都是导致线程长时间停顿的原因。线程出现停顿的时候通过jstack来查看各个线程的调用堆栈，就可以知道没有响应的线程到底在后台做些什么事情，或者等待着什么资源。

&ensp;&ensp;命令格式: jstack [option] vmid;

* jstack -f vmid: 当正常输出的请求不被响应时，强制输出线程堆栈
* jstack -l vmid: 初堆栈外，显示锁的附加信息
* jstack -m vmid: 如果调用到本地方法的话，可以显示C/C++的堆栈









