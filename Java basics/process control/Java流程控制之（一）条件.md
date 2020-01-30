# Java流程控制之（一）————条件

> 条件语句+循环语句，直接甩图甩代码！

## if条件语句

> Java希望在某个条件为真时执行相应的语句。

### 单if情况

```java
int a=6;
if (a==6)
{
    System.out.println("n=6");
}
System.out.println("已跳出循环");

```

> statement中如果是单语句，花括号可以省略。但是建议保留花括号，利于阅读，不容易混乱。
>
> **循环外的语句一定会执行。**

### 单if/else情况

```java
int a=6;
if (a==6)
{
    System.out.println("n=6");
}
else 
{
    System.out.println("n!=6");
}
System.out.println("循环外");
```

> 不是你死，就是我亡。

### if/else多分支情况

```java
Scanner s = new Scanner(System.in);
System.out.println("请输入一个整数：");
int n = s.nextInt();
if (n==3)
{
    System.out.println("n=3");
}
else if(n>3)
{
    System.out.println("n>3");
}
else 
{
    System.out.println("n<3");
}
System.out.printf("循环之外，都会输出 %d ",n);
```

> 选择很多。。

## switch条件语句

> 其实是选择语句，根据<u>**整数**</u>表达式的值，从一系列代码中选出一段去执行。

![switch](D:\myblog\pictures\switch.png)

```java
int n;//double n;错误: 不兼容的类型: 从double转换到int可能会有损失    
switch (n)
	{
        case 1:
            System.out.println("n="+n);
            break;
        case 2:
            System.out.println("n="+n);
            break;
        case 3:
            System.out.println("n="+n);
            break;
        default:
            System.out.println("其他");
            break;
	}
```

- switch(xxx),**括号内一定是一个整数值，或者能产生整数值的表达式**！！（byte,short,char,int,枚举类型，String,而不能是boolean类型）
- case xxx，当然case后面也需要整数值，**且switch语句将表达式的整数值与case后的值一一对比**，相符合就执行里面的代码，不相符就向下走。
- default后类似于else，其他都找不到，再轮到它。
- break是可选的，**使代码跳至switch主体的末尾**，如果不加break，会依次执行后面case部分的语句，直到遇到break。
- 如果多个case条件后面的执行语句是一样的，执行语句只需要在最后满足条件的地方写一次就可，简化结构。

