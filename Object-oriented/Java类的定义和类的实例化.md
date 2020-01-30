[TOC]
# Java类的定义与类的实例化
## 类的定义

>面向对象的程序设计中，类可以看作是我们自定义的数据类型，那么，如何能更加优美，更加高效地定义它就显得尤为重要。
>类中的成员有很多，每一部分都是十分关键的，毕竟“面向对象”在Java学习中真的很重要，许许多多的Java开发者定义出来的类等待着我们去使用，加油！攻克它！

**直接上代码：**
```java
package com.my.pac02;
/**
 * @author Summerday
 * @date 2019/11/26 21:40
 */
 //类名和文件名一致，且包含main方法
public class CatTest{
    //程序入口
    public static void main(String[] args) {
        //创建对象
        Cat cat = new Cat();
        //为对象的属性赋值
        cat.name = "sink";
        cat.isMale = true;
        //通过对象调用方法
        cat.sleep();
        cat.jump();
        cat.laughAt("Susan");
        System.out.println(cat.isNotMale());
        //打印引用变量的值
        System.out.println(cat);
        //创建一个新的引用变量并指向原先的对象
        Cat otherCat = cat;
        System.out.println(otherCat);
        System.out.println(otherCat.name);//"sink"
        //将cat和实际对象之间的引用消除
        cat = null;
    }
}
//定义一个Cat类
class Cat{
    //构造方法
    Cat() {
        System.out.println("cat is cute.");
    }
    //成员变量
    String name;
    int age;
    boolean isMale;
    String color = "Blue";
    //方法
    void sleep(){
        System.out.println(name+"is sleeping---");
    }
    public void jump() {
        System.out.println(name+"is jumping---");
    }
    public void laughAt(String otherName)
    {
        System.out.println(name+"is laughing at "+otherName);
    }
    //返回boolean类型的方法
    boolean isNotMale() {
        return !isMale;
    }
}
```
### 定义一个简单的类

```java
[修饰符] class 类名
{
    （零个到多个）构造器...
    （零个到多个）成员变量（属性）...
    （零个到多个）方法...
}

```
- [修饰符]修饰类时可以是上面代码中的**public**，或者final，abstract，也可以省略不写，但不建议使用private和protected。参考:[
Java的外部类为什么不能使用private、protected进行修饰](
https://www.cnblogs.com/langren1992/p/9539641.html)
- 类名命名规范：要见名知义，意思是需要多个有意义的英文单词组成，**每个单词首字母大写**，规范这种东西，建议遵守！养成好习惯。
- 上面三种成员（构造器，成员变量，方法）都可以定义零个或多个，但是都是零个就变成了空类，没啥意义。

### 定义一个成员变量
```java
[修饰符] 类型 成员变量名 [=默认值];
//例如
boolean isMale;
String color = "Blue";
```
- **成员变量包括实例变量和类变量**，**static**修饰的成员变量称作类变量，关于static之后再细讲。

### 定义一个方法
```java
[修饰符] 返回值类型 方法名（形参列表）
{
    零条到多条可执行语句组成的方法体...
}
//例如
void sleep(){
System.out.println(name+"is sleeping---");
}
public void jump() {
System.out.println(name+"is jumping---");
}
public void laughAt(String otherName)
{
System.out.println(name+"is laughing at "+otherName);
}
//返回boolean类型的方法
boolean isNotMale() {
    return !isMale;
}
```

### 定义一个构造器
```java
[修饰符] 构造器名 （形参列表）
{
    （零条到多条可执行语句组成的构造器执行体...
}
//例如
//构造方法
Cat() {
    System.out.println("cat is cute.");
}
```
- **构造器用于构造类的实例**，也就是创建某个类的对象时会自动调用构造方法，之后会讨论。
- **构造器名一定要和类名相同！**
- 没有定义构造器的话，系统会提供默认的构造器。

设计类时的具体细节，接下来将会一一展开，还有未涉及到的**内部类**和**代码块**部分，之后将会进行学习，暂且从这三个部分展开探究。

## 类的实例化
通过类构造属于该类对象的过程就叫做类的实例化。对象是具体存在的事物，也称作实例，可以调用类中定义的实例变量以及方法。（不考虑static修饰变量的情况下）

### 创建对象及使用对象：
```java
//使用Cat类创建了Cat类型的对象
//并调用Cat类的构造器，返回Cat的实例，赋值给变量cat
Cat cat = new Cat();
//访问cat的实例变量name和isMale，并为他们赋值
cat.name = "sink";
cat.isMale = true;
//调用cat的方法
cat.sleep();
cat.jump();
cat.laughAt("Susan");
//输出isNotMale（）方法的返回值
System.out.println(cat.isNotMale());
```
- 如果访问权限允许的情况下（访问权限涉及到private等关键字，暂且不谈），类中定义的方法和成员变量都可以通过类或实例来调用。
- 创建对象：`类型 实例名 = new 类型（参数列表）；`例如：`Cat cat = new Cat();`
- 调用类成员变量或方法：`类.类变量或类.方法`或`实例.实例变量或实例.方法`，类变量涉及static关键字，也是暂且放一边，之后继续回顾。
可以这么理解：定义类就是为了创建许许多多该类的实例，这些实例具有相同的特征。

### 创建对象的过程在内存中的表现

类是一种**引用数据类型**：也就是说，在栈内存里的引用变量并不是真正存储对象的成员变量，而是它的引用，实际的成员变量藏在堆内存中，这一点类似于前面提到过的数组类型。而且，可以知道，<u>栈内存中存储的是实际对象在堆内存中的地址值，</u>可以直接打印引用变量cat的值验证。
- **创建对象**
```java
//创建对象，并初始化
Cat cat = new Cat();
```
![9dcccb4ee14c5e84ff0bb9ef26b72fd2.png](en-resource://database/913:1)
- **访问实例变量**
```java
//访问实例变量
cat.name = "sink";
cat.isMale = true;
```
![34da916bd4807fd6d74ff1e43f1837e0.png](en-resource://database/915:1)

Java不允许直接访问堆内存中的对象，只能通过该对象的引用操作该对象。

另外，堆内存中的同一个对象可以由栈内存中多个引用变量所指向。例如：
```java
//把引用变量cat赋值给另一个引用变量otherCat
Cat otherCat = cat;
```
这时，cat和otherCat都在栈内存中被创建，且他们都指向了原先cat所指向的那块堆内存，所以他们操作的是同一个实际对象。
>堆内存的对象没有**任何变量**指向时，就会在一段时间内作为“垃圾”被Java的**垃圾回收机制回收**，释放对象所占用的内存区。
>所以想让他提前释放，直接给引用变量赋值为null。

参考书籍：《疯狂Java讲义》