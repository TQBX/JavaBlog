[toc]

> Class Object is the root of the class hierarchy.

Object类是所有类的顶级父类，任何一个对象（除了基本类型）都实现了Object类的方法，包括数组。

# 一、equals

> public boolean equals(Object obj)

## 1、equals与==有啥区别？

我们通常会见到一类问题：`==`和`equals`有啥区别？每次见到这种问题，都有一种模棱两可的感觉，这次完完整整地总结一波：

从Object类中的equals方法作为切入点，我们看看它的源码：
```java
    public boolean equals(Object obj) {
        return (this == obj);
    }
```
事实上，在Object类中，a.equals(b)等价于a==b，即判断两个对象是否具有相同的引用。

> `==`对于基本数据类型，判断数值是否相等，对于引用数据类型，判断地址值是否相等，也就是是否具有相同的引用。

我们发现，如果两个对象具有相同引用，则equals结果相等。但大多数情况下，这样的判断形式没啥意义，在实际情况下，我们往往需**要用equals检测对象状态、属性的相等性**，往往会在类中重写equals方法。

我们以String来举例，看看String类中的源码：

```java
    //重写注意形参类型必须是Object
    public boolean equals(Object anObject) {
        //引用相同，必然返回true
        if (this == anObject) {
            return true;
        }
        //判断anObject类型是否和String相同
        if (anObject instanceof String) {
            //anObject向下转型
            String anotherString = (String)anObject;
            int n = value.length;
            //每个位置上字符逐一比较
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
    }
```
我们可以发现，如果是两个字符串对象，除了判断是否同一引用，**还比较字符串值是否相等**（至于字符串的地址相关问题，我们之后再做总结）。同样的例子还有Integer等包装类，如果感兴趣，可以查看源码。

## 2、equals方法的规范

equals方法实现了**非空对象引用的等价关系**：

1. 自反性（reflexive）：对于任何非空引用x，x.equals(x)为true。
2. 对称性（symmetric）：对于任何非空的引用x和y，当且仅当y.equals(x)返回true时，x.equals(y)返回true。
3. 传递性（transitive）：对于任何非空引用x、y和z，如果x.equals(y)返回true，而y.equals(z)返回true，那么x.equals(z)也应该返回true。
4. 一致性（consistent）：如果非空引用x和y的对象没有变化，反复调用x.equals(y)返回相同的结果。
5. 对于任何非空引用x，x.equals(null)应该返回false。


## 3、instanceof 和getClass()
如果equals方法判断双方属于同一类，按照上面的规则编写代码其实比较轻松。但是，如果双方不同类，则关于对称性，就需要考虑用哪种方式判定。

我们通过小测试看看`instanceof`和`getClass()`的区别：
```java
public class EqualsDemo {
    public static void main(String[] args) {
        Super aSuper = new Super();
        Sub sub = new Sub();
        System.out.println(sub.getClass() == aSuper.getClass());//false
        System.out.println(sub instanceof Super);//true
    }
}
class Super{}
class Sub extends Super{}
```
可以发现，getClass判断时，**子类与父类类型严格不同**；而instanceof意味着父类的**概念适用于所有子类**，相同类。

对于一个要编写equals的类而言：

- 如果equals的语义在子类中有所改变，则应使用getClass检测。

- 如果所有的子类都拥有同一的语义，就使用instanceof检测。

## 4、其他总结
- 重写equals方法时，注意**形参类型一定是Object**。
- 数组类型的域可以使用Arrays工具类的静态方法`static boolean equals(type[] a,type[] b)`判断是否相等。
```java
    int[] a = {1,2,3};
    int[] b = {1,2,3};
    System.out.println(Arrays.equals(a, b));//true
```
# 二、hashCode
> public native int hashCode();

Object底层的hashCode方法是native修饰的，不是Java语言编写的，我们要知道：

这个方法将会返回对象的哈希码值，哈希码是整型且没有规律的，也叫做散列码。

我们之前进行过基于JDK1.8的HashMap源码分析，了解到通过哈希函数将键值转化为整型的哈希值，然后通过巧妙的操作，将其映射到数组的各个索引上，利用数组查询快的优势，大大提升了性能。


## 1、hashCode的规范

1. 当equals方法被重写时，应该重写hashCode方法，从而**保证两个相等的对象拥有相同的哈希码**。
2. 程序执行过程中，如果对象的数据没有被修改，则多次调用hashCode方法将返回相同的整数。
3. **两个不相等的对象可能具有相同的哈希码**，但在实现hashCode方法时应避免太多这样的情况出现。

## 2、String类的hashCode实现
hash函数非常多样，我们以常见的String类的hashCode实现举例：

```java
    public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }
```
- 字符串底层是由字符数组组成，当我们传入`"abc"`时，用val[]这个字符数组接收['a','b','c']。
- 然后将字符转化为int，也就是[97,98,99]。
- (（97x31)+98）x31+99 = 96354


# 三、toString
> public String toString()

该方法返回对象的字符串表现形式，结果应该是简洁但信息丰富的表示，便于阅读。建议所有子类都重写此方法。

如果不重写的话，Object类中定义的形式会让人很不爽，以下是toString()源码：
```java
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
```

返回字符串 = 对象对应运行时类的名称+“@”+对象哈希码的无符号十六进制表示形式
## 1、打印对象信息

我们可以试一试：

```java
    public static void main(String[] args) {
        Super s = new Super();
        System.out.println(s);
    }
    //输出结果
    com.my.objectClass.equals.Super@677327b6
```
`System.out.println(s);`意思是标准输出s到打印台上，我们看看具体执行的步骤：
```java
    public void println(Object x) {
        //首先调用String类的valueOf方法，获得s的字符串表现形式
        String s = String.valueOf(x);
        synchronized (this) {
            //打印
            print(s);
            //换行
            newLine();
        }
    }
```
我们继续看看这个`valueOf`是怎么一回事：
```java
    public static String valueOf(Object obj) {
    //如果为null，则输出"null"，非空则调用toString()
        return (obj == null) ? "null" : obj.toString();
    }
```
至此，我们可以知道，我们在试图打印对象的时候，都会调用对象的toString方法，我们可以试着重写该方法，则测试的时候能够快速清晰地定位。
## 2、论优雅打印数组
数组也是一种特殊的类型，我们通过打印可以发现，他的结果比较离谱，看上去比较奇怪，具体规则可以查看官方文档Class类的getName()方法。
```java
    int[] arr = {1,2,3};
    System.out.println(arr);//[I@14ae5a5
```
总之，我们总是希望我们打印出来的是，数组中的数整整齐齐排列着的，对吧。

我们可以利用Arrays工具类的静态方法toString方法，优雅地打印数组:
```java
    public static String toString(int[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }
```
## 3、自定义toString方法
既然是自定义，那么就没啥严格规定，为了测试数据的时候更加清晰，可以想想适合自己的打印信息的方法。

现在的IDE一般都是支持根据字段，自动生成toString方法的，比如我是用的IDEA，按住`Alt`和`Ins`键就可以快速生成。

```java
    
public class EqualsDemo {
    public static void main(String[] args) {
        System.out.println(new Person());
    }
}
class Person{

    String name = "天乔巴夏丶";
    int age = 18;

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

//测试结果
Person{name='天乔巴夏丶', age=18}
```

---

# 五、其他重要方法

至此，已经总结了Object中三个比较重要的方法，其他的诸如

- 并发编程相关的wait()、notify()、notifyAll()等方法
- 用于垃圾回收终结的finalize()方法
- 用于创建对象副本的clone()方法
- 用于获取对象运行时类信息getClass()的方法

这些方法，我们之后再做总结。





---



参考资料：《Java核心技术卷Ⅰ》