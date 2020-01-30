# 小白学Java：奇怪的RandomAccess

我们之前在分析那三个集合源码的时候，曾经说到：ArrayList和Vector继承了`RandomAccess`接口，但是LinkedList并没有，我们还知道继承了这个接口，就意味着其中元素支持**快速随机访问（fast random access）**。

## RandomAccess是个啥
出于好奇，我特意去查看了RandomAccess的官方文档，让我觉得异常惊讶的是！这个接口中啥也没有！是真的奇怪！（事实上和它类似的还有`Cloneable`和`java.io.Serializable`，这俩之后会探讨）只留下一串冰冷的英文。

 > **Marker interface** used by List implementations to indicate that they support **fast (generally constant time) random access**. The primary purpose of this interface is to allow generic algorithms to **alter their behavior** to provide good performance when applied to either random or sequential access lists. 

哎，不管他，翻译就完事了,今天的生活也是斗志满满的搬运工生活呢！

我用我自己的语言组织一下：
- 它是个啥呢？这个接口本身只是一个**标记接口**，所以没有方法也是情有可原的。
- 标记啥呢？它用来作为List接口的实现类们**是否支持快速随机访问**的标志，这样的访问通常只需要常数的时间。
- 为了啥呢？在访问列表时，根据它们是否是RandomAccess的标记，来选择访问他们的方法以提升性能。

我们知道，ArrayList和Vector底层基于数组实现，**内存中占据连续的存储空间**，每个元素的下标其实是偏移首地址的偏移量，这样子查询元素只需要根据：**元素地址 = 首地址+(元素长度*下标）** ，就可以迅速完成查询，通常只需要花费常数的时间，所以它们理应实现该接口。但是链表不同，链表依据不同节点之间的地址相互引用完成联系，本身**不要求地址连续**，查询的时候需要遍历的过程，这样子会导致，在数据量比较大的时候，查询元素消耗的时间会很长。

>RandomAccess接口的所有实现类：
>ArrayList, AttributeList, CopyOnWriteArrayList, RoleList, RoleUnresolvedList, Stack, Vector 

> the given list is an **instanceof** this interface before applying an algorithm that would **provide poor performance if it were applied to a sequential access list**, and to alter their behavior if necessary to guarantee acceptable performance. 


可以通过`xxList instanceof RandomAccess)`判断该列表是否为该接口的实例，如果是顺序访问的列表（如LinkedList），就不应该通过下标索引的方式去查询其中的元素，这样效率会很低。

```java
/*for循环遍历*/
for (int i=0, n=list.size(); i < n; i++)
    list.get(i);
/*Iterator遍历*/
for (Iterator i=list.iterator(); i.hasNext();)
    i.next();
```
对于实现RandomAccess接口，支持快速随机访问的列表来说,<u>for循环+下标索引遍历的方式比迭代器遍历的方式要更快。</u>


## forLoop与Iterator的区别
对此，我们是否可以猜想，如果是LinkedList这样并不支持随即快速访问的列表，是否是Iterator更快呢？于是我们进行一波尝试：

- 定义关于for循环和Iterator的测试方法
```java
    /*for循环遍历的测试*/
    public static void forTest(List list){
        long start = System.currentTimeMillis();
        for (int i = 0,n=list.size(); i < n; i++) {
            list.get(i);
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println(list.getClass()+" for循环遍历测试 cost:"+time);
    }
    /*Iterator遍历的测试*/
    public static void iteratorTest(List list){
        long start = System.currentTimeMillis();
        Iterator iterator = list.iterator();
        while(iterator.hasNext()){
            iterator.next();
        }
        long end = System.currentTimeMillis();
        long time = end-start;
        System.out.println(list.getClass()+"迭代器遍历测试 cost:"+time);
    }
```

- 测试如下
```java
    public static void main(String[] args) {    
        List<Integer> linkedList = new LinkedList<>();
        List<Integer> arrayList = new ArrayList<>();
        /*ArrayList不得不加大数量观察它们的区别，其实差别不大*/
        for (int i = 0; i < 5000000; i++) {
            arrayList.add(i);
        }
        /*LinkedList 这个量级就可以体现比较明显的区别*/
        for(int i = 0;i<50000;i++){
            linkedList.add(i);
        }
        /*方法调用*/
        forTest(arrayList);
        iteratorTest(arrayList);
        forTest(linkedList);
        iteratorTest(linkedList);
    }
```
- 测试效果想当的明显
![1ieRPI.png](https://s2.ax1x.com/2020/01/20/1ieRPI.png)

我们可以发现：
- 对于支持随机访问的列表（如ArrayList），for循环＋下标索引的方式和迭代器循环遍历的方式访问数组元素，差别不是很大，在加大数量时，for循环遍历的方式更快一些。
- 对于不支持随机访问的列表（如LinkedList），两种方式就相当明显了，用for循环＋下标索引是相当的慢，因为其每个元素存储的地址并不连续。
- 综上，如果列表并不支持快速随机访问，访问其元素时，建议使用迭代器；若支持，则可以使用for循环+下标索引。

## 判断是否为RandomAccess

上面也提到了， 这个空空的接口就是承担着**标记**的职责（Marker），标记着是否支持随机快速访问，如果不支持的话，还使用索引来遍历的话，效率相当之低。既然有标记，那我们一定有方法去区分标记。这时，我们需要使用`instanceof`关键字帮助我们做区分，以选择正确的访问方式。

```java
public static void display(List<?> list){
    if(list instanceof RandomAccess){
        //如果支持快速随机访问
        forTest(list);
    }else {
        //不支持快速随机访问，就用迭代器
        iteratorTest(list);
    }
}
```
继续进行测试：

![1iefRP.png](https://s2.ax1x.com/2020/01/20/1iefRP.png)

事实上，集合工具类`Collections`中有许多操作集合的方法，我们随便举一个从前往后填充集合的方法：

```java
    public static <T> void fill(List<? super T> list, T obj) {
        int size = list.size();
            
        if (size < FILL_THRESHOLD || list instanceof RandomAccess) {
            //for遍历
            for (int i=0; i<size; i++)
                list.set(i, obj);
        } else {
            //迭代器遍历
            ListIterator<? super T> itr = list.listIterator();
            for (int i=0; i<size; i++) {
                itr.next();
                itr.set(obj);
            }
        }
    }
```
还有许多这样的方法，里面有许多值得学习的地方。我是一个正在学习Java的小白，也许我的知识还未有深度，但是我会努力把自己学习到的做一个体面的总结。对了，如果文章有理解错误，或叙述不清之处，还望大家评论区批评指正。

参考资料：JDK1.8官方文档