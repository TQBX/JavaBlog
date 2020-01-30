# Java成员变量和局部变量
变量相当于一个有名称的容器，用于装不同类型的数据。而Java中根据变量定义位置的不同，又把变量分为**成员变量**和局部变量。
由于对修饰符还没有系统学习，就暂且以public访问权限最高的作为修饰符。
还是先上一段代码：

```java
package com.my.pac03;

/**
 * @author Summerday
 * @date 2019/11/29 14:44
 */

public class VariablesTest {
    //成员变量会默认初始化
    public int value;//定义实例变量
    public static String name;//定义类变量，属于类，而不是类的单个实例。

    //main方法，程序入口
    public static void main(String[] args) {
        VariablesTest test = new VariablesTest();
        //实例访问实例变量
        test.value = 10;
        //类访问类变量
        VariablesTest.name = "typeName";
        System.out.println(VariablesTest.name);//typeName
        //实例访问类变量
        test.name = "noTypeName";
        System.out.println(VariablesTest.name);//noTypeName
        VariablesTest test2 = new VariablesTest();
        //另一个实例访问类变量
        System.out.println(test2.name);//noTypeName
        int val = test.addNum(5);
        System.out.println(val);//15
        System.out.println(test.value);//15
        //System.out.println(m);  错误，形参变量m已经消失
        //System.out.println(n);  错误，方法局部变量n也会消失
        test.printForeachOneTOTen();
    }

    public int addNum(int m) {
        int n;//方法局部变量并不会初始化
        //public int m;//局部变量不允许使用访问修饰符修饰
        //System.out.println(n);报错，因为方法局部变量不会初始化
        n = 3;

        return value += m;
    }
    public void printForeachOneTOTen(){
        for(int i = 1;i<=10;i++){
            System.out.print(i+" ");
        }
        //System.out.println(i);错误，i的值存在于代码块中，代码块之外就莫得了
    }

}

```
> 既然是作为变量，那么命名方面就应该遵循变量命名的规范，由多个有意义的英文单词连成，**首单词首字母小写，其他单词首字母大写**，务必见名知义。
## 成员变量
- 是在类里定义的变量，有些地方也叫做**字段（field）**，也可以叫做**域**。
- <u>根据变量是否由static关键字修饰</u>，分为**实例变量**和**类变量**，有些地方也叫做实例域和静态域（类域）。
### 1.实例变量
**1. 定义实例变量**
```java
//定义实例变量
public int value;
```
- 没有static修饰
- 从类的实例被创建开始存在，实例消亡时毁灭。

**2. 访问实例变量**
```java
//实例访问实例变量
    test.value = 10;
```
- 通过实例访问，格式：`实例.实例变量`。
### 2.类变量
**1. 定义类变量**
```java
//定义类变量
public static String name;
```
- 有static修饰
- 从类的准备阶段起存在，类消亡时毁灭。

**2. 访问类变量**
```java
//类访问类变量
VariablesTest.name = "typeName";
 //实例访问类变量
test.name = "noTypeName";
System.out.println(VariablesTest.name);//noTypeName
VariablesTest test2 = new VariablesTest();
//另一个实例访问类变量
System.out.println(test2.name);//noTypeName
```
- 通过类访问，格式：`类.类变量`。
- 也可以通过实例访问，格式：`实例.类变量`。
- 关于类变量涉及到的static关键字，之后还会深入学习。
> 第一条通过类直接访问类变量可以接受，但第二条有点迷惑，但也可以理解，毕竟类存在的范围比实例要大。
> 通过测试：该实例并不拥有这个类变量，所以他访问的不是实例变量，而依然是类变量。所以上面的操作，<u>通过实例修改类变量和通过类直接修改类变量效果时一样的。</u>那么，如果实例可以直接修改该类的类变量的话，那么这个类再创建其他实例时，这个所谓的“共有的特性”就说改就改，变得十分不堪。
## 局部变量
是在方法里定义的变量，根据定义形式的不同，分为以下三种：

### 1.形参变量
```java
//参数列表里是形参
public int addNum(int m) {
    int n;
    n = 3;
    return value += m;
}
```
- 在定义方法签名（方法名+参数列表）时定义的变量。
- 作用域在整个方法内有效。
- 形参不用显式初始化。
```java
int val = test.addNum(5);
System.out.println(val);//15
```
形参m的值人为指定，初始化在调用方法的时候由系统完成。这里相当于，把5赋值给了int类型的形参变量m。
### 2.方法局部变量
```java
public int addNum(int m) {
//int类型的n为方法局部变量
    int n;//局部变量并不会初始化
    //System.out.println(n);报错，因为方法局部变量不会初始化
    n = 3;
    return value += m;
}
```
- 是在方法体内定义的局部变量。
- 作用域在该变量定义时生效，到方法结束时失效。
- **需要显式初始化**，否则会报错。
### 3.代码块局部变量

```java
//i就是存在于for循环代码块中的代码块局部变量
public void printForeachOneTOTen(){
    for(int i = 1;i<=10;i++){
        System.out.print(i+" ");
    }
    //System.out.println(i);错误，i的值存在于代码块中，代码块之外就莫得了
}
```
- 在代码块中定义的局部变量。
- 作用域在定义该变量时生效，在代码块结束时失效。
- 需要显示初始化，否则会报错。

## 关于命名
- 在同一个类中，不能定义两个同名的成员变量，就算是一个类变量，一个实例变量也不行。
- 一个方法里面不能定义两个同名的方法局部变量，且方法局部变量和形参变量不能同名。
- 同一个方法中，不同代码块内的代码块局部变量可以同名。
- 先定义代码块，后定义方法局部变量，两者也可以同名。
- 如果方法局部变量和成员变量同名，那么局部变量会覆盖成员变量。（尽量避免）