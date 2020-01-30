[toc]
# 小白学Java：内部类

## 内部类的定义

即将一个类的定义放在另一个类定义的内部，就像下面这样：
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
哦呦，有意思了，我们在另一个类OuterTest中再次测试我们之前定义的内部类，结果出现了非常明显的变化，我们陷入了沉思：
- 我们不能够像之前一样，用`Inner in = new Inner();`创建内部类实例。
- 没关系，我们可以通过Outer对象的returnInner方法，来创建一个实例，成功！
- 需要注意的是：我们如果需要一个内部类类型的变量指向这个实例，我们需要明确指明类型为：`Outer.Inner`，即`外部类名.内部类名`。
- 好啦，得到的内部类对象，我们试着**直接去访问它的私有属性！失败！**
- 那就老老实实地通过getId方法访问吧，成功！

说到这，我们大概就能猜测到：内部类的存在可以很好地隐藏一部分具有联系代码，实现了那句话：我想让你看到的东西你随便看，不想让你看的东西你想看，门都没有。

## 链接到外部类
其实我们之前在分析ArrayList源码的时候，曾经接触过内部类。我们在学习迭代器设计模式的时候，也曾领略过内部类带了的奥妙之处。
下面我通过《Java》编程思想上：通过一个内部类实现迭代器模式的简单案例做相应的分析与学习：
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
## .new和.this
我们稍微修改一下最初的Outer:
```java
public class Outer {
    String id = "乔巴";
    class Inner{
        private String id = "夏天";

        public String getId() {
            return id;
        }
        public Outer returnOuter(){
            return Outer.this;
        }
    }
}
```
- Outer和Inner的内部都有同名id属性，并不冲突。
- 在内部类Inner体内添加了returnOuter的引用，`return Outer.this;`，即`外部类名.this`。

我们来测试一波：
```java
class TestOuter{
    public static void main(String[] args) {
        Outer.Inner oi = new Outer().new Inner();
        System.out.println(oi.getId());//夏天
        Outer o = oi.returnOuter();
        System.out.println(o.id);//乔巴
    }
}
```
- 外部类产生内部类对象的方法已经被我们删除了，这时我们如果想要通过外部类对象创建一个内部类对象：`Outer.Inner oi = new Outer().new Inner();`，即在外部类对象后面用`.new 内部类构造器`。

## 其他类型的内部类

### 局部内部类
即在方法作用域内创建一个完整的类。
```java
public class Outer {
    public TestOuter test(final String s){
        final int a = 0;
        class Inner implements TestOuter{
            @Override
            public void testM() {
                //!false: s+="g";
                //!false: a+=1;
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

### 任意作用域内的内部类
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
- 内部类定义在if条件代码块中，并不意味着创建该内部类有相应的条件。内部类一开始就会被创建，if条件只决定能不能用里头的东西。
- 如上所示，if作用域之外，编译器就不认识内部类了，因为它藏起来了。

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
真的非常奇怪，乍一看，InterfaceInner是个接口，而Outer类的inner方法怎么出现了`new InterfaceInner()`的字眼呢？接口不是不能创建实例对象的么？

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
- 针对类而言，上述匿名内部类的语法就表明：<u>创建一个继承OuterTest类的子类实例</u>。所以可以在匿名内部类定义中调用父类方法与父类构造器。
- 传入的参数传递给构造器，没有在类中直接使用，可以不用在参数前加final。