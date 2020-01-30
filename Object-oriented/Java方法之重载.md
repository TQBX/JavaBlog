# Java方法之重载
本篇探究Java中的方法重载。那么，什么是**重载**呢？先上一串代码：
```java
package com.my.pac06;

/**
 * @author Summerday
 * @date 2019/12/2 19:46
 */
public class OverloadTest {

    public static void main(String[] args) {
        Overload overload = new Overload();
        //调用public void test()：无参数
        overload.test();
        //调用 public void test(String arg):arg= overloading...
        overload.test("overloading...");
        //调用public void test(int arg)：arg= 10
        overload.test(10);
        //调用形参长度可变的方法
        overload.test(new int[]{10});
        //调用形参长度可变的方法
        overload.test(1, 2, 3, 4, 5);

    }

}

class Overload {
    public void test() {
        System.out.println("调用public void test()：无参数");
    }

    public void test(String arg) {
        System.out.println("调用 public void test(String arg):arg= " + arg);
    }

    //false:public int test(){}
    //false: private void test(){}
    public void test(int arg) {
        System.out.println("调用public void test(int arg)：arg= " + arg);
    }

    //false:public void test(int par)
    //包含参数长度可变的情况
    public void test(int... args) {
        System.out.println("调用形参长度可变的方法");
    }

}
```
> 我们知道，一个方法的创建包含了 修饰符，返回类型，方法名，参数列表等多项，**只要同一个类中，两个或两个以上的方法的方法名相同，参数不同**，就出现了**重载**现象。

方法名相同很好理解，参数不同指的是可以是**参数类型不同**，也可以是**参数个数不同**。如下：
```java
//方法名相同，参数类型、个数均不同
public void test(){};
public void test(int arg){};
//方法名相同，参数类型不同
public void test(int arg){};
public void test(String arg){};
//方法名相同，参数个数不同
public void test(int arg){};
public void test(int... args){};
```
但**参数名不同不**算！！！如下：
```java
//不能构成重载，因为方法名和参数类型个数均相同
public void test(int arg){};
public void test(int par){};
```
**仅仅返回值类型不同，不可以重载。** 如下：
```java
//仅仅返回值类型不同，不可重载
public void test(){}；
public int test(){}；
```
仅仅修饰符不同，也没用，如下：
```java
//仅仅修饰符不同，不可重载
public void test(){};
private void test(){};
```
如果重载情况中同时出现形参可变和指定形参个数的情况，如果目标个数明确，那么直接锁定目标；如果不是才考虑形参可变的情况，跟定义的位置无关。
```java
public void test(int arg) {
        System.out.println("调用public void test(int arg)：arg= " + arg);
    }
    //包含参数长度可变的情况
public void test(int... args) {
    System.out.println("调用形参长度可变的方法");
}
```
```java
 //调用public void test(int arg)：arg= 10
overload.test(10);
 //调用形参长度可变的方法
overload.test(1, 2, 3, 4, 5);
```
注：如果上述情况非要传入一个数，且让他执行形参长度可变的那个方法，可以将他转换为存储一个数的数组，可以尝试如下方法：
```java
//调用形参可变的方法
overload.test(new int[]{10});
```
但是大部分时候，都不建议重载参数长度可变的方法，没必要。

---

**关于为什么仅仅返回值类型不同不能构成重载条件的原因：**

Java中方法在调用时，可以忽略方法的返回值，也就是说，`public int test(){}`和`public void test(){}`两个方法在调用的时候，都只需要写上`test();`，并没有提及返回值，而造成迷惑。