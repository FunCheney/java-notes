## RabbitMq 消息发送模式

### 迅速消息发送
&ensp;&ensp;迅速消息是指消息不进行落库存储，不做可靠性的保障。在一些非核心消息、日志数据、或者统计分析等场景下使用。迅速消息的有点就是性能高，吞吐量大。

实现：

&ensp;&ensp;调用RabbitMq的covertAndSend()方法即可。

### 确认消息发送

### 批量消息发送

&ensp;&ensp;批量消息是指将消息放到一个集合里统一进行提交，这种方案设计思路是期望消息在一个会话里，比如投掷到threadLocal里的集合，然后拥有相同的会话Id，并且带有这次提交消息size相关属性，最重要的一点是要把这一批消息进行合并。对于Channel而言，就是发送一次消息。这种方式也是希望消费端在消费的时候，可以进行批量的消费，针对某一个原子业务的操作去处理，但是不保障可靠性，需要进行补偿机制。

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitMq_Batch_Send.jpg "批量发送")



### 延迟发送
&ensp;&ensp;延迟消息在Message封装时添加delayTime属性即可。
