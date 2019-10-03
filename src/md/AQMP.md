## AQMP

* AMQP全称：Advanced Message Queuing Protocol (高级消息队列协议)

* AMQP定义：

&ensp;&ensp;是具有现代特征的二进制协议。是一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。

* AMQP协议模型：

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/amqp.jpg "AMQP协议模型")

Server: 又称Broker，接受客户端连接，实现AMQP实体服务

Connection：连接，应用程序与Broker的网络连接

Channel：网络信道，几乎所有的操作都在Channel中进行，是进行消息读写的通道。客户端可建立多个Channel，每个Channel代表一个会话任务。

Message: 消息，服务器和应用程序之间传送数据，有Properties和Body组成。Properties可以对消息进行修饰，比如消息的优先级，延迟等高级特性；Body则就是消息内容。

Virtual Host：虚拟地址，进行逻辑隔离，最上层的消息路由。一个Virtual Host里面可以有若干个Exchange和Queue，同一个Virtual Host里面不能有相同名称的Exchange或Queue。

Exchange：交换机，接收消息，根据路由键转发消息到绑定的队列。

Binding: Exchange和Queue之间的虚拟连接，binding中可以包含routing key。

Routing key：是一个路由规则，虚拟机可用来确定如何路由一个特定消息

Queue：也称为Message Queue，消息队列，保存消息并将它们转发给消费者。
