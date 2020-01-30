[toc]

# 浅谈Java的默认和静态方法

**允许在接口中声明默认方法和静态方法**，是JDK1.8新增的特性。存在即合理，两者的出现，让接口越来越像抽象类（关于两者之别下一篇做总结），那么它们为什么出现呢，它们的出现产生了哪些便利，Java小白开始学习并总结，不足之处，还望评论区指点一二！

## Java新增默认方法有啥用

**官方解答**：<u>默认方法允许您添加新的功能到现有库的接口中，并能确保与采用旧版本接口编写的代码的二进制兼容性。</u>
这个光看枯燥的介绍好像很难理解，举个简单的例子。假设有一个很大很大的项目，一个接口被很多很多的类所实现，大家都平安无事平稳地运行着。突然有一天，出现了一个小小地问题，或者说有一个更好的优化方案，需要在这些实现类去增加。在默认方法出现之前，只有抽象方法，且需要在实现类中给出具体定义才能操作，那岂不是只能两眼一闭，直接从早干到晚地添加啦。
但是，**默认方法地出现允许在接口中给出方法的具体实现，且实现类中能够自动实现默认方法**，我只需要将这个优化放在接口的默认方法里面，就能完成对所有实现类的优化啦。当然，纯属个人理解，如果我的例子有不恰当的地方，欢迎指正哦。

```java
package com.my.pac21;

/**
 * @auther Summerday
 */

interface Closable {
    void close();
    //假设是新增的默认方法
    default void makeSound() {
        System.out.println("peng!");
    }
}

interface Openable {
    default void makeSound() {
        System.out.println("peng!");
    }
}

class Window implements Closable {

    @Override
    public void close() {
        System.out.println("Window.close");
    }
}

public class Door implements Closable, Openable {

    @Override
    public void close() {
        System.out.println("Door.close");
    }

    //两个接口中包含同名的方法，需要重写，指定一个
    @Override
    public void makeSound() {
        System.out.println("need to override default methods");
    }

    public static void main(String[] args) {
        Closable cw = new Window();
        Closable cd = new Door();
        cw.close();//Window.close
        cd.close();//Door.close

        //实现默认方法
        cw.makeSound();//peng!
        cd.makeSound();//need to override default methods
    }
}
```
## Java新增的静态方法有啥用

默认方法和静态方法的在接口的出现让接口失去“全是抽象方法”的特性，在探究完新增的默认方法之后，我们该对静态方法下手啦。开始疯狂查找资料。。。

> Before Java 8 made it possible to declare static methods in interfaces, it was common practice to place these methods in companion utility classes. For example, the java.util.Collections class is a companion to the java.util.Collection interface, and declares static methods that would be more appropriate in the relevant Java Collections Framework interfaces. You no longer need to provide your own companion utility classes. Instead, you can place static methods in the appropriate interfaces, which is a good habit to cultivate.

这个是我在stack overflow上找到的答案，什么意思呢，在没有新增静态方法之前，我们如果想让一些固定的操作在接口中出现，就必须定义一个和接口配套的实现类。而接口中静态方法的出现，可以直接通过接口调用静态方法。

```java
package com.my.pac21;

/**
 * @auther Summerday
 */
public class Test {
    public static void main(String[] args) {
        int val1 = 5;
        int val2 = 6;
        //通过创建实现类的对象
        Countable b = new CountableCompanion();
        System.out.println(b.getNum(val1, val2));
        //直接通过接口调用
        Countable.getMul(val1,val2);
    }
}
interface Countable{
    //普通抽象方法
    int getNum(int a,int b);
    //静态方法
    static int getMul(int a,int b){
        return a*b;
    }
}
//实现接口的实现类
class CountableCompanion implements Countable{
    @Override
    public int getNum(int a,int b) {
        return a+b;
    }
}
```
这是一个我自认为还比较幼稚的例子，仅供理解。
- **普通抽象方法**的情况：我在接口中定义了一个抽象方法，而后我又定义了实现该方法的实现类，最后通过创建实现类的实例来调用该方法，最后算得两值之和。可以想象，在实际中，如果相同性质的方法想要在多个实现类中实现，这种做法是比较麻烦的。
- **静态方法**的情况：就很直接地在接口中定义静态方法，且可以被接口直接调用，**不需要再定义与其配套的实现类**，多舒服哦。