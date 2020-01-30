[toc]
# 小白学Java：迭代器原来是这么回事
上一篇，我们谈到了那个古老的迭代器Enumeration，还谈到了取代他的新迭代器——**Iterator**。相比于以往，这个新物种又有哪些优点呢？

迭代器这个词，在没查找许多资料之前，我只知道个大概，我知道它可以用来遍历集合，但是至于它其中的奥妙，并没有做深究。本篇文章关于Iterator迭代器做了小小的总结，巩固学习，如果有理解错误，或叙述不当之处，还望大家评论区批评指针。
## 迭代器概述
官方文档对Iterator的解释是：
- 它取代了Enumeration。
- 它作用于任何一个Collection。
- 它增加了remove的功能。
- 它优化了方法命名。

不行不行，这描述也太简略了，我继续查找资料：

> - 迭代器本身是个对象，创建迭代器的代价很小，通常被称为**轻量级对象**。
> - 迭代器其实也是一种**设计模式**，它提供了一种方法顺序访问一个聚合对象中的各个元素，但又不暴露该对象的内部表示。

## 迭代器设计模式
针对以上种种，我充满了好奇，于是在复杂的继承关系里画了又画，最终才渐渐理清集合中所谓迭代器模式的体现，暂时以**ArrayList**为例：
![1pqbX4.png](https://s2.ax1x.com/2020/01/18/1pqbX4.png)

- 定义了一个**迭代器的接口Iterator**，里面定义了迭代器的功能，但并没有提供实现。
- 定义了一个**聚集接口Iterable**，里面的iterator()抽象方法，表明返回一个针对类型T的迭代器。此时将Iterable和Iterator联系了起来。
- 我们知道聚集接口被许多接口所扩展，定义相同的方法，**Collection**接口就是其一，所以说，迭代器针对于所有集合都有效。
- 我们以**集合的具体类ArrayList**为例，暂时忽略之间的继承关系，ArrayList显然提供了抽象方法iterator()的具体实现，我们查看源码发现，它的返回值是一个Itr对象。
- 这个**Itr其实是ArrayList的一个内部类**，它**提供了迭代器接口的具体实现**（当然不一定是内部类），这样所有东西都联系在了一起。
## Iterator定义的方法

- `hasNext():boolean` 判断下一个元素还有没有，有就是true。
- `next(): E` 返回序列中的下一个元素。

![1pqLnJ.png](https://s2.ax1x.com/2020/01/18/1pqLnJ.png)

通过查看源码，我发现，在这个Itr这个实现类中，定义了两个指针：`cursor`和`lastRet`。（还有个属性为expectedModCount初始化为ArrayList的版本号modCount，这部分与fail-fast机制相关，之后会再提）而cursor初始为0，与**专门用来和集合元素数目size做比较的**。而lastRet初始化为-1，如果成功执行next操作，将会加1变成0，也就是上面说的“下一个元素”可想而知，可以把lastRet认为是**初始化为第一个元素之前的指针**，和将要返回的值的索引相同，这样会好记一些。

除了上面两个方法，JDK1.8新增了两个方法，也是体现处它与老迭代器不同的新优势：支持了删除操作。
- `remove():void` 将新近返回的元素删除。
需要注意的是：**remove方法没有新近返回的元素，也就是说lastRet<0**，会抛出异常。如果移除成功，让cursor往回退一格，lastRet重置为-1。

- `forEachRemaining(Consumer<? super E> consumer)：void` 这个是JDK1.8中Iterator新增的默认方法：**对剩余的元素执行指定的操作**。
可能不太好理解：我们通过测试来说明一下：

```java
    List<Integer> list = new ArrayList<>();
    list.add(1);
    list.add(2);
    list.add(3);
    //创建一个Iterator对象
    Iterator<Integer> it = list.iterator();
    //返回第一个值
    System.out.print(it.next()+" ");
```
测试结果很明显，只输出了第一个元素：1。
我们继续在原代码的基础上我们的新方法：
```java
    it.forEachRemaining(new Consumer<Integer>() {
        @Override
        public void accept(Integer integer) {
            System.out.print(integer+" ");
        }
    });
```
测试结果为：1 2 3，在原来的基础上，把剩下的元素都打印了出来。而这个新增的方法，其实和我们熟悉的这个是一样的：
```java
    while(it.hasNext()){
        System.out.print(it.next()+" ");
    }
```
> 值得一提的是：我们之前学习的**增强for循环**，在底层其实就是运用了Iterator，我通过IDE的debug调试功能，发现在调用运行到增强for循环时，自动调用了集合的iterator()方法，返回了一个Iterator的实现类实例。

## 迭代器：统一方式

通过对Iterator中定义方法的学习，我们大概知道了迭代器的用途，就是从前向后一个一个遍历元素，而无视其内部结构。欸，遍历我都懂，可无视结构在哪里体现啊？别急，下面来看一个例子，让我们无视两个不同集合的结构：

首先我们定义一个方法，它可以接收一个迭代器对象：
```java
    public static void display(Iterator<?> T){
        while(T.hasNext()){
            System.out.print(T.next());
        }
    }
```
然后我们创建两个不一样的集合，一个是ArrayList，一个是HashSet，本身是无序的，我们接下来应该会做相应的源码学习。
```java
        //ArrayList 有序
        List<String> list = new LinkedList<>();
        list.add("天");
        list.add("乔");
        list.add("巴");
        list.add("夏");
        //HashSet 无序
        Set<Integer> set = new HashSet<>();
        set.add(11);
        set.add(22);
        set.add(33);
        set.add(44);
        display(list.iterator());//天 乔 巴 夏
        System.out.println();
        display(set.iterator());//33 22 11 44

```
可以看出来，两个不同集合的迭代器传入display方法之后，都能用一种相同的方式访问集合中的元素。
通过上面的一顿分析，我们可以确定，迭代器这玩意儿，**统一了访问容器的方式**。
## Iterator的总结
- Iterator支持从前向后顺次遍历，**统一了对不同集合里元素的操作**。
- 还在Enumeration的基础上，**简化了命名**，而且Enumeration并不是对所有集合都适用。
- 四大技能增删改查，虽然**支持删和查**，但不支持增和改。
- 只**支持单向迭代**，某些情况下不是很灵活。（ListIterator可以支持双向，但只支持List类型）

最后，关于迭代器，还有一部分内容，在日后会做总结。
参考资料：《大话设计模式》、《Java编程思想》