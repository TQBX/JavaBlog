# 数组定义及初始化

> 数组这玩意是一种用于存放数据最常见数据结构。

## 数组的的概念及注意点

- 数组要求所有的数组元素具有相同的数据类型，且只能存在一种数据类型，要多专一有多专一。
- 数据类型既可以是基本类型也可以是引用类型，类里虽然有不同对象，但都属于同一类，也可存于数组。
- 数组就是一种引用类型。
- 数组一旦初始化完成，长度就已经确定，所占内存空间也被固定且不能改变，把数据删除也没用。

## 数组的定义

```java
/ 定义数组
char[] initArrays;
char initArrays1[];
```

- 两者都可以定义一个数组，但建议使用第一种，也就是`type[] arrayname`,因为具有较好的可读性，让人一看就知道是个char[]类型的。
- 仅仅定义了一个引用对象，且并没有指向任何的内存，所以暂时是不能使用的，需要对它**初始化**！

## 数组的初始化

### 形式

- 静态初始化

```java
//静态初始化
char[] initArrays0;
initArrays0 = new char[]{'1','2'};
//char[] initArrays0= {'1','2'};
```

<u>静态初始化需要人为显式指定每个数组元素的初始值，由系统决定数组长度。</u>

`type[] arrayname={element1，element2...};`是上面两条语句的对于数组静态初始化的简化语法格式。

**注意**：**数组元素由花括号扩起，且由逗号分隔。**

- 动态初始化

```java
//动态初始化
int[] arrays;
arrays = new int[3];
//int[] arrays = new int[3];
```

<u>动态初始化是人为指定数组长度，由系统为数组元素分配初始值。</u>

同样，动态初始化也有简化格式：`type []arrayname = new int[length];`。

**注意**：**方括号内需指定数组的长度。**

> 一旦为数组的每个元素数组分配了内存空间，每个内存空间里存储的内容就是该元素的值，即使内容为空，也是null值，所以不论哪种方式初始化数组，都会使数组元素获得初始值。只不过初始值的获得的方式不一样罢了，一个是人给的，一个是系统给的。

### 动态分配的初始值

既然动态初始化数组只需要指定每个元素所需的内存空间，由系统为各个元素赋初值，那么，这些初值系统又是如何来赋予的呢。以下是对各个不同类型的数组赋初值的尝试：

```java
int[] arrays = new int[3];
boolean[] arrays1 = new boolean[3];
String[] arrays2 = new String[3];
float[] arrays3 = new float[3];
char[] arrays4 = new char[3];
System.out.println(arrays[0]);//0
System.out.println(arrays1[0]);//false
System.out.println(arrays2[0]);//null
System.out.println(arrays3[0]);//0.0
System.out.println(arrays4[0]);//输出'\u0000'为空
}
```

再根据查找资料，做出总结：

- 整数类型（byte，short，int，long）->0;
- 浮点类型（float，double）->0.0;
- 字符类型（char） ->'\u0000';
- 布尔类型（boolean） ->false;
- 引用类型（类，接口，数组）->null;

*小tips：关于输出'\u0000'为空引发的思考：[Java 中各种空（""、\u0000、null）的区别？](http://www.caotama.com/96796.html)*

