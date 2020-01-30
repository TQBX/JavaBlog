[toc]
# Java面向对象之异常【一】

终于完成本学期的最后一门考试，考试周的我，边复习通信之傅里叶变换，边学习Java的新知识。虽然很久没更，但是私底下的笔记满满，特地总结一波。
总结什么呢？异常！嗯？异常？最近倒是人有些异常……复习到一两点，早上早早起来刷题，不异常才怪。异常嘛，很好理解，就是本该正常的事情出了问题嘛。
这时我们回想我们之前写过一个再简单不过的例子：计算两个整数相除。二话不说，直接就可以写出如下代码，对吧。

```java
public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    System.out.println("Enter two integers: ");
    int num1 = input.nextInt();
    int num2 = input.nextInt();
    System.out.println(num1 / num2);
}
```
这短短的代码中就存在着隐患，而这个隐患有可能就会演变成异常。什么情况呢？我们知道，在Java的整数计算中，如果除数为零，计算式将会是没有任何意义的。带着我们的猜想，编译运行，不出所料，它飘红了。
![7229c5f38e87c9abeacccef7d82c708a.png](en-resource://database/6223:1)
于是，我们对红字进行分析，得出结论：一个名为ArthmeticException的异常在执行main方法的第17行时发生：除数为零。清清楚楚，明明白白。
可以看到，异常一旦发生，程序就将终止，而且这种抛出异常的机制，能够让我们有效地找到问题所在，并及时解决问题。异常机制的存在，就是为了更好地解决问题，保证程序的健壮性。
所以，我们试图修改代码，让它在方法中实现：
```java
public static int quotient(int num1, int num2) throws ArithmeticException{
    if (num2 == 0) {
        throw new ArithmeticException("Divisor can not be zero");
    }
    return num1 / num2;
}
```
在main方法中调用查看结果：
```java
int num1 = input.nextInt();
int num2 = input.nextInt();
try {
    System.out.println(QuotientWithMethod.quotient(num1, num2));
}catch (ArithmeticException e) {
    System.out.println(e.getMessage());
}
```
继续测试（当然为了测试，我们这里抛出了一个运行时异常，本可以不抛）：
![131e2d9ee470ad07420de8b25fb400db.png](en-resource://database/6329:1)
- 在求两数之商的方法中主动抛出（throw）一个异常，`throw new ArithmeticException("Divisor can not be zero");`对象。
- 在方法定义处声明方法将会抛出的异常类型，`throws ArithmeticException`。
- 在调用该方法时捕获方法，`try{...}catch (ArithmeticException e){...}`并执行我们希望处理异常的语句，`System.out.println(e.getMessage());`。

当然，上述的例子集抛出异常、声明异常及处理异常于一身，但是异常的内容可不仅仅是这么简单，我们在之后的内容中来探一探异常的究竟：
## 异常的继承体系
先来看一下异常的类继承图，当是不完全统计的，因为还有还多好多的异常等待着被探索。
![734466aefcf6d84b6e8ddc1cec93a019.png](en-resource://database/5998:1)
通过图片我们可以明显的发现，这些异常的命名非常好认，果真见名知义。`Throwable`是所有异常的顶级父类，在它的下面有两个大类：`Exception`和`Error`。下面是官方文档对两者进行的解释：
###  Error
- <u>合理的应用程序出现的不应该捕获的严重问题</u> ，合理指的是语法和逻辑上。

- 不能处理，不要尝试用**catch捕获**，只能尽量优化。
- 它及它的子类异常不需要在**throws语句**中声明，不需要说明他们将会被抛出。
- 它们属于**不受检异常**（unchecked exceptions）。
- 与虚拟机相关，如系统崩溃、虚拟机错误、动态链接失败等



### Exception
- 与**Erro**r不同的是，**Exception**代表的是一类<u>在合理的应用程序中出现的可以处理的问题。</u>
- 除了特例**RuntimeException**及其子类之外，**Exception**的其他异常子类都是**受检异常**（checked exceptions）。

## 异常是否受检

### unchecked exceptions（不受检异常）
也叫**运行时异常**，由Java虚拟机抛出，**只是允许编译时不检测**，在没有捕获或者声明的情况下也一样能够通过编译器的语法检测，所以不需要去亲自捕获或者声明，当然要抛出该类异常也是可以的。典型的异常类型：**Error及其子类异常**，**RuntimeException及其子类异常**。注意：RuntimeException代表的是一类编程错误引发的异常：算数异常、空指针异常、索引越界异常、非法参数异常等等，这些错误如果代码编写方面没有任何漏洞，是完全可以避免的，这也是不需要捕获或者声明的原因，也**有助于简化代码逻辑**。
### checked expections（受检异常)
也叫**编译时异常**，Java认为受检异常需要在编译阶段进行处理， 必须在显式地在调用可能出现异常的方法时捕获（catch），或者在声明方法时**throws**异常类型，否则编译不会通过。除了上面提到的Exception及其子类都属于受检异常，当然，不包括上面提到的**RuntimeException**。

## 异常的处理方式
我们上面提到，我们无法去直接处理不受检异常，但是我们必须强制地对可能发生受检异常（**编译时异常**）的行为做处理，处理方法主要有以下：

- 在当前方法中不知道如何处理该异常时，直接在方法签名中throws该异常类型。
```java
    public void m1() throws ClassNotFoundException, IOException {
        //to do something
    }
```
- 在当前方法中明确知道如何处理该异常，就使用`try{...}catch{...}`语句捕获，并在**catch块**中处理该异常。
```java
public void m3() {
        try {
            m1();
        } catch (ClassNotFoundException e) {
            //to do something
        } catch (IOException e) {
            //to do something
        }
    }
    
```
也可以直接捕获Exception的实例，因为它是这些异常的父类，但是上面的捕获异常引发的错误会更加直接一些。
```java
    public void m2() {
        try {
            m1();
        } catch (Exception e){
            //do something
        }
    }
```
**如果处理不了，就一直向上抛，直到有完善解决的办法出现为止**。当然我们也可以自行抛出系统已经定义的异常：
```java

    public void m2(int i) throws IOException, ClassNotFoundException {
        if (i >1)
            throw new IOException("!");
        throw new ClassCastException();
    }
```
需要注意的是：
- 用throw语句抛出**异常的实例**！注意是异常的实例！且一次只能抛出一个异常！
- throw和throws是不一样的！睁大眼睛！一个是抛出异常实例，一个是声明异常类型。
- 创建异常实例可以传入字符串参数，将被显示异常信息。

## 自定义异常
我们知道，程序发生错误时，系统会自动抛出异常。那么，我们如果想独家定制一个属于自己的异常可以不？答案显然是可以的，情人节快到了，直接自定义一个异常：
```java
    class noGirlFriendException extends Exception{
        noGirlFriendException(){
        }
        noGirlFriendException(String msg){
            super(msg);
        }
    }
```
需要注意的是：
- 如果自定义异常继承RuntimeException，那么该异常就是运行时异常，不需要显式声明类型或者捕获。
- 如果继承Exception，那么就是编译时异常，需要处理。
- 定义异常通常需要提供两个构造器，**含参构造器传入字符串，作为异常对象的描述信息**。


## 异常的捕获方式
- 每个异常**分别对应一个catch语句**捕获。
- 对所有异常处理方式相同，用父类接收，向上转型,Exception对应的catch块应该放在最后面，不然的话，在他后面的块没有机会进入处理。下面就是一个典型的错误示范：
```java
//下面的形式错误！
public static void main(String[] args) {
    try {
        System.out.println(1 / 0);
    } catch (Exception e) {
        e.printStackTrace();
    } catch (ArithmeticException e) {
        System.out.println(e.getMessage());
    }
}
```
- JDK1.7之后，可以分组捕获异常。
    - 异常类型用竖线分开。
    - 异常变量被隐式地被final修饰。
```java
try {
    System.out.println(1 / 0);
} catch (ArithmeticException | NullPointerException e) {
    //捕获多异常时，异常变量被final隐式修饰
    //！false: e = new ArithmeticException();
    System.out.println(e.getMessage());
} 
```
本文参考诸多资料，并加上自身理解，如有叙述不当之处，还望评论区批评指正。关于异常，还有一部分内容，下篇进行总结，晚安。
参考资料：
[https://stackoverflow.com/questions/6115896/understanding-checked-vs-unchecked-exceptions-in-java](
https://stackoverflow.com/questions/6115896/understanding-checked-vs-unchecked-exceptions-in-java)

[https://www.programcreek.com/2009/02/diagram-for-hierarchy-of-exception-classes/](
https://www.programcreek.com/2009/02/diagram-for-hierarchy-of-exception-classes/)

[https://stackoverflow.com/search?q=%5BJava%5D+Exception](
https://stackoverflow.com/search?q=%5BJava%5D+Exception)