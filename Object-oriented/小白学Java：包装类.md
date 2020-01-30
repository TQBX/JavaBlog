[toc]
# 小白学Java：包装类

学习了许久的Java，我们知道Java是一种面向对象的语言，万物皆对象。但是我们之前在说到Java基本数据类型的时候，由于处理对象需要额外的系统开销，于是出于对性能的考虑，**基本数据类型并不做为对象使用**。
既然是面向对象的，在Java中许多方法需要把对象作为参数，但是基本类型变量身上没有任何方法和属性，于是Java提供了一个简单的方法，就是**为每一个基本数据类型类型都配套提供一个包装类型**，我们便可以在两者之间来回反复地横跳。

## 包装类的继承关系
先看一波包装类型的继承图：
![5423a2bd53c0e24e6a68adc7d8c46202.png](en-resource://database/7057:1)

数值类型都直接继承于父类**Number类**，非数值类型**Character**和**Boolean**直接继承于**Object类**。

除此之外，包装类型的名字也非常好记，除了`int->Integer`和`char->Character`两个比较特殊之外，其他都是基本数据类型的首字母改为大写即可,如：`byte->Byte`。

通过查看官方文档，我们可以发现，数值类型继承的Number类其实是一个抽象类，那么可想而知，该类中的抽象方法已经在这几个数值类型中得到实现，看一波：
![f046004020f993eeb0fe9741cfa13b8b.png](en-resource://database/7063:1)
很明显，除了最后一个serialVersionUID（这个以后再总结），其他的方法在数值型包装类中都存在，可以通过这些方法将对象“转换”为基本类型的数值。
## 创建包装类实例
我们再来看看包装类型的构造器，我们再查看所有包装类之后，发现：
- 所有的包装类型都**不存在无参构造器**。
- 所有包装类的实例都是**不可变**的。
- 一旦创建对象后，它们的内部值就不能进行改变。
在JDK1.5之前，我们可以这样把**基本数据类型转换为包装类对象，这个过程也叫做装箱**，当然反向的过程叫做拆箱：
```java
Integer i1 = new Integer(5);//5
Integer i2 = new Integer("5");//5
```
- 第一句调用的是传入int类型参数的构造器，`this.value = value`，一目了然。
- 第二句调用的是传入String类型参数的构造器，其实又是调用了静态方法**parseInt(String s,int radix)**:
```java
public Integer(String s) throws NumberFormatException {
    this.value = parseInt(s, 10);
}
```
深究一下，parse(String s,int radix)中的radix其实代表着**进制信息**，而我们的构造器默认让radix为10，代表着**输出字符串s在十进制下的数**，所以除了数字0-9之外，字符串中不能有其他的玩意儿，否则会抛出**NumberFormatException**的异常。

> 

## 自动装箱与拆箱
我们在上面说过，基本数据类型和包装类型之间的转换涉及到装箱与拆箱的操作，为了简化代码，在JDK1.5之后，Java允许基本类型和包装类型之间可以自动转换。

### 自动装箱
将基本类型直接赋值给对应的引用类型，编译器在底层**自动调用**对应的valueOf方法。
就像下面这样：
```java
int i = 5;
Integer in = i; 
```
我们利用debug调试工具设上断点，发现在执行`Integer in = i; `时，将会自动调用下面的方法：
![45b1e9520df3ea2c190d94aab1f650b7.png](en-resource://database/7169:1)

继续深究其底层实现，我们发现**IntegerCache**其实是**Integer**包装类的一个内部类，我们进入**IntegerCache**一探究竟：
![1804112eb10fc1f8ff8170e4ffa87019.png](en-resource://database/7171:1)
我们会发现所有的整数类型的（包括Character）包装类里都有类似的玩意儿，所以大致运行的规则应该大致相同，在这里就总结几点不太一样的：
- 只有Integer包装类才可以更改缓存大小。
- **Character容量只有128**。
浮点数类型包装类并不存在缓存机制，是因为在一定的范围内，该类型的数值并不是有限的。
看到这，我们大致就可以得出结论，整数数值类型在自动装箱的时候会进行判断数值的范围，如果正好在缓存区，那么就不必创建新的对象，它们将会指向同一地址。Java中另一个例子就是我们说的字符串常量池。
所以下面很火的几条语句，结果就很明显了：
```java
int num = 100;
Integer i1 = num;
Integer i2 = num;
System.out.println(i1==i2);//true
//num改为200，结果为false
```
```java
Integer i1 = 100;
Integer i2 = new Integer(100);
System.out.println(i1 == i2);//false
```

### 自动拆箱
将引用类型字节赋值给对应的基本类型，编译器在底层自动调用对应的**xxxvalue**方法（如intValue）。
```java
Integer in = 5;
int i = in;
```
自动拆箱相对来说就稍微简单一点了，我们还是利用debug工具，发现上面的代码将会自动调用下面的方法
![bda39dbdb824f19df5878028fb22d942.png](en-resource://database/7173:1)

## 包装类型的比较
### "=="比较
```java
int num = 100;
Integer i1 = num;
Integer i2 = num;
//都是包装器类型的引用时，比较是否指向同一对象。
System.out.println(i1==i2);//true

Integer i1 = 128;
int i2 = 128;
//如果包含算数运算符，则底层自动拆箱，即比较数值。
System.out.println(i1 == i2);//true
Integer i3 = 1;
Integer i4 = 129;
System.out.println(i4 == i1+i3);//true
```
### equals比较
equals比较的是同一包装类型，即比较两者数值是否相等
```java
Integer i1 = 5;
Integer i2 = 5;
Integer i3 = 10;
//同一包装类型，比较数值是否相等
System.out.println(i1.equals(i2));//true
System.out.println(i3.equals(i1+i2));//true

Long l1 = 5L;
Long l2 = 10L;
//Long与Integer比较，不是同一类型，false
System.out.println(l1.equals(i1));//false
//先自动拆箱，i1先转为int，l转为long，int自动类型提升转为long，最后相等
System.out.println(l2.equals(l1+i1));//true
```
## 自动装箱与拆箱引发的弊端
### 自动装箱弊端
```java
Integer sum = 0;
for(int i = 500;i<5000;i++){
    //先自动拆箱，而后自动装箱
    sum+=i;
}
```
在拆箱装箱操作之后，由于sum数值超过缓存范围，所以会new出4500个毫无用处的实例对象，大大影响了程序的性能。所以在循环语句之前，务必声明正确的变量类型。
### 自动拆箱引起的空指针
```java
private static Integer sum;
public static void setSum(Integer num,boolean flag){
    sum = (flag)?num:-1;
}
```
上面的代码，当num传入为null时，即会引发空指针异常，因为包装类<u>在进行算术运算时（上述是三目运算），如果数据类型不一致，将会先自动拆箱转换成基本类型进行运算</u>，而null如果调用了intValue()方法就会形成空指针。
改进方案：
```java
public static void setSum(Integer num,boolean flag){
//这样类型一致，便不会自动拆箱了
    sum = (flag)?num:Integer.valueOf(-1);
    
}
```
参考链接：

[Java 自动装箱与拆箱的实现原理](https://www.jianshu.com/p/0ce2279c5691)

[Java的自动装箱、拆箱](https://www.cnblogs.com/qingshanli/p/9534614.html)

[Integer缓存池（IntegerCache）及整型缓存池](https://blog.csdn.net/maihilton/article/details/80101497)
[Java中的自动装箱与拆箱](https://droidyue.com/blog/2015/04/07/autoboxing-and-autounboxing-in-java/)