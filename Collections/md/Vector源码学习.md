[toc]
# Vector源码学习
Vector是JDK1.0中的集合，是集合中的老大哥，其中大部分的方法都被**synchronized**关键字所修饰，与ArrayList和LinkedList不同，它是**线程安全**的（关于线程安全，之后学习再做系统总结）。但是随着一系列的更新迭代，它的缺点渐渐暴露：如方法名字太长，实现接口时出现了许多重复多余的方法等等。
从JDK1.2开始，Vector类被改进以实现List接口，让它成为Java集合框架的一员，如果不需要线程安全，建议使用ArrayList，效率更高。
## Vector继承体系
还是按照惯例，先看看它的继承图，当然这张图是基于JDK1.8的。
![1SwsaV.png](https://s2.ax1x.com/2020/01/17/1SwsaV.png)

- 可以看出来它和`ArrayList`一样，继承了`AbstractList`，实现`List`接口。
- 实现了`RandomAccess`接口，支持随机访问。
- 实现了`Cloneable`接口，实现了克隆的功能。
- 实现了`Serializable`接口，支持序列化。
## Vector核心源码

### 基本属性
```java
    //存储元素的数组
    protected Object[] elementData;
    //元素个数
    protected int elementCount;

    //该值决定了增长机制的不同
    protected int capacityIncrement;
    private static final long serialVersionUID = -2767605614048989439L;
```
### 构造器
我们可以得出结论：`Vector`的底层也是基于数组实现的，但是这些属性和我们之前提到的`ArrayList`有什么不同之处呢？我们继续向下看它所提供的几个构造器：
```java
    //两个参数，创建initialCapacity大小的数组，并为capacityIncrement赋值
    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

    //带一个参数，调用public Vector(int initialCapacity, int capacityIncrement)
    public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }

    //无参构造器，调用Vector(int initialCapacity)
    public Vector() {
        this(10);
    }

    //传入集合
    public Vector(Collection<? extends E> c) {
        elementData = c.toArray();
        elementCount = elementData.length;
        //类型判断
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
    }
```
我们可以发现：
- `initialCapacity`代表的是数组的容量，我们可以指定容量，**不指定默认为10**。
- `capacityIncrement`从字面上看，就可以知道它代表的是**容量增量**，意味着这个值将会影响之后的扩容，可以指定，不指定默认为0。
### 扩容机制
那么我们继续来看看它的**扩容机制**，是否可以验证我们的说法：
基本上的部分，都是和ArrayList类似，我们直接截取有差异的部分：
```java
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        //如果增量大于0,新容量 = 原容量 + 增量
        //如果增量不大于0，新容量 = 原容量*2
        int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                         capacityIncrement : oldCapacity);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```
其他的部分就不做分析了，之前讲的很详细，可以看看前面的文章。我们需要关注的是：
- 如果指定增量（增量>0)，那么每次在扩容的时候，新容量就是在**原容量的基础上加上指定增量**。
- 如果没有指定增量，那么每次在扩容的时候，**新容量默认变成原容量的两倍**。

## Enumeration
### 概述
说起迭代器，我们总是第一个想到的就是`Iterator`，而再Iterator是在JDK1.2的时候诞生的，用于取代JDK1.0版本的唯一迭代器`Enumeration`。官方对它的解释是这样的：
> An object that implements the Enumeration interface **generates a series of elements**, one at a time. Successive calls to the **nextElement** method return successive elements of the series. 

我用拙劣的英语试着翻译一下：实现Enumeration这个接口的对象呢，**将会生成一系列的元素，生成的时候是一个一个生成的**，通过调用`nextElement`这个方法，就可以返回这个系列里所有的连续元素。

今天我们励志做个光荣的官方文档搬运工！

> Methods are provided to enumerate through **the elements of a vector**, **the keys of a hashtable**, and **the values in a hashtable**. Enumerations are also used to specify the input streams to a SequenceInputStream. 

这段的意思也很明白：Enumeration接口为**Vector的元素**，**hashtable的键和值**提供了枚举的方法，它也被运用到指定**SequenceInputStream**的输入流中。我们暂时只需要知道，Vector类中，有一种方法能够产生Eumeration对象就完事了。其他的我们后面会进行总结。

接下来这段话相当关键！官方文档中用了大写的NOTE:
> **NOTE**:The functionality of this interface is duplicated by the Iterator interface. In addition, Iterator adds an optional remove operation, and has shorter method names. New implementations should consider using Iterator in preference to Enumeration.

大致的意思就是：现在这个方法呢，不太适应潮流了，那个年代用起来挺不错，现在需要年轻一辈来代替了。这个新一代的产物就是Iterator，它**复制了Enumeration的功能**，并且**增加可选的remove操作**，而且**提供了更简短的命名**。官方仿佛在嬉皮笑脸对你说：亲，这边建议你迭代器尽量用Iterator哟。

但是尽管如此，我们还需需要了解以下它的基本操作，毕竟以后可能还是会见到。
### 源码描述
```java
//Enumeration接口  
public interface Enumeration<E> {
    //判断是否还有更多的元素
    boolean hasMoreElements();
    //没有下一个元素就报错，有就返回
    E nextElement();
}
//Vector中的elements方法对接口的实现
public Enumeration<E> elements() {
    //通过匿名内部类实现接口
    return new Enumeration<E>() {
        int count = 0;

        public boolean hasMoreElements() {
            return count < elementCount;
        }

        public E nextElement() {
            synchronized (Vector.this) {
                if (count < elementCount) {
                    return elementData(count++);
                }
            }
            throw new NoSuchElementException("Vector Enumeration");
        }
    };
}
```
### 具体操作
```java
    Vector<String> v = new Vector<>();
    v.add("天");
    v.add("乔");
    v.add("巴");
    v.add("夏");
    //利用vector对象产生迭代器对象
    Enumeration<String> e = v.elements();
    //判断后边是否还有元素
    while(e.hasMoreElements()){
        //挪动指针指向下一个元素
        System.out.print(e.nextElement()+" ");
    }
    //天 乔 巴 夏 
```
## Vector总结

- **底层基于数组**，可以实现动态扩增，支持根据索引快速访问。
- 如果没有指定容量，默认为10。如果没有指定增量，默认为0。
- 扩容时，如果增量>0，则新容量=**指定增量+原容量**；如果增量<=0，则新容量为**原容量的两倍**。（注意，Vector里也有实现确定容量的方法ensureCapacity）
- 线程安全，如在创建迭代器之后的任何时间对结构进行修改，除了迭代器本身的remove和add外，都会抛出ConcurrentModification异常。所以，Vector通过`synchronized`关键字实现线程同步。可以参考：[https://blog.csdn.net/yjclsx/article/details/85283169](https://blog.csdn.net/yjclsx/article/details/85283169)






参考资料：JDK1.8官方文档