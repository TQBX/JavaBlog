[toc]

# Java继承之方法重写
在Java继承中，子类可以获得父类所有的结构，也可以增加与父类不同的属性和方法。但是，有一种情况，一个相同的方法，子类的行为并不像父类那样，这时，就需要**重写父类的方法**，下面是重写的代码实现：
## 代码体现
```java
package com.my.pac12;

/**
 * @author Summerday
 * @date 2019/12/11 21:26
 */

public class Person {
    public void say(){
        System.out.println("say something..");
    }
    public static void see(){
        System.out.println("see something..");
    }
    public int returnNumber(int number){
        return number;
    }
    private void run(){
        System.out.println("running ..");
    }
    public Person returnPerson(){
        return this;
    }
}
class student extends Person{
    //方法名相同，形参列表相同
    public void say(){
        System.out.println("student say something..");
    }
    //返回类型为父类或父类的子类
    public Person returnPerson(){
        System.out.println("子类返回类型可以是父类返回类型或者是其子类类型");
        return this;
    }
    //并不是重写，只是重新定义了新方法
    public void run(){
        System.out.println("student is running..");
    }
    //不是重写，而是发生在父类与子类之间的重载
    public int returnNuber(int number,int otherNumber){
        return number+otherNumber;
    }
    public static void main(String[] args) {
        student s = new student();
        s.say();
        student.see();
        s.see();
        s.run();
        //涉及向上转型
        Person sn = s.returnPerson();
        //调用的是父类的方法
        System.out.println(s.returnNumber(5));
        //调用子类重载父类的方法
        System.out.println(s.returnNuber(5,5));

    }
}
```
## 概念
- <u>子类包含与父类同名，同参数列表的现象就是**方法重写(Override)**，也叫方法覆盖</u>。

## 注意事项

### "两同两小一大"
- **方法名相同，形参列表相同**。
- 子类方法**返回值类型小于等于**父类方法返回值类型。
```java
//父类
public int returnNumber(int number){
    return number;
}
/*基本类型：子类返回值类型小于等于父类返回值类型，下面的语句不允许*/
//子类
public long returnNumber(int number)
```
```java
//父类
public void say(){
    System.out.println("say something..");
}
/*void类型只能由同样void类型的方法重写*/
//子类
public void say(){
    System.out.println("student say something..");
}
```
```java
//父类
public Person returnPerson(){
    return this;
}
/*引用类型：子类返回值类型需要与父类相同或者是父类的子类*/
//子类
public Person returnPerson(){
    System.out.println("子类返回类型可以是父类返回类型或者是其子类类型");
    return this;
}
```
- 子类方法声明抛出的**异常类小于等于**父类抛出的异常类。(这个部分之后再进行补充)
- 子类方法的**访问权限大于等于**父类方法的访问权限。
```java
//父类
public void say(){
    System.out.println("say something..");
}
/*子类方法的访问权限大于等于父类方法的访问权限，下面的语句不允许*/
//子类
private(protected or 缺省) void say()
```

### 其他注意点
- 两者必须同为类方法或者同为实例方法。（**实际上类方法无法被重写，只是单纯被隐藏起来**，关于static关键字之后再总结）
```java
//父类
public static void see(){
    System.out.println("see something..");
}
/*两者必须同为类方法(static修饰)或者同为实例方法，下面的语句不允许*/
//子类
public void see()
```
- 父类被private修饰，子类无法重写父类。
```java
//父类
private void run(){
    System.out.println("running ..");
}
/*子类无法重写方法，下面的语句是假象，其实是重新定义了一个新方法*/
//子类
public void run(){
    System.out.println("student is running..");
}
```
## 重写与重载
- 重载主要（对！是主要）发生在**同一个类的多个同名方法之间**，且参数列表不同。
为什么说是主要呢，因为重载有可能在子类与父类之间发生，如下：
```java
//父类
public int returnNumber(int number){
    return number;
}
/*发生在父类与子类之间的重载*/
//子类
public int returnNuber(int number,int otherNumber){
    return number+otherNumber;
}
```
 - 重写是发生在**父类与子类同名方法之间**，且参数列表相同。

 ## @Override注解

**@Override注解**对方法重写起到辅助作用，并不会对代码本身产生影响。
- 标注该注解，向人表明下面的方法将要重写父类的某些方法。
- 标注该注解，向机器表明下面部分将要重写，让机器帮忙检查错误。如果不是重写，那么就会产生让人不舒服的提示，如图所示。

![ke98bF.png](https://t1.picb.cc/uploads/2019/12/12/ke98bF.png)

上图也验证了三种不是方法重写的例子：
- static修饰的父类方法。
- private修饰的父类方法。
- 子类重载而非重写父类方法。