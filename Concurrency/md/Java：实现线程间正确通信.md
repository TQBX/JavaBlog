

[toc]

在并发编程中，保证线程同步，从而**实现线程之间正确通信**，是一个值得考虑的问题。本篇将参考许多著名书籍，学习如何让多个线程之间相互配合，完成我们指定的任务。

当然本文只是学习了一部分线程间通信的方法，还有一些例如使用Lock和Condition对象，管道输入输出、生产者消费者等内容，我们之后再做学习。

# 一、synchronized 与 volatile

`synchronized`关键字是Java提供的**互斥的内置锁**，该锁机制**不用显式加锁或者释放锁**。互斥执行的特性可以确保对整个临界区代码的执行具有原子性，同步机制保证了**共享数据在同一个时刻只被一个线程使用**。

回顾以下synchronized的底层实现：

我们可以对下面这段代码进行反编译：`javap -v TestData.class`。

```java
public class TestData {
    public static synchronized void m1(){}
    public synchronized void m2(){}
    public static void main(String[] args) {
        synchronized (TestData.class){
        }
    }
}
```
编译结果如下：
![1O2w5Q.png](https://s2.ax1x.com/2020/02/13/1O2w5Q.png)
![1O2KED.png](https://s2.ax1x.com/2020/02/13/1O2KED.png)

虽然同步方法和代码块的实现细节不同，但是归根结底：JVM对于方法或者代码块的实现是**基于对Monitor对象的进入和退出操作**。

以同步代码块举例：

- monitorenter指令被安排到了**代码块开始位置**，monitorexit被安排到代码块**正常结束和异常处**。
- 任何对象都有一个monitor与之相关联，当一个monitor被持有之后，它将会出于锁定状态。
- 当JVM执行到monitorenter指令时，将会尝试去获取当前对象对应的monitor的所有权。
    - 若其他前程已经有monitor的所有权，那么当前线程将会进入同步队列(SynchronizedQueue)，陷入阻塞状态（BLOCKED)，直到monitor被释放。
    - 若monitor进入数为0，线程可以进入monitor，此时该线程称为monitor的持有者（owner），并计数加一。
    - 若当前线程已经拥有monitor，是允许重新进入该monitor的，此时计数加一。
- 获得锁，锁计数加一。失去锁，计数减一。计数为0，即为释放锁。**释放锁的操作将会唤醒阻塞在同步队列中的的线程**，使其重新获得尝试对monitor的获取。

下图源自《Java并发编程得艺术》4-2
![1O2nHO.png](https://s2.ax1x.com/2020/02/13/1O2nHO.png)



---

新Java内存模型中提供了比锁更加轻量级的通信机制，它增强了volatile的内存语义，让volatile拥有和锁一样的语义：告知程序任何对volatile修饰变量的访问都要从共享内存中获取，对它的改变必须同步刷新回共享内存，**保证了线程对变量访问的可见性**。

关于volatile的重点学习，之后再做总结。
# 二、等待/通知机制

**等待/通知**相关的方法被定义在`java.lang.Object`上，这些**方法必须由锁对象来调用**。同步实例方法为this，静态方法为类对象，代码块的锁是括号里的玩意儿。

这些方法必须需要获取锁对象之后才能调用，也就是**必须要在同步块中或同步方法中调用**，否则会抛出`IllegalMonitorStateException`的异常。


## 等待

`wait()` : 调用该方法的线程进入`WAITING`状态，**并释放对象的锁**，此时当前线程只有被其他线程通知或中断才会返回。

`wait(long)`、`wait(long, int)`：进入`TIMED_WAITING`状态，释放锁，当前线程有通知或中断会返回，时间到了也会返回。



## 通知

`notify()` : 当前线程通知一个在**该对象**上等待的另一线程，被唤醒的线程从等待队列（WAITING)被移动到同步队列(BLOCKED)中，意思是被唤醒的线程不会立即执行，需要等当前线程释放锁之后，并且在同步队列中的线程得到了锁才能执行。
`notifyAll()` ：当前线程**通知所有等待在该对象上的线程**，将所有在等待队列中的线程全部移到同步队列中。
![1O2MUe.png](https://s2.ax1x.com/2020/02/13/1O2MUe.png)

假设A和B需要获取**同一把锁**，A进入之后，B进入同步队列，陷入阻塞（BLOCKED)。

如果A中调用锁的wait()方法，A释放锁，并陷入等待(WAITING)。此时另外一个线程B获取的当前锁，B运行。

如果此时B中调用锁的notify()方法，A被唤醒，从等待队列转移到同步队列，只有B运行完毕了，锁被释放了，A拿到锁了，A才出来运行。


等待/通知机制依托于同步机制，**确保等待线程从wait()方法返回时能够感知到通知线程对变量做出的修改**。



## 面试常问的几个问题
### sleep方法和wait方法的区别
sleep()和wait()方法都可以让线程放弃CPU一段时间，**进入等待（WAITING）状态**。

sleep()静态方法定义在Thread类中，wait()定义在Object类中。

如果线程持有某个对象的监视器，**wait()调用之后，当前线程会释放锁，而sleep()则不会释放这个锁**。

### 关于放弃对象监视器

对于放弃对象监视器，wait()方法和notify()/notifyAll()有一定区别：

锁对象调用wait()方法之后，**会立即释放对象监视器**。而notify()/notifyAll()则不会立即释放，而是等到线程剩余代码执行完毕之后才会释放监视器。

# 三、等待通知典型

通过wait()和notify()/notifyAll()可以有效地协调多个线程之间的工作，提高了线程通信的效率。

## 生产者消费者模型

- 通过平衡生产者的生产能力和消费者能力来提升整个系统的运行效率。
- 减少生产者与消费者之间的联系。实现很好的解耦。

下面代码保留主要的思路，具体的视情况而定。

**定义一个简单的产品类Product，里面定义一个判断产品有无的标识位。**

```java
    //产品
    public class Product {
        public boolean exist = false;
    }
```

**然后定义消费方中的run方法。**
首先获取对象的锁，如果产品不存在，则等待，否则消费一次，并把标识位置为false，并唤醒生产线程。

```java
    //消费方
    synchronized (product) {
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            while (!product.exist) {
                product.wait();
            }
            System.out.println("消费一次");
            product.exist = false;
            product.notifyAll();
        }
    }
```

生产方与消费方对应，依旧是先获取对象的锁，然后对标识位进行判断，如果已经有产品了，就等待，否则就生产一次，并把标识位附为true，最后唤醒正在等待的消费方。

```java
    //生产方
    synchronized (product) {
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            while (product.exist) {
                product.wait();
            }
            System.out.println("生产一次");
            product.exist = true;
            product.notifyAll();
        }
    }
```

这个过程是最基本的，我们更需要理解wait()，notify()等方法带来的便利之处。在真实场景更加复杂的情况下，比如在生产与消费速度不对等的情况下，需要创建缓冲区等等。



## 可能会出错的代码

```java
    //T1
    synchronized (product) {
            product.exist = false;
            product.notifyAll();
        }
    }

    //T2
    synchronized(product){
        while(product.exist)
            product.wait();
    }
    
    //T3
    while (product.exist) {
        //A
        synchronized(product){
            product.wait();
        }
    }
```

假设T1和T3是通信双方，这时就可能会产生**通知丢失**的情况：

- 假设T3还没有获得锁，运行到A点，这时线程调度器将资源分给T1线程，此时T3在A点阻塞。
- T1线程中希望阻止T3陷入等待，于是将标识符设置位false，在标识位上出现了竞争。
- 但是当T1执行完毕，T3继续执行的时候，并不能知道这个标识位已经发生改变，于是它将会永久陷入等待。

于是，我们可以学习到一点，为了消除多个线程在标识位上出现的竞争，我们可以**采用T2的形式，给线程上一把锁**，保证被通知之后先检查条件是否符合。

# 四、使用显式的Lock和Condition

我们之前也学习过，使用显式的Lock对象来保证线程同步的话，隐式的监视器就不存在了，也就无法使用wait()和notify()/notifyAll()。

Java提供了`Condition`接口来保持线程之间的协调通信，通过**Condition对象和Lock对象的配合**，可以完成synchronized同步方法与代码块完成的任务。


我很好奇Condition和Lock是怎么建立联系的，于是查看了它们的继承关系：

>Condition接口是JDK1.5出现的，该接口提供的方法被ConditionObject类实现，该类是AbstractQueuedSynchronizer（AQS）的内部类，而ReentrantLock类内部维护了一个Sync对象，Sync拥有一个返回ConditionObject实例的方法，Sync继承于AQS。

## Condition接口内的方法详解

> 参考JDK1.8官方文档

> void await() throws InterruptedException;

当前线程进入等待状态直到被通知或者中断，当前线程进入运行状态且从await()方法返回的四种情况：

- 其他线程调用这个条件的signal()方法，且**当前线程被选择为要唤醒的线程**。
- 其他线程会为此条件调用signalAll()方法。
- 其他线程中断当前线程。
- 发生”虚假唤醒“现象，参考：[虚假唤醒（spurious wakeup）](https://www.jianshu.com/p/0eff666a4875)


需要注意的是：在上面所有情况下，**要想从await()返回，当前线程必须重新获取与此条件关联的锁**。


> void awaitUninterruptibly()

当前线程进入等待状态直到被通知，它对中断不敏感，因此他从等待状态返回的场景区别于await()，仅仅少了第三点中断场景。



> long awaitNanos(long nanosTimeout) throws InterruptedException


该方法导致当前线程等待，直到被通知、中断，或超时。该方法根据返回时提供的nanosTimeout值，返回剩余等待的纳秒数的估计值，如果超时，则返回小于或等于零的值。此值可用于确定在等待返回但等待条件仍然无效的情况下是否需要重新等待，以及需要多长时间重新等待。

还有零一个类似的方法就不赘述了。


> boolean awaitUntil(Date deadline) throws InterruptedException


该方法导致当前线程等待，直到被通知或中断，或到了指定的截止日期。如果返回时截止日期已经过了，则为false，否则为true。


> void signal()

唤醒一个正在等待的线程，被唤醒的线程想要从await方法返回需要重新获得Condition相关联的锁。

> void signalAll()

唤醒所有等待的线程，同样的，想要从await方法返回就必须重新获得Condition相关联锁。



## Condition与Lock配合

Condition接口依赖于Lock，我们可以这样创建特定Lock实例的Condition实例：

```java
Lock lock = new ReentrantLock(); //创建Lock对象
Condition condition = lock.newCondition(); //利用lock对象的newCondition()创建Condition对象
```

既然具有依赖关系，那么只有获取了lock，才可以调用Condition中提供的方法，也就是只能在Lock.lock()与Lock.unlock()之间调用。

> 官方文档给出的定义：Condition的存在可以将**对象监视器**方法(wait、notify和notifyAll)分解到不同的对象中，通过将它们与任意Lock实现结合使用，实现每个对象具有多个等待集的效果。锁代替同步方法和语句的使用，条件代替对象监视器方法的使用。



一个Lock对象可以关联多个Condition对象，分别作为不同的条件检测，这里给一个简易版生产者消费者模型的Demo：

- 首先定义一个公共产品类，在类中定义相应的生产、消费逻辑。

```java
public class Product {
    //共享产品编号
    private int count = 0;
    //标识位，标识是否还有产品
    private boolean flag = false;

    //创建Lock锁对象
    private Lock lock = new ReentrantLock();
    //创建两个Condition对象，作为两种条件检测
    private Condition condProducer = lock.newCondition();
    private Condition condConsumer = lock.newCondition();

    //生产方法
    public void produce() {
        lock.lock(); //上锁
        try {
            //驱使线程等待的条件
            while (flag) {
                condProducer.await(); //如果flag为true，则不用生产
            }
            count++;
            System.out.println(Thread.currentThread().getName() + "生产产品一件,产品编号" + count);
            //生产完成，将标识为改为false
            flag = true;
            //唤醒conConsumer条件下的所有线程（当然，这里只有一个）
            condConsumer.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();//在finally中，保证解锁
        }
    }
    //消费方法
    public void consume() {
        lock.lock();
        try {
            //驱使线程等待的条件
            while (!flag) {
                condConsumer.await(); //如果flag为false，则不用消费
            }

            //消费的逻辑
            System.out.println(Thread.currentThread().getName() + "消费产品一件,产品编号" + count);
            flag = false;
            condProducer.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

- 然后创建生产者Producer和消费者Consumer两个线程类，只需将公共产品对象传入构造器中，使其建立联系。

```java
//生产者线程
class Producer implements Runnable {
    private Product product;

    Producer(Product product) {
        this.product = product;
    }

    @Override
    public void run() {
        //每个生产者线程生产会生产五件产品
        for (int i = 0; i < 5; i++) {
            product.produce();
        }
    }
}
//消费者线程
class Consumer implements Runnable {
    private Product product;

    Consumer(Product product) {
        this.product = product;
    }

    @Override
    public void run() {
        //每个消费者线程会消费五件产品
        for (int i = 0; i < 5; i++) {
            product.consume();
        }
    }
}
```

还是那个问题：我们需要用wait(）方法需要被包含在while循环语句中，防止过早或意外的通知，保证只有不符合等待的条件才能退出循环。换句话说，使用while循环而不用if判断可以有效防止”虚假唤醒“的现象。

## Condition接口与Object监视器

下表参考自《Java并发编程的艺术》方腾飞

| 对比项                             | Object Monitor Method    | Condition                                               |
| ---------------------------------- | ------------------------ | ------------------------------------------------------- |
| 前置条件                           | 获取对象的锁             | Lock.lock()获取锁、Lock.newCondition()获取Condition对象 |
| 调用方式                           | 如object.wait()          | 如condition.await()                                     |
| 等待队列个数                       | 一个                     | 多个                                                    |
| 释放锁、进入等待                   | 支持、如void wait()      | 支持、如void await()                                    |
| 释放锁、进入超时等待               | 支持、如void wait(long ) | 支持long awaitNanos(long)                               |
| 释放锁、进入等待状态到将来某个时间 | 不支持                   | 支持、例如 long awaitNanos(long)                        |
| 等待状态不响应中断                 | 不支持                   | 支持、例如 void awaitUninterruptibly()                  |
| 唤醒等待队列中的一个线程           | 支持、如void notify()    | 支持、如 void signal()                                  |
| 唤醒等待队列中的所有线程           | 支持、如void notifyAll() | 支持、如void signalAll()                                |

---

关于Condition和Lock，之后会有相关文章对它们进行更详细的系统学习，本篇文章主要理解它们进行线程通信的基本方法。

# 五、管道输入、输出流

管道输入输出流主要用于**线程之间的数据传输**，传输媒介为内存。

面向字节：PipedOutputStream、PipedInputStream
面向字符：PipedWriter、PipedReader

下面是一个通过管道输入输出流完成线程间通信：

```java
public class Piped {
    public static void main(String[] args) throws IOException {
        //创建管道输入输出流
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();

        //将输入输出流连结起来，否则在使用的时候会抛出异常
        out.connect(in);
        Thread printThread = new Thread(new Print(in),"PrintThread");
        printThread.start();
        
        //标准输入流转化到管道输出流
        int receive;
        try{
            while((receive = System.in.read())!=-1){
                out.write(receive);
            }
        }finally {
            out.close();
        }
    }

    //定义线程类，接收管道输入流，写入标准输出流
    static class Print implements Runnable{
        private PipedReader in;
        public Print(PipedReader in){
            this.in = in;
        }
        @Override
        public void run() {
            int receive;
            try{
                while((receive = in.read())!=-1){
                    System.out.print((char)receive);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
```

# 六、Thread.join()

关于join方法，之前已经做过相应的总结，这边就不再做详细的说明。

官方解释简洁明了：`Waits for this thread to die.`，很明显，针对线程来说，谁调用，等谁死。举个例子：<u>当在A线程中调用B线程的join()方法时，A线程将会陷入等待或超时等待，直到B线程执行完毕消亡才转变为阻塞</u>。

join()方法具体有三个：

```java
//等待该线程消亡。
public final void join()
//等待该线程消亡，只不过最多等millis毫秒。
public final synchronized void join(long millis)
//等待该线程消亡，只不过最多等millis毫秒+nanos纳秒（毫微秒）。
public final synchronized void join(long millis, int nanos)
```

# 七、利用ThreadLocal

同样的，关于ThreadLocal更详细的学习会在之后出炉，本篇着重理解通信方法。

`ThreadLocal`，是**线程局部变量**，它是一个以`ThreadLocal`对象为键、**任意对象为值**的存储结构，该结构被附带在线程上，线程可以根据一个Thread对象查询到绑定在这个线程上的值。

它为每一个使用该变量的线程都提供了一个变量值的副本，使得**每一个线程都可以独立地改变自己的副本**，而不会产生多个线程在操作共享数据经过主内存时产生的数据竞争的问题。

我们可以利用set和get，设置和取出局部变量的值。需要明确的是：不管有多少个线程，用ThreadLocal定义了局部变量，就会在线程中各自产生一份副本，自此，**各个线程之间的读和写操作互不相关**，我们可以利用这一性质，完成我们特殊的需求。

```java
public class Profiler {
    // 定义一个ThreadLocal类型的变量，该变量是一个线程局部变量
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<Long>(){
        //重写方法，为该局部变量赋初始值
        protected Long initialValue(){
            return System.currentTimeMillis();
        }
    };
    //public void set(T value)，设置该局部变量值
    public static  final void begin(){
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }
    //public T get() ，取出该局部变量的值
    public static final long cost(){
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }
    //测试
    public static void main(String[] args) throws Exception{
        Profiler.begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Cost: "+ Profiler.cost()+" mills");

    }
}
```

上面使用案例摘自《Java并发编程的艺术》，关于ThreadLocal更详细的分析，之后会再做总结。

---

参考：《Java并发编程的艺术》、JDK官方文档