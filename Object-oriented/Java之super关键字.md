[toc]
# Java之super关键字
话不多说，直接上代码：
```java
package com.my.pac14;

/**
 * @auther Summerday
 */
public class SuperTest {
    public static void main(String[] args) {
        SubClass sb = new SubClass(20);
        //创建子类的对象，调用子类的print方法
        sb.print();
    }
}

class BaseClass {
    public int a = 5;
    public int b;
    static public int c = 30;

    BaseClass(int b) {
        this.b = b;
        System.out.println("superb:" + this.b);
    }

    public void print() {
        System.out.println("父类的a = " + a);
        //System.out.println(this.a);
    }

    public void read() {
        System.out.println("read..");
    }
}

class SubClass extends BaseClass {
    public int a = 10;
    //子类中的实例变量将父类中的隐藏，所以下面的this.b=0
    public int b;
    static public int c = 40;
    
    SubClass(int b) {
    //调用父类的构造器
        super(b);//20
        System.out.println("superb:" + super.b + "，subb:" + this.b);//0
    }

    public void print() {
        //打印父类中的a=5
        System.out.println(super.a);//5
        //调用父类的print方法，打印a=5
        super.print();//5
        //直接打印a，会打印出当前类的a，打印子类的a=10
        System.out.println("子类的a = " + a);
        //类变量也会覆盖
        System.out.println(c);
        //用父类名访问父类的类变量
        System.out.println("父类中的类变量c = " + BaseClass.c);
        //没有重写，不需要使用super调用父类方法
        read();
    }

}

```
super用于限定该对象调用从父类继承得到的实例变量或方法，因此和this相同的是，**super不能出现在static修饰的方法中**。（因为static修饰的方法属于类，调用者将会是类，而不是一个对象）

## 调用父类成员变量
- 实例变量
可以直接利用`super.xx`限定访问父类的实例变量。
```java
//父类
public int a = 5;
//子类
public int a = 10;
//在子类中访问父类的实例变量
public void print() {
    //打印父类中的a=5
    System.out.println(super.a);//5
    //直接打印a，会打印出当前类的a，打印子类的a=10
    System.out.println("子类的a = " + a);
}
```
- 类变量
如果父类变量是用static修饰的类变量，则需要用`父类类名.xx`限定访问其类变量。（虽然可以使用super访问父类中的类变量，就像我们之前说的用类的实例访问类变量，但是极不规范，不建议。）

```java
//父类
static public int c = 30;
//子类
static public int c = 40;
//在子类中访问父类的类变量
public void print() {
    //类变量也会隐藏
    System.out.println(c);
    //用父类名访问父类的类变量
    System.out.println("父类中的类变量c = " + BaseClass.c);
} 
```
**总结**：
- 子类中没有包含和父类同名的成员变量，就无需使用super或父类名显式调用。
-  如果在没够方法中访问某成员变量，但没有显式指定调用者，则查找该成员变量的顺序：
    - 查找该方法中有没有。
    - 查找该类中有没有。
    - 查找父类中有没有。
    - ……
- 如果没有找到，就会报错。
>当创建一个子类对象时，<u>系统不仅会为该类中定义的实例变量分配内存，也会为它从父类继承得到的所有实例变量分配内存</u>，即使子类定义了与父类中同名的实例变量。所以，子类中定义与父类同名的实例变量并不会完全覆盖父类中定义的实例变量，**只是简单地隐藏**。


## 调用父类的方法
我们之前说过，子类中定义与父类同名且参数列表相同的实例方法（static方法不能被重写）就是所谓的**方法重写**，此时子类的方法会覆盖父类的方法。
- 在子类中想调用父类的同名实例方法，可以利用：`super.父类方法名`；
- 如果是类方法，则可以用`父类名.父类方法名`调用。（super依旧可以，但不建议）
- 如果没有同名，就不需要显式用super调用父类方法。

```java
//父类
public void print() {
    System.out.println("父类的a = " + a);
    //System.out.println(this.a);
}
public void read() {
    System.out.println("read..");
}
//子类
 public void print() {
    //调用父类的print方法，打印a=5
    super.print();//5
    //没有重写，不需要使用super调用父类方法
    read();
}
        
```
## 调用父类构造器
关于子类中利用super关键字调用父类构造器，在上一篇中，已经做出总结，具体可参看：[Java继承之再谈构造器](https://www.cnblogs.com/summerday152/p/12041632.html)

参考书籍：《Thinking in Java》、《疯狂Java讲义》