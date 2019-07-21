### 多线程与单例

#### 前言 
上一周的文章中，从如何正确的书写一个单例模式开始(先不要去管性能，线程安全等一系列问题)，然后学习了每一种书写方式有什么样的优点与弊端，最后学习到--
双重检查锁定(Double-Checked Locking)这一种写法的代码，提到这种方式也是非线程安全的，下面就以这种方式为什么不是线程安全的方式开始今天的学习。

#### 1 双重检查锁定(Double-Checked Locking)
首先简要代码回顾，以及抛出问题点：
```
    //双重检测机制
    if(instance == null){
        //同步锁
        synchronized (Singleton.class){
            if(instance == null){
                instance = new Singleton();
            }
        }
    }
```
&ensp;&ensp;上述所截取的代码部分，是通过对instance==null的双重判断来降低上一篇文章中提到synchronized带来的性能开销。如果第一次instance不为null(即：对象已经创建好)就直接返回，不需要进行后续的加锁，初始化对象的操作。通过synchronized来保证同一时刻只有一个线程创建对象。

&ensp;&ensp;但是，上述代代码在多线程的情况下任然是有问题的，可能出现的问题是当某一个线程第一次判断**instance == null**为flase 时，instance引用的对象还未被初始化完成。

##### 问题的根源
&ensp;&ensp;上述问题是由，编译器，处理器对字节码指令的重排序(优化)带来的。上述截取的代码中的(instance = new Singleton();)可以分为如下的伪代码：
```
memory = allocate()   //1.分配对象的内存空间
ctorInstance(memory)  //2.初始化对象
instance = memory     //3.设置instance指向刚分配的内存
```
&ensp;&ensp;JVM和CPU发生了指令重排(在单线程内，允许不会对程序执行结果有影响的重排序)，也就是上面伪代码，重排序后的执行顺序可能会下：
```
memory = allocate() //1. 分配对象的内存空间
instance = memory   //3.设置instance指向刚分配的内存
ctoInstance()       //2.初始化对象
```
&ensp;&ensp;这种重排序在单线程的情况下不会对单线程的执行结果产生什么影响，但是在多线程的情况下就会出问题，我们结合刚开始截取的**双重检查锁定(Double-Checked Locking)**代码为例，来看看多线程情况下会有产生什么结果。

假设所有的线程都是第一次访问：

```
  1.程A进入到synchronized代码块内，判断instance==null成立；
  2.线程A执行 instance = new Singleton();
     由于指令重排的原因，先分配对象的内存空间，
                       然后设置instance指向分配的内存空间
                       但是对象还未完成初始化
  3.此时，线程B来到第个instance==null的地方，由于instance已
    在步骤2)中指向分配的内存空间，所以instance == null 不成
    立，直接返回instance引用的对象。
  4.此时，线程B将会访问到一个还未被初始化的对象。
```

基于上述的分析，这种优化也是存在问题的，并且也知道问题出在什么地方。既然找到了问题，那我们就来解决问题。根据上述问题的根源，有以下两点:

* 禁止指令重排序
* 允许重排序，但不允许其他线程看到这个重排序。

通过以上两种方式就可以写出线程安全的单例模式了。

#### 2 基于volatile的双重检查锁定(Double-Checked Locking)

&ensp;&ensp;使用volatile关键字修饰 instance对象，这样通过volatile禁止上述指令重拍序中的步奏3与步奏2重排序来使得延迟加载的单例模式是线程安全的。代码如下:

```
public class Singleton {
    /**
     * 构造方法私有化
     */
    private Singleton() {
    }

    /**
     * 单例对象 volatile 加 双重检测机制 禁止 指令重排
     */
    private volatile static Singleton instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static synchronized Singleton getInstance(){
        //双重检测机制
        if(instance == null){
            //同步锁
            synchronized (Singleton.class){
                if(instance == null){
                    /**
                     *  1. memory = allocate() 分配对象的内存空间
                     *  2. 初始化对象
                     *  3. instance = memory 设置instance指向刚分配的内存
                     */
                    instance = new Singleton5();
                }
            }
        }
        return instance;
    }
}
```
其中对于volatile 的详细说明请看前面的文章，以及happens-before原则。

#### 2 基于类初始化的解决方案

&ensp;&ensp;初始化一个类，包括执行这个类的静态初始化和初始化在这个类中声明的静态字段。更具Java语言规范，在首次发生下面任意一种状况时，一个类或接口类型T将被立即初始化。

1. T是一个类，而且一个T类型的实例被创建。
2. T是一个类，且T中声明的静态方法被调用。
3. T中声明的一个静态字段被赋值。
4. T中声明的一个静态字段被使用，而且这个字段不是一个常量字段。
5. T是一个顶级类，而且一个断言语句嵌套在T内部被执行。


##### 基于类初始化的单例模式

代码:
```
public class InstanceFactory(){
    private static class InstanceHolder{
        public static Instance instace = new Instance();
    }
    
    public static Instance getInstance(){
        return InstanceHolder.getInstance();
    }
}
```
&ensp;&ensp;在上述代码中，首次执行getInstance()方法时将导致InstanceHolder初始化。JVM在类初始化的加载阶段(Class被加载之后，且被线程使用之前)，会执行类的初始化。在执行类的初始化期间，JVM会获取一个锁，这个锁可以同步多个线程对一个类的初始化。

&ensp;&ensp;这个方案就是：上面描述的允许重排序，但是不允许其他线程看到这个重排序的解决方安。(对于JVM是如何完成类的加载，以及初始化的,后面再详细的学习)。

&ensp;&ensp;在使用静态内部类的时候要注意一点，使用上述的方法可以得到线程安全的单例模式，但是如果遇到序列化对象时，使用默认的方式得到的还是多例。这个适用于我们之前书写的所有的单例模式，只要我们定义的单例加上了** “implements Serializable” **字样，它不在是一个单例。任何一个readObject()方法都会返回一个新建实例，这个新建实例不同于该类初始化时创建实例。

&ensp;&ensp;如果创建的实例实现了序列化接口，为了保证单例，要在所写的单例模式中提供如下的方法：
```
private Object readResolve(){
    return InstanceHolder.getInstance();
}
```
&ensp;&ensp;这个方法忽略了被反序列化的对象，是返回该类在初始化时创建的那个特殊的实例对象。对于一个正在被反序列化的对象，如果它的类定义了一个readResolve()方法，并且具备正确的声明，那么反序列化之后，新创建的对象上的readResolve()方法就会被调用。然后，该方法返回的对象引用将被返回，取代新创建的对象。

&ensp;&ensp;但是，在《Effective Java》中第77条:**对于实例控制，枚举优先于readResolve。**使用枚举，可以避免反序列化带来的问题，并且枚举的使用是线程安全的。


#### 3 基于枚举的单例模式
```
public class Singleton7 {

    /**
     * 私有构造函数
     */
    private Singleton7() {
    }
    public static Singleton7 getInstance(){
        return Singleton.INSTANCE.getInstance();
    }
    private enum Singleton{
        INSTANCE;

        private Singleton7 singleton;

        /**
         *  JVM 保证这个方法绝对只调用一次
         */
        Singleton(){
            singleton = new Singleton7();
        }

        public Singleton7 getInstance(){
            return singleton;
        }
    }
}
```
&ensp;&ensp;在《Effective Java》中也提到，这种方式是最佳的实现方式。

#### 4 总结

&ensp;&ensp;这两周的文章中，系统的学习的多线程与单例结合的相关知识点，以及，每种单例模式遇到问题时，要如何去思考，去解决。最中的到我们想要的结果。希望以后再用到单例模式时，可以选择对我们来说最为合适的。





























