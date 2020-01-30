[toc]
# Java中final修饰的数据
**final**是Java中的一个重要关键字，它可以修饰数据、方法和类，本篇将从final修饰的**数据**角度对final做出总结。

final修饰的数据代表着：**永远不变**。意思是，一旦你用final修饰一块数据，你之后就只能看看它，你想修改它，没门。
我们不希望改变的数据有下面两种情况：
- **永不改变的编译时常量**。
```java
//编译时知道其值
private final int valueOne = 9;
```
- **在运行时（不是编译时）被初始化的值，且之后不希望它改变**。
```java
//在编译时不能知道其值
private final int i4 = rand.nextInt(20);
```

> **设置成常量有啥好处呢？**
> 很简单，让编译器觉得简单，就是最大的好处。比如把PI设置成final，且给定值为3.14，编译器自然会觉得这个东西不会再被修改了，是足够权威的。那么，编译器就会在**运行之前**（编译时）就把这3.14代入所有的PI中计算，这样在真正运行的时候，速度方面当然会快一点。

## 有初始值的final域

即**声明为final且当场就给定初始值的域**。
```java
 private final int valueOne = 9;
```

### final+基本数据类型
final修饰的基本数据类型变量**存储的数值**永恒不变。
```java
/*基本类型变量*/
//带有编译时数值的final基本类型
private final int valueOne = 9;
private static final int VALUE_TWO = 99;
public static final int VALUE_THREE = 39;
//!false:fd1.valueOne++;
//!false:fd1.VALUE_TWO++;
//!false:fd1.VALUE_THREE++;
```
康康上面醒目的三句false语句，很好地印证了我们之前说的：数值不让改！！！
需要注意的是，按照惯例，下面是**定义常量的典型方式**：
```java
//典型常量的定义方式
public static final int VALUE_THREE = 39;
```
- public修饰符使其可被用于包之外。
- static使数据只有一份。
- final表示其无法被更改
- 名称**全为大写英文字母，以下划线隔开**。
### final+引用数据类型
我们之前说过，基本类型存数值，引用类型存地址值。那么既然`final+基本数据类型`不让改数值，聪明的我们稍微一联想就明白，`final+引用数据类型`就是不让你改变量**存储实际对象的地址值**啦。（也就是不能再让它指向新的对象，很专一）
```java
private Value v1 = new Value(1);
private final Value v2 = new Value(22);
private static final Value V_3 = new Value(333);

```
```java

//引用变量并不是常量，存储地址可以改变
fd1.v1 = new Value(10);

//v2是引用变量，final修饰之后表示地址不能改变，但是实际对象的值是可以改变的
fd1.v2.i++;
//!false:fd1.v2 = new Value(3);

//V_3与v2类似，是静态和非静态的区别，下面会说明
fd1.V_3.i++;
//!false:fd1.V_3 = new Value(10);
}

```
通过例子，确实也证明上面所说，**一个以final修饰的引用数据类型变量，无法再指向一个新的对象，因为它所存储的地址值已经无法被更改，但是并不影响它指向的实际对象**。就拿一个比较典型的引用类型来举例，我们知道数组就是一种典型的引用类型，数组的引用变量存储的是数组再堆中的地址，堆中存放的就是数组每个索引的数值。
```java
/*引用变量之数组*/
private final int[] a = {1,2,3,4,5,6};
```
引用变量a被指定为final，所以它里面的**地址值不能再改**，也就无法再让它指向一个新的数组。
```java
//!false:fd1.a = new int[]{2,3,4,5,6,7};
for (int i = 0; i < fd1.a.length; i++) {
    fd1.a[i]++;
```
但是，它指向的数组里的**每个元素却可以改动**，因为数组中的元素并没有任何的限定。
### final与static final 
```java
private final int i4 = rand.nextInt(20);
static final int INT_5 = rand.nextInt(20);
```
```java
System.out.println(fd1);//fd1: i4 = 15,INT_518

FinalData fd2 = new FinalData("fd2");
System.out.println(fd2);//fd2: i4 = 13,INT_518
FinalData fd3 = new FinalData("fd3");
System.out.println(fd3);//fd3: i4 = 1,INT_5 = 18
```
- 上面示例分别创建了三个不同的对象，对其final 和final static 进行测试。
- 需要明确的是，两者都以final修饰，都不能被改变。
- 三个对象的i4值，没有用static修饰，不相同且不能改变。
- 而INT_5的值因为被static修饰，在类加载时已经被初始化，不随对象改变而改变。
## 空白final域

即**声明为final却没有给定初始值的域**。

```java
private final String id;//空白final
```
如果只有上面的这句，编译器会报错，因为它没有初始化。
```java
Variable 'id' might not have been initialized
```
所以，若定义了空白final域，一定记得在构造器中给它赋值！（必须在域的定义处或者每个构造器中以表达式对final进行赋值，因为系统不会为final域默认初始化）
```java
//在构造器中为空白final域赋初值
public FinalData(){
    id = "空白final默认id";
}
public FinalData(String id){
    this.id = id;
}
```
不要试图在初始化之前访问域，不然会报错。

> final让域可以根据对象的不同而不同，增加灵活性的同时，又保留不被改变的特性。


## final修饰的参数

### 基本数据类型的参数
类似地，就是**传入的参数不让改，只让读**，这一点很好理解。
```java
public int finalParamTest(final int i){
    //!false:i++;
    //不让改，只让读
    return i+1;
}
```
但是，我又新增了许多测试,分别定义四种不同的参数传入该方法，发现传入param0和param1编译会报错。（**非常疑惑，这部分书上没提，查了许多资料也没有理解清楚，希望大牛可以评论区指点迷津**）
```java
/*检测传入参数*/
int param0 = 5;
final int param1 = 10;
static final int PARAM_2 = 15;
static int param3 = 20;
//!false:System.out.println(fd1.finalParamTest(param0));
//!false:System.out.println(fd1.finalParamTest(param1));
//non-static field'param1' cannot be referenced from a static context
System.out.println(fd1.finalParamTest(PARAM_2));
System.out.println(fd1.finalParamTest(param3));
/*为什么形参列表里的参数用final修饰，但是用final修饰的param1无法传进去，
一定要static修饰？*/
```

### 引用数据类型的参数
```java
public void finalReferenceTest(final FinalData fd){
    //!false:fd = new FinalData();
    //不能再指向新的对象，存储地址不准变
    fd.param0++;
}
```
还是类似，不可以让这个引用类型的参数再指向新的对象，但是可以改变其实际指向对象的值。

最后的最后，下面的代码是根据《Thinking in Java》中的示例，结合自己的思想，将各个板块融合而成的超级无敌测试代码，冲冲冲！
```java
package com.my.pac16;

import java.util.Arrays;
import java.util.Random;

/**
 * @auther Summerday
 */

class Value{
    int i;//package access
    public Value(int i){
        this.i =i;
    }

}
/*final域在使用前必须被初始化：定义时，构造器中*/
public class FinalData {
    /*检测传入参数*/
    int param0 = 5;
    final int param1 = 10;
    static final int PARAM_2 = 15;
    static int param3 = 20;
    private static Random rand = new Random(47);
    private final String id;//空白final
    public FinalData(){
        id = "空白final默认id";
    }
    public FinalData(String id){
        this.id = id;
    }
    //带有编译时数值的final基本类型
    private final int valueOne = 9;
    private static final int VALUE_TWO = 99;
    //典型常量的定义方式
    public static final int VALUE_THREE = 39;
    //在编译是不能知道其值
    private final int i4 = rand.nextInt(20);
    static final int INT_5 = rand.nextInt(20);
    private Value v1 = new Value(1);
    private final Value v2 = new Value(22);
    private static final Value V_3 = new Value(333);

    private final int[] a = {1,2,3,4,5,6};
    @Override
    public String toString(){
        return id+": "+"i4 = "+i4+",INT_5 = "+INT_5;
    }
    public int finalParamTest(final int i){
        //!false:i++;
        //不让改，只让读
        return i+1;
    }
    public void finalReferenceTest(final FinalData fd){
        //!false:fd = new FinalData();
        //不能再指向新的对象，存储地址不准变
        fd.param0++;

    }

    public static void main(String[] args) {
        FinalData fd1 = new FinalData("fd1");
        /*基本类型变量*/
        //!false:fd1.valueOne++;
        //!false:fd1.VALUE_TWO++;
        //!false:fd1.VALUE_THREE++;
        /*引用变量*/
        fd1.v1 = new Value(10);
        fd1.v2.i++
        //!false:fd1.v2 = new Value(3);
        System.out.println("fd1.v2.i = [" + fd1.v2.i + "]");

        //!false:fd1.V_3 = new Value(10);
        fd1.V_3.i++;
        System.out.println("fd1.V_3.i = [" + fd1.V_3.i + "]");
        /*引用变量之数组*/
        System.out.println("before:fd1.a[] = " + Arrays.toString(fd1.a));

        /*数组引用变量a是final修饰，
        但是不代表它指向的数据值是final，
        而是a存储的地址值不能改变
         */
        //!false:fd1.a = new int[]{2,3,4,5,6,7};
        for (int i = 0; i < fd1.a.length; i++) {
            fd1.a[i]++;
        }
        System.out.println("after :fd1.a[] = " + Arrays.toString(fd1.a));
        /*final 与static final*/
        //下面示例分别创建了三个不同的对象，对其final 和final static 进行测试
        /*可以发现，三个对象的i4值是随机生成且不能改变的，且不相同,
        而INT_5的值不随对象改变而改变，因为被static修饰，在类加载时已经被初始化*/
        System.out.println(fd1);//fd1: i4 = 15,INT_518

        FinalData fd2 = new FinalData("fd2");
        System.out.println(fd2);//fd2: i4 = 13,INT_518
        FinalData fd3 = new FinalData("fd3");
        System.out.println(fd3);//fd3: i4 = 1,INT_5 = 18

        //!false:System.out.println(fd1.finalParamTest(param0));
        //!false:System.out.println(fd1.finalParamTest(param1));
        //non-static field'param1' cannot be referenced from a static context
        System.out.println(fd1.finalParamTest(PARAM_2));
        System.out.println(fd1.finalParamTest(param3));
        /*为什么形参列表里的参数用final修饰，但是用final修饰的param1无法传进去，
        一定要static修饰？*/

        System.out.println("fd1.param0 = "+fd1.param0);
        fd1.finalReferenceTest(fd1);
        System.out.println("fd1.param0 = "+fd1.param0);
    }

}

```

# Java中final修饰的方法

- `final`修饰的方法不能被重写，但是可以被重载。
- `private`被认为是`final`，再加一个`final`也没啥额外意义。
- 需要注意的是：子类中定义了一个与父类`private`或`private final`修饰的方法时，其实是重新定义了一个方法，并不是方法重写。（因为`final`修饰的方法无法被重写，可用`@Override`注解加以验证。
```java
package com.my.pac16;

/**
 * @auther Summerday
 */
public class FinalMethod {
    public final int age = 10;
    public final void test() {
        System.out.println("test()");
    }

    private final void test2() {
        System.out.println("test2()");
    }
    
}

final class Subclass extends FinalMethod {
    //不能重写：public final void test()
    //可以重载
    public final void test(int a) {
        System.out.println("test()");
    }
    
    //!false:@Override
    public final void test2() {
        System.out.println("is not override test2()");
    }

    public static void main(String[] args) {
        Subclass sb = new Subclass();
        sb.test(2);
        sb.test();
        sb.test2();//并不是重写
        System.out.println(sb.age);//final修饰的域可以被继承
    }
}
```
# Java中final修饰的类

- final修饰的类**无法被继承**。
- Java中的`String`类就是典型的例子。
`public final class String extends Object`

- 一个类被final修饰，里面的成员自然也就相当于用final修饰。

文章如有理解错误或叙述不到位，欢迎大家在评论区加以指正。
参考书籍：《Thinking in Java》