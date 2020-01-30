[TOC]

# Java流程控制之（二）————循环

> while循环和for循环基本概念……直接上代码！

## while循环

```java
int i = 0;
while(i<10)
{
    System.out.println(i);
    i++;
}
System.out.println("跳出循环！");
//输出0 1 2 3 4 5 6 7 8 9 跳出循环！
```

- while(xx),括号内为true，则执行一条语句或一个语句块，还是那句话，建议保留“块”的花括号，利于阅读。
- 括号内为false，while循环体内的代码一次也不会执行。

 ![2222](D:\myblog\pictures\2222.png)

## do..while循环

```java
int a = 0;
do{
    a++;
    System.out.println(a);

}while(a<0);
System.out.println("跳出循环~");

```

do {statement}while (xx),与单纯while不同的是，do……while结构至少会执行循环体内的代码一次，随后再进行判断。

## for循环

> for循环语句是支持迭代的一种通用结构，利用每次迭代后更新的计数器或者类似的变量来控制迭代次数。

```java
for(int i=1;i<=10;i++)
{
    System.out.print(i+" ");
}
```

![5555](D:\myblog\pictures\5555.png)

**关于for循环**

- 第一部分用于对计数器初始化。
- 第二部分用于检测循环执行条件。
- 第三部分用于指示如何更新计数器。

**注意**：

- `for(double x=0;x!=10;x+=0.1)`这个条件永远不会结束，因为0.1无法精确用二进制表示。

- for语句第一部分声明的变量的作用域只是整个for循环体。

  ```java
  for(int i=0;i<10;i++){
      System.out.print(i);
  }
  System.out.print(i);//false,跳出循环，i未定义。
  //可以用以下方式：
  /*  int i;
  	for(i=0;i<10;i++)
  ...*/
  ```

- for循环其实是while循环的一种简化形式。上面可以改写为：

  ```java
  int i =0;
  while(i<10){
  	System.out.print(i);
      i++;
  }
  ```

