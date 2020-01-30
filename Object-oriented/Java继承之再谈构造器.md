[toc]

 # Java继承之再谈构造器

 ## 初始化基类
 前面提到，继承是子类对父类的拓展。《Thinking in Java》中提到下面一段话：
 > 当创建一个导出类的对象时，该对象包含了一个基类的子对象。这个子对象与你用基类直接创建的对象是一样的。二者区别在于，后者来自于外部，而基类的子对象被包装在导出类的对象内部。

我们在创建子类对象时，调用了父类的构造器，甚至父类的父类构造器。我们知道，构造器用于创建对象，那么突然产生疑惑：<u>关于创建一个子类对象时，是否会先创建父类对象？</u>
**经过查找资料，得出结论**：
**并没有**。在创建子类对象时，会把父类的成员变量和方法加载进内存，既然要加载，便调用父类构造器看看这些数据是如何进行初始化的，仅此而已，并不是创建了父类的对象。
所以，可以看作，子类对象中包含着父类的子对象。我们知道，对象的初始化是至关重要的。那么，这个父类的子对象如何正确初始化呢？对了，就是接下来要说的：**在构造器中调用基类构造器来执行初始化**。
<u>注意：子类并不能继承父类的构造器，只是单纯调用了基类构造器中的初始化代码。</u>
## 默认构造器
先看一段简单的测试代码：

```java
package com.my.pac13;
/*继承中的构造*/
public class Person {
    Person(){
        System.out.println("Person()");
    }
}
class Student extends Person{
    Student(){
        System.out.println("Student()");
    }
}
class PrimaryStudent extends Student{
    PrimaryStudent(){
        //super();
        System.out.println("PrimaryStudent()");
    }
    public static void main(String[] args) {
        //创建了PrimaryStudent对象
        new PrimaryStudent();
    }
}
/*
 Person()
 Student()
 PrimaryStudent()
*/
```


关于构造器，我们前面提到，任何没有显式构造器的类都存在着一个无参数的默认构造器。我们上面的例子在默认构造器中加入了打印输出，以便理解。
可以看到的是：
- 在创建`PrimaryStudent`时，他的直接父类`Student`和间接父类`Person`中的构造器都被调用了，而且可以看到，是"**自上而下**"的。
- 父类在子类构造器可以访问它之前，就已经完成了初始化的操作。
- 若子类没有显式调用父类的构造器，则自动调用父类的默认（无参）构造器。
## 带参数的构造器
前面的代码中，每个类都含有默认的构造器，创建子类对象时，是自上而下，且子类会默认调用父类的无参构造器。那么，假设父类正好没有无参构造器或者你正想调用父类的带参构造器，这时就需要我们的**super**关键字。（super关键字之后还会进行总结）
我们直接在原来的基础上稍作修改，并进行测试。
```java
package com.my.pac13;
/*调用基类构造器是子类构造器中要做的第一件事*/
public class Person {
    //没有默认构造器
    Person(String name){
        System.out.println("Person()\t"+name);
    }
}
class Student extends Person{
    //也没有默认构造器，且用super显式调用
    Student(String n){
    //super关键字调用父类的构造器
        super(n);
        System.out.println("一参数Student\t"+n);
    }
    Student(String n,String m){
    //this关键字调用同一类中重载的构造器
        this(n);
        System.out.println("二参数student()\t"+m);
    }
}
class PrimaryStudent extends Student{
    //隐式调用父类构无参数构造器,但是父类没有，所以要用super显式调用
    PrimaryStudent(){
    //没有下面的语句会报错
        super("hello");
        System.out.println("PrimaryStudent（）");
    }

}
class ExtendsTest{
    public static void main(String[] args) {
        new Person("the shy");
        System.out.println("***********");
        new Student("rookie");
        System.out.println("***********");
        new Student("the shy","rookie");
        System.out.println("***********");
        new PrimaryStudent();
        System.out.println("***********");
    }

}
/*
Person()    the shy
***********
Person()    rookie
一参数Student  rookie
***********
Person()    the shy
一参数Student  the shy
二参数student()    rookie
***********
Person()    hello
一参数Student  hello
PrimaryStudent（）
***********
 */
```
- this是正在创建的对象，用于**调用同一类中重载的构造器**，可以参看我之前的文章：[Java关键字之this](https://www.cnblogs.com/summerday152/p/12005295.html)。
- super在调用构造器时，使用方法和this相似。（但super和this本身有本质的不同，**super并不是一个对象的引用**！！！）
- super和this语句都必须出现在第一行，也就是说一个构造器中**只能有其中之一**。


## 子类调用父类构造器
无论是否使用super语句来调用父类构造器的初始化代码，**子类构造器总是会事先调用父类构造器**！这是一定要记住的！

- 子类构造器A在第一行显式使用super调用父类构造器B，格式`super(参数列表)`,根据参数列表选择对应的父类构造器。
```JAVA
//父类
 Person(String name){
        System.out.println("Person()\t"+name);
    }
//子类
 Student(String n){
    //super关键字调用父类的构造器
        super(n);
        System.out.println("一参数Student\t"+n);
    }
```
- 子类构造器A先用this调用本类重载的构造器B，然后B调用父类构造器。
```java
//父类
 Person(String name){
        System.out.println("Person()\t"+name);
    }
//子类
Student(String n){
    //super关键字调用父类的构造器
        super(n);
        System.out.println("一参数Student\t"+n);
    }
Student(String n,String m){
//this关键字调用同一类中重载的构造器
    this(n);
    System.out.println("二参数student()\t"+m);
}
```
- 子类构造器中没有super和this时，系统会隐式调用父类的无参构造器，要是没有无参的，那就报错。
```java
//隐式调用父类构无参数构造器,但是父类没有，所以要用super显式调用
PrimaryStudent(){
//没有下面的语句会报错
    super("hello");
    System.out.println("PrimaryStudent（）");
}
```
**综上所述**：

当调用子类构造器对子类对象进行初始化时，**父类构造器总会在子类构造器之前执行**。甚至，父类的父类会在父类之前执行……一直追溯到所有类的超类Object类的构造器。