在并发编程中，保证线程同步，从而**实现线程之间正确通信**，是一个值得考虑的问题。本篇将参考许多著名书籍，学习如何让多个线程之间相互配合，完成我们指定的任务。

当然本文只是学习了一部分线程间通信的方法，还有一些例如使用Lock和Condition对象，管道输入输出、生产者消费者等内容，我们之后再做学习。

# synchronized 与 volatile

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
# 等待/通知机制

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