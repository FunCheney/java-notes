## RabbitMq 消息发送模式

### 迅速消息发送
&ensp;&ensp;迅速消息是指消息不进行落库存储，不做可靠性的保障。在一些非核心消息、日志数据、或者统计分析等场景下使用。迅速消息的有点就是性能高，吞吐量大。

实现：

&ensp;&ensp;调用RabbitMq的covertAndSend()方法即可。

### 确认消息发送
[消息可靠性投递解决方案]

### 批量消息发送

&ensp;&ensp;批量消息是指将消息放到一个集合里统一进行提交，这种方案设计思路是期望消息在一个会话里，比如投掷到threadLocal里的集合，然后拥有相同的会话Id，并且带有这次提交消息size相关属性，最重要的一点是要把这一批消息进行合并。对于Channel而言，就是发送一次消息。这种方式也是希望消费端在消费的时候，可以进行批量的消费，针对某一个原子业务的操作去处理，但是不保障可靠性，需要进行补偿机制。

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq_Batch_Send.jpg "批量发送")

* step1业务入库

* 批量发送，批量发送组件中包含SessionId，ThreadLocal，MessageHoder；一批消息共用一个sessionId。然后将这一批消息放到ThreadLocal中。MessageHoder可能是一个List集合，承装这一批消息，装到规定数量之后，进行消息入库(记录相关sessionId的消息)。如果要做可靠性投递，则数据落库；反之则不需要。

* step3 消息投递

* step4 消息确认

* step5 修改状态

后面的步奏就与可靠性投递类似。

对于Consumer

&ensp;&ensp;接收到消息之后，根据类型(批量)进行拆分。根据size获取这一批消息有几条记录，然后组成一个原子性操作即可。


### 延迟发送
&ensp;&ensp;延迟消息在Message封装时添加delayTime属性即可。

### 顺序发送
* 1.发送的顺序消息，必须保障消息投递到同一个队列，且消费者只能有一个。

* 2.需要统一提交(可能是合并成一个大消息，也肯能拆分成多个消息)，并且所有消息的会话ID一致。

* 3.添加消息属性：顺序标记的序号，和本次顺序消息的size属性，进行落库操作

* 4.并行进行发送给自身的延迟消息(注意带上关键属性：会话ID，size)进行后续处理消费

* 5.当收到延迟消息后，根据会话ID，size抽取数据库数据进行处理(顺序消息的消费不是即时消费的，有一个延迟投递的过程)。

* 6.定时轮询补偿机制，处理异常情况

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitmq_order_msg.jpg "顺序消息")

### 消息的幂等性




[消息可靠性投递解决方案]:https://github.com/FunCheney/concurrency/blob/master/src/md/RabbitMq_1.md#消息如何保障100的投递成功