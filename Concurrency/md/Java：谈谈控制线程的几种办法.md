[toc]
# Java：谈谈控制线程的几种办法

控制线程的办法诸多，本篇做出一部分整理总结。

## join()
官方解释简洁明了：`Waits for this thread to die.`，很明显，针对线程来说，谁调用，等谁死。举个例子：<u>当在A线程中调用B线程的join()方法时，A线程将会被阻塞，直到B线程执行完毕消亡才取消阻塞</u>。

`join()`方法具体有三个：

```java
//等待该线程消亡
public final void join()
//等待该线程消亡，只不过最多等millis毫秒。
public final synchronized void join(long millis)
//等待该线程消亡，只不过最多等millis毫秒+nanos纳秒（毫微秒）。
public final synchronized void join(long millis, int nanos)
```

简单测试一下，理解更加深刻：
```java
    public static void main(String[] args) throws InterruptedException {
        //创建线程
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName()+":"+i);
                }
            }
        });
        //启动线程
        t.start();
        for (int i = 0; i < 3; i++) {
            System.out.println(Thread.currentThread().getName()+":"+i);
            //i为1时，主线程阻塞，等待线程t执行并消亡
            if(i == 1)
                t.join();
        }
    }
```
需要注意的是，join的线程必须已经start了才行。
## sleep()
`sleep(long)`是Thread类中的**静态方法**，上一篇谈到过`sleep(long)`方法和yield()方法的不同之处：
- `sleep(long)`方法会**使线程转入阻塞状态**，时间到了之后才会转入就绪状态。而`yield()`方法不会将线程转入阻塞状态，而是强制线程进入就绪状态。
- 使用`sleep(long)`方法**需要处理异常**，而`yield()`不用。

sleep()方法具体具体有两个：
```java
//让当前线程暂停millis毫秒，并进入阻塞。
public static native void sleep(long millis)
//让当前线程暂停millis毫秒+nanos纳秒（毫微秒），并进入阻塞
public static void sleep(long millis, int nanos)
```
两个方法都会受系统计时器和线程调度器的精度和准确性影响。

## 守护线程

Java中有两类线程：**用户线程(User Thread)** 和 **守护线程(Daemon Thread)**。

脑海里想到一个画面，以前魂斗罗之类的游戏闯关，往往会有一个大boss，boss身边围绕着许许多多的“打工仔”小怪。boss只要一死，小兵统统GG，boss只要还活着，小兵就会一直战斗。

我们可以把boss看成用户线程，把小兵看成守护线程。JVM实例中只要有一个非守护线程还在执行，守护线程就必须工作。当最后一个非守护线程结束的时候，守护线程就随着JVM一起结束了。

我们熟知的**垃圾回收器就是一个典型的守护线程**，而**main主线程是一个用户线程**。

### 主要方法
- public final void setDaemon(boolean on)：通过线程对象调用，传入参数为true，即将该线程**设置为守护线程**。
- public final boolean isDaemon()：判断线程对象**是否为守护线程**。

为了理解更加深刻，可以简单测试一下：
```java

    public static void main(String[] args) throws InterruptedException {
        //创建线程对象
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                for(int i = 0;i<100;i++){
                    System.out.println(Thread.currentThread().getName()+" : "+i);
                    
                }
            }
        });
        //设置为守护线程
        t.setDaemon(true);
        //启动守护线程
        t.start();
        //为了更明显，主线程睡眠100毫秒
        Thread.sleep(100);
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName()+" : "+i);
        }
    }
```
测试结果如下：
![151gPS.png](https://s2.ax1x.com/2020/02/10/151gPS.png)

当前程序中，除了main主线程外没有其他非守护线程的线程了，因此，main线程结束之后，所有守护线程也将结束。

还有一个值得去注意的点：如果所有非守护线程的线程结束，守护线程也将结束，**守护线程中finally块中的代码也不会执行**（这个可以自行检验一下），因此**不能依赖守护线程完成清理或者收尾工作**，因为你完全不知道自己下一秒守护线程是否还健在。

### 需要注意

- 由**守护线程创建的线程默认也是守护线程**，由用户线程创建的也就是用户线程。
- 在线程启动（start）之后，不允许将线程设置为守护线程（setDaemon），否则将会抛出`java.lang.IllegalThreadStateException`异常。
参考：[Java中守护线程的总结](https://blog.csdn.net/shimiso/article/details/8964414)
## 优先级

我们说过，各个处于就绪状态线程等待资源调度是按照一定规则的，这个规则就是线程拥有的优先级。

以下参考《Java编程思想》：
> JDK有10个优先级，但它**和大多数操作系统都不能很好地进行映射**。例如Windows有7个优先级且不是固定的，所以这种映射关系也是不确定的。所以在调整优先级的时候，使用下面三种常量，具有**更好的移植性**。
```java
    //线程可有的最小优先级
    public final static int MIN_PRIORITY = 1;

   //默认优先级
    public final static int NORM_PRIORITY = 5;

    //线程可有的最大优先级
    public final static int MAX_PRIORITY = 10;
```

> 调度器会倾向于让优先级较高的线程先执行，但**并不意味着优先级较低的线程将得不到执行**。

进行试验：

- 定义一个实现Runnable接口的类。
```java
class PDemo implements Runnable {
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 10; i++) {
            System.out.println(name + ":" + i);
        }
    }
}
```
- 创建两个线程对象。
```java
Thread t1 = new Thread(new PDemo(),"A");
Thread t2 = new Thread(new PDemo(),"B");
```
- 可以通过线程对象的`getPriority()`方法获取当前优先级。
```java
//默认情况下，线程的优先级为5
System.out.println(t1.getPriority());//5
System.out.println(t2.getPriority());//5
```
- 通过线程对象的`setPriority(int newPriority)`方法设置优先级。
```java
//设置线程优先级
t1.setPriority(Thread.MIN_PRIORITY);
t2.setPriority(Thread.MAX_PRIORITY);
```
- 调用线程对象的start()方法启动线程。
```java
t1.start();
t2.start();
```

- 每个线程默认的优先级都与创建它的父线程的优先级的相同。
- main线程的优先级是`NORM_PRIORITY`为5。

## 弃用三兄弟


### stop()

![15175T.png](https://s2.ax1x.com/2020/02/10/15175T.png)


### resume

![151oV0.png](https://s2.ax1x.com/2020/02/10/151oV0.png)


### suspend

![151R2Q.png](https://s2.ax1x.com/2020/02/10/151R2Q.png)

## 中断三兄弟

### interrupt()


`public void interrupt()`是Thread类的一个实例方法，说是说用来中断线程，但其实**只是给线程设置了一个"中断"标志(true)** ，线程仍然会继续运行，用户可以监视线程的状态并做出相应处理。

官方文档是这么说的：
线程调用`interrupt()`将会把标志位设置为true，除此之外，情况不同，处理不同：
- 如果这个线程由于wait()，join()，sleep()等方法陷入等待状态时，**它的中断状态将被清除**（也就是true重新变为false)，而且会收到一个`InterruptedException`。

但是我按照下面代码测试了一下，join()和sleep()都能成功检验，但是wait()检验不出，不知问题出在哪，评论区大神求助！！
![1514rn.png](https://s2.ax1x.com/2020/02/10/1514rn.png)

下面这俩目前还没有接触到，以后有机会做总结：
- 如果这个线程由于`java.nio.channels.InterruptibleChannel`中的IO操作发生阻塞，线程还将收到一个`ClosedByInterruptException`。
- 如果这个线程在`Selector`中被阻塞，它可能带有一个非零值，从选择操作立即返回，就像调用了选择器的`wakeup()`方法一样。


### interrupted()


`public static boolean interrupted()`是静态方法，内部调用当前线程的`isInterrupted方`法，**会重置当前线程的中断状态**。也就是说，如果线程被设置为中断标志，第一次调用此方法将会返回true，并将中断标志重置，第二次调用该方法，将会返回false。

### isInterrupted()

`public boolean isInterrupted()`是实例方法，测试当前线程的对象是否被中断，而**不会重置当前线程的中断状态**。

---
关于这三个方法的测试，可以参考这篇博客，非常详细：
[https://blog.csdn.net/zhuyong7/article/details/80852884](https://blog.csdn.net/zhuyong7/article/details/80852884)

## 安全终止线程

以下内容参考：《Java并发编程的艺术》

上面提到，`interrupt()`方法只是给线程标志为“中断”状态，并不会让线程真正中断，我们可以对标识位进行监测并做出相应处理，比如，我们可以通过中断操作与自定义变量来控制是否需要停止任务并终止该线程。

定义一个线程内部类。
```java
    private static class Runner implements Runnable {
        private long i;
        //定义变量作为标识位，用volatile修饰，自身拥有可见性和原子性
        private volatile boolean on = true;

        @Override
        public void run() {
            //对自定义标识位以及中断标识进行校验
            while (on && !Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println("Count i = " + i);
        }
        //取消操作
        public void cancel() {
            on = false;
        }
    }
```
利用标识位优雅地中断或结束线程。
```java
    public static void main(String[] args) throws InterruptedException {
        Runner one = new Runner();
        Thread countThread = new Thread(one, "CountThread");
        countThread.start();
        //睡眠一秒，main线程对CountThread进行中断，使CountThread能够感知中断而结束
        TimeUnit.SECONDS.sleep(1);
        countThread.interrupt();
        Runner two = new Runner();
        countThread = new Thread(two, "CountThread");
        countThread.start();
        //睡眠一秒，main线程对Runner two进行取消，使CountThread能够感知on为false而结束
        TimeUnit.SECONDS.sleep(1);
        two.cancel();
    }
```




