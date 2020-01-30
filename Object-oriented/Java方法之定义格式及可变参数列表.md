[TOC]

# Java方法之定义形式及可变参数

Java中的方法类似于面向过程程序设计中的函数，但与其不同的是，<u>Java中的方法不能独立存在，它属于类或对象。</u>既然方法属于类或对象，那么，**方法的调用者就必须是类或对象**。（当然，之后将会提到的同一个类中方法互相调用，实际上也是类或对象在作为调用者）
还是先上一段代码：
```java
package com.my.pac05;

/**
 * @author Summerday
 * @date 2019/12/1 15:30
 */
public class MethodTest {
    public static void main(String[] args) {
        //通过类直接调用static修饰的first()方法
        MethodTest.first();
        //MethodTest.second();错误
        //方法由static修饰，可以通过类直接调用static修饰的方法
        //创建实例对象，访问没有static修饰的普通方法
        MethodTest method = new MethodTest();
        method.second();
        //接收secondVal方法的返回值
        int secondValue = method.secondVal();
        System.out.println(secondValue);//10
        
        method.third(2);
        //接收thirdVal方法的返回值
        int thirdValue = method.thirdVal(20);
        System.out.println(thirdValue);//20
        
        method.fourth('a', 'b');
        method.fifth("hello","what's","your","name");
        System.out.println();
        method.sixth(new String[]{"hello","what's","your","name"});
    }

    //static修饰的静态方法
    public static void first(){
        System.out.println("static: no param...");
    }
    //无参数(无参无返）
    public void second() {
        System.out.println("no param..");
    }
    //无参有返
    public int secondVal(){
        return 10;
    }
    //一个参数（有参无返）
    public void third(int param1) {
        System.out.println("one param.. value is " + param1);
    }
    //有参有返
    public int thirdVal(int param1){
        return param1;
    }
    //两个参数
    public void fourth(char param1, char param2) {
        System.out.println("two params.. param1 is "
                + param1 + ",param2 is  " + param2);
    }
    //多个参数1
    public void fifth(String...params){
        System.out.println("*type...param*");
        for(String param:params){
            System.out.print(param+" ");
        }
    }
    //错误：public void fifth1(String...params,int a )
    //多个参数2
    public void sixth(String[] params){
        System.out.println("*type[] param*");
        for(String param:params){
            System.out.print(param+" ");
        }
    }
}
```
## 方法调用
### 使用static修饰的方法
```java
//通过类直接调用static修饰的fourth()方法
    MethodTest.first();
//也可以通过创建的对象调用static修饰的方法
    method.first();
```
```java
//static修饰的静态方法
    public static void first(){
        System.out.println("static: no param...");
}
```
- 属于这个类本身。
- 类和对象都可以调用。
- 类和对象调用的结果相同。
### 没有static修饰的方法
```java
//MethodTest.second();错误
//创建实例对象，访问没有static修饰的普通方法
    MethodTest method = new MethodTest();
    method.second();
```
```java
//无static修饰的普通方法
    public void second() {
        System.out.println("no param..");
    }
```
- 属于类的对象，不属于类本身。
- 只能由对象作为调用者。
- 使用不同对象调用同一个方法，结果可能不同。

## 方法的定义格式
### 无参无返
```java
//无参无返
    public void second() {
        System.out.println("no param..");
    }
```
### 无参有返
```java
 //无参有返
    public int secondVal(){
        return 10;
    }
```
### 有参无返
```java
//有参无返
    public void third(int param1) {
        System.out.println("one param.. value is " + param1);
    }
```
### 有参有返
```java
 //有参有返
    public int thirdVal(int param1){
        return param1;
    }
```
## 形参个数可变的方法
### 采用数组形参来定义
<u>把数组作为形参加入形参列表中</u>。类似于我们用的最多的`public static void main(String[] args)`相当于main方法接收一个String类型的数组，也就是命令行参数，关于命令行参数，就暂时不提啦。
```java
//多个参数2
    public void sixth(String[] params){
        System.out.println("*type[] param*");
        for(String param:params){
            System.out.print(param+" ");
        }
    }
```
-  只能接收数组。
- 可以出现在形参列表的任意位置。
  
### 采用JDK1.5新特性来定义    
JDK1.5之后,可以在**最后一个参数类型和参数名**之间加上`...`
```java
 //多个参数1
    public void fifth(String...params){
        System.out.println("*type...param*");
        for(String param:params){
            System.out.print(param+" ");
        }
    }
    //需要在最后一个参数类型和参数名之间！！！
    //错误：public void fifth1(String...params,int a )
```
- 表明可以接收多个参数值，多个参数值被当作数组传入。
- 传入参数形式**可以是多个参数以‘，’相隔，也可以传入数组。**
- **只能处于参数列表之后**，所以一个方法中最多只能有一个长度可变的形参。