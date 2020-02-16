`Lock`位于`java.util.concurrent.locks`包下，是一种线程同步机制，就像`synchronized`块一样。但是，`Lock`比`synchronized`块更灵活、更复杂。

话不多说，我们直接来看官方文档对Lock接口相关概念及功能的描述，今天又是看英文文档，翻译理解的一天。
# 一、Lock继承关系
![39VQFe.png](https://s2.ax1x.com/2020/02/16/39VQFe.png)

# 二、官方文档解读
![39VlJH.png](https://s2.ax1x.com/2020/02/16/39VlJH.png)
![39V8SA.png](https://s2.ax1x.com/2020/02/16/39V8SA.png)

# 三、Lock接口方法解读
> void lock()

**获取锁**。如果锁不可用，则当前线程将出于线程调度目的而禁用，并处于休眠状态，直到获得锁为止。

> void lockInterruptibly() throws InterruptedException;


如果当前线程未被中断，则获取锁。如果锁可用，则获取锁并立即返回。


如果锁不可用，出于线程调度目的，将禁用当前线程，该线程将一直处于休眠状态。

下面两种情形会让当前线程停止休眠状态：

- 锁由当前线程获取。

- 其他一些线程中断当前线程，并且支持对锁获取的中断。


当前线程出现下面两种情况时，将抛出`InterruptedException`，并清除当前线程的中断状态。

- 当前线程在进入此方法时，已经设置为中断状态。

- 当前线程在获取锁时被中断，并且支持对锁获取中断。

> boolean tryLock();

**尝试获取锁，如果锁处于空闲状态，则获取锁**，并立即返回true。如果锁不可用，则立即返回false。

该方法的典型使用：
```java
    Lock lock = ...;
    //确保锁在被获取时被解锁
    if (lock.tryLock()) {
        try {
            // manipulate protected state
        } finally {
            lock.unlock();
        }
    } else {
        // perform alternative actions
    }
```
> boolean tryLock(long time, TimeUnit unit) throws 
> InterruptedException;

该方法为tryLock()的重载方法，两个参数分别表示为：

- time：等待锁的最长时间
- unit：时间单位



如果在给定的等待时间内是空闲的并且当前线程没有被中断，则获取锁。如果锁可用，则此方法立即获取锁并返回true，如果锁不可用，出于线程调度目的，将禁用当前线程，该线程将一直处于休眠状态。

下面三种情形会让当前线程停止休眠状态：

- 锁由当前线程获取。

- 其他一些线程中断当前线程，并且支持对锁获取的中断。
- 到了指定的等待时间。

当前线程出现下面两种情况时，将抛出InterruptedException，并清除当前线程的中断状态。

- 当前线程在进入此方法时，已经设置为中断状态。

- 当前线程在获取锁时被中断，并且支持对锁获取中断。


如果指定的等待时间超时，则返回false值。如果时间小于或等于0，则该方法永远不会等待。

> void unlock()

释放锁，与lock()、tryLock()、tryLock(long , TimeUnit)、lockInterruptibly()相对应。

> Condition newCondition()


返回绑定到此锁实例的Condition实例。当前线程只有获得了锁，才能调用Condition实例的await()方法，并释放锁。


# 四、重要实现类ReentrantLock

顾名思义，ReentrantLock是重入锁，关于这个重入锁，之前涉及过一些知识，在这里做整合，并稍微地补充一下。


`ReentrantLock`位于`java.util.concurrent（J.U.C）`包下，是Lock接口的实现类。基本用法与`synchronized`相似，都具备**可重入互斥**的特性，但拥有扩展的功能。


RenntrantLock推荐的基本写法：
```java
class X {
    //定义锁对象
    private final ReentrantLock lock = new ReentrantLock();
    // ...
    //定义需要保证线程安全的方法
    public void m() {
        //加锁
        lock.lock();  
        try{
        // 保证线程安全的代码
        }
        // 使用finally块保证释放锁
        finally {
            lock.unlock()
        }
    }
}
```
## 1、API层面的锁
ReentrantLock表现为**API层面的互斥锁**，通过`lock()`和`unlock()`方法完成，是显式的，而synchronized表现为**原生语法层面的互斥锁**，是隐式的。
## 2、可重入的

重进入意味着：**任意线程在获取到锁之后能够再次获取该锁而不会被锁阻塞**，`synchronized`和Reentrant都是可重入的，隐式显式之分。

实现可重入需要解决的两个关键部分：

1. 锁需要去识别获取锁的线程是否是当前占据锁的线程，如果是的话，就成功获取。
2. 锁获取一次，内部锁计数器需要加一，释放一次减一，计数为零表示为成功释放锁。

## 3、可公平的

关于锁公平的部分，官方文档是这样描述的（英文我就不贴了），词汇较简单，我试着翻译一下：


Reentrant类的构造函数接受一个**可选的公平性参数fair**。这时候就出现两种选择：

- 公平的（fair == true）：**保证等待时间最长的线程优先获取锁**，即FIFO。
- 非公平的（fair == false）：此锁不保证任何特定的访问顺序。

公平锁往往体现处的总体吞吐量比非公平锁要低，也就是更慢。

锁的公平性并不保证线程调度的公平性，但公平锁能够减少"饥饿"发生的概率。

需要注意的是：不定时的tryLock()方法不支持公平性设置。如果锁可用，即使其他线程等待时间比它长，它也会成功获得锁。



## 4、等待可中断
当持有线程长期不释放锁的时候，正在等待的线程可**以选择放弃等待**或**处理其他事情**。

## 5、锁绑定

一个ReentrantLock对象**可以通过newCondition()同时绑定多个Condition对象**。

---

JDK1.6之前，ReentrantLock在性能方面是要领先于synchronized锁的，但是JDK1.6及之后版本实现了各种锁优化技术，可参考：
[聊聊并发Java SE1.6中的Synchronized](https://blog.csdn.net/Sky_QiaoBa_Sum/article/details/104347808)，后续性能改进会更加偏向于原生的synchronized。

---

参考资料：
《深入理解Java虚拟机》周志明
《Java并发编程的艺术》方腾飞