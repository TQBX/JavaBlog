# Java流程控制之（四）中断

在程序设计时，循环直接的跳转显得十分重要，虽然Java没有提供goto语句去控制程序的跳转，但为了控制循环，Java提供了continue，break，以及特殊的标签来实现特定的中断及跳转，当然还有return，这个相对不同一些。本篇将会做出总结。

## break

> 在循环时，如果不想等到false就终止循环的话，可以使用break来完成。

例如以下这个简单的例子：

```java
for (int i = 0;i<10 ;i++ ) {
    System.out.println(i);
    if(i==1) break;
}
System.out.println("跳出循环");//输出 0 1 跳出循环
```

可以直到，如果没有`if(i==1) break;`这个语句，这个循环将会依次输出0-9的数字，但是加上这句之后，如果执行到i等于1时，执行break语句，将直接跳出循环。

## continue

> continue有点像是放弃一部分，再从头开始。

依旧给出例子：

```java
for (int i = 0;i<10 ;i++ )
{	
    if(i%2==0) continue;
    System.out.println(i);
}
System.out.println("跳出循环");
//输出1 3 5 7 9 跳出循环
```

- continue用于忽略本次循环剩下的语句，接着下一次循环，但是不会像break一样直接终止循环。
- 上面的例子，只要时偶数就跳过后面的输出环节，后面也就都是输出奇数啦。

## return

> 其实return并不是专门用于结束循环的关键字，而是用来结束一个方法。

```java
for (int i = 0;i<10 ;i++ ) {
    System.out.println(i);
    if(i==1) return;
}
System.out.println("跳出循环");//输出 0 1 
```

- 可以看出，将第一个break语句的例子改成return，后面的“跳出循环”并不会被输出，也就是说return结束了整个程序。
- 虽然return也可以结束一个循环，但是与前两个不同，他结束了整个方法，不管return藏在多少层嵌套循环里面。
- return与break和continue不同的地方有很多，return后面可以跟一个值，并将值返回。

## 标签

> Java中没有goto但是，continue和break两个本属于中断语句的关键字，配合上“标签”之后，有了和goto类似实现跳转的机制，能够轻易控制多层的循环嵌套。

**break和continue配合标签类似，但也有差别。**

**标签需要放在循环语句之前，否则有啥意义吖，具体形式如：`label:`**

- break+标签

```java
outer:
for (int i = 0;i<5 ;i++ ) {
    for (int j = 0;j<3 ;j++ ) {
        System.out.print(" i="+i+" j="+j);
        if(j==1)
        {
            break outer;
        }
        System.out.println();
    }

}
//输出
 i=0 j=0
 i=0 j=1
```

当j==1时，遇到break outer语句，导致**结束outer标签指定的循环**，不是结束break所在的循环！不是结束break所在的循环！！！！

- continue+标签

```java
outer:
for (int i = 0;i<5 ;i++ ) {
    for (int j = 0;j<3 ;j++ ) {
        System.out.print(" i="+i+" j="+j);
        if(j==1)
        {
            continue outer;
        }
        System.out.println();
    }
}
//输出
 i=0 j=0
 i=0 j=1 i=1 j=0
 i=1 j=1 i=2 j=0
 i=2 j=1 i=3 j=0
 i=3 j=1 i=4 j=0
 i=4 j=1
```

j的值永远都不会超过1，因为每当j=1，遇到continue outer语句就结束了outer标签控制循环的当此循环，直接开始下一次循环，这时候i从i+1开始，j又将从0开始。

