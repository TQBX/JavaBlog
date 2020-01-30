[toc]

# Java面向对象之初始化块

在程序设计中，让数据域正确地执行初始化一直是一个亘古不变的真理。
那么，有哪些手段可以初始化数据域呢：

- 在构造器中设置值。
- 在声明中赋值。
- 使用**初始化块**。

本篇探讨关于Java中的初始化块的注意点：Java中的初始化块是类中的一种成员，但是既没有名字，也没有标识，不能够被调用，它仅仅只是在创建Java对象时隐式执行初始化。

## 普通初始化块
- 普通的初始化块就是**非static**修饰的。
- 声明时以花括号`{}`包起代码，被包住的就是初始化代码，整体就是一个初始化块。
- 可以有很多个初始化块，按顺序先后且全部地执行，所以没什么必要分开，一起就完事。
- **声明实例变量时指定默认值**和**普通初始化块**都被看做是对象的初始化代码，按先后顺序执行。

- **初始化块总是在构造器之前被调用**。 
- 如果多个重载的构造器有相同且与传入形参无关的语句可以一起提入初始化块。
```java
public class NormalBlock {
    int a = 5;
    {
//        a = 6;
        System.out.println("初始化块之后的a为"+a);
    }
//    {
//        int a = 8;
//        System.out.println("初始化块中重新定义了一个a？"+a);
//    }
    NormalBlock(){

        System.out.println("构造器中赋a的值为"+a);
    }
}
class NormalTest{
    public static void main(String[] args) {
        new NormalBlock();
    }
}

```
- 上面注释语句时，结果如下：
```java
初始化块之后的a为5
构造器中赋a的值为5
```
> 可以看到，在这个例子中，声明实例变量指定默认值也被看作初始化代码，且依次执行，先初始化块，后构造器。（可以试着调换它们的位置验证一下哈）

- 上面解除注释语句之后，我对结果是产生疑惑的：

```java
初始化块之后的a为6
初始化块中重新定义了一个a？8
构造器中赋a的值为6
```
> **我的疑惑点在于，我一开始以为，我在第二个代码块中定义的和之前同名的变量a是同一个（然而并不是）这样也就算了，初始化代码之后，执行构造器时，调用了a，那么这时这个a调用的是哪个呢，于是产生疑惑，希望知道的小伙伴可以为我指点迷津**。

- 我在测试的时候还遇到了`ilegal forward reference`，即前向引用错误,如下图。
```java
    {
        age = 50;
        if(age>40) System.out.println("Father类的初始化块且age>40");
        System.out.println("Father类的第一个初始化块");
    }
    int age =20;
```
产生原因：是因为在还没有定义该变量时，就引用了它，所以为了避免这样的错误，尽量将初始化块放在成员变量声明之后。


## 静态初始化块
和普通的对应的就是**静态初始化块**啦，也就是用**static**修饰的，也称为**类初始化块**。根据名称分析，<u>类初始化块负责对类进行初始化，而普通初始化块负责对对象执行初始化。</u>

- 以`static{}`的格式包围对类变量的初始化。
- 由于静态初始化块和类相关，负责对类进行初始化，所以总是比普通初始化块先执行。
- 通常用于对类变量执行初始化处理，而不能对实例变量进行初始化处理。
- 静态初始化块属于类的静态成员，遵循静态成员不能访问非静态成员的规则：**即不能访问非静态成员（实例变量和实例方法）**。
- 类似地，**静态初始化块**和**声明静态成员变量时指定初始值**都时该类的初始化代码。


## 初始化块与构造器
关于**初始化块与构造器的先后调用顺序**，结合代码来理解一下子。
```java
package com.my.pac17;

/**
 * @auther Summerday
 */
public class A {
    {
        System.out.println("A.instance initializer");
    }
    static {
        System.out.println("A.static initializer");
    }
    public A() {
        System.out.println("A.A");
    }
}
class B extends A {
    {
        System.out.println("B.instance initializer");
    }
    static {
        System.out.println("B.static initializer");
    }
    public B() {
        System.out.println("B.B");
    }
    public B(String m) {
        this();
        System.out.println("B.B," + m);
    }
}
class C extends B {
    {
        System.out.println("C.instance initializer");
    }
    static {
        System.out.println("C.static initializer");
    }
    public C() {
        super("ccc");
        System.out.println("C.C");
    }
}
class BTest {
    public static void main(String[] args) {
        new C();
        System.out.println("*******");
        new C();
    }
}
```
```java
/*测试结果*/
A.static initializer
B.static initializer
C.static initializer
/*类初始化阶段，限制性最顶层父类的静态初始化块
然后依次向下，直到执行当前类的静态初始化块*/
A.instance initializer
A.A
B.instance initializer
B.B//调用B的无参构造器
B.B,ccc//调用B的带参构造器
C.instance initializer//最后执行C
C.C
/*对象初始化阶段，先执行最顶层父类的初始化块，
最顶层父类的构造器，然后依次向下，直到执行当前
类的初始化块、当前类的构造器*/
*******
//不用执行静态初始化语句
A.instance initializer
A.A
B.instance initializer
B.B
B.B,ccc
C.instance initializer
C.C
```
- static修饰的静态初始化块，**总是先被调用**，且在继承关系中，最早的父类中的静态初始化块先执行。
- 可以看到，第二次创建子类对象时，就没有再执行静态初始化块中的初始化，因为**三个类已经加载成功**。
- 普通初始化块和构造器的执行顺序为，普通初始化块在构造器之前执行，从最早的父类一直到当前类。