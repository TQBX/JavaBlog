# ArrayList源码学习
本文基于JDK1.8版本，对集合中的巨头**ArrayList**做一定的源码学习，将会参考大量资料，在文章后面都将会给出参考文章链接，本文用以巩固学习知识。
## ArrayList的继承体系
![lXRhsH.png](https://s2.ax1x.com/2020/01/15/lXRhsH.png)

**ArrayList**继承了**AbstracList**这个抽象类，还实现了**List**接口，提供了添加、删除、修改、遍历等功能。至于其他接口，以后再做总结。

## ArrayList核心源码
底层基于数组实现，我们可以查看源码，了解其拥有的一些属性：
```java
private static final long serialVersionUID = 8683452581122892189L;

//默认的初始容量为10
private static final int DEFAULT_CAPACITY = 10;

//如果指定数组容量为0，返回该数组，相当于new ArrayList<>(0);
private static final Object[] EMPTY_ELEMENTDATA = {};

//没有指定容量时，返回该数组，与上面不同的是：new ArrayList<>();
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

//该数组保存着ArrayList存储的元素，任何没有指定容量的ArrayList在添加第一个元素后，将会扩容至初始容量10
transient Object[] elementData; // non-private to simplify nested class access

//代表了当前存储元素的数量
private int size;
```
再次强调将`EMPTY_ELEMENTDATA`和`DEFAULTCAPACITY_EMPTY_ELEMENTDATA`区分开来是为了明确添加第一个元素时，应该扩容的大小，具体扩容的机制，后面会分析。

我们再来瞧瞧它的构造器：
```java
    
    //该构造器用以创建一个可以指定容量的列表
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            //创建一个指定容量大小的数组
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            //指定容量为0，对应EMPTY_ELEMENTDATA数组
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }

    //默认无参构造器，赋值空数组，但是在第一次添加之后，容量变为默认容量10
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    //传入一个集合，根据该集合迭代器返回顺序，构造一个指定集合里元素的列表
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        //传入集合不为空长
        if ((size = elementData.length) != 0) {
            //传入集合转化为的数组可能不是Object[]需要判断
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            //传入集合为元素数量为0，用空数组代替即可
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
    //指定集合为null的话（并不是说集合为空长），调用ArrayList的toArray方法，可能会抛出空指针异常
```
## ArrayList扩容机制

了解完ArrayList基本的属性和构造器之后，我们将对里面包含的方法进行学习：

- 上面说到，<u>使用默认构造器时，初始化赋值其实是个空数组，在添加了一个元素之后，容量才会变成10</u>，是不是会觉得有点好奇呢，我们先来瞧一瞧它的add系列方法：
```java

    //没有指定索引，默认在尾部添加元素
    public boolean add(E e) {
 
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //扩容之后，下一位赋值为e，size加1
        elementData[size++] = e;
        return true;
    }

    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    //判断是否为默认构造器生成的数组，并将minCapacity置为0；如果不是，minCapacity还是传入的size+1
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            //使用默认构造器，那么才会返回所需要的最小容量为默认容量10
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        //minCapacity = size+1 
        return minCapacity;
    }

    private void ensureExplicitCapacity(int minCapacity) {
        //定义在AbstractList中，用于存储结构修改次数
        modCount++;

        //如果最小容量比数组总长度还大，就扩容
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    //扩容操作
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        //将旧容量右移一位在加上本身，像当于新容量为就容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        //1.新数组的容量还是不能满足需要的最小容量，如初始指定容量为0时的情况
        //2.新数组越过了整数边界，newCapacity将会小于0
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        //如果新数组的容量比数组最大的容量Integer.MAX_VALUE - 8还大，
        //调用hugeCapacity方法
        if (newCapacity - MAX_ARRAY_SIZE > 0)

            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    //比较最小容量和MAX_ARRAY_SIZE
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        //三目表达式：如果真的需要扩这么大容量的情况下：
        //1.最小容量大于MAX_ARRAY_SIZE，新容量等于Integer.MAX_VALUE，否则新容量为Integer.MAX_VALUE-8
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```
- 根据扩容操作，如果我们一开始使用的是默认构造器生成的数组，在第一次增加之后容量就会变成默认容量10，之后才会以1.5倍进行扩容。
- 但是如果我们指定的是以0为容量的话，会通过grow方法，前四次扩容每次都只是增加1，频繁地调用copyOf就非常难受了，所以在知道目标大概多大时，可以通过`public void ensureCapacity(int minCapacity)`方法预先设置容量。参考:[https://www.iteye.com/topic/577602](https://www.iteye.com/topic/577602)。（但是经过我的个人测试：在1亿级或以上地数量上，没有调用该方法要快一些，但是真实场景应该不会把这么多的数据存放在里面吧，所以可以的话，用上这个方法，提升性能呀。）
- 关于`newCapacity - minCapacity < 0`的思考，很容易能看出判定条件是**新容量<需要的最小容量**。但是这个条件怎样才能达到呢
    - 当原容量为0或1时，扩容就会满足该条件。
    - 当原容量足够大时，它的1.5倍会越过整数边界，变为负值，同样满足。
- 注意：**移位运算效率会比整除运算更高一些**。

- **modCount**代表的是已对列表进行结构更改的次数，可以看到，每次执行添加操作时，一定都会让该次数加1。设计到的fail-fast机制，我们之后将会继续学习，暂不赘述。
- 其实扩容的方式就是我们看到的，创建一个<u>以新容量为长度的新数组，并将原来数组的值全部拷贝到新数组上，最后让elementData指向这个新数组。</u>

文章写到这里，我大舒一口长气，层层嵌套的调用终于结束了，不知道你们的内心是否也和我一样哈。我们趁热打铁，赶紧看看另一个重载的add方法。
```java
    //在索引为index处插入E
    public void add(int index, E element) {
        //索引越界判断
        rangeCheckForAdd(index);

        //同上，确保有足够容量添加元素
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //实际上Arrays.copyOf的底层调用的就是这个方法，意思是在原数组上从索引的位置到最后整体向后复制一位，相当于移动的长度为 (size-1) - index +1 = size -index
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        //在将index处填上元素E
        elementData[index] = element;
        //元素数量+1
        size++;
    }
```
有了前面的铺垫，相对来说就比较轻松了。我们不妨看看判断数组越界的方法，妈呀，这就更加清晰了，但是需要注意的是**index==size**在添加操作里，相当于从尾部插入，并不会构成越界：
```java
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```
看完了“增”的两个方法，该轮到同是四胞胎兄弟的“删”了。再谈“删”之前，我们要明确，**ArrayList**底层**基于数组实现**，**可依靠连续索引值存取获取数据**就变得理所当然了：
```java
E elementData(int index) {
    return (E) elementData[index];
}
```
下面是“删”操作，需要注意的是，<u>remove操作并不能够将容量减少，只是将其中的元素数量变少，自始至终只是size在变化</u>，不信你看：
```java
    //移除指定位置的元素，并将其返回 
    public E remove(int index) {
        //范围判断
        rangeCheck(index);
        //操作列表，计数加1
        modCount++;
        //取出旧值
        E oldValue = elementData(index);

        //相当于把index+1位置向后的所有元素集体向前复制一位，复制的长度就是
        //(size-1)-(index+1)+1 = numMoved
        int numMoved = size - index - 1;
        if (numMoved > 0)
            //执行集体拷贝动作
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        //并让最后一个空出来的位置指向null，点名让GC清理
        elementData[--size] = null; 
        //返回旧值
        return oldValue;
    }
```
可以稍微看一下**rangeCheck**的代码，与add操作里判定略有不同，省去了**index<0**的判断，我一开始很疑惑，后来发现后面有对数组的索引值取值，还是会发生异常：
```java
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```
> 我觉得有必要总结一下**System.arraycopy**这个方法，`public static native void arraycopy(Object src, int srcPos,Object dest, int destPos,int length);`native修饰符，底层并不是Java实现，而是c和c++。
> 这个方法的作用呢：就是从**指定的源数组**（src）从**指定位置**（srcPos）开始复制数组到**目标数组**（dest）的**指定位置**（destPos），**复制的个数**正好是length。
> 而**Arrays.copyOf**这个方法虽然底层调用了**System.arraycopy**，但是使用上是不太一样的，它不需要目标数组，系统会自动在内部新建一个数组，并返回。
> 哇，感觉add部分讲完，真的思路及其清晰，简直豁然开朗呢。咱们继续来remove！
```java
    //移除指定元素，找到并删除返回true，没找到返回false
    public boolean remove(Object o) {
        //判断指定的元素是否本身就是null值
        if (o == null) {
            for (int index = 0; index < size; index++)
                //找到同为null值的那个“它”
                if (elementData[index] == null) {
                    //快速删除，删除操作和之前类似，只是省略了范围判断，就不赘述了
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                //不是空值的话，就找值相等的，注意不要elementData[index].equals(o),时刻避免空指针
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
```
还有一个范围性的**removeRange**就不赘述了，总结一下：<u>ArrayList中的remove操作基于数组的拷贝，并将remove的长度置空，元素数量相应减少（只是元素数量减少，数组容量并不会改变）。</u>
对了，清理的话，clear方法会清理的相对干净一些，但是依旧**只是size变化**：

```java
    public void clear() {
        modCount++;

        //将所有元素置空，等待GC宠幸
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }
```
当然，如果你希望**数组容量也发生变化**的话。你可以试试下面的这个方法：
```java
    //将ArrayList容量调整为当前size的大小
    public void trimToSize() {
        modCount++;
        //基于三目运算
        if (size < elementData.length) {
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }
```
接下来，讲一讲相当简单的**set**与**get**这对基佬操作：
```java
    //用指定值替换只当索引位置上的值
    public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
    //获取指定索引位置上的值
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }
```
然后是姐妹花操作：**indexOf**和**lastIndexOf**。(ps:**寻找元素的过程可以参考remove指定元素的过程**），以indexOf为例，lastIndexOf从尾部向前遍历即可。
```java
    //判断o在ArrayList中第一次出现的位置
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
```
通过indexOf方法的返回值，我们还可以判断某个元素是否存在：
```java
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
```

除了单个元素增之外，**ArrayList**中还提供了可以<u>将整个集合增加到本身尾部的方法</u>：
```java
    //把传入集合中的所有元素全部加到本身集合的后面，如果发生改变就返回true
    public boolean addAll(Collection<? extends E> c) {
        //将传入集合转化为列表，如果传入集合为null，会发生空指针异常
        Object[] a = c.toArray();
        int numNew = a.length;
        //确定新长度是否需要扩容
        ensureCapacityInternal(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        //传入为空集合就为false，因为不会发生改变
        return numNew != 0;
    }
```
它的重载方法是<u>在指定位置插入另一个集合中地所有元素，并且以迭代的顺序排列</u>：
```java
    //在指定位置插入另一集合中的所有元素
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);
        //还是会引发空指针
        Object[] a = c.toArray();
        //传入新集合c的元素个数
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount
        //要移动的个数：(size-1)-index+1 = numMoved
        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                             numMoved);
        //size<index的情况，前面就会抛异常，所以这里只能index==size，相当于从尾部添加
        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }
```
## 最后的总结
- ArrayList**基于数组**实现，**查询便利**，通过扩容机制实现动态增长。

- **默认构造器生成的ArrayList初始化赋值其实是空数组**，增加第一个元素之后变为10.
- 扩容机制让每次的新容量都是原容量的1.5倍，且基于**右移运算**。
- **增加和删除的操作底层基于数组拷贝**，底层都调用了arraycopy的方法。
- 由于复制拷贝，导致增删的操作大多数情况下的效率会降低，但是并不是绝对的，如果一直在尾部插，尾部删的话，还是挺快的。
- 对了，它是**线程不安全**的，这个以后学习的时候在做总结吧。

对了如果不出意外的话，之后会带来LinkedList的源码学习，如果觉得我有叙述错误的地方，或者我没有说明白点地方，还望评论区批评指正，一起学习交流，加油加油！
参考链接：
[浅谈ArrayList动态扩容](https://blog.csdn.net/zymx14/article/details/78324464)
[List集合就这么简单【源码剖析】](https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484130&idx=1&sn=4052ac3c1db8f9b33ec977b9baba2308&chksm=ebd743e3dca0caf51b170fd4285345c9d992a5a56afc28f2f45076f5a820ad7ec08c260e7d39&scene=21###wechat_redirect)
[https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/ArrayList.md](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/ArrayList.md)