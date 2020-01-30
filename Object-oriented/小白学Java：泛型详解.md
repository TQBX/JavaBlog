[toc]
# 小白学Java：泛型详解
## 泛型概述

>  使用泛型机制编写的程序代码要比哪些杂乱地使用Object变量，然后再进行强制类型转换地代码具有更好的安全性和可读性。

以上摘自《Java核心技术卷一》

在谈**泛型**的定义之前，我先举一个简单又真实的例子：如果我想定义一个容器，在容器中放同一类的事物，理所当然嘛。但是在没有泛型之前，容器中默认存储的都是Object类型，如果在容器中增加不同类型的元素，都将会被接收，在概念上就不太符合了。关键是放进去不同元素之后，会造成一个很严重的情况：在取出元素并对里面的元素进行对应操作的时候，就需要复杂的转型操作，搞不好还会出错，就像下面这样：
```java
//原生类型
ArrayList cats = new ArrayList();
cats.add(new Dog());
cats.add(new Cat());
for (int i = 0; i < cats.size(); i++) {
//下面语句类型强转会发生ClassCastException异常
    ((Cat) cats.get(i)).catchMouse();
}
```
而泛型又是怎么做的呢？通过尖括号`<>`里的类型参数来指定元素的具体类型。
```java
ArrayList<Dog> dogs = new ArrayList<>();
dogs.add(new Dog());
dogs.add(new SkyBarking());
dogs.add(new Snoopy());
dogs.add(new Cat());//编译不通过
//向上转型,另外两个是Dog的子类对象
for(Dog d:dogs){
    System.out.println(d);
}
```
至此，泛型优点显而易见：
- **可读性**：很明显嘛，一看就知道是存着一组Dog对象。
- **安全性**：如果类型不符，编译不会通过，因此**不再需要进行强制转换**。

妙啊，从中我们可以体会泛型的理念：泛型只存在编译器，**宁可让错误发生在编译期，也不愿意让程序在运行时出现类型转换异常。** 因为bug发生在编译期更容易去找到并修复。 除此之外：
- 可将子类类型传入父类对象的容器之中，向上转型。
- 不必纠结对象的类型，可用增强for循环实现遍历。

## 定义泛型
再次强调，所谓泛型，即**参数化类型**，就是只有在使用类的时候，才把类型确定下来，相当的灵活。
### 泛型类的定义
```java
class Element<T>{
    private T value;
    Element(T value){
        this.value = value;
    }
    public T getvalue() {
        return this.value;
    }
}
```
- 引入类型变量T（按照规范，也可以有多个，用逗号隔开），并用`<>`扩起，**放在类名后面**。
- 其实就是可以把T假想成平时熟悉的类型，这里只不过用个符号代替罢了。
```java
Element<String> element = new Element<>("天乔巴夏");
System.out.println(element.getvalue());
```
- 使用泛型时，用具体类型（只能是**引用类型**）替换类型变量T即可。泛型其实可以堪称普通类的工厂。
- 泛型接口的定义与类定义类似，就暂且不做赘述。


### 泛型方法的定义
```java
class ParaMethod {
    public static <T> T getMiddle(T[] a) {
        return a[a.length/2];
    }
}
```
- 注意该方法并不是在泛型类中所定义，而是在普通类中定义的泛型方法。
- **类型变量T放在修饰符的后面，返回类型的前面**，只是正好我们这边返回类型也是T。
```java
int m = ParaMethod.getMiddle(new Integer[]{1,2,3,4,5});
//返回Integer类型，自动拆箱
System.out.println(m);//3
```
### 类型变量的限定
我们上面讲到，泛型拥有足够的灵活性，意味着我传啥类型，运行的时候就是啥类型。但是，实际生活中，我要是想对整数类型进行操作，不想让其他类型混入，怎么办呢？对了，加上类型限定。
```java
public static <T extends Number> T getNum(T num) {
    return num;
}
```
- 定义格式：`修饰符 <T extends 类型上限> 返回类型 方法名 参数列表`，如上表示对类型变量的上限进行限定，只有Number及其子类可以传入。
- 规定类型的上限的数量最多只能有一个。
- 既然类的定义是这样子，那大胆猜测一下，定义接口上线是不是就应该用`implements`关键字呢？答案是：否！接口依旧也是`extends`。
```java
public static <T extends Comparable & Serializable> T max(T[] a) {
    if (a == null || a.length == 0) return null;
    T maximum = a[0];
    for (int i = 1; i < a.length; i++) {
        if (maximum.compareTo(a[i]) < 0) maximum = a[i];
    }
    return maximum;
}
```
- 需要注意的是：如果允许多个接口作为上限，接口可以用&隔开。
- 如果规定上限时，接口和类都存在，类需要放在前面，`<T extends 类&接口>`。
- 没有规定上限的泛型类型可以视为:`<T extends Object>`。

## 原生类型与向后兼容
使用泛型类而不指定具体类型，这样的泛型类型就叫做原生类型（raw type）,用于和早期的Java版本向后兼容，毕竟泛型JDK1.5之后才出呢。其实我们在本篇开头举的例子就包含着原生类型，`ArrayList cats = new ArrayList();`。
```java
ArrayList cats = new ArrayList();//raw type
```
它大致可以被看成指定泛型类型为Object的类型。
```java
ArrayList<Object> cats = new ArrayList<Object>();
```
注意：**原生类型是不安全的**！因为可能会引发**类型转换异常**，上面已经提到。所以我们在使用过程中，尽量不要使用原生类型。


## 通配泛型
我们通过下面几个例子，来详细总结通配类型出现的意义，以及具体的用法。
### 非受限通配
<u>如果我想定义一个方法，让它接收一个集合，不关注集合中元素的类型，并把集合中的元素打印出来，应该怎么办呢？</u>
上面谈到泛型，你可能会这样写，让方法接收一个Object的集合，这样子你传进来啥我都接，完成之后美滋滋，一调试就不对了：
```java
public static void print(ArrayList<Object> arrayList){
    //错误！：arrayList.add(5);
    for(int i = 0;i< arrayList.size();i++){
        System.out.println(arrayList.get(i));
    }
}
```
```java
ArrayList<Integer> arr = new ArrayList<>();
print(arr);
```
究其原因：Integer是Object的子类的确没错，但是**ArrayList<Integer>并不是ArrayList<Object>的子类型**。那可咋办啊？这时**非受限通配符**它来了……
```java
public static void print(ArrayList<?> arrayList)
```
- 定义格式：`?`表示接收所有的类型，可以看成是`? extends Object`，这个就是我们即将要说的受限通配的格式了，非受限通配就是以Object为上限的通配，可不是嘛。
- 使用通配符`?`时，由于类型的不确定，你不能够调用与对象类型相关的方法，就像上面的`arrayList.add(5);`就是错误的。
### 受限通配
<u>如果我想定义一个方法，让它接收一个整数类型的集合，应该怎么办呢？</u>
```java
public static void operate(ArrayList<Number> list){
    /*operate a List of Number*/
}
```
```java
/* 调用方法 */
ArrayList<Integer> arr = new ArrayList<>();
operate(arr);
```
上面的这个错误，想必你不会再犯，因为**ArrayList<Integer>并不是ArrayList<Number>的子类型**。那这个时候又咋办啊？这时**受限通配符**它来了……
```java
public static void operate(ArrayList<? extends Number> list){
    /*operate a List of Number*/
}
```
- 形式：`？extends T `，表示T或者T的子类型。
### 下限通配

说完了上面两个，第三个我就不卖关子了，直接写上它的定义格式：`? super T`，表示T或者T的父类型。
```java
public static <T> void show(ArrayList<T> arr1,ArrayList<? super T>arr2){
    System.out.println(arr1.get(0)+","+arr2.get(0));
}
```
```java
ArrayList<Number> arr1 = new ArrayList<>();
ArrayList<Integer> arr2 = new ArrayList<>();
//编译出错
show(arr1,arr2);
```
以上将会编译错误，因为限定show方法中第二个参数的类型必须时第一个参数类型或者其父类。
## 泛型的擦除和限制
### 类型擦除
- 泛型的相关信息**可被编译器使用**，但是这些信息**在运行时是不可用**的。
- 泛型**仅仅存在于编译**，一但编译器确认泛型类型的安全性，就会将它转换原生类型。

- 当编译泛型类、接口或方法时，编译器会用**Object**代替泛型类型。以上面的例子举例：
```java
Element<String> element = new Element<>("天乔巴夏");
System.out.println(element.getvalue());
```
将会变成：
```java
Element element = new Element("天乔巴夏");
System.out.println((String)element.getvalue());
```
- 当一个泛型受限时，编译器会用其首限类型替换它。
```java
public static void operate(ArrayList<? extends Number> list){
    /*operate a List of Number*/
}
```
将变成下面这样：
```java
public static void operate(ArrayList<Number> list){
    /*operate a List of Number*/
}
```
- 不管实际的具体类型是什么，泛型类总是**被它的所有实例所共享**。
```java
ArrayList<Number> arr1 = new ArrayList<>();
ArrayList<Integer> arr2 = new ArrayList<>();
System.out.println(arr1 instanceof ArrayList);//true
System.out.println(arr2 instanceof ArrayList);//true
```
可以看到，虽然`ArrayList<Number>`和`ArrayList<Integer>`是两种类型，但是由于泛型在编译器进行**类型擦除**，它们在运行时会被加载进同一个类，即**ArrayList**类。所以下面这句将会编译出错。
```java
System.out.println(arr1 instanceof ArrayList<Number>);//编译出错
```
### 类型擦除造成的限制
- **不能使用泛型类型参数创建实例**。
```java
T t = new T();//错误
```
- **不能使用泛型类型参数创建数组**。
```java
//错误：E[] elements = new E[5];
E[] elements = (E[])new Object[5];
//可以通过类型转换规避限制，但仍会导致一个unchecked cast警告，编译器不能够确保在运行时类型转换能否成功。

```
- **不允许使用泛型类创建泛型数组**。
```java
ArrayList<String>[] list = new ArrayList<String>[5];//错误
```
- 上面说到，泛型类的所有实例具有相同的运行时类，**所以泛型类的静态变量和方法时被它的实例们所共享的**，所以下面三种做法都不行。
```java
class Test<T> {
    public static void m(T o1) {//错误
    }
    public static T o1;//错误
    static {
        T o2;//错误
    }
}
```
- **异常类不能是泛型的**。因为如果异常类可以是泛型的，那么想要捕获异常，JVM需要检查try子句抛出的异常是否和catch子句中的异常类型匹配，但是泛型类型擦除的存在，运行时的类型并不能够知道，所以没什么道理。

本文若有叙述不当之处，还望评论区批评指正。
参考资料：
《Java核心结束卷一》、《Java语言程序设计与数据结构》
[泛型就这么简单](https://segmentfault.com/a/1190000014120746)
[https://www.programcreek.com/category/java-2/generics-java-2/](https://www.programcreek.com/category/java-2/generics-java-2/)

