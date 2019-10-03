## RabbitMQ 简单介绍

&ensp;&ensp;RabbitMQ是一个开源的消息代理和队列服务器，用来通过普通协议在完全不同的应用之间共享数据，RabbitMQ是基于AMQP协议的。

* 可靠性投递模式

* 返回模式

集群模型

* 表达式配置

* HA模式

* 镜像队列模型

高性能的原因

1.Erlang语言最初在于交换机领域的架构模式，这样使得RabbitMQ在Broker之间进行数据交互的性能非常优秀；

2.Erlang有着和原生Socket一样的延迟。


RabbitMQ消息流转

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitmq_0.jpg "消息流转")

发送消息时首先要指定消息要到那个Exchange上，然后通过指定Routing key 到Queue中。