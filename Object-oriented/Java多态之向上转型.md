[toc]
# Java多态之向上转型

**多态性是面向对象的第三大特征。**

## 多态的优点
- 改善代码的组织结构和可读性。
- 能够创建可扩展的程序。（**随时可以加入新功能**）
- 消除类型之间的**耦合**关系。

说实话，作为小白的我，并不太能够理解上面三个优点。随着深入学习，理解应该会越来越深吧，共勉。

## 向上转型

### 概念
**Java允许把子类对象赋值给父类的引用变量，不用做任何强制转换，系统自动完成**。向上转型来自于自下而上的继承关系，子类继承父类，子类是一种特殊的父类，所以向上转型的操作其实是情理之中的。


下面依照简单的代码，来试着理解向上转型的概念与好处。
```java
package com.my.pac14;

/**
 * @auther Summerday
 */
public class DynamicBinding {
    //Object是所有类的超类，根据向上转型，该方法可以接受任何类型的对象
    public static void test(Object x) {
        System.out.println(x.toString());
    }

    public static void main(String[] args) {
        test(new PrimaryStudent());//Student
        test(new Student());//Student
        test(new Person());//Person
        test(new Object());//java.lang.Object@1b6d3586
    }
}

class Person extends Object {
    @Override
    public String toString() {
        return "Person";
    }
}

class Student extends Person {
    @Override
    public String toString() {
        return "Student";
    }
}

class PrimaryStudent extends Student {
}
```
- 我们可以看到，下面的方法接收一个`Object`类型的对象，并调用该对象的`toString()`方法。
```java
 public static void test(Object x) {
        System.out.println(x.toString());
    }
```
- 下面是调用语句，除了第四句，其他的传入对象都看起来与形参类型不符,但当然是可以运行的，这里面就蕴含着我们说的**向上转型**。
```java
public static void main(String[] args) {
    test(new PrimaryStudent());//Student
    test(new Student());//Student
    test(new Person());//Person
    test(new Object());//java.lang.Object@1b6d3586
}
```
- 就拿传入Student类型的对象来说吧，拆解一下，是以下的表达式：
```java
Object x = new Student();
```
- Object类是所有类的超类，上式中将创建的子类类型对象直接赋给父类类型的引用变量，这在Java中是允许的，这就是所谓的向上转型。能够实现的原因，也是<u>因为子类在向上转型的过程中，也许会缩小接口，但至少不会比父类中有的接口还要窄</u>。
> 举个简单的例子，假设人类可以分为很多很多种，我们可以说<u>学生是人类的一种，却不能说人类是学生的一种</u>。向上转型一定程度上允许子类扩展超类的部分丢失，通过父类引用变量只能调用父类中的方法来实现，<u>我们去操作人类的时候，只能在人类具有的行为属性中做选择，而不能直接以学生类的标准去操作它，因为我们并不知道他是哪一类，万一不是学生呢</u>，对吧，用人类总没错，因为我人类有的东西，你学生类一定有。这就是我所理解的向上转型。


### 向上转型好在哪
如果没有向上转型机制，我们想要达到原来的效果，就需要增加许多重载的`test`方法，这样就显得过于繁琐。如果要增加类似`test()`的方法或者添加从`Object`导出的新类，还会做更多复杂的操作，不利于扩展，不可取不可取。
```java
// 原来的情况：需要创建很多很多的测试方法。
    public static void test(Object x) {
        System.out.println(x.toString());
    }
    public static void test(Person x) {
        System.out.println(x.toString());
    }
    public static void test(Student x) {
        System.out.println(x.toString());
    }
    public static void test(PrimaryStudent x) {
        System.out.println(x.toString());
    }
```
多态的存在正好解决了这个棘手的问题，为了利于扩展，**只需要写一个仅接收基类作为参数的简单方法，不管导出类如何，在运行时自动选择调用对应导出类的方法**，真的就很舒服。

> 那么，编译器又是如何确定应该调用哪个方法呢？这就涉及到所谓的“绑定”啦，这个呢，我们在下片总结。


参考书籍：《Thinking in Java》