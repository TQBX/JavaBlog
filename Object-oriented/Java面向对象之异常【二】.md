[toc]
# [Java面向对象之异常【一】]()
# Java面向对象之异常【二】
> 往期回顾：上一篇我们大致总结了异常的继承体系，说明了Exception和Error两个大类都继承于顶级父类Throwable，又谈到编译时异常与运行时异常的区别，谈到异常的处理方式，以及处理方式中关于捕获方式的几种类型。
> 本篇承上启下，将从异常的其余部分进行总结，但是毕竟现在处于初学阶段，未必能够体会异常在真实场景中运用的便利之处，所以本文只是对目前所学内容的归纳整理，后续新的体会将会及时更新。
## 捕获异常的规则
- 在执行try块的过程中没有出现异常，那么很明显，**没有异常当然就会跳过catch子句**。
- 相反，如果抛出了一个异常，那么就会**跳过try中的剩余语句**，开始查找处理该异常的代码。以下是查找处理异常代码的具体过程：
>  - 从当前方法开始，沿着方法的调用链，按照异常的反向传播方向找到异常的处理代码。
>   - 从第一个到最后一个检查catch块，判断是否相匹配。如果是，那么恭喜！直接进入catch块中执行处理异常语句；如果不是，就将该异常传给方法的调用者，在调用者中继续执行相同步骤：匹配就处理，不匹配就向上传……
>   - 直到最后如果都没有找到的话，程序将会终止，并在打印台上打印出错信息。
>  如果是相对单一方法而言，其实是很简单的；如果方法层层嵌套呢，情况又是咋样的呢，咱们来验证一下以上内容：
```java
//主方法
public static void main(String[] args) {
    try{
    //调用m1()
        m1();
        System.out.println("ExceptionMyDemo.main");
    }catch (Exception e){
        System.out.println("ExceptionMyDemo.main.catch");
    }
}
//m1()
private static void m1(){
    try{
    //调用m2()
        m2();
        System.out.println("ExceptionMyDemo.m1");
    }catch (NullPointerException e){
        System.out.println("ExceptionMyDemo.m1.catch");
    }
}
//m2()
private static void m2(){
    String str = null;
    System.out.println(str.hashCode());
}
//测试结果：
ExceptionMyDemo.m1.catch
ExceptionMyDemo.main
```
- 可以看到，m1中捕获了m2抛出匹配的空指针异常类型，直接处理，在main方法中就接收不到异常，也就正常执行。
- 假如我们把m1的catch的异常类型换成其他的类型，比如`catch (ArithmeticException e)`，这时的测试结果会是这个样子：
```java
//更改之后的测试结果：
ExceptionMyDemo.main.catch
```
因为m2()抛出的异常在m1中并没有被合适地处理，所以向上抛出，在main方法中找到了处理方法，遂执行处理语句。
- 依据我们上面所说，如果在上面更改之后的基础上，再把main方法中的处理异常语句删去，那么程序运行的结果会是啥呢？哦，不出所料，是下面的结果：
![d5d7d0df6396bf0c3995bdf1be70fcf7.png](en-resource://database/6333:1)

因为抛出的异常没人处理，它就会在控制台上打印异常的栈轨迹，关于抛出的异常信息，我们接下来进行详细分析。
## 访问异常信息
我们提到，无论是虚拟机抛出异常还是我们主动抛出，异常的错误信息都包含其中，以便于我们得知并更好地处理异常，那么顺着上面所说，我们刚刚看到的就是异常的栈轨迹：


![4725225c87dcba438cb01a3d191bc3d1.png](en-resource://database/6337:1)


- `public void printStackTrace()`：默认将该Throwable对象及其调用栈的跟踪信息打印到标准错误流。
- `public String getMessage()`：返回描述异常对象信息的字符串。
- `public String toString()`:异常信息message为空就返回异常类的全名，否则返回`全名：message`的形式。
- `public StackTraceElement[] getStackTrace()`：返回栈跟踪元素的数组，表示和该异常对象相关的栈的跟踪信息。

## 异常对方法重写的影响
- 异常对方法重载没有影响。
- 方法重写时，**子类中重写的方法抛出的编译时异常不能超过父类方法抛出编译时异常的范围**。

## finally详解
名言警句：**无论异常是否会发生，finally修饰的子句总是会被执行**。

于是我们进行了简单的尝试：

```java
public static void m2(){
    try{
        System.out.println("0");
        System.out.println(1/0);
        System.out.println("1");
    }
    catch (Exception e){
        System.out.println("2");
    }
    finally {
        System.out.println("3");
    }
    System.out.println("4");
}
//测试结果
0 2 3 4 被打印在控制台上
```
- 可以看到1没有被打印，因为在执行` System.out.println(1/0);`时发生了异常，于是进入catch块，finally子句必会被执行，然后执行try语句后的下一条语句。
- 想象以下：假如把接收异常的实例类型改为另外一个不匹配的类型的话，也就是说无法正常捕获，结果又会如何呢？结果如下：
![25b131a70efce43bb505fdfa92bafd7f.png](en-resource://database/6339:1)
- 很明显，这时候finally的效果就出来了，就算你出了异常，我finally块中的语句必须要执行，这个在现实场景中对于**释放资源**起了很关键的作用，但是具体来说，由于还没有学习后面的内容，就暂且不提了，有些东西还是体会之后会更加真实一些。
- 还有一个注意点就是4也没有被打印出来，是因为没有捕获到异常，将会把异常抛给调用者，所以不会执行`System.out.println("4");`。

但是，化名为几千万个为什么的我又开始疑惑了，我们直到return可以将方法直接返回，强制退出。那么如果在try中使用return语句，finally还会不会不忘初心，继续执行呢？

前方高能！各单位注意！！！
猜猜看，这四个方法执行结果是啥呢？
```java
private  static int m1(){
    try{
        return 1;
    }catch(Exception e){
    }
    return 2;
}
```
```java
private static int m2(){
    try{
        return 1;
    }finally {
        return 2;
    }
    //使用finally子句时可以省略catch块
}
```
```java
private static int m3(){
    try{
        return 1;
    }finally {
        try{
            return 2;
        }finally {
            return 3;
        }
    }
}
```
```java
private static int m4(){
    int i = 4;
    try{
        return i++;
    }finally {
        i++;
    }
}
```
答案揭晓：分别是：1，2，3，4。你们猜对了吗？哈哈……

我想前三个答案应该是毋庸置疑的，但是这第四个就有点离谱了。不是说finally语句一定会执行吗，执行哪去了呢，你要是执行的话，你i难道不应该变成6了吗？
额……咳咳，这个嘛，我也有点迷惑，但是经过一番讨教，稍微懂了一些：
- 当执行try之前，如果后面有finally，会将try中的返回过程延迟，就是说把i=4放到结果区。
- 然后在计算区进行自增运算变为5，finally语句一定会执行，但是只是在计算区域自增为6了，结果区域还是原来的那个4。
- 不信的话，你可以在finally语句的i++后面看看i的值，它！就是6！所以说finally子句一定执行是毋庸置疑的的！

但是如果进行改变的是引用数据类型的变量时，那么就会随之改变了，人家村的是地址，改的就是本身。我在这边就稍微来个简单的例子奥：
```java
public static Student m(){
    Student s = new Student();
    try{
        s.age = 20;
        s.name = "天乔";
        return s;
    }finally {

        s.name = "巴夏";
        s.age = 2;
    }
}
//测试结果
//Student{age=2, name='巴夏'}
```