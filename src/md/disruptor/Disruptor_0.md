### Disruptor
#### 介绍
&ensp;&ensp;Disruptor是一个 开源的并发框架，使用事件驱动的方式。是一个高性能的异步处理框架，或者可以认为是最快的消息框架，也可以认为是一个观察者模式的实现，或则事件监听模式的实现。

#### 入门
* 1.建立一个Event类；
* 2.建立一个工厂Event类，用于创建Event类实例对象
* 3.需要一个事件监听类，用于处理(Event类)
* 4.编写测试代码。实例化Disruptor实例，配置一系列参数。然后对Disruptor实例绑定监听事件类，接受并处理数据。
* 5.在Disruptor中，真正存储数据的核心叫做RingBuffer，我们通过Disruptor实例拿到它，然后把数据生产出来，把数据加入到RingBuffer的实例对象中即可。

#### 代码示例
步骤一：
```java
public class LongEvent { 
    private long value;
    public long getValue() { 
        return value; 
    } 
 
    public void setValue(long value) { 
        this.value = value; 
    } 
} 
```
步骤二：
```java
// 需要让disruptor为我们创建事件，我们同时还声明了一个EventFactory来实例化Event对象。
public class LongEventFactory implements EventFactory { 

    @Override 
    public Object newInstance() { 
        return new LongEvent(); 
    } 
} 
```
步骤三：
```java
public class LongEventMain {

	public static void main(String[] args) throws Exception {
		//创建缓冲池
		ExecutorService  executor = Executors.newCachedThreadPool();
		//创建工厂
		LongEventFactory factory = new LongEventFactory();
		//创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
		int ringBufferSize = 1024 * 1024; // 

		/**
		//BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
		WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
		//SleepingWaitStrategy 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景
		WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
		//YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性
		WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
		*/
		
		//创建disruptor
		Disruptor<LongEvent> disruptor = 
				new Disruptor<LongEvent>(factory, ringBufferSize, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
		// 连接消费事件方法
		disruptor.handleEventsWith(new LongEventHandler());
		
		// 启动
		disruptor.start();
		
		//Disruptor 的事件发布过程是一个两阶段提交的过程：
		//发布事件
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		
		LongEventProducer producer = new LongEventProducer(ringBuffer); 
		//LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		for(long l = 0; l<100; l++){
			byteBuffer.putLong(0, l);
			producer.onData(byteBuffer);
			//Thread.sleep(1000);
		}

		
		disruptor.shutdown();//关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
		executor.shutdown();//关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在 shutdown 时不会自动关闭；			
	}
}
```
步骤四：
```java
public class LongEventProducer {

	private final RingBuffer<LongEvent> ringBuffer;
	
	public LongEventProducer(RingBuffer<LongEvent> ringBuffer){
		this.ringBuffer = ringBuffer;
	}
	
	/**
	 * onData用来发布事件，每调用一次就发布一次事件
	 * 它的参数会用过事件传递给消费者
	 */
	public void onData(ByteBuffer bb){
		//1.可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//2.用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			LongEvent event = ringBuffer.get(sequence);
			//3.获取要通过事件传递的业务数据
			event.setValue(bb.getLong(0));
		} finally {
			//4.发布事件
			//注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
			ringBuffer.publish(sequence);
		}
	}	
}
```
步骤五：
```java
public class LongEventHandler implements EventHandler<LongEvent>  {

	@Override
	public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
		System.out.println(longEvent.getValue()); 		
	}
}
```

#### 术语说明
* RingBuffe：负责存储和更新在Disruptor中流通的数据。
* Sequence：Disruptor使用Sequence来表示一个特殊组件处理的序号。和Disruptor一样，每个消费者(EventProcessor)都维持着一个Sequence。大部分的并发代码依赖这些Sequence值运转，因此Sequence支持多种当前为AtomicLong类的特性。
* Sequencer：这个是Disruptor正真的核心。实现了这个接口的两种生产者(单生产者和多生产者)均实现了所有的并发算法，为生产者和消费者之间进行准确快速的数据传递。
* SequencerBarrier：有Sequencer生成，并且包含了已经发布的Sequence的引用，这些Sequence源于Sequencer和一些独立消费则的Sequence。它包含了决定是否有提供消费者来消费Event的逻辑。
* WaitStrategy：决定一个消费者将如何等待生产者将Event置入Disruptor。
* Event：从生产者到消费者过程中所处理的数据单元。Disruptor中没有代码表示Event，因为它完全是由用户定义的。
* EventProcessor：主要事件循环。处理Disruptor中的Event，并且拥有消费者的Sequence。它有一个实现类是BatchEventProcessor，包含了event loop有效实现。并且将回调到一个EventHandler接口的实现对象。
* EventHandler：由用户实现并且代表了Disruptor中的一个消费者接口
* Producer：由用户实现，它调用RingBuffer来插入事件(Event),在Disruptor中没有相应的代码实现，由用户实现。
* WorkProcessor：确保每个sequence只被一个processor消费，在同一个WorkPool中的处理多个WorkProcessor不会消费同样的sequence。
* WorkerPool：一个WorkProcessor池，其中的WorkProcessor将消费Sequence，所以任务可以实现WorkHandler和worker之间移交
* LifecycleAware：当BatchEventProcessor启动和停止时，于实现这个接口用于接收通知。



#### 理解RingBuffer
&ensp;&ensp;它是一个环，用作不同线程之间传递数据。RingBuffer拥有一个序号，这个序号指向数组中下一个可用元素。随着数据不停的填充这个buffer，这个序号会一直增长，直到绕过这个环。要找到数组中当前序号指向的元素，通过mod操作。缓冲区的大小为2的N次方更有利于基于二进制计算机进行计算。



