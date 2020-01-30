[TOC]
# Java值参数传递机制
Java方法中如果声明了**形参**，在调用方法时就必须给这些形参指定参数值，实际传进去的这个值就叫做**实参**。
这就涉及到Java中的参数传递机制，**值传递**。

## 基本数据类型
基本数据类型，值传递的体现是**数值**的传递。
```java
public class TransferTempTest {
    public static void main(String[] args) {
        //基本数据类型参数传递
        TransferTempTest test = new TransferTempTest();
        int num1 = 10;
        int num2 = 20;
        System.out.println("进行交换前：");
        System.out.println("num1 = " + num1 + ",num2 = " + num2);
        test.swap(num1, num2);
        System.out.println("交换之后：");
        System.out.println("num1 = " + num1 + ",num2 = " + num2);
    }
   public void swap(int a, int b) {
        int temp = a;
        a = b;
        b = temp;
        System.out.println("交换过程中：");
        System.out.println("num1 = " + a + ",num2 = " + b);
    }
}
```
```java
//运行结果
进行交换前：
num1 = 10,num2 = 20
交换过程中：
num1 = 20,num2 = 10
交换之后：
num1 = 10,num2 = 20
```
整个过程的内存图演示：
![02cfb42d2fb565cbe0dd30ea5c60e4ae.png](en-resource://database/949:0)

如图所示：

1. 在堆内存中创建 TransferTempTest类型的对象，假设此时地址值时0x1177（只是假设），并让栈内存中的引用变量test存储0x1177，指向该对象。
2. 在栈内存中声明两个变量num1和num2，并为他们赋值10和20。这时可以看到打印的结果为：`num1 = 10,num2 = 20`
3. 程序继续向下，进入swap方法，首先在栈区声明两个变量a和b，他们都是形参，这时将num1和num2的**值**分别传入，此时如蓝色字体所显示，a=10,b=20。
4. 接着还是在栈区声明一个名为temp的变量，这时执行的交换过程是在swap方法中，**交换的变量其实是num1和num2的拷贝值**，所以方法内打印:`num1 = 20,num2 = 10`
5. 最后，退出这个方法，**局部变量销毁**，发现其实一顿操作，原先的值根本就没有改变，打印结果和原先一样：`num1 = 10,num2 = 20`




## 引用数据类型
基本数据类型，值传递的体现是**地址值**的传递。
```java
public class TransferTempTest {
    public static void main(String[] args) {
    //引用类型参数传递
        DataTemp data = new DataTemp();
        data.a = 2;
        data.b = 4;
        System.out.println("进行交换前：");
        System.out.println("data.a = " + data.a + ",data.b = " + data.b);
        data.swapClass(data);
        System.out.println("进行交换后：");
        System.out.println("data.a = " + data.a + ",data.b = " + data.b);
    }
}
class DataTemp {
    int a;
    int b;

    public void swapClass(DataTemp data) {
        int temp = data.a;
        data.a = data.b;
        data.b = temp;
        System.out.println("交换过程中：");
        System.out.println("data.a = " + data.a + ",data.b = " + data.b);

    }
}
```
```java
//运行结果
进行交换前：
data.a = 2,data.b = 4
交换过程中：
data.a = 4,data.b = 2
进行交换后：
data.a = 4,data.b = 2
```
整个过程的内存图显示：
![eadbbe0b05be8fc51c6083ad03ee9abb.png](en-resource://database/951:0)
如图所示：
1. 在堆内存中创建DataTemp类型的对象，并让栈内存中的引用变量data存储0x7788（假设地址值），指向该对象，实例变量a和b的初始值都是0。
2. 让data.a=2,data.b=4,如蓝色字体显示，这时打印：`data.a = 2,data.b = 4`
3. 进入swapClass方法，首先在栈区声明形参变量data，将原来的data的**地址值**传入，**红黑两个data不是同一个！！**,<u>红色data只是黑色data的拷贝值，且拷贝的是地址值</u>。这时两个data指向同一片堆区内存，如红色箭头显示。
4. 接着还是在栈区声明一个名为temp的变量，参与交换操作，这时操作的是堆区的数据，所以方法里显然打印：`data.a = 4,data.b = 2`
5. 最后，退出这个方法，局部变量销毁，执行的交换过程虽然是在swapClass方法中，但引用变量传入形参传递的是地址值，意味着，这时的交换操作直接对堆区对象生效，所以最终交换成立：`data.a = 4,data.b = 2`

---
## 综合练习

```java
package com.my.pac05;

public class TransferTest {
    public static void main(String[] args) {
        TransferTest test = new TransferTest();
        test.first();
    }

    public void first() {
        int i = 5;
        Value v = new Value();

        v.i = 25;
        second(v, i);
        System.out.println(v.i);
    }

    public void second(Value v, int i) {
        i = 0;
        v.i = 20;
        Value val = new Value();
        v = val;
        System.out.println(v.i + " " + i);
    }
}

class Value {
    int i = 15;
}
```

```java
//运行结果
15 0
20
```
整个过程的内存图显示：
![120707aa436596d38147b37069706abf.png](en-resource://database/953:0)

## 总结

- 方法不能修改基本数据类型的参数，他们改变的仅仅是他们的拷贝。
- 方法可以改变对象参数的状态，因为方法可以通过对象引用的拷贝修改对象状态。
- 方法不能让对象参数引用一个新的对象，方法得到的永远都是拷贝值。