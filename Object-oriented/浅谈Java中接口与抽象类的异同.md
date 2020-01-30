# 浅谈Java中接口与抽象类的异同
**抽象类**和**接口**这两个概念困扰了我许久，在我看来，接口与抽象类真的十分相似。期间也曾找过许许多多的资料，参考了各路大神的见解，也只能是简简单单地在语法上懂得两者的区别。硬着头皮，做出一波小总结。或许，学习了之后的知识，理解会更加深刻吧，奥利给！

## 语法上的区别
- **一个类最多只能继承一个抽象类，但是可以继承多个接口**。

- 接口中的变量声明默认是**final**的，但是抽象类中可以包含非final的变量。
- 接口中的成员必须都是**public**的，而抽象类的成员可以是普通类成员的常用风格，即public，private和protected都可以修饰。
- 接口应用关键字`implements`来实现，抽象类应使用关键字` extends`进行扩展。

- 一个普通类要是想要实现一个接口，就**必须实现接口中的所有方法**，但是继承抽象类的情况，可以让继承类用abstract修饰，从而不直接实现抽象类中的抽象方法。

- 接口只能继承接口，而抽象类能继承其他类或者多个接口，且抽象类可以在不提供接口方法实现的情况下实现接口。


- 抽象类中可以拥有构造方法，但不能用于创建实例，仅仅供子类去调用构造方法而已。而**接口中不允许有构造方法**。

## 深入理解

- JDK1.8之前，接口中全部为隐式抽象方法，不能有具体方法实现， 而抽象类可以有普通方法；JDK1.8之后，接口是**允许声明默认方法或静态方法**来提供具体的方法实现的。
> 在默认方法在接口中出现之前，抽象类相比于接口有一个明显的优势在于：有更好的**前向兼容性**（forward-compatibility），即**在不破坏既有代码的情形下继续在类中添加新的功能**。但是默认方法的出现，让接口也能够实现这个效果。

> - **抽象类是面向"对象"的**，它需要提供的是"对象"**应该具有的基本属性或基本的功能行为**，就像人具有年龄的属性，会干活等等。从相同抽象类继承的对象享有相同的基本特征，学生，老师，警察叔叔……他们都具有年龄，也都会干活，无非是可能大家年龄不同，干的活不一样罢了。而这些其实就是一种"is-a"的概念，也就是"学生类是人类的一种"。

```java
abstract class People{
    private int age;
    public abstract void doSomething();
}
```
> - **而接口是面向"功能"的**，它**需要定义对象具有的功能**，而不管是什么对象，我只需要知道你能干什么就行，我才不管你是谁。就像学习的功能，不管是机器还是学生都具备，他们就可以就都可以实现这个接口，而不关心他们自身的类型。那么一个对象的功能或者技能肯定是很多的，这时，接口的多实现就显得非常的合理。不同于"is-a"的关系，**接口代表的是一种"has-a"的概念，表示"对象具有学习的技能"**。

```java
interface studyable {
    void howToStudy();

    default void info() {
        System.out.println("study is important for everyone..");
    }
}
```

- 接口和抽象类都可以实现多态，多态的好处有目共睹，利用向上转型及动态绑定，即在运行时才确定对象的类型，大大增加了扩展性。
```java
public static void main(String[] args) {
    studyable[] studyables = new studyable[]{new Robot(), new Student()};
    for (studyable s : studyables) {
        s.howToStudy();
        s.info();
    }
    System.out.println("*************************************");
    People[] peoples = new People[]{new Student(), new Teacher()};
    for (People p : peoples) {
        p.doSomething();
    }

}
```
```java
//测试结果
Robot comes out
Student comes out!
robot need to practice
study is important for everyone..
student will read book..
study is important for everyone..
*************************************
Student comes out!
Teacher comes out!
student should study..
teacher should teach..
```




参考链接：[what is the difference between an interface and abstract class](https://stackoverflow.com/questions/1913098/what-is-the-difference-between-an-interface-and-abstract-class?noredirect=1)