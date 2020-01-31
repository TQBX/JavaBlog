[toc]
# 小白学Java：内部类
内部类是封装的一种形式，是定义在类或接口中的类。
## 内部类的分类
### 成员内部类

即定义的内部类作为外部类的一个普通成员（非static），就像下面这样：
```java
public class Outer {
    class Inner{
        private String id = "夏天";

        public String getId() {
            return id;
        }
    }

    public Inner returnInner(){
        return new Inner();
    }
    public void show(){
        Inner in = new Inner();
        System.out.println(in.id);
    }
}
```
我们通过以上一个简单的示例，可以得出以下几点：
- Inner类就是内部类，它的定义在Outer类的内部。
- Outer类中的returnInner方法**返回一个Inner类型的对象**。
- Outer类中的show方法通过我们熟悉的方式创建了Inner示例并访问了其私有属性。

可以看到，我们像使用正常类一样使用内部类，但实际上，内部类有许多奥妙，值得我们去学习。至于内部类的用处，我们暂且不谈，先学习它的语法也不迟。我们在另外一个类中再试着创建一下这个Inner对象吧：
```java
class OuterTest{
    public static void main(String[] args) {
        //!false:Inner in = new Inner();
        Outer o = new Outer();
        o.show();
        Outer.Inner in = o.returnInner();
        //!false: can't access --System.out.println(in.id);
        System.out.println(in.getId());
    }
}
```
哦呦，有意思了，我们在另一个类`OuterTest`中再次测试我们之前定义的内部类，结果出现了非常明显的变化，我们陷入了沉思：
- 我们不能够像之前一样，用`Inner in = new Inner();`创建内部类实例。
- 没关系，我们可以通过Outer对象的`returnInner`方法，来创建一个实例，成功！
- 需要注意的是：我们如果需要一个内部类类型的变量指向这个实例，我们需要明确指明类型为：`Outer.Inner`，即`外部类名.内部类名`。
- 好啦，得到的内部类对象，我们试着**直接去访问它的私有属性！失败！**
- 那就老老实实地通过`getId`方法访问吧，成功！

说到这，我们大概就能猜测到：内部类的存在可以很好地b一部分具有联系代码，实现了那句话：我想让你看到的东西你随便看，不想让你看的东西你想看，门都没有。

#### 链接到外部类
其实我们之前在分析`ArrayList`源码的时候，曾经接触过内部类。我们在学习迭代器设计模式的时候，也曾领略过内部类带了的奥妙之处。下面我通过《Java编程思想》上：**通过一个内部类实现迭代器模式的简单案例**做相应的分析与学习：
首先呢，定义一个“选择器”接口：

```java
interface Selector {
    boolean end();//判断是否到达终点
    void next();//移到下一个元素
    Object current();//访问当前元素
}
```
然后，定义一个序列类Sequence：
```java
public class Sequence {
    private Object[] items;
    private int next = 0;
    //构造器
    public Sequence(int size) {
        items = new Object[size];
    }
    public void add(Object x) {
        if (next < items.length) {
            items[next++] = x;
        }
    }
    //该内部类可以访问外部类所有成员（包括私有成员）
    private class SequenceSelector implements Selector {
        private int i = 0;
        @Override
        public boolean end() {
            return i == items.length;
        }
        @Override
        public void next() {
            if (i < items.length) {
                i++;
            }
        }
        @Override
        public Object current() {
            return items[i];
        }
    }
    //向上转型为接口，隐藏实现的细节
    public Selector selector() {
        return new SequenceSelector();
    }
}
```
- 内部类`SequenceSelector`以private修饰，实现了`Selector`接口，提供了方法的具体实现。
- 内部类访问外部类的私有成员`items`，可以得出结论：内部类自动拥有对其外部类所有成员的访问权。
> 当内部类是**非static**时，当外部类对象创建了一个内部类对象时，内部类对象会产生一个指向外部类的对象的引用，所以非static内部类可以看到外部类的一切。
- 外部类`Sequence`的`selector`方法返回了一个内部类实例，意思就是用接口类型接收实现类的实例，实现向上转型，既隐藏了实现细节，又利于扩展。

我们看一下具体的测试方法：
```java
    public static void main(String[] args) {
        Sequence sq = new Sequence(10);
        for (int i = 0; i < 10; i++) {
            sq.add(Integer.toString(i));
        }
        //产生我们设计的选择器
        Selector sl = sq.selector();

        while (!sl.end()) {
            System.out.print(sl.current() + " ");
            sl.next();
        }
    }
```
- **隐藏实现细节**：使用Sequence序列存储对象时，不需要关心内部迭代的具体实现，用就完事了，这正是内部类配合迭代器设计模式体现的高度隐藏。
- **利于扩展**：我们如果要设计一个反向迭代，可以在Sequence内部再定义一个内部类，并提供Selector接口的实现细节，及其利于扩展，妙啊。
#### .new和.this
我们稍微修改一下最初的Outer:
```java
public class Outer {
    String id = "乔巴";
    class Inner{
        private String id = "夏天";

        public String getId() {
            return id;
        }
        public String getOuterId(){
            return Outer.this.id;
        }
        public Outer returnOuter(){
            return Outer.this;
        }
    }
    public static void main(String[] args) {
        Outer o = new Outer();
        System.out.println(o.new Inner().getId());//夏天
        System.out.println(o.new Inner().getOuterId());//乔巴
    }
}
```
- 在内部类Inner体内添加了returnOuter的引用，`return Outer.this;`，即`外部类名.this`。
- 我们可以发现，内部类内外具有同名的属性，我们在内部类中，不加任何修饰的情况下默认调用内部类里的属性，我们可以通过引用的形式访问外部类的id属性，即`Outer.this.id`。



我们来测试一波：
```java
    public static void main(String[] args) {
        Outer.Inner oi = new Outer().new Inner();
        System.out.println(oi.getId());//夏天
        Outer o = oi.returnOuter();
        System.out.println(o.id);//乔巴
    }
```
- 外部类产生内部类对象的方法已经被我们删除了，这时我们如果想要通过外部类对象创建一个内部类对象：`Outer.Inner oi = new Outer().new Inner();`，即在外部类对象后面用`.new 内部类构造器`。

> 我们对内部类指向外部类对象的引用进行更加深入的理解与体会，我们会发现，上面的代码在编译之后，会产生两个字节码文件：`Outer$Inner.class`和`Outer.class`。我们对`Outer$Inner.class`进行反编译：
> ![136jn1.png](https://s2.ax1x.com/2020/01/31/136jn1.png)
> 确实，内部类在创建的过程中，依靠外部类对象，而且会**产生一个指向外部类对象的引用**。

### 局部内部类
#### 方法作用域内部类
即在方法作用域内创建一个完整的类。
```java
public class Outer {
    public TestOuter test(final String s){
        class Inner implements TestOuter{
            @Override
            public void testM() {
                //!false: s+="g";
                System.out.println(s);
            }
        }
        return new Inner();
    }
    public static void main(String[] args) {
        Outer o = new Outer();
        o.test("天乔巴夏").testM();//天乔巴夏
    }
}
interface TestOuter{
    void testM();
}
```
需要注意两点：
- 此时Inner类是test方法的一部分，Outer不能在该方法之外访问Inner。
- 方法传入的参数s和方法内本身的局部变量都需要以final修饰，不能被改变！！！

> JDK1.8之后可以不用final显式修饰传入参数和局部变量，但其本身还是相当于**final修饰**的，不可改变。我们去掉final，进行反编译：
> ![136OXR.png](https://s2.ax1x.com/2020/01/31/136OXR.png)


#### 任意作用域内的内部类
可以将内部类定义在任意的作用域内：
```java
public class Outer {
    public void test(final String s,final int value){
        final int a = value;
        if(value>2){
            class Inner{
                public void testM() {
                    //!false: s+="g";
                    //!false: a+=1;
                    System.out.println(s+", "+a);
                }
            }
            Inner in = new Inner();
            in.testM();
        }
        //!false:Inner i = new Inner();
    }
    public static void main(String[] args) {
        Outer o = new Outer();
        o.test("天乔巴夏",3);
    }
}
```
同样需要注意的是：
- 内部类定义在if条件代码块中，并不意味着创建该内部类有相应的条件。**内部类一开始就会被创建**，if条件只决定能不能用里头的东西。
- 如上所示，if作用域之外，编译器就不认识内部类了，因为它藏起来了。
### 静态内部类


即用`static`修饰的成员内部类，归属于类，即**它不存在指向外部类的引用**。
```java
public class Outer {
    static int a = 5;
    int b = 6;
    static class Inner{
        static int value;
        public void show(){
            //!false System.out.println(b);
            System.out.println(a);
        }
    }
}
class OuterTest {
    public static void main(String[] args) {
        Outer.Inner oi = new Outer.Inner();
        oi.show();
    }
}
```
需要注意的是：
- 静态内部类也可以定义非静态的成员属性和方法。
- 静态内部类对象的创建不依靠外部类的对象，可以直接通过：`new Outer.Inner()`创建内部类对象。
- 静态内部类中可以包含静态属性和方法，而除了静态内部类之外，即我们上面所说的所有的内部类内部都不能有（但是可以有静态常量`static final`修饰）。
- 静态内部类不能访问非静态的外部类成员。
- 最后，我们反编译验证一下：

![136Lc9.png](https://s2.ax1x.com/2020/01/31/136Lc9.png)
### 匿名内部类
这个类型的内部类，看着名字就怪怪的，我们先看看一段违反我们认知的代码：
```java
public class Outer {
    public InterfaceInner inner(){
    //创建一个实现InterfaceInner接口的是实现类对象
        return new InterfaceInner() {
            @Override
            public void show() {
                System.out.println("Outer.show");
            }
        };
    }
    public static void main(String[] args) {
        Outer o = new Outer();
        o.inner().show();
    }
}
interface InterfaceInner{
    void show();
}
```
真的非常奇怪，乍一看，`InterfaceInner`是个接口，而Outer类的inner方法怎么出现了`new InterfaceInner()`的字眼呢？接口不是不能创建实例对象的么？

确实，这就是匿名内部类的一个使用，其实inner方法返回的是实现了接口方法的实现类对象，我们可以看到分号结尾，代表一个完整的表达式，只不过表达式包含着接口实现，有点长罢了。所以上面匿名内部类的语法其实就是下面这种形式的简化形式：
```java
public class Outer {   
    class Inner implements InterfaceInner{
        @Override
        public void show(){
            System.out.println("Outer.show");
        }
    }
    public InterfaceInner inner(){ 
        return new Inner();  
    }
    public static void main(String[] args) {
        Outer o = new Outer();
        o.inner().show();
    }
}
interface InterfaceInner{
    void show();
}
```

不仅仅是接口，普通的类也可以被当作“接口”来使用：
```java
public class Outer {
    public OuterTest outerTest(int value) {
        //参数传给匿名类的基类构造器
        return new OuterTest(value) {
            
            @Override
            public int getValue() {
                return super.getValue() * 10;
            }
        };
    }
    public static void main(String[] args) {
        Outer o = new Outer();
        System.out.println(o.outerTest(10).getValue());//100
    }
}
class OuterTest {
    public int value;
    OuterTest(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
```
需要注意的是：
- **匿名类既可以扩展类，也可以实现接口**，当然抽象类就不再赘述了，普通类都可以，抽象类就更可以了。但不能同时做这两件事，且每次最多实现一个接口。
- 匿名内部类没有名字，所以**自身没有构造**器。
- 针对类而言，上述匿名内部类的语法就表明：<u>创建一个继承OuterTest类的子类实例</u>。所以可以在匿名内部类定义中调用父类方法与父类构造器。
- 传入的参数传递给构造器，没有在类中直接使用，可以不用在参数前加final。



## 内部类的继承
内部类可以被继承，但是和我们普通的类继承有些出处。具体来看一下：
```java
public class Outer {
    class Inner{
        private int value = 100;
        Inner(){
        }
        Inner(int value){
            this.value = value;
        }
        public void f(){
            System.out.println("Inner.f "+value);
        }
    }
}
class TestOuter extends Outer.Inner{
    TestOuter(Outer o){
        o.super();
    }
    TestOuter(Outer o,int value){
        o.super(value);
    }

    public static void main(String[] args) {
        Outer o = new Outer();
        TestOuter tt = new TestOuter(o);
        TestOuter t = new TestOuter(o,10);
        tt.f();
        t.f();
    }
}
```
我们可以发现的是：
- 一个类继承内部类的形式：`class A extends Outer.Inner{}`。
- 内部类的构造器必须链接到指向外部类对象的引用上，`o.super();`，即都需要传入外部类对象作为参数。

## 内部类有啥用

可以看到的一点就是，内部类内部的实现细节可以被很好地进行封装。而且Java中存在接口的多实现，虽然一定程度上弥补了Java“不支持多继承”的特点，但内部类的存在使其更加优秀，可以看看下面这个例子：
```java
//假设A、B是两个接口
class First implements A{
    B makeB(){
        return new B() {
        };
    }
}
```
这是一个通过匿名内部类实现接口功能的简单的例子。对于接口而言，我们完全可以通过下面这样进行，因为Java中一个类可以实现多个接口：
```java
class First implements A,B{
}
```
但是除了接口之外，像普通的类，像抽象类，都可以定义独立的内部类去单独继承并实现，**使用内部类使“多重继承”更加完善**。

---

由于后面的许多内容还没有涉及到，学习到，所以总结的比较浅显，并没有做特别深入，特别真实的场景模拟，之后有时间会再做系统性的总结。如果有叙述错误的地方，还望评论区批评指针，共同进步。
参考：
《Java 编程思想》
[https://stackoverflow.com/questions/70324/java-inner-class-and-static-nested-class?r=SearchResults](https://stackoverflow.com/questions/70324/java-inner-class-and-static-nested-class?r=SearchResults)