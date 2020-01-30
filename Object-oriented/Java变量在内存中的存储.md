[TOC]

# Java变量在内存中的存储

以下探究**成员变量**和**局部变量**在内存中的存储情况。
```java
package com.my.pac04;

/**
 * @author Summerday
 * @date 2019/12/1 13:05
 */
public class ValStoreTest {
    public static void main(String[] args) {
        //分别创建两个Value类的实例，val0和val1
        Value val0 = new Value();
        Value val1 = new Value();
        //修改val0的num值
        val0.num = 5;
        System.out.println("val0's num = " + val0.num);//val0'num =5
        System.out.println("val1's num = " + val1.num);//val0'num =0
        
        //直接使用类调用类变量
        Value.staticNum = 10;
        //使用实例对象调用类成员变量,不建议使用
        //val0.staticNum = 10;
        System.out.println("val0's staticNum =" + val0.staticNum);//10
        System.out.println("val1's staticNum =" + val1.staticNum);//10
    }
}
class Value {
    public int num;//实例变量
    public static int staticNum;//类变量
}

```
## 成员变量
上面提到成员变量分为实例变量和类变量。这两者在内存中的存储形式又是如何呢？
```java
class Value {
public int num;//实例变量
public static int staticNum;//类变量
}
```
**实例变量**
```java
Value val0 = new Value();
Value val1 = new Value();
//修改val0的num值
val0.num = 5;
System.out.println("val0's num = " + val0.num);//val0'num =5
System.out.println("val1's num = " + val1.num);//val0'num =0
```
- 在**堆区**开辟内存，存放实例变量，并默认初始化。
- 在**栈区**声明引用变量val0和val1，让他们分别存储两个堆区对象的地址，意思是<u>让引用变量指向实际对象。</u>
- 两个对象分别独立，互不影响。

**类变量**
```java
//直接使用类调用类变量
Value.staticNum = 10;
//使用实例对象调用类成员变量,不建议使用
//val0.staticNum = 10;
System.out.println("val0's staticNum =" + val0.staticNum);//10
System.out.println("val1's staticNum =" + val1.staticNum);//10
```
- 在**方法区（不是栈区）**存储static修饰的变量，即staticNum。
- 两个引用变量val0和val1还是在栈区存储，且**他们指向方法区的同一块内存区域。**
- 在同一个类中，实例对象共享类变量。意思是，如果Java程序运行在不同JVM进程中，就不会共享数据。如下：
```java
package com.my.pac04;
/* ClassVal.java */
public class ClassVal {
    public static int value = 6;
}
```
```java
package com.my.pac04;
/* ClassValTest01.java */
public class ClassValTest01 {
    public static void main(String[] args) {
        ClassVal val01 = new ClassVal();
        val01.value = 10;//修改static修饰的值为10
        System.out.println(val01.value);
    }
}
```
```java
package com.my.pac04;
 /* ClassValTest02.java*/
public class ClassValTest02 {
    public static void main(String[] args) {
        ClassVal val02 = new ClassVal();
        System.out.println(val02.value);//不是10，而是6
    }
}

```
关于JVM及类加载的机制，在之后的学习之中将会继续深入，在这里就不发表自己的肤浅之谈了，之后会进行补充哈。

---


## 局部变量

- 定义局部变量时，只有对他进行初始化赋值之后，才会获得系统给予的内存。
- 局部变量不属于类或实例，总是保存在栈内存中，所以**随方法或代码块结束而结束，后进先出。**
- 基本数据类型存放具体数值，引用类型存放实际引用对象的**地址值**。

**注意**：虽然大部分时间，可以之间用成员变量来代替局部变量解决问题，但是这种做法，有几个缺点：

- 增大变量的生存时间，增加内存开销。
- 扩大变量的作用域，不利于提高程序的内聚性。

**局部变量的作用范围越小，它在内存中停留的时间就越短，程序性能就会越好。**
## 总结

- 假如定义的变量用于描述某个类或对象的固有信息，请用成员变量。
- 假如定义的变量用于保存类或实例运行的状态信息，请用成员变量。
- 假如定义的变量用于保存共享在多个方法间的消息，请用成员变量。