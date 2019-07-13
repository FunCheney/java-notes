~~### 多线程与单例
&ensp;&ensp;最近这一段时间在学习多线程，我们知道在使用多线程的时候我们要考虑线程安全的问题。而单例模式要求我们创建全局的一个对象，那么当单例模式遇到多线程时会发生什么样的化学反应呢？我们应该怎样做才能使得单例模式遇到多线程时的使用是安全的、正确的呢？下面我们就来一探究竟。。。*（今天我们来模拟一个面试场景，由浅入深来看看单例与多线程）*

&ensp;&ensp;之前的文章里面也有写过单例模式的学习。其中有些知识点的理解也不是很到位，随着更加深入的学习，对以前不理解的地方慢慢的也就明白是怎么回事了。对以前觉得自己理解了的知识点，也可能有了一些新的认识。反正学习的过程就是循环往复的，不断的理解、修正、归纳总结的过程。只是希望有所成长，我们都有理由成为更好的自己...闲言少叙，一起再来看看多线程与单例。

**我一直在想，当有人问我单例模式的时候，他到底是想要问什么？我要怎么回答，才会更加有条理，使得相关知识的脉络更加清晰呢？经过这一段时间的学习，我想他可能是想知道你对如下知识点的理解：**

#### 如何书写一个正确的单例模式
&ensp;&ensp;九层之台，起于垒土。首先，我们要保证我们写的东西是正确的，这是最基本的先决条件。因为只有这样，你后续的努力才会有意义。以并发与单例为例，如果单例模式都书写不正确，那么，我们怎么保证后续的分析是正确的呢。因此我们先来看看如何书写一个正确的单例：

思路:

&ensp;&ensp;单例模式是指仅仅被实例化一次的类。创建方式，大体上的思路就是把构造器保持为私有的，并导出公有有的静态成员，以便允许客户端能够访问唯一的实例。

代码如下：
```
public class Singleton {
    
    public static final Singleton INSTANCE = new Singleton();
    
    private Singleton(){}
}
    
```
回头看看：

&ensp;&ensp;私有的构器只被调用一次，用来实例化公有的静态的final修饰的Singleton对象，由于构造器私有，所以保证了Singleton对象的唯一性。

&ensp;&ensp;缺点的话，就是如果调用方通过反射的方式通过私有的构造器时可以创建新的对象的，这样一来，就破坏了单例。我们可以在构造方法中加以判断制止这种情况的发生。
```
public class Singleton {
    
   public static final Singleton INSTANCE = new Singleton();

    private Singleton(){
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized.");
        }
    }
}
```

&ensp;&ensp;当我们理解了单例模式的书写思路，可以编写正确的代码之后，我们再来看看，是否有其他的实现方式。

#### 基于静态工厂方法的方式

&ensp;&ensp;基于静态工厂的方式创建单例的实现思路与上述的思路一致。只不过是导出的公有域有对象变成了方法，类的成员变量变成私有的静态的final修饰而已。
```
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton(){}
    
    public static Singleton getInstance(){
        return INSTANCE;
    }
}
```
回头看看：

&ensp;&ensp;通过使用静态工厂方法的方式，在不改变API的前提下，我们可以改变该类是否应该是单例的想法。工厂方法返回该类的唯一实例。但是他很容易被修改，比如修改成每个调用该方法的线程返回一个唯一的实例。但是也存在通过反射改变单例的风险。

&ensp;&ensp;上述这两种创建单例的方式是线程安全的(也是我们常说的--饿汉模式)。因为在类初始化时已经创建好了，用的时候拿来就用。但是，这样一来就使得性能可能会差点。(我是不会提懒汉模式的，因为我依稀记得在《Effective Java》中有提到，要慎用延迟初始化的建议，尤其是在并发的场景下会存在线程安全安全的问题，要做好正确的同步。)

&ensp;&ensp;既然有性能问题，那我们要解决啊，不能放之任之啊！于是乎，就有了下面的解决方案：


#### 延迟初始化的方式(懒汉模式)

&ensp;&ensp;延迟初始化，类创建的时候对象不进行初始化，知道使用到对象的时候完成初始化。也就是我们经常说的单例模式的懒汉模式。
```
public class Singleton {
    /**
     * 构造方法私有化
     */
    private Singleton() {
    }

    /**
     * 单例对象
     */
    private static Singleton1 instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static Singleton getInstance(){
        if(instance == null){
            // 1.多线程的情况下 这里可能会被调用两次 拿到两个不同的对象
            instance = new Singleton();
        }
        return instance;
    }
}
```
回头看看：

&ensp;&ensp;这里也是通过静态工厂的方式初始化，但是该方法存在线程安全问题！毛病出在哪里呢？

答：毛病出在如下图所示的地方：

假设:

    1.线程B运行到如上所示的地方，但还未完成 instance = new Singleton()的操作。
    2.由于未做任何的同步措施，使得线程A运行到如上图所示的地方。
    3.由于1中的原因，导致 instance == null 所以线程A也进入到if{}里面。
    4.这就导致线程A、线程B都会拿到各自初始化的对象，这就违背了单例模式。。。


#### 基于synchronized延迟初始化的方式

答：有的。就是通过正确的同步手段，使得上述出现线程安全问题的地方，不要并发的被访问。我们之前的学习也了解到，线程间同步，使用synchronized关键字。

```
public class Singleton {
    /**
     * 构造方法私有化
     */
    private Singleton() {
    }

    /**
     * 单例对象
     */
    private static Singleton instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static synchronized Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }
}
```
回头看看：

&ensp;&ensp;这里通过同步方法的方式来保证线程之间的互斥同步，实现单例模式。但是，同样有引入新的问题。我们是为了提高性能才使用懒加载的方式创建对象。但是，这里为了保证安全，又使得线程阻塞来完成锁的获取与释放。如果频繁的调用getInstance()方法，同样性能也不会好到哪里去。。。

**当有人问：哪就没有两全其美的办法吗？**

答：有的。这种问题怎么能难道我们聪明的程序员前辈们呢。况且，我们要相信：办法总比困难多啊！！！

于是乎，就有了下面的解决方案,我们称之为双重检查锁定(Double-Checked Locking)来降低阻塞同步带来的开销：
```
public class Singleton4 {
    /**
     * 构造方法私有化
     */
    private Singleton4() {
    }

    /**
     * 单例对象
     */
    private static Singleton4 instance = null;

    /**
     * 静态的工厂方法
     * @return
     */
    public static synchronized Singleton4 getInstance(){
        //双重检测机制
        if(instance == null){
            //同步锁
            synchronized (Singleton4.class){
                if(instance == null){
                    instance = new Singleton4();
                }
            }
        }
        return instance;
    }
}
```
&ensp;&ensp;然而，故事的发展总是跌宕起伏的。与此同时，我也只能抱歉的说一声，这种改进并非线程安全的。我想有人会问，这个改进既然是有问题的，那还拿他出来干什么呢？我想说的是，虽然这种改进方案是失败的。但是，其失败的原因还是值得我们去研究一下的，毕竟我们之前学了那么久，关于计算机处理器、Java内存模型的知识。我们可以使用前面的知识来分析这种优化的问题究竟是出在什么地方的。~~


