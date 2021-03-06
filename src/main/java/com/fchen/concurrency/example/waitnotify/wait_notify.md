### 线程间通信

&ensp;&ensp;线程拥有自己的栈空间，如若各个线程之前不能通信，其实对于多线程来说是没有意义的，因为只有各个线程之间相互协作，才能发挥多和处理器的最大价值。在前面Java内存模型的学习，也知道，java线程会将主内存的数据拷贝一份到自己的本地内存中，而线程之间通信的原理就是通过主内存来完成数据的共享。

#### 线程通信的方式
1).volatile

&ensp;&ensp;对于volatile关键字来说，我们前面已经做了详细的介绍，这里就不在赘述。对于其可以实现线程间的通信的原理，请查看volatile相关文章。 

2).synchronized

&ensp;&ensp;synchronized实现线程间通通信的原理，前面关于synchronized关键字的文章也做了较详细的介绍。我们以下图的方式在做一个总结：


3).等待/通知机制

&ensp;&ensp;首先，等待/通知机制是通过对象的wait(),notify()/notifyAll方法来实现的。

&ensp;&ensp;其次，等待通知机制需要与synchronized关键字配合使用。并且synchronized后面括号的部分必须是同一对象。

&ensp;&ensp;最后，等待通知机制的实现方式可以归纳出如下金典范式，该范式分为两部分，分别针对等待方(消费者)和通知方(生产者)

* 等待方遵循原则如下：

&ensp;&ensp;1) 获取对象的锁

&ensp;&ensp;2) 如果条件不满足，那么调用对象的wait()方法，被通知后扔要检查条件

&ensp;&ensp;3) 条件满足则执行对应的逻辑

对应的伪代码如下：
```
synchronized(对象){
    while(条件不满足){
        对象.await()
    }
    对应的处理逻辑
}
```

* 通知方遵循原则如下：

&ensp;&ensp;1) 获得对象的锁

&ensp;&ensp;2) 改变条件

&ensp;&ensp;3) 通知所有等待在对象上的线程

对应的伪代码如下:
```
synchronized(对象){
    改变条件
    对象.notifyAll()
}
```

解释：

1）notify() 通知一个在对象上等待的线程，使其从wait()方法返回，而返回的前提是该线程获取到对象的锁。

2）notifyAll() 通知所有等待在该对象上的方法

3）wait() 调用该方法的线程进入WAITING状态，只有等待另外线程的通知或被中断才会返回、需要注意，调用wait()方法会释放锁。

4）wait(long) 超时等待一段时间(毫秒)，到达等待时长，没有通知也会返回


&ensp;&ensp;等待/通知机制,是指一个线程A调用了对象O的wait()方法进入等待状态，而另一个线程调用了对象O的
notify()或者notifyAll()方法，线程A收到对象通知后从对象O的wait()方法返回，进而执行后续的操作。


## 面试题总结

**1.交替打印数组与字符串**
```java
@Slf4j
public class PrintTest {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws Exception{
        Thread waitThread = new Thread(new PrintTest.Wait(),"WaitThread");
        waitThread.start();
        Thread notifyThread = new Thread(new PrintTest.Notify(), "NotifyThread");
        notifyThread.start();
    }
    static class Wait implements Runnable{
        @Override
        public void run() {
            // 加锁，拥有lock的Monitor
            String num = "123456789";
            synchronized (lock){
                // 当条件不满足时，继续wait，同时释放lock锁
                try {
                    for (int i = 0; i < num.length(); ){
                        if(flag){
                            System.out.println(num.charAt(i));
                            flag = false;
                            i++;
                            lock.notify();
                        }else {
                            lock.wait();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Notify implements Runnable{
        @Override
        public void run() {
            //加锁，拥有lock的Monitor
            String str = "abcdefgh";
            try {
                synchronized (lock){
                    // 获取当前lock锁，然后进行通知，通知时不会释放lock锁
                    // 知道当前线程释放了lock后，WaitThread才能从Wait方法中返回

                    for (int i = 0; i < str.length();){
                        if(!flag){
                            System.out.println(str.charAt(i));
                            flag = true;
                            i++;
                            lock.notify();
                        }else {
                            lock.wait();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```












