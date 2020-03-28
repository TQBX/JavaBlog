# list.size()与list==null的区别

两者的区别是显而易见的，下面这两段就能显示两者差别：

```java
List<Integer> list1 = new ArrayList<>();
System.out.println(list1.size());//0

List<Integer> list2 = null;
System.out.println(list2.size());//NullPointerException
```

上面表示的是一个empty array，在堆中已经存在，只是其中没有数据，所以size为0。

```java
public ArrayList() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}
//初始化{}
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
transient Object[] elementData; //ArrayList底层维护的数组
```

而下面是单纯的null，当然抛出空指针异常。

因此，如果某个方法返回的是一个List，并且有可能返回为null的时候，务必在调用的时候进行非空判断，或者在方法内将List进行初始化赋值。

如果记性时常不在线，忘记进行非空判断，可以试着用下面的方式去避免。

# 避免null的初始化赋值方式

【直接创建一个ArrayList】：`List<Integer> list1 = new ArrayList<>();`

【使用Collections.emptyList()】：`List<Integer> list = Collections.emptyList();`

下面这种方法，是我今天查找资料的时候了解到的：[Effective Java之返回零长度的数组或者集合，而不是null](https://blog.csdn.net/qq_33394088/article/details/78995608?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task)

特意去查看了以下源码，官方称之为这是一种`type-safe`的方法用于获取空list：

```java
//emptyList()是一个静态方法，返回一个不可变的empty list
public static final <T> List<T> emptyList() {
    return (List<T>) EMPTY_LIST;
}
//EmptyList静态内部类
//独一份的不可变，序列化的empty list
public static final List EMPTY_LIST = new EmptyList<>();
```

并且，使用这个方法不需要给每个列表都创建一个对象。

需要注意的是，这个返回的空集合是没有add、remove之类的方法的，因此如果对产生的list进行添加元素或移除元素的操作，是会报错的。

```java
List<Integer> list = Collections.emptyList();
list.add(5);
System.out.println(list);//java.lang.UnsupportedOperationException
```

他的兄弟姐妹还有很多，像emptySortedSet、emptySet、emptyMap、emptySortedMap……Collections这个工具类还是相当使用的，里面还有许多方法，有机会再做总结。