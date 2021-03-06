[toc]

# Java之抽象类与抽象方法

## 抽象概念

本篇关键词是**抽象**，那么何为抽象？百度百科告诉我们，抽象就是概括具体事务共同的方面、本质属性等，而将个别的方面、属性等舍弃的思维过程。在Java里，也就是**把各个具体的类中共有的方法提取出来，放到基类之中，而基类并不需要了解子类中该方法具体是怎么实现的**，这个基类就是所谓的**抽象类**，这些不需要知道具体实现方式的方法就是**抽象方法**。

> 抽象类体现**模板模式**的设计，抽象类作为多个子类的通用模板，子类在抽象类的基础上进行扩展、改造，但子类总体上会大致保留抽象类的行为方式。

## 抽象类与方法

让我们结合代码，好好地分析一波：

```java
package com.my.pac18;

/**
 * @auther Summerday
 */
/*抽象是很有用的重构工具，能够让共有的方法沿着继承层次向上移动。*/
public abstract class Shape {
    //抽象类中的初始化块
    {
        System.out.println("Shape.instance initializer");
    }
    //抽象类中的实例变量
    private String color;
    //抽象类中可以有静态方法及属性。
    public static String name="形状";
    public static void output(){
        System.out.println("父类的static方法");
    }
    //计算周长，但是并不知道具体细节的抽象方法
    public abstract double calPerimeter();
    //返回形状的抽象方法

    //[修饰符] abstract 返回类型 方法名();
    public abstract String getType();
    //抽象类中的构造器，用于被继承
    public Shape() {
    }

    public Shape(String color) {
        System.out.println("Shape.Shape");
        this.color = color;
    }
    //抽象类中的普通实例方法
    public String getColor() {
        return color;
    }
}

class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double calPerimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String getType() {
        return getColor() + "圆形";
    }

    //如果从一个抽象类继承，并创建该新类的对象，就必须给基类的所有抽象方法提供定义。
    public static void main(String[] args) {
        Shape.output();
        //Shape是抽象类，不能创建Shape类的对象
        //但是可以让Shape类的引用变量指向其子类的对象
        Shape[] shapes = new Shape[]{new Circle("红色", 5), new Rectangle("绿色", 6, 6)};
        for (Shape p : shapes) {
            
            System.out.println(p.getType() + "的周长为" + p.calPerimeter());
        }

    }
}

class Rectangle extends Shape {
    
    private double length;
    private double width;

    public Rectangle() {
        System.out.println("父类的构造器不是为了创建对象，而是供给子类继承的");
    }

    public Rectangle(String color, double length, double width) {
        super(color);
        this.length = length;
        this.width = width;
    }

    public boolean isSquare() {
        return length == width;
    }

    public double calPerimeter() {
        return (length + width) * 2;
    }

    public String getType() {
        if (isSquare()) return getColor() + "正方形";
        return getColor() + "长方形";
    }
}

//测试结果
父类的static方法
Shape.instance initializer
Shape.Shape
Shape.instance initializer
Shape.Shape
红色圆形的周长为31.41592653589793
绿色正方形的周长为24.0
```

顺着上面的例子，我们简单地分析一下，**抽象类**与**抽象方法**到底是啥？又需要注意些啥？
- 首先定义了一个**抽象类**：Shape类。可以看到，抽象类由`abstract`关键字修饰。
```java
//[修饰符] abstract class 类名
public abstract class Shape
```
- 然后再Shape类里定义了两个**抽象方法**（方法可以被重写，属性不可以，**抽象是针对方法而言的**）：`getType()`和`calPerimeter()`可以看出抽象方法声明时并没有定义方法体，因为基类中并不需要知道具体的实现方式是怎样，只要知道有这个方法就可以了。你创建一个圆形实例，具体如何算圆形的周长，在圆形类给出抽象方法的定义即可。
```java
//[修饰符] abstract 返回类型 方法名();
 public abstract double calPerimeter();
 public abstract String getType();
```
## 注意事项

- **如果子类从抽象父类继承而来，那么子类就必须重写父类的抽象方法，给出方法的具体实现形式**。你的爸爸说你会算周长，你总得告诉大家周长怎么算吧。但是，如果**子类中也重写父类的方法也声明为抽象**的话，这时父类方法在子类中的实现也无效了。就好比，你说你也不知道怎么算周长，隔壁小王才是真的会。
```java
@Override
public double calPerimeter() {
    return 2 * Math.PI * radius;
}
@Override
public String getType() {
    return getColor() + "圆形";
}
```
- **抽象类不能实例化**，不能用new来调用抽象类构造器创建抽象类的实例。可以想象，既然是抽象的，创建它的实例就没多大意义，创建具体实现的子类的实例才能解决问题。
- **抽象类可以用作一种数据类型，可以让抽象类的引用变量指向它的子类对象**，很好理解，不这样的话，抽象带来的好处体现在哪呢。通过抽象类的引用变量调用抽象类中的方法，就能够根据动态绑定，在运行时根据引用对象的实际类型执行抽象方法的具体表现形式。
- 抽象类中是能够有具体实现的成员方法（实例方法和类方法），以及明确的成员属性（实例属性和类属性）。
```java
//抽象类中的实例方法，子类继承使用。
public String getColor() {
        return color;
    }
//抽象类中可以有静态方法和属性，类名.方法（属性）调用。
public static String name="形状";
public static void output(){
    System.out.println("父类的static方法");
}
```
- 抽象类中还可以有构造器，但是仅仅是为了**供给子类继承使用**，因为上面讲过，抽象类无法创建实例对象！！！(重点回顾：<u>子类无法继承父类构造器，只是单纯调用父类构造器的初始化代码，继承和调用是不一样的！！！</u>）

```java
//子类
public Shape() {
    }

public Shape(String color) {
    System.out.println("Shape.Shape");
    this.color = color;
}
```
- 抽象类中还有代码块和内部类，关于内部类之后将会学习，暂时就不举例了。
```java
 //抽象类中的初始化块
{
    System.out.println("Shape.instance initializer");
}
```
- **一个类中如果有抽象方法，那么这个类也一定是抽象的**。毕竟你的方法定义尚且都不完全，创建你的实例又有啥用呢。**但是一个抽象类中可以没有抽象方法**，只是单纯不让人创建它的实例对象。

- **子类是抽象，父类也可以是具体的**。如上，Object类是所有类的超类，它是具体的，我们定义的Shape类是可以抽象的。

- 抽象是实现多态的一种机制，那么不具备多态性的，就不能称之为多态。诸如private、static、final修饰的方法或构造器属于静态绑定，不具备多态性，不能作为抽象的修饰。

本文是根据资料以及个人测试之后所得，若有错误或者理解不当之处，还望评论区指出，谢谢。