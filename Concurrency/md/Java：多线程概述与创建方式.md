[toc]
# Java：多线程概述与创建方式

在之前的学习过程中，已经不止一次地提到了并发啊，线程啊，同步异步的内容，但是出于内容的局部一体，之前总是几笔带过，并附上：以后学习的时候再细说。

那么，现在到了细说的时候，在翻阅并参考了介绍Java并发编程的书之后，突然感觉压力有些大，因为有些概念确实比较抽象。所以之后的内容不定长短，但是每天都会试着输出一些。

## 进程和线程
一个进程可以拥有多个线程，一个线程必须拥有一个父进程。
**进程**：当前操作系统正在执行的任务，也是操作系统运行程序的一次执行过程。
**线程**：是进程的执行单元，是进程中正在执行的子任务。
就好像我们正在使用的QQ，正在放歌的音乐软件，正在打的游戏，就是一个个的进程。我们在QQ进程中执行的各种操作，就是一个个的线程。
> 每个Java的应用程序运行的时候其实就是个进程，JVM启动之后，会创建一些进行自身常规管理的线程，如**垃圾回收**和终结管理，和一个**运行main函数的主线程**。

![16IMg1.png](https://s2.ax1x.com/2020/02/06/16IMg1.png)




## 并发与并行
现在大部分的操作系统都是支持多进程并发运行的，就像我们现在正在使用电脑，可以通过任务管理器查查看，会发现有几十个几百个进程在“同时执行”。”同时执行“被打上了引号，显然事实上并不是。

**并发**：就拿进程来说，**在同一个时刻，只能有一条指令执行**，但是多个进程可以被**快速地轮换执行**，CPU的执行速度之快，让人产生这些个进程就是在同时执行。
**并行**：就是同一时刻，**多条进程指令在多个处理器上同时执行**。

看看下面的图就懂了：

![16IZEF.png](https://s2.ax1x.com/2020/02/06/16IZEF.png)

接下来是我对于并发和并行假想场景：
并发场景：假设现在有一台只能一个人玩的电脑，老大和老二兄弟俩都想玩一小会儿，那没办法，得想办法解决啊。打一架吧，谁抢到算谁的。不管是谁抢到，他们一定玩到满足才会罢休，这就是现在操作系统所采用的高效率的**抢占式多任务操作策略**。
并行场景：现在有两台电脑，老大老二都各自玩各自的电脑，不争也不抢。


## 多线程的优势
线程被称为**轻量级进程**，大多数情况下，进程中的多线程的执行是抢占式的，就和操作系统的并发多进程一样。


> 线程拥有自己的**堆栈**、**程序计数器**和**局部变量**，允许程序控制流的多重分支同时存在于一个线程，**共享进程范围内的资源**，因此，同一进程中的线程访问相同的变量，并从同一个堆中分配对象，实现良好的数据共享，但是如果处理不当，会为线程安全造成一定的隐患。

多线程相比于多进程的优势：

- 多个**线程之间可以共享内存**，而进程之间不可以。
- 操作系统创建线程的代价比进程小，实现**多任务并发效率更高**。

以下参考自《JAVA并发编程实战》：
> - 一个单线程应用程序一次之能运行在一个处理器上。在双处理器系统中只运行一个应用程序，相当于其中一个处理器空闲，50%的CPU资源没有利用上。随着处理器的增多，单线程的应用程序放弃的CPU资源将会更多。这一点，正好也侧面反映了多线程能够**更有效地利用空闲的处理器资源**。
> - 处理器在某些情况是空闲的，如在等待一个同步IO操作完成的时候。这个时候，暂且不论多处理器，仅仅针对单处理器，多线程的优势也是相当明显的，可以很好地**利用处理器空闲的时间运行另外一个线程**。

## 线程的创建和启动

先来看看多线程编程中这个相当关键的类，`java.lang.Thread`，官方文档说了：有两种方式创建线程，就是下面这俩：

### 继承Thread类

- 将一个类声明为`Thread`的子类。
- 这个子类应该覆盖类`Thread`的`run()`方法。

创建线程如下：
```java
/*创建线程*/
//创建一个类继承Thread类
class TDemo extends Thread{
    //线程要执行的任务在run方法中
    @Override
    public void run(){
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
        }
    }
}
```
启动线程如下：
```java
    public static void main(String[] args){
        //创建了TDemo的实例
        TDemo t1 = new TDemo();
        //启动线程，并调用run方法
        t1.start();
        System.out.print("main");
    }
    //输出结果：main01234
```
创建TDemo的实例对象不等于启动了该实例所对应的线程，启动需要调用线程对象的`start()`方法。

### start()和run()
- new创建了TDemo的实例，只是创建了一个线程，此时它处于**新建状态**，有JVM分配内存，并初始化成员变量的值，是个配置的过程。
- 线程对象调用`start()`方法之后，**线程就会处于就绪状态**，JVM会为其创建方法调用栈和程序计数器，表示这个线程可以执行，但**真正啥时候开始执行取决于JVM中线程调度器的调度**。

- 之后才进入运行状态，执行`run()`方法中的方法体。

我们试着把start()方法换成run()方法看看结果：`01234main`

---

我们通过输出结果可以看到，调用start()方法，系统会把run()方法当成线程执行体处理，主线程和我们创建的线程将并发执行。但如果单纯调用run()方法，系统会把线程对象当成一个普通的对象，run()方法也只是普通对象方法的一部分，是**主线程的一部分**。

![1R4UJg.png](https://s2.ax1x.com/2020/02/08/1R4UJg.png)


### 实现Runnable接口
这是`Runnable`接口的内容，`@FunctionalInterface`注解表示函数式接口，和Java8新特性lambda表达式相关，之后再做学习总结。
```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```
- 创建线程的另一种方法是声明一个实现Runnable接口的类。
- 然后，该类实现run方法。然后可以分配类的实例，
- 在创建线程时作为参数传递，并启动它。

```java
//实现Runnable接口
class RDemo implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
        }
    }
}

//创建并启动线程
Thread t = new Thread(new RDemo());
t.start();
```
调用`public Thread(Runnable target)`构造器，将Runnble接口类型对象传入作为参数，构建线程对象。
当然还可以用匿名内部类的形式：
```java
    //匿名内部类创建并启动线程
    new Thread(new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.print(i);
            }
        }
    }).start();
```

### 实现Callable接口
这是Callable接口的内容：
```java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
```
除了上面两种方法之外，从书上看到还有一种Java5新增的方法，利用Callable接口，官方文档是这样描述的：

- Callable接口类似于Runnable，需要**实现接口中的call()方法**。但是，Runnable不返回结果，也不能抛出已检查的异常。
- Runnable接口提供run()方法支持用户定义线程的执行体，而Callable中提供call()方法。
    - **拥有返回值**。
    - **允许抛出异常**。
- 通过泛型我们可以知道，Callable接口中的形参类型需要和call方法返回值类型相同：


光有`Callable`接口还不行，毕竟隔了5年才出来，为了尽量避免修改之前的代码，适应当前环境，Java5还新增了配套的`Future`接口：
```java

public interface Future<V> {

    //试图取消Callable中任务的执行，如果任务已经完成、已经被取消、或因其他原因无法被取消，返回false。
    boolean cancel(boolean mayInterruptIfRunning);

    //如果此任务在正常完成之前被取消，则返回true
    boolean isCancelled();

    //如果此任务已完成（正常的终止、异常或取消），则返回true
    boolean isDone();

    //如果需要，则等待计算完成，然后检索其结果。
    V get() throws InterruptedException, ExecutionException;
    
    //如果需要，将等待最多给定的时间以完成计算，然后检索其结果。
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```
通过继承关系可以发现，`RunnableFuture`接口同时继承了`Runnable`和`Future`接口，意味着实现`RunnableFuture`接口的类既是Runnable的是实现类，又是Future的实现类。FutureTask就是充当这样的角色，**它的实例可以作为target传入Thread的构造器中。**

通过查看源码，可以发现**FutureTask内部维护了一个Callable的对象**，可以通过下面的这个构造器初始化Callable对象。
```java
    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
```
- 用匿名内部类的方式，将实现call()方法的Callable实现类对象作为参数传递给FutureTask的构造器中，构建一个FutureTask类的对象。
```java
    FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            int i = 0;
            while(i<10){
                System.out.println(Thread.currentThread().getName());
                i++;
            }
            return i;
        }
    });
```
- 之后可以通过Thread类构造器：`public Thread(Runnable target, String name) `将task对象作为参数创建新线程并启动。`name`参数是可以自定义线程的名字。
```java
new Thread(task,"name").start();
```
- 最后可以通过task对象调用get()方法得到call()方法的返回值，需要注意处理抛出的异常。
```java
    try {
        System.out.println(task.get());
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
```
### 创建方式的区别
**继承类Thread**和**实现接口（Runnable或Callable）**这两种方式的区别？

- 前者需要定义子类继承Thread类，可以直接通过创建子类对象作为线程对象，而后者创建的Runnable对象只是线程对象的target。
- 同样的，获取当前对象的方法也不同，前者可以直接使用this获取当前对象的引用。后者则需要调用Thread的静态方法`currentThread()`。
下面是两个获取当前线程名的示例：
```java
//继承Thread
System.out.print(this.getName()+i);
//实现Runnable接口
System.out.print(Thread.currentThread().getName()+i);
```
- 前者线程类每创建一个线程都需要创建一个对象，对象之间不能共享实例变量。而后者通过接口的实现类创建的多个线程可以共享同一个Runnable类型的target，也就是这个线程类的实例变量。

- 前者定义线程类需要继承Thread，而Java只**支持单继承，支持接口多实现**，显然在灵活性方面，后者优于前者。

---

本文作为个人学习笔记，仍停留在比较浅显的层面，还需要大量的实践去感悟并发编程的奥义。

参考资料：《JAVA并发编程实战》、《疯狂Java讲义》、《JAVA多线程设计模式》