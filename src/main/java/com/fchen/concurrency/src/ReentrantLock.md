## ReentrantLock(重入锁)源码学习
&ensp;&ensp;重入锁，就是支持同一个线程多次获得锁，其释放也要经过相同的次数后其他线程才可以获取到该锁。此外该锁还支持获取该锁时的公平和非公平选择。

&ensp;&ensp;通过之前的学习，在Java中synchronized关键字是隐式的支持锁的重入的，这个是通过JVM的底层自己是现代的。ReentrantLook虽然没有像synchronized一样支持隐式的重进入，但是在调用Lock方法的时，已经获得锁的线程，再次调用lock()方法获取锁而不被阻塞。

&ensp;&ensp;同时上述文字中有提到，锁的获取时的公平性问题。ReentrantLock中也提供了锁获取的是否公平的方法。

### 类图
![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantLock1.png "ReentrantLock1")

&ensp;&ensp;通过这张类图，ReentrantLock实现了Lock接口，通过重写Lock接口中的方法来实现对应的锁的属性。通过ReentrantLock可以创建公平锁(FairSync)和非公平锁(NonfairSync)。

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantLock1.png "ReentrantLock2")

&ensp;&ensp;通过这张类图，可以看到在ReentrantLock中，包含有一个抽象的内部类(Sync)，通过Sync来实现FairSync和NonfairSync。

![image](https://github.com/FunCheney/concurrency/blob/master/src/main/java/com/fchen/concurrency/src/image/ReentrantLock1.png "ReentrantLock3")

&ensp;&ensp;最后这张图可以看到，sync是AbstractQueuedSynchronizer的子类，这里和上篇文章中使用队列同步容器实现锁的使用方式一样。说明ReentrantLock的功能也是通过同步队列容器来实现的。只不过这里提供了公平锁与非公平锁两种形式。下面我们就分别来看看这两种锁。

### 非公平锁(NonfairSync)

