## RabbitMq集群架构模式

### 主备模式

&ensp;&ensp;实现RabbitMq的高可用集群，一般在并发和数据量不高的情况下。主备模式也称之为Warren模式。

主备模式中，主节点提供读写，从节点备份，不提供读写。作用就是主节点故障时，保证服务的可用(切换到从节点)。

主从模式中，主节点提供读写，从节点只读。

 ![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq集群架构_1.jpg "主备模式")

 ### 镜像模式
 
 &ensp;&ensp;Mirror镜像模式，保证100%数据不丢失，在实际工作中用的最多。且实现集群简单。
 
 * 镜像队列：
 
 &ensp;&ensp；目的就是抱枕RabbitMq数据的高可靠性解决方案，实现数据的同步，一般来讲是2-3个节点实现数剧同步(对于100%数据可靠性解决方案一般是3节点)。
 
  ![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq集群架构_2.jpg "镜像模式")
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 