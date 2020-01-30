[toc]
# Java小白的源码学习系列：HashMap
春节拜年取消，在家花了好多天时间啃一啃HashMap的源码，同样是找了很多很多的资料，有JDK1.7的，也有JDK1.8的，当然本文基于JDK1.8。将所学到的东西进行整理，希望回过头再看的时候，有更深刻的见解。

## 官方文档解读
先来看看史诗级长屏之官方介绍
![1urdJ0.png](https://s2.ax1x.com/2020/01/27/1urdJ0.png)


## 基本数据结构
实际上，在JDK1.8中，HashMap底层是依据**数组+单链表+红黑树**的结构存储数据的。具体是怎么样的呢？
![33d3eba4334fdc9c3ebd5007583577fd.png](en-resource://database/9290:1)
HashMap实现了Map接口，维护的是一组组**键值对**，以便于我们根据键就能立刻获取其对应值。另外，HashMap用了特殊的手法，优化了它的性能，我们本篇来具体学习并总结一下。
- 我们知道，数组的结构利于查询，HashMap依据**哈希函数**，将元素以某种方式映射到数组的某个位置上，就可以依据数组结构查询快的特点迅速锁定目标。
- 但是，哈希函数并不是万能的，两个不同的元素完全有可能算出相同的哈希值，这个时候就产生了**哈希碰撞**。

- HashMap是如何解决的呢？上面已经提到，采用的是**链地址法**，就是<u>将每个元素看成单链表中的节点，都有指向下一个节点的指针。</u>这是一个不错的办法，能够减少重哈希的概率。
- 但，又有一个问题，要是真的出现了极端的情况：有大量的元素通过哈希函数求得的值聚集在同一个链表上，这时想要找到这个元素，需要花费大量的时间。JDK1.8中，运用了红黑树结构，**链表中的节点数>TREEIFY_THRESHOLD**时，链表结构将会转化为树形结构，将查找元素的时间复杂度从O(n)降为O(logn)，大大提高了效率。
## 基本源码解读
### 基本成员变量
再看看HashMap中定义的一些**常量**：
```java
    //序列号
    private static final long serialVersionUID = 362498820763181265L;
    //默认的初始容量为16（必须为2的幂）
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    //允许的最大容量2的30次幂
    static final int MAXIMUM_CAPACITY = 1 << 30;
    //没有指定负载因子时，默认为0.75f
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    //链表转化为红黑树的阙值
    static final int TREEIFY_THRESHOLD = 8;
    //红黑树退化为链表的阙值
    static final int UNTREEIFY_THRESHOLD = 6;
    //数组的容量大于64时，桶才有可能转化为树形结构
    static final int MIN_TREEIFY_CAPACITY = 64;
```
还有一些**成员变量**：
```java
    //存储的元素的数组，数组容量一定时2的幂次
    transient Node<K,V>[] table;    
    //存放具体元素的集
    transient Set<Map.Entry<K,V>> entrySet;
    //存放元素的个数
    transient int size;
    //每次更改结构的计数器
    transient int modCount;
    //阙值，还没有分配数组时，阙值为默认容量或指定容量，之后该值等于容量*负载因子
    int threshold;
    //负载因子
    final float loadFactor;
```
### 构造器
我们根据源码，来看看在JDK1.8中，这些到底是如何实现的，以及为什么要这样考虑。
还是先看看其中三个**构造器**（暂时先忽略最后一个）：
```java
    //无参构造器
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    //指定容量的构造器
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    //两参构造器
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
    //传入映射集的构造器
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
```
这就是HashMap中提供的四个构造器，我们从中可以察觉出一些端倪。
- **如果没有指定负载因子，默认为0.75**，且指定的负载因子需要大于0。
- 初始容量并没有在构造器中直接指定，我们暂时保留疑惑。
- 通过两个参数的构造器，我们发现通过`tableSizeFor`对我们传入的初始容量进行计算，并为阈值赋值。
### 巧妙的tableSizeFor
说到这，我们来看看这个巧妙的`tableSizeFor`，我们通过注解可以知道，这个方法返回的是大于等于传入值的最小2的幂次方（传入1时，为1）。它到底是怎么实现的呢，我们来看看具体的源码：
```java
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```
说实话，我再看到这个方法具体实现之后，感叹了一句，数学好牛！我通过代入具体数字，翻阅了许多关于这部分的文章与视频，通过简单的例子，来做一下总结。
- 我们先试想一下，我们想得到比n大的最小2次幂只需要**在最高位的前一位置1，后面全置0**就ok了吧。如0101代表的是5，1000就符合我们的需求为8。
- 我们再传入更大的数，为了写着方便，这里就以8位为例：
![1uyQKS.png](https://s2.ax1x.com/2020/01/27/1uyQKS.png)

- 第一步`int n = cap -1`这一步其实是为了防止cap本身为2的幂次的情况，如果没有这一步的话，在一顿操作之后，会出现翻倍的情况。比如传入为8，算出来会是16，所以事先减去1，保证结果。
- 最后n<0的情况的判定，排除了传入容量为0的情况。
-  n>=MAXIMUM_CAPACITY的情况的判定，排除了移位和或运算之后全部为1的情况。

讲到这里，我知道了为什么数组的容量总是2的幂次数了：是因为运算规定,但是这基本不算是原因，选择2的幂次方数一定有出于便利的方面的原因，这部分我们待会再说。


> 我们在分析成员变量的时候说过，`threshold`是用来表示一个阈值，表示数组容量和负载因子的乘积。但是我们发现，还没分配数组的时候，其实是我们不小于指定容量的二次幂。

那么，**数组什么时候才进行初始化呢**？脑瓜子转一下，应该就知道，是**往里面存元素**的时候。我们来看一看HashMap里面存储元素的方法。
### put方法
```java
    //联系指定的键Key和值Value，如果在这之前map包含相同的key，返回旧key对应的value
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
```
### 巧妙的hash方法
其中调用了hash方法，对传入的键key进行哈希计算，具体计算细节如下：
```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```
我们着重了解一下，key不为null的情况下hash函数的实现，具体为啥要这样设计，我们之后再总结：

- h存储的是传入key的哈希值，这个方法继承于Object类，产生一个int值。
- 将上面这个老哈希值和无符号右移16位（将原高16位向低位移动，原高位全部以0填充）之后的新哈希值进行亦或运算，相同为0，不同为1。

> 有效地将高低位二进制特征混合，防止由高位的细微区别产生的频繁哈希碰撞，具体可以看一下文末的参考链接。

## JDK1.8的putVal方法
下面是一个及其关键的方法putVal。
```java
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        //如果数组未初始化或者长度为0，则调用resize()初始化数组
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        //根据hash值计算数组中的桶位，如果为null，则在该桶位上新建节点
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            //hash值相同，落入同一个桶中，且key相同
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            //判断是否为树形节点
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                //在节点后面插入新节点，桶中链表最多有8个节点，再加就变成了树
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        //超过阈值，转为树形
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    //判断后面节点是否存在key相同的情况
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    //e=p.next;p=e;这两步完成遍历
                    p = e;
                }
            }
            //如果存在相同key值相同，新值替换旧值
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        //容量大于阈值，resize();
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```

在没了解resize方法之前，我们暂且将他定义成**扩容和重哈希**的重要方法，我们先就putVal方法进行一些总结：
- 我们看到，在没有添加键值对的时候，数组并没有初始化；在调用put方法之后，**putVal中将会调用resize()真正对数组进行初始化**，至于如何实现，我们待会分析resize。
- 我们还说过，HashMap主要利用了哈希函数对传入的key值进行哈希运算，然后利用特殊的方法将求得的哈希值正确放入数组中的每个桶中。这个特殊的方法即:`p = tab[i = (n - 1) & hash]`，n为数组的长度，它是2的幂次方，我们很容易能够明白，**通过(n-1)&hash产生的索引值必然落在0~n-1的范围内**，相当于`i=hash%n`,但是位运算的效率更高。这就是容量设置为2的幂次方数的另外原因。
- `(k = p.key) == key || (key != null && key.equals(k)))`,这一步两边分别表示key是否为null的情况。
- 我们知道，`TREEIFY_THRESHOLD`为8，是链表结构转换为树形结构的阙值，通过源码我们可以知道，链表结构最多只能存储8个节点，如果要存第9个，就需要调用` treeifyBin(tab, hash);`，转换为树。
- 通过遍历的结构，我们可以发现，JDK1.8中，**添加的操作会在链表的尾部执行**。
- 遍历之后，节点e不为null，说明确实找到了key相同的节点，这时替换value值，返回旧值。
- `++size > threshold)`,从这部分我们可以看出，除了初始化的时候是先resize再插入，其他的时候都是先插入，再判断是否需要扩容。

## JDK1.8的resize方法
那么接下来，终于轮到resize方法了，我们先看一下代码的实现部分，哇这部分可是花了我好多的功夫，如果还有理解不正确的地方，还希望评论区批评指正：

```java
    final Node<K,V>[] resize() {
        //oldTab存储的是扩容前的数组
        Node<K,V>[] oldTab = table;
        //oldCap存储的是扩容前的数组容量
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        //oldThr存储的是扩容前的阈值
        int oldThr = threshold;
        //newCap新数组容量，newThr新数组阈值
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                //如果老数组容量比数组最大容量还大，阈值变为Integer的最大值，返回老数组
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            //新数组容量变为老数组容量的两倍
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                //新阈值变为两倍需要上面的条件都成立（1、扩容两倍之后的数组容量小于最大容量2、老容量大于等于16）
                newThr = oldThr << 1; // double threshold
        }
        
        else if (oldThr > 0) // initial capacity was placed in threshold
            //使用带有初始容量构造器，让新容量变为通过initial capacity求得的threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            //使用默认构造器，初始化容量为16
            newCap = DEFAULT_INITIAL_CAPACITY;
            //新容量变为16，新阈值变为0.75*16 = 12
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        //使用带有初始容量的构造器进行扩容
        if (newThr == 0) {
            //新阈值 = 新容量 * 指定的负载因子
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        //将newThr赋值给threshold表示阈值
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        //数组如果进行初始化的步骤，不用进入下面的代码段
        //判断老数组是否为空
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                //创建临时节点存储老数组oldTab上的元素
                Node<K,V> e;
                //如果老数组上索引j的位置不为null
                if ((e = oldTab[j]) != null) {
                    //将该位置置空
                    oldTab[j] = null;
                    //判断下一位是否还有元素
                    if (e.next == null)
                        //下一位为空，则表明该桶位只有一个元素，搬移至新数组
                        newTab[e.hash & (newCap - 1)] = e;
                    //判断是否为树形节点
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    //下一位不为空且为链表节点
                    else { // preserve order
                        
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            //在原来索引位置新建链表
                            if ((e.hash & oldCap) == 0) {
                                //尾节点为空时
                                if (loTail == null)
                                    //头节点指向原头节点，不再变化
                                    loHead = e;
                                else
                                    //在尾部接上老数组中的当前节点
                                    loTail.next = e;
                                //尾节点指向当前节点
                                loTail = e;
                            }
                            //在原来索引位置+老数组容量的位置新建链表
                            else {
                                //与上述相同
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                            //while循环保证从到到尾遍历链表
                        } while ((e = next) != null);
                        //如果尾节点不为空，就让它的next指向空，链表完整
                        if (loTail != null) {
                            loTail.next = null;
                            //新数组的原索引位置指向链表头节点
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            //新数组的原索引加老数组容量的索引位置指向链表头节点
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```
### 初始化部分
我们先谈一谈数组的初始化部分：
- 结合之前的putVal方法，我们知道当我们通过默认构造器创建HashMap，初始化为空的数组，threshold = 0。**当第一次添加元素时进行扩容，此时数组容量为16，threshold为12。**
- 当我们指定指定`initialCapacity`的时候，threshold一开始表示的是大于等于initialCapacity最小的2的幂次方数，直到第一次添加元素时进行扩容，**数组容量为threshold的值，而threshold此时为指定负载因子与数组容量的乘积**。
- 若数组已经初始化，即数组容量>0时，再扩容，**新容量变为原容量的两倍**，如果新容量小于最大容量，并且老容量>=16,此时threshold也变为原来的两倍，否则threshold不变。
- 如果老数组的容量比最大容量还要大的话，**阈值变为Integer的最大值，原数组不变。**
### 数组搬移部分
我们重点谈一谈数组的搬移的基础部分：
- 可以看到，通过for循环，通过j的改变，遍历数组中的每个桶的位置。
- 如果桶位上只有一个节点，搬移操作很简单：`newTab[e.hash & (newCap - 1)] = e;`。
- 如果桶位上为树形节点，就按树形操作来：`((TreeNode<K,V>)e).split(this, newTab, j, oldCap);`。

最难的是，**发生哈希碰撞**时，数组的搬移是如何实现的呢？我们可以发现，源码中对`e.hash & oldCap`的值是0还是1进行了分类判断，为啥要这样做呢？
- 我们知道，获取数组中的桶的位置，可以通过数组容量-1&hash求得。
- 也就是说假如旧容量为16时，哈希值10和26和15进行与运算之后，**都会保留二进制后四位的数**，也就是都为10,其实这就是哈希碰撞产生的原因嘛。

我们首先必须明确，同样的哈希值，扩容前后的区别只是在于被截取的那一位，就拿26而言（0001 1010），以16为容量时，它的有效索引位置为1010，而以32为容量时，它的有效索引则是11010，刚好差了10000，即oldCap，如下图：
![1urNon.png](https://s2.ax1x.com/2020/01/27/1urNon.png)

- `e.hash&oldCap`为0，节点在新数组中的**索引不变**，newTab[j]。
- `e.hash&oldCap`为1，节点在新数组中的**索引值 = 老数组容量+原索引值**，newTab[j + oldCap]。

了解完这个，我们对其中哈希碰撞时节点搬移的代码的分析开始！
关于其中针对`e.hash & oldCap`不同而定义的一对作用相同的节点，我们暂且将他们单独拎出来，研究loHead和loTail，另外一对其实同理即可。
- 我们知道，单链表的组成由存储的值和指向下一节点的指针next组成。
- 通过do……while循环从链表的头节点向后，一直向尾节点进行遍历，直到其为空。
- 建立临时节点e指向老链表的头节点，拥有相同的地址，其实就是**拥有了与老链表相同的结构**。
- 其实链表的遍历的操作我们之前的文章已经分析过，这边是通过下面的语句完成的。
```java
//do……while循环
do{
    next = e.next;
}while((e = next)!=null);
```
- 第一次进入循环时，loHead和loTail同时指向e，我在图中用灰色表示loHead，用白色表示loTail。
- 后面每次进入循环，都会利用loTail节点向后移动，并将老链表的节点赋给新链表，一直串在头节点之后。
- 直到遍历至老链表的最后一个节点，退出循环。
- 如果新链表的尾节点不为null，将它的next指向null，此时一个完整的新链表就已经诞生。`loTail.next = null;`
- 将原数组的索引位置指向这个新链表的头节点。`newTab[j] = loHead;`
![95c94333a1b4858f94accffb6b0e4f84.png](en-resource://database/9296:1)![1uraiq.png](https://s2.ax1x.com/2020/01/27/1uraiq.png)

---
最后的最后，本文还有许多方面需要完善或者修改，之后会陆续将新体会上传，还望评论区批评指正。

参考：

[HashMap中的hash算法中的几个疑问](https://www.cnblogs.com/zxporz/p/11204233.html)
[HashMap中的hash函数](https://www.cnblogs.com/zhengwang/p/8136164.html)
[jdk1.8 HashMap工作原理和扩容机制(源码解析)](https://blog.csdn.net/u010890358/article/details/80496144)
[Java 1.8中HashMap的resize()方法扩容部分的理解](https://blog.csdn.net/u013494765/article/details/77837338)