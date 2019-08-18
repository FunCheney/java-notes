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
 ```
public class ConditionUseCase {
    /** 创建锁*/
    private Lock lock = new ReentrantLock();
    /** 使用锁的newCondition()方法*/
    private Condition condition = lock.newCondition();
    
    public void conditionWait(){
     lock.lock();
     try {
         /** 调用Condition的await() 方法*/
         condition.await();
     } catch (InterruptedException e) {
         e.printStackTrace();
     } finally {
         lock.unlock();
     }
    }
    
    public void conditionSingal(){
     lock.unlock();
     try {
         /** 调用condition的signal()方法*/
         condition.signal();
     } finally {
         lock.unlock();
     }
    }
}
```
&ensp;&ensp;在使用Condition对象时，一般会将Condition对象作为成员变量。当调用await()方法后，当前线程会释放锁并在此等待，而其他线程调用Condition对象的signal()方法，通知当前线程后，当前线程才从await()方法返回，并且在返回前已经获取了锁。

**使用Condition完成线程的顺序执行:**

&ensp;&ensp;假设有三个线程T1,T2,T3执行，通过Condition对象使得者三个线程按照T1，T2，T3顺序执行：

```
public class MyDemo {

    private  int flag;
    Lock lock = new ReentrantLock();
    Condition test1 = lock.newCondition();
    Condition test2 = lock.newCondition();
    Condition test3 = lock.newCondition();

    public  void test1(){
        lock.lock();
        while (flag != 0){
            try {
                test1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("T1线程执行");
        flag++;
        test2.signal();
        lock.unlock();
    }

    public void test2(){
        lock.lock();
        while (flag != 1){
            try {
                test2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("T2线程执行");
        flag++;
        test3.signal();
        lock.unlock();
    }

    public void test3(){
        lock.lock();
        while (flag != 2){
            try {
                test3.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("T3线程执行");
        flag = 0;
        test1.signal();
        lock.unlock();
    }

    public static void main(String[] args) {
        MyDemo demo = new MyDemo();
        Thread1 a = new Thread1(demo);
        Thread2 b = new Thread2(demo);
        Thread3 c = new Thread3(demo);

        new Thread(a).start();
        new Thread(b).start();
        new Thread(c).start();
    }
}
```

Thread1类:

```
public class Thread1 implements Runnable{
    private MyDemo demo;

    public Thread1(MyDemo demo) {
        this.demo = demo;
    }

    @Override
    public void run() {
        while (true){
            demo.test1();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
Thread2类
```

public class Thread2 implements Runnable{
    private MyDemo demo;

    public Thread2(MyDemo demo) {
        this.demo = demo;
    }

    @Override
    public void run() {
        while (true){
            demo.test2();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
``` 
Thread3类

```
public class Thread3 implements Runnable{
    private MyDemo demo;

    public Thread3(MyDemo demo) {
        this.demo = demo;
    }

    @Override
    public void run() {
        while (true){
            demo.test3();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

执行结果：


 
 
 
 
 
 
 
 