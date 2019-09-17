## Exchange

Exchange：接收消息，并更具路由键转发消息所绑定的队列

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitmq_1_exchange.jpg "消息流转")

上图中：

* Send Message: 消息的生产方，生产者；将消息投递到Exchange，再由Exchange根据路由键发送到相应的队列中。

* Receive Message：消费的消费方，消费者；监听队列，接收消息。

* Routing Key：路由键和绑定的欢喜。Exchange和队列要建立一个绑定的关系，消息到达Exchange后如何到达队列就是通过Routing Key 来完成的。

#### Exchange 属性

* Name：交换机名称

* Type：交换机类型direct、topic、fanout、headers

&ensp;&ensp;所有发送到Direct Exchange的消息被转发到RouteKey中指定的Queue。Direct模式可以使用RabbitMq自带的Exchange：default Exchange，所以不需要将Exchange进行任何的绑定(binding)操作，消息传递时，RouteKey必须完全匹配才会被队列接收，否则该消息会被抛弃。


* 匹配规则如下：

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitmq_2_exchange.jpg "direct")

&ensp;&ensp;所有发送到Topic Exchange的消息被转发到所有关心RouteKey中指定Topic的Queue；Exchange将RouteKey和Exchange进行模糊匹配，此时队列需要绑定一个Topic。

注意:

符号“#”匹配一个或多个词

符号“*”匹配一个词

* 匹配规则如下：

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitmq_3_exchange.jpg "Topic")

&ensp;&ensp;fanout不处理路由键，只需要简单的将队列绑定到交换机上。发送到交换机上的消息都会被转发到与该交换机绑定的所有队列上。转发消息是最快的。

* 匹配规则如下：

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/rabbitmq_4_exchange.jpg "Topic")

* Durability：是否需要持久化，true为持久化

* Auto Delete：当最后一个绑定到Exchange上的队列删除后，自动删除该Exchange

* Internal：当前Exchange是否用于RabbitMq内部使用，默认为false

* Arguments：扩展参数，用于扩展AMQP协议自制定化使用

#### Binding-绑定

&ensp;&ensp;Exchange和Exchange、Queue之间的连接关系；Binding中可以包含RoutingKey或者参数




 

 
 
