### Condition接口及其使用

#### Condition接口
&ensp;&ensp;Condition定义了等待/通知两种类型的方法，当前线程调用这些方法时，需要提前获取到Condition对象关联的锁。Condition对象是由Lock对象(调用Lock对象的newCondition()方法)创建出来的，也就是说Condition是依赖Lock对象的。

```
public interface Condition {

    void await() throws InterruptedException;

    void awaitUninterruptibly();

    long awaitNanos(long nanosTimeout) throws InterruptedException;

    boolean await(long time, TimeUnit unit) throws InterruptedException;

    boolean awaitUntil(Date deadline) throws InterruptedException;

    void signal();

    void signalAll();
}
```
##### Condition 接口中的方法及描述

方法名称 | 描 述
 ---|---
 void await() throws InterruptedException | 当前线程进入等待状态直到被通知(signal)或中断，当前线程将进入运行状态且从await()方法返回的情况，包括：<br/> 其他线程调用该Condition的signal()或signalAll()方法，而当地贤臣被唤醒<br/>  其他线程(调用interrupt()方法)中断当前线程 <br/>如果当前线程从await()方法返回，那么表明该线程已经获取了Condition对象对应的锁
 void awaitUninterruptibly(); | 当前线程进入等待状态直到被通知，该方法对中断不敏感
 long awaitNanos(long nanosTimeout) throws InterruptedException | 当前线程进入等待状态直到被通知、中断或者超时。返回值表示剩余的时间，如果在nanosTimeout纳秒之前被唤醒，那么返回值就是(nanosTimeout - 实际耗时)。如果返回值是0或者负数，表示已经超时了。
 boolean awaitUntil(Date deadline) throws InterruptedException; | 当前线程进入等待状态直到被通知、中断或者到某个时间。如果没有到指定时间就被通知，方法返回true，否则，表示到了指定时间，方法返回false
 void signal() | 唤醒一个等待在Condition上的线程，该线程从等待方法返回前必须获得与Condition相关联的锁
 void signalAll() | 唤醒所有等待在Condition上的线程，能够从等待方法返回的线程必须获得与Condition相关的锁。
 
 
 ##### Condition 使用代码示例