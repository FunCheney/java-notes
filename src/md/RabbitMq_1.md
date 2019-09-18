## RabbitMq高级特性

### 消息如何保障100%的投递成功

生产端的可靠性投递

* 1.保障消息成功的发出

* 2.MQ节点的成功接收

* 3.发送端收到MQ节点(Broker)确认应答

* 4.完善的消息进行补偿机制

**可靠性投递的解决方案**

_1.消息落库，对消息进行打标_

&ensp;&ensp;解释：
```
发送消息时，持久化到数据库，并使用状态记录消息的状态。比如刚发送出去，消息的状态的发送中;
到达Broker端，生产端接收到Broker端的应答状态，将其状态改为发送成功。对于一直出处于发送中
消息状态，进行重试。比如轮询发送、多次尝试等。
```

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/可靠性投递_1.jpg "消息落库解决方案")

&ensp;&ensp; Step 1： 首先把消息信息(业务数据）存储到数据库中，紧接着，我们再把这个消息记录也存储到一张消息记录表里（或者另外一个同源数据库的消息记录表）

&ensp;&ensp; Step 2：发送消息到MQ Broker节点（采用confirm方式发送，会有异步的返回结果）

&ensp;&ensp; Step 3、4：生产者端接受MQ Broker节点返回的Confirm确认消息结果，然后进行更新消息记录表里的消息状态。比如默认Status = 0 当收到消息确认成功后，更新为1即可！

&ensp;&ensp; Step 5：但是在消息确认这个过程中可能由于网络闪断、MQ Broker端异常等原因导致 回送消息失败或者异常。这个时候就需要发送方（生产者）对消息进行可靠性投递了，保障消息不丢失，100%的投递成功！（有一种极限情况是闪断，Broker返回的成功确认消息，但是生产端由于网络闪断没收到，这个时候重新投递可能会造成消息重复，需要消费端去做幂等处理）所以我们需要有一个定时任务，（比如每5分钟拉取一下处于中间状态的消息，当然这个消息可以设置一个超时时间，比如超过1分钟 Status = 0 ，也就说明了1分钟这个时间窗口内，我们的消息没有被确认，那么会被定时任务拉取出来）

&ensp;&ensp; Step 6：接下来我们把中间状态的消息进行重新投递 retry send，继续发送消息到MQ ，当然也可能有多种原因导致发送失败

&ensp;&ensp; Step 7：我们可以采用设置最大努力尝试次数，比如投递了3次，还是失败，那么我们可以将最终状态设置为Status = 2 ，最后 交由人工解决处理此类问题（或者把消息转储到失败表中）。

* 总结：

&ensp;&ensp;上述的解决方案中有两次数据库持久化操作，第一次保存业务数据，第二次要处理消息数据，对消息进行记录。这样一来在数据量不是很大的情况下，完全满足需求。但是随着消息的数量增加，这样一来瓶颈就会出现在数据库。于是，就有了下面的另一种解决方案。

_2.消息的延迟投递，做二次确认，回调检查_

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/可靠性投递_2.jpg "延迟投递回调检查解决方案")

&ensp;&ensp; Step 0：业务数据入库；

&ensp;&ensp; Step 1：消息数据发送；

&ensp;&ensp; Step 2：延迟投递(step1发完之后，过几分钟在发送，根据业务场景来定)；

&ensp;&ensp; Step 3：消费端监听处理消息；

&ensp;&ensp; Step 4：发送消息处理完的响应，再次生成消息(确认消息，确认收到的消息处理成功)，发送到MQ；

&ensp;&ensp; Step 5：监听确认消息，监听到消息之后，持久化消息；

&ensp;&ensp; Step 6：监听延迟投递消息，监听到该消息后，根据相关信息查找之前投递消息在Callback Service 中是否处理成功，成功之后不做任何处理，说明消息处理完成。若没有查找到之前投递的消息，或者监听确认出现问题，做补偿。Callback服务通过RPC调用上传消息的服务，根据延迟检查消息中对应的关键信息(比如在步骤1中对应的id)，重新组装消息数据，并重复上述过程。
 
### 幂等性

定义：对一件事情进行操作，这个操作无论执行多少次，没次的都的结果都是一样的。

### 消费端-幂等性保障

 高并发的情况下，如何避免消息的重复消费？
 
 &ensp;&ensp;消费端实现幂等性，就以意味着，消息永远不会消费多次，即使首到多条一样的消息。
 
解决办法：

1. 唯一ID + 指纹码机制，利用数据库主键去重

&ensp;&ensp; 首先通过 唯一ID + 指纹码 去数据库查找，有相关数据说明数据已经被操作过，返回失败；没有，插入数据库。

好处：实现简单

坏处：高并发下有数据库写入性能瓶颈。

解决方案：根据ID进行分库分表进行算法路由，减少单个数据库的压力


2. 利用Redis的原子性实现

&ensp;&ensp;通过判断某个key在Redis中是否存在，来确定数据的处理。

要考虑的问题：

1.是否要进行数据落库，如果要的话，就要考虑数据库和缓存的数据一致性问题。如何保证缓存与落库如何同时成功同时失败?

2.如果不进行落库，存储到缓存中，如何设置定时同步策略?


### Confirm 确认消息

&ensp;&ensp;消息的确认，是指生产者投递消息后，如果Broker收到消息，则会给我们生产者一个应答。

&ensp;&ensp;生产者进行接收应答，用来确定这条消息是否正常发送到Broker。这种方式也是消息的可靠性投递的核心保障。

确认机制：

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/confirm_1.jpg "Confirm消息机制")


如何实现：

1.在channel上开启确认模式：channel.confirmSelect()

2.在channel上添加监听：addConfirmListener，监听成功和失败的返回结果，根据具体的结果对消息进行重新发送、或记录日志等待后续处理。


### Return 消息机制
&ensp;&ensp;Return Listener用于处理一些不可路由的消息。

&ensp;&ensp;消息生产者，通过制定的一个Exchange和Routingkey，把消息送达到某一个队列中去，然后消费者监听队列，进行消费处理。

&ensp;&ensp;在某种情况下，如过在发送消息的时候，当前的exchange不存在或者指定的路由key路由不到，这个时候需要监听这种不可达的消息，就要使用Return Listener

* API的配置

Mandatory：如果为true，则监听器会接收到路由不可达的消息，然后进行后续的处理，如果为false，那么broker端自动删除该消息；

Return机制流程图：

![image](https://github.com/FunCheney/concurrency/blob/master/src/Image/return_1.jpg "Confirm消息机制")

### 消费端自定义监听

&ensp;&ensp;使用自定义的Consumer，解耦性更加的强。通过创建类实现DefaultConsumer类，并重写handlerDelivery()方法。


### 消费端限流

什么是消费端限流？

&ensp;&ensp;假设一个场景，首先，我们RabbitMQ服务器有成千上万条未处理的消息， 我们随便打开一个消费者客户端，会出现下面情况：

a. 巨量的消息瞬间全部推送过来，但是我们单个客户端无法同时处理这么多数据！

b. 轻则消费端卡顿。重则宕机

&ensp;&ensp;RabbitMq提供了一种qos(服务质量保证)功能，即在非自动确认消息的前提下，如果一定数目的消息(通过consumer或者channel设置qos的值)未被确认前，不进行消费新的消息。

&ensp;&ensp;void BasicQos(unit prefetchSize,ushort prefetchCount, bool global);

1.prefetchSize:消息的限制大小，消费端一般设置为0；

2.prefetchCount表示一次处理多少条消息，一般设置为1；

3.global表示限流策略在什么地方应用，在rabbitMq中有两个不同的级别，一个是channel(global为true)，一个是consumer(一个channel可以有多个consumer监听)。

&ensp;&ensp;prefetchSize和global这两项，rabbitmq没有实现，暂且不研究。prefetchCount在no_ask=false的情况下生效，即在自动应答的情况下这两个值是不生效的。

代码示例：

消息的生产端：
```java
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = RabbitMqUtil.getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_qos_exchange";
        String routingKey = "qos.save";

        String msg = "Hello RabbitMQ QOS Message";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchangeName, routingKey, true, null, msg.getBytes());
        }
    }
}
```
消息的消费端：

```java
public class Consumer {


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = RabbitMqUtil.getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_qos_exchange";
        String queueName = "test_qos_queue";
        String routingKey = "qos.#";

        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);


        //限流方式 第一件事就是将 autoAck 设置为false

        /**
         * @param prefetchSize  消息大小限制 一般为0 不限制
         * @param prefetchCount 一次最多处理消息个数
         *        会告诉RabbitMQ不要同时给一个消费者推送多余N个消息，即一旦有N个消息还没有ack,则该consumer将block掉。直到有消息ack
         * @param global       true/false 是否将上面设置应用于channel。
         *                                简单说。就是上面限制是channel级别还是consumer级别。
         */
        channel.basicQos(0, 1, false);

        channel.basicConsume(queueName, false, new MyConsumer(channel));


    }
}
```

```java
public class MyConsumer extends DefaultConsumer {
    private Channel channel;


    MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.err.println("-----------consume message----------");
        System.err.println("consumerTag: " + consumerTag);
        System.err.println("envelope: " + envelope);
        System.err.println("properties: " + properties);
        System.err.println("body: " + new String(body));

        //ack应答
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
```

### 消费端的ACk与重回队列

* 消费端的手工ACk和NACK

&ensp;&ensp;channel.basicConsume方法的第二个参数(autoAck)设置为false即可。手动签收又分为两种方式，一种是Ack(确认)和Nack(失败)。

两种方式的区别：

ACK:表示手工签收后消息处理成功；

NACk:表示手动签合后消息处理失败。这个时候broker会自动重新发送消息。


使用场景：

场景一：

假设我们设置的自动重复消息次数是3次，那么在Nack后，broker会重复发送三次消息。如果三次之后，还是Nack的，这种情况下，我们不可能一直重复发送，此时就可以设置为Ack,然后在消费端进行消费的时候，如果由于业务处理而产生的异常，我们可以进行日志的记录或者给开发人员发送警报邮件，然后进行补偿。

场景二：

如果由于服务器宕机等严重性的问题，此时是不可能收到ack或者Nack,这种情况下也会一直重复发送消息的，那么我们就需要手工的Ack,来保证消费端消费成功。在服务器重启之后，会自动的消费之前未消费成功的消息的。


### 消费端的重回队列

&ensp;&ensp;对没有处理成功的消息，把消息重新回递给Broker。在一般我们在实际的应用中，都会关闭重回队列，也就是设置未false


### TTL队列/消息

&ensp;&ensp;TTL：Time To Live 的缩写，也就是生存时间。RabbitMQ支持消息的过期时间，在消息发送的时候可以进行设置。RabbitMQ支持队列的过期时间，从消息如队列开始计算，只要超过了队列的超时时间配置，那么消息会自动的清除。

### 死信队列

&ensp;&ensp;死信队列：DLX，Dead-Letter-Exchange。消息在队列中没有消费者消费，表示该消息已经变为死信(dead  message)。该消息会被publish到另一个Exchange，这个Exchange就是死信队列(DLX)。

&ensp;&ensp;DLX也是一个正常的Exchange，和一般的Exchange没有区别，它能在任何队列上被指定，实际上就是设置某个队列的属性。

&ensp;&ensp;当这个队列中有死信时，RabbitMq就会自动将这个消息重新发布到设置的Exchange上去，进而被路由到另一个队列。

&ensp;&ensp;可以监听这个队列中的消息进行相应的处理。

消息变成死信的情况：

* 消息被拒绝(basic.reject/basic.nack)并且requeue=false

* 消息TTL过期

* 队列达到最大长度

























