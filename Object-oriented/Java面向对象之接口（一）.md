[toc]
# Java之接口（一）  

距离上篇已经过了好久啦，主要是临近期末，忙着复习，接口方面也看了好多天，查看了很多资料，层次不齐，最终查看了官方文档对于`interface`的介绍，才逐渐清晰一些。但是，毕竟是英文，看起来还是相对有些费劲的，但是看完一整篇下来，产生了一种英文进步飞快的错觉哈哈哈哈。


话不多说，直接给一个形象生动的例子：
> There are a number of situations in software engineering when it is important for disparate groups of programmers to agree to a "**contract**" that spells out how their software interacts. Each group should be able to write their code without any knowledge of how the other group's code is written. Generally speaking, interfaces are such contracts.

大概的意思就是，开发的时候，每个小组只需认可一份软件交互的“**契约**”，不需要关注其他小组的代码是怎么写的。我们将要学习的接口，就是充当这个“契约”的角色。其实也挺好理解的，面向对象的口号不就是高内聚低耦合嘛，接口的出现很好地让规范和实现分离，从而大大降低了各组件的耦合，大大提高了可扩展性和可维护性。
再接着说，**接口是一种规范**，是其他组成部分都必须遵守的，**是一组公共的方法**，和我们之前谈到的抽象类真的很像，它们达到的功能其实也差不多，两者差别我们之后会详细分析。接下来应该会分许多个部分探究接口，敬请期待。

## 接口中的定义格式
```java
package com.my.pac19;
//Output.java
/**
 * @auther Summerday
 */
public interface Output {
    //常量: public static final int MAX_CACHE_LINE=50;
    int MAX_CACHE_LINE = 50;
    //普通方法: public abstract void out();
    void out();

    void getData(String msg);
    //默认方法: public default void print(String...msgs)
    default void print(String...msgs){
        for(String msg:msgs){
            System.out.println(msg);
        }
    }

    default void test(){
        System.out.println("default test()");
    }
    
    
    //静态方法：public static String staticTest()
    static String staticTest(){
        return "the static method in interface";
    }

    //!false:static String staticTest1();
}
```
使用`interface`关键字表示接口，可以把接口看作是特殊的类，**命名规范**类似。而且，一个Java源文件中最多只能有一个public接口，且Java源文件名应该和public接口名相同。
```java
[修饰符] interface [接口名] extends [父接口1],[父接口2] {
    //零到多个常量定义
    //零到多个抽象方法定义
    //零到多个默认方法和类方法定义
    //零到多个内部类、接口、枚举定义
}
```
- [修饰符]可以是public，也可以是缺省，缺省就默认采用包权限。
- 接口可以继承多个接口，以逗号分隔，不能继承类。
- **Java8之前并不允许默认方法和类方法的定义**，加强之后就允许了。

- 接口里**不能有构造器(不能创建对象）**和初始化块。
- 接口中的成员（静态变量、方法等）默认都是public修饰的，所以加不加public没啥区别，但也只有这两种选择，因为毕竟规范就是需要让别人知道并遵循的。
## 接口中的成员变量

接口中只能有静态变量，不能有实例变量，因为它**不能创建对象**，也就不能有实例。
```java
//常量: public static final int MAX_CACHE_LINE=50;
    int MAX_CACHE_LINE = 50;
```
- 接口中的域默认且只能是以`public static final`修饰的静态常量，所以这几个修饰词可以省略，如上。
- 既然**没有构造器和初始化块**，那么定义静态变量时就应该给他初始化赋值，不然会报错哦。
- 静态常量，仅有一份，且不能修改。

## 接口中的普通方法

Java8之前，接口中只允许声明抽象方法，Java8之后做了增强，增加了对默认方法和类方法声明的允许。这里，暂且除那俩之外的称作普通方法，也就是**抽象方法**。

```java
//普通方法: public abstract void out();
    void out();

    void getData(String msg);
```
- 类似的，接口中的普通方法既然是抽象方法，当然默认有`abstract`关键字了，上面提到接口中的成员都是`public`修饰的，所以普通方法默认`public abstract`，同样的，可以省略不写。
- 很显然，抽象方法不能有具体的方法体实现。

## 接口中的默认方法

作为**Java8之后增加的默认方法**，所谓存在即合理，它的出现一定解决了一些棘手的问题，但是目前我对此理解还不够透彻，在之后的学习中，慢慢理解，再做总结。我们先了解它的定义格式：

```java
//默认方法: public default void print(String...msgs)
    default void print(String...msgs){
        for(String msg:msgs){
            System.out.println(msg);
        }
    }
//!false:default void test1();
//extension method should have a body
```
- 默认方法以`default`关键字修饰，而不是`static`。
- 默认方法在接口中**必须有具体实现**，否则会报错。
- 默认方法是可以在实现类中**被重写**的。

## 接口中的静态方法
同样的，他和默认方法都是新引进的宝贝，具体优点还是之后再做分析。先看一手定义：
```java
//静态方法：public static String staticTest()
    static String staticTest(){
        return "the static method in interface";
    }

    //!false:static String staticTest1();
```
- 静态方法以`static`关键字修饰，且不能被重写。
- 一样的，接口中的静态方法也需要有具体的方法实现，不然就错啦。
- 静态方法只属于接口,只能在接口中调用静态方法，并不能在接口实现类中调用方法。就是说只能以`接口.静态方法`的格式调用接口中的静态方法。