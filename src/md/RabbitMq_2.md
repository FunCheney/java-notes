## RabbitMq集群架构模式

### 主备模式

&ensp;&ensp;实现RabbitMq的高可用集群，一般在并发和数据量不高的情况下。主备模式也称之为Warren模式。

主备模式中，主节点提供读写，从节点备份，不提供读写。作用就是主节点故障时，保证服务的可用(切换到从节点)。

主从模式中，主节点提供读写，从节点只读。

 ![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq集群架构_1.jpg "主备模式")

 ### 镜像模式
 
 &ensp;&ensp;Mirror镜像模式，保证100%数据不丢失，在实际工作中用的最多。且实现集群简单。
 
 * 镜像队列：
 
 &ensp;&ensp;目的就是抱枕RabbitMq数据的高可靠性解决方案，实现数据的同步，一般来讲是2-3个节点实现数剧同步(对于100%数据可靠性解决方案一般是3节点)。
 
  ![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq集群架构_2.jpg "镜像模式")
  
  ### 多活模式
&ensp;&ensp;多活模式是实现异地数据复制的主流模式，因为Shovel模式配置比较复杂，所以一般来说实现异地集群都是使用这种双活或者多活的模式来实现。这种模型需要依赖rabbitmq的federation插件，可以实现持续可靠的AMQP数据通信，多活模式在实际配置与应用非常简单。



&ensp;&ensp;RabbitMq部署架构采用双中心模式(多中心)，那么在两套(或多套)数据中心各部署一套RabbitMq集群，各个中心的RabbitMq服务除了需要为业务提供正常的消息服务，中心之间还需要实现不分队列消息共享。


![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq集群架构_3.jpg "多活模式")
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 