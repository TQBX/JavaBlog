[toc]

本篇参考许多著名的书籍，形成读书笔记。
# 导致线程不安全的原因
当一个变量被多个线程读取，且**至少被一个线程写入**时，如果读写操作不遵循`happens-before`规则，那么就会存在**数据竞争**的隐患，如果不给予正确的同步手段，将会导致线程不安全。


# 什么是线程安全

Brian Goetz在《Java并发编程实战》中是这样定义的：

> 当多个线程访问一个类时，如果不用考虑这些线程在运行时环境下的调度和交替执行，并且**不需要额外的同步**及在调用方代码**不必做其他的协调**，这个类的行为仍然是正确的，那么这个类就是**线程安全**的。

---

周志明在《深入理解Java虚拟机》中提到：多个线程之间存在共享数据时，这些数据可以按照线程安全程度进行分类：

## 不可变
**不可变的对象一定是线程安全的**，只要一个不可变的对象被正确地构建出来，那么它在多个线程中的**状态就是一致**的。例如用final关键字修饰对象：

- 修饰的是基本数据类型，final修饰不可变。
- 修饰的是一个对象，就需要保证其状态不发生变化。

JavaAPI中符合不可变要求的类型：String类，枚举类，数值包装类型（如Double）和大数据类型（BigDecimal)。

## 绝对线程安全
即完全满足上述对于线程安全定义的。

满足该定义其实需要付出很多代价，Java中标注线程安全的类，实际上绝大多数都不是线程安全的（如Vector），因为它**仍需要在调用端做好同步措施**。Java中绝对线程安全的类：`CopyOnWriteArrayList`、`CopyOnWriteArraySet`。

## 相对线程安全

即我们通常所说的线程安全，Java中大部分的线程安全类都属于该范畴，如`Vector`，`HashTable`，`Collections`集合工具类的`synchronizedCollection()`方法包装的集合等等。就拿Vector举例：如果有个线程在遍历某个Vector、有个线程同时在add这个Vector，99%的情况下都会出现`ConcurrentModificationException`，也就是`fail-fast`机制。


## 线程兼容
对象本身并不是线程安全的，可以通过在调用段正确同步保证对象在并发环境下安全使用。如我们之前学的分别与Vector和HashTable对应的`ArrayList`和`HashMap`。

---

对象通过synchronized关键字修饰，达到同步效果，本身是安全的，但相对来说，效率会低很多。
## 线程对立
无论调用端是否采取同步措施，都无法正确地在多线程环境下执行。Java典型的线程对立：Thread类中的suspend()和resume()方法：如果两个线程同时操控一个线程对象，一个尝试挂起，一个尝试恢复，将会存在死锁风险，**已经被弃用**。

常见的对立：`System.setIn()`，`System.setOut()`和`System.runFinalizersOnExit()`。




# 互斥同步实现线程安全

**互斥同步**也被称做**阻塞同步**(因为互斥同步会因为线程阻塞和唤醒产生性能问题），它是实现线程安全的其中一种方法，还有一种是**非阻塞同步**，之后再做学习。


互斥同步：**保证并发下，共享数据在同一时刻只被一个线程使用。**

## synchronized内置锁
其中使用`synchronized`关键字修饰方法或代码块是最基本的互斥同步手段。

`synchronized`是Java提供的一种**强制原子性的内置锁机制**，以`synchronized`代码块的定义方式来说：

```java
synchronized(lock){
    //访问或修改被锁保护的共享状态
}
```

它包含了两部分：1、锁对象的引用 2、锁保护的代码块。

**每个Java对象都可以作为用于同步的锁对象**，我们称该类的锁为**监视器锁（monitor locks）**，也被称作内置锁。

可以这样理解：线程在进入synchronized之前需要获得这个锁对象，在线程正常结束或者抛出异常都会释放这个锁。

而这个锁对象很好地完成了互斥，假设A持有锁，这时如果B也想访问这个锁，B就会陷入阻塞。A释放了锁之后，B才可能停止阻塞。

### 锁即对象

- 对于普通同步方法，锁是当前实例对象（this）。
```java
//普通同步方法
public synchronized void do(){}
```
- 对于静态同步方法，锁是当前的类的Class对象。
```java
//静态同步方法
public static synchronized void f(){}
```
- 对于同步方法块，锁的是括号里配置的对象。
```java
//锁对象为TestLock的类对象
synchronized (TestLock.class){    
    f();
}
```

明确：synchronized方法和代码块本质上没啥不同，方法只是对跨越整个方法体的代码块的简短描述，而这个锁是方法所在对象本身（static修饰的方法，对象是当前类对象）。这个部分可以参考：[Java并发之synchronized深度解析](https://www.jianshu.com/p/a499d13ca702?from=timeline&isappinstalled=0)

### 是否要释放锁
释放锁的情况：
- 线程执行完毕。
- 遇到return、break终止。
- 抛出未处理的异常或错误。
- 调用了当前对象的wait()方法。

不释放锁的情况：
- 调用了Thread.sleep()和Thread.yield()暂停执行不会释放锁。
- 调用suspend()挂起线程，不会释放锁，已被弃用。
### 实现原理
JVM基于**进入和退出Monitor对象**来实现方法同步和代码块同步，但两者实现细节不同。

代码块同步使用`monitorenter`和`monitorexit`两个指令实现，JVM的要求如下：
- `monitorenter`指令会在编译后插入到同步代码块的开始位置，而`monitorexit`则会插入到方法结束和异常处。
- 每个对象都有一个`monitor`与之关联，且当一个`monitor`被持有之后，他会处于锁定状态。
- 线程执行到`monitorenter`时，会尝试获取对象对应`monitor`的所有权。

- 在获取锁时，如果对象没被锁定，或者当前线程已经拥有了该对象的锁（可重进入，不会锁死自己），将锁计数器加一，执行`monitorexit`时，锁计数器减一，计数为零则锁释放。
- 获取对象锁失败，**则当前线程陷入阻塞**，直到对象锁被另外一个线程释放。

### 啥是重进入？
重进入意味着：**任意线程在获取到锁之后能够再次获取该锁而不会被锁阻塞**，`synchronized`是隐式支持重进入的，因此不会出现锁死自己的情况。

这就体现了**锁计数器**的作用：获得一次锁加一，释放一次锁减一，无论获得还是释放多少次，**只要计数为零，就意味着锁被成功释放**。
## ReentrantLock（重入锁）
`ReentrantLock`位于`java.util.concurrent（J.U.C）`包下，是Lock接口的实现类。基本用法与`synchronized`相似，都具备**可重入互斥**的特性，但拥有扩展的功能。
> Lock接口的实现提供了比使用synchronized方法和代码块更广泛的锁操作。允许更灵活的结构，具有完全不同的属性，并且可能支持多个关联的**Condition**对象。

RenntrantLock官方推荐的基本写法：
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
### API层面的互斥锁
ReentrantLock表现为**API层面的互斥锁**，通过`lock()`和`unlock()`方法完成，是显式的，而synchronized表现为**原生语法层面的互斥锁**，是隐式的。


### 等待可中断
当持有线程长期不释放锁的时候，正在等待的线程可**以选择放弃等待**或**处理其他事情**。
### 公平锁
ReentrantLock锁是公平锁，即**保证等待的多个线程按照申请锁的时间顺序依次获得锁**，而synchronized是不公平锁。
### 锁绑定

一个ReentrantLock对象**可以同时绑定多个Condition对象**。

---

JDK1.6之前，ReentrantLock在性能方面是要领先于synchronized锁的，但是JDK1.6版本实现了各种锁优化技术，后续性能改进会更加偏向于原生的synchronized。


参考数据：《Java并发编程实战》、《Java并发编程的艺术》、《深入理解Java虚拟机》