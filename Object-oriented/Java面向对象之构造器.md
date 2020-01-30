# Java面向对象之构造器


## 利用构造器确保初始化

> 初始化问题是关系编程方式是否安全的一个重要的问题。

 **功能**：在创建对象时执行初始化。

 **在Java中，每个类至少有一个构造器**。格式如下：
  ```java 
 [修饰符] 构造器名(参数列表){
    ...执行体
    }
  ```
 这时，突然迷惑，之前写过的代码里都没有构造器的说法呀，是怎么回事呢？

先看下面的语句：
 ```java
 Student s0 = new Student();
 ```
这是我们之前经常写的，我们管他叫做创建对象，并让引用变量指向这个对象的过程（类的实例化）。
在执行`new Student();`语句时，系统就会为对象分配内存空间，并调用相应的构造器，执行响应的执行体，确保对象在被我们操作之前，就已经正确初始化。

可是构造器在我们之前的代码中，还是没有出现构造器的影子啊。这是因为，我们在设计类时，<u>如果没有定义任何的构造器的话， 系统默认为类提供默认构造器（即无参构造器），执行体为空。</u>

**注意**：
- 对象在构造器执行体之前就已经被建立，只不过这个对象只能在内部被**this**（关于this的用法之后会有专门的篇幅总结）引用。
- 只有在构造器执行体结束之后才作为返回值被返回。
- new表达式确实返回了新建对象的引用，但构造器本身是没有返回值的。

**关于构造器的访问修饰符**：

- 如果允许系统中任何位置的类来创建该类的对象，修饰符需要public。
- 如果想让子类调用，设置为protected。
- 如果不想让别人创建该类的实例，设置为private。

**其他**：
- **构造器名必须与类名相同**，这一点表明普通方法的命名规范并不适用于构造器。
  
 - 构造器是一种特殊的方法，但没有返回值。
 - 系统默认为类提供默认构造器（即无参构造器），执行体为空。

 - 可以自定义构造器，改变默认初始化。

-  一旦自定义构造器之后，系统默认的无参构造器就不再存在。

 ## 构造器重载

 假如，我们想要用多种方式创建对象，那么我们需要设计多种构造器，但是构造器名必须和类名相同，那么就自然而然地引出构造器的重载。

 **构造器重载**：同一个类中具有多个构造器，构造器形参列表不同，即为构造器重载。

 构造器重载代码演示：
 ```java
package com.my.pac09;

/**
 * @author Summerday
 * @date 2019/12/6 18:23
 */
public class Student {
    //暂且先把属性设为public
    public String name;
    public int grade;

    //重新定义默认构造器，假如输出语句
    public Student() {
        System.out.println("创建了一个学生");
    }
    //this的用法之一
    public Student(String name) {
        this.name = name;
        System.out.println("创建了一个名为" + this.name + "的学生");

    }

    public Student(String name, int grade) {
    //调用另一个构造器
        this(name);
        this.grade = grade;
        System.out.println("创建了一个名为" + this.name + "的学生，" + this.grade + "年级");
    }
}

 ```
**重载构造器调用**：系统通过new调用构造器时，根据传入的实参列表决定到底用哪个构造器。
```java
package com.my.pac09;

/**
 * @author Summerday
 * @date 2019/12/6 18:29
 */
public class StudentTest {
    public static void main(String[] args) {
        //用new创建对象，调用新定义的无参构造器
        Student s0 = new Student();
        //两个属性值默认初始化
        System.out.println(s0.name+","+s0.grade);//null

        //调用重载构造器，为name赋值
        Student s1 = new Student("小明");
        System.out.println(s1.name+","+s1.grade);//小明

        Student s2 = new Student("小红",2);
        System.out.println(s2.name+","+s2.grade);//小红

    }
}

```
```java
//输出
创建了一个学生
null,0
创建了一个名为小明的学生
小明,0
创建了一个名为小红的学生
创建了一个名为小红的学生，2年级
小红,2
```
关于关键字**this**（还有些用法在下一篇一起总结）：

- `this.name = name;`表示把形参name赋值给正在调用构造器方法的对象的name属性赋值。`this`在这里表示正在调用方法的对象。
- `this(name);`表示在该**构造器中调用另一个构造器**，且this语句必须出现在执行体的第一句，也就是说一个构造器里不能调用两次其他构造器。