# Java关键字之this
## 概念
this关键字只能在方法内部使用，表示对“正在调用方法的对象”的引用。
具体来讲。有以下两种情况：
- 构造器中引用该构造器**正在初始化的对象**。
- 在方法中引用**调用该方法的对象**。

```JAVA
package com.my.pac09;

/*
 *  ThisTest.java
 *  this  对“调用方法那个对象”的引用
 */

public class ThisTest {
    private int height;
    public int width;

    public void setHeight(int height) {
        //this.height代表对象属性，和形参height进行区分
        this.height = height;
    }

    public int getHeight() {
        //省略了this,下面两句效果相同
        //都显示正在调用method方法。
        method();
        this.method();
        return this.height;
    }

    public ThisTest addOneToWidth() {
        //this代表正在调用addOneToWidth方法的对象的引用
        this.width++;
        System.out.println("宽度加一，现在是："+this.width);
       //返回该对象的引用
        return this;
    }

    public ThisTest(int height) {
        this.height = height;
    }

    public ThisTest(int height, int width) {
        //调用ThisTest(int height)构造器
        this(height);
        this.width = width;
    }

    public void method() {
        System.out.println("正在调用method方法");
    }
}
```


```java
/*ThisAllTest.java*/
package com.my.pac09;

public class ThisAllTest {
    public static void main(String[] args) {
        ThisTest test = new ThisTest(1, 2);
        System.out.println("高度为：" + test.getHeight() + "，宽度为： " + test.width);
        ThisTest me = test.addOneToWidth().addOneToWidth();
        System.out.println("宽度两次加1之后：" + me.width);

    }
}
```
```java
//测试结果
正在调用method方法
正在调用method方法
高度为：1，宽度为： 2
宽度加一，现在是：3
宽度加一，现在是：4
```
## 表现形式
- **内部调用同一个类的另外一个方法，可以省略this。**
```java
 public int getHeight() {
    //省略了this,下面两句效果相同
    //都显示正在调用method方法。
    method();
    this.method();
    return this.height;
}

//结果
正在调用method方法
正在调用method方法
```
> 虽然在非static的前提下，方法必须需要以`对象.方法`的形式调用，但是在一个方法中调用同类的另一个方法，对象已经创建，则不需要重复地在方法中再次创建，这时候this就是这个已创建对象的引用。加之省略this，也就成了如上直接`method();`的情况。
>
> -  **返回当前对象的引用。**
```java
public ThisTest addOneToWidth() {
    //this代表正在调用addOneToWidth方法的对象的引用
    System.out.println("宽度加一，现在是："+this.width);
    this.width++;
    //返回该对象的引用
    return this;
}
    
ThisTest me = test.addOneToWidth().addOneToWidth();

//结果
宽度加一，现在是：3
宽度加一，现在是：4
```
> 可以看到，`addOneToWidth()`方法返回的是**当前对象**的引用，所以可以在同一条语句里对对象执行多次操作。
- **在构造器中调用另一个构造器。**
```java
public ThisTest(int height) {
    this.height = height;
}

public ThisTest(int height, int width) {
    //调用ThisTest(int height)构造器
    this(height);
    this.width = width;
}
```
> 构造器中this表示的是**正在初始化的对象**的引用，`this`加上参数列表，就产生例如对另一个构造器明确的调用，可以减少许多代码量。
> 另外，前面提到，上一篇关于构造器提到，<u>this语句必须放在构造器执行体除注释外的第一句。因此，不能再一个构造器中同时运用两个this！！！</u>
- **形参名和属性名相同的情况。**
```java
public void setHeight(int height) {
    //this.height代表对象属性，和形参height进行区分
    this.height = height;
}
```
> 当然，形参处可以使用任何一个变量名称，但是，用`this.height=height;`的形式，可以很清楚地让别人区分传入形参和成员变量，且见名知义。
- **在方法内将自身传递给另外一个方法。**
> ps:这个点其实我还是不太能够掌握，或者说举不出一个合适的例子，所以我参照Thinking in Java 并进行一定的修改，以助理解。先附上代码：

```java
package com.my.pac09;
/*PassingThis.java*/
public class PassingThis {
    public static void main(String[] args) {
        Person p = new Person();
        Apple apple = new Apple(5);
        apple.displayPeelNum();//此时皮的数量：5
        p.eat(apple);
    }
}
class Person {
    public void eat(Apple apple) {
        System.out.println("开始剥皮...");
        Apple peeled = apple.getPeeled();
        peeled.displayPeelNum();
        System.out.println("剥完的苹果就是好吃~");
    }
}
class Apple {
    public int num;
    //构造器，传入皮的数量
    Apple(int num){
        this.num = num;
    }
    Apple getPeeled() {
        //意思是把apple传给Peeler的peel方法剥皮，this代表传进去的apple
        return Peeler.peel(this);
    }
    void displayPeelNum() {
        System.out.println("此时皮的数量：" + num);
    }
}
class Peeler {
    //static修饰的类方法，可以直接利用Peeler.peel(..)调用
    static Apple peel(Apple apple) {
        //...remove peel 剥皮的过程
        while (apple.num > 0) apple.num--;
        return apple;
    }
}
```
```java
//测试结果
此时皮的数量：5
开始剥皮...
此时皮的数量：0
剥完的苹果就是好吃~
```
> 可以看到，下面的部分**在Apple类中调用Peeler的peel方法**，将刚刚创建的apple对象作为形参传入其中进行剥皮操作，Peeler的peel是一个外部工具方法（将要用于许多其他的类，比如香蕉，梨等），所以将自身传递给外部方法，this就很有用。