[toc]
# Java多态之动态绑定  
上篇回顾：多态是面向对象程序设计非常重要的特性，它让程序拥有 更好的可读性和可扩展性。
- 发生在继承关系中。
- 需要子类重写父类的方法。
- 父类类型的引用指向子类类型的对象。
自始至终，**多态都是对于方法而言**，对于类中的成员变量，没有多态的说法。
上篇说到：一个基类的引用变量接收不同子类的对象将会调用子类对应的方法，这其实就是**动态绑定**的过程。在理解动态绑定之前，先补充一些概念。

## 引用变量的类型
引用类型的变量具有两种类型：**编译时类型**和**运行时类型**。（也分别叫做声明类型和实际类型。举个简单的例子：
```java
//假设Student类是Person类的子类
Person p = new Student();
```
### 编译时类型
- 也叫声明类型，即由**声明变量时**的类型所决定。
- 上式的`Person`即为引用变量p的编译时类型。

### 运行时类型
- 也叫实际类型，即由指向**对象的实际类型**所决定。
- 上式的`Student`即为引用变量p的运行时类型。
## 方法绑定
将方法调用同方法主体关联起来被称为**绑定**。
### 静态绑定
在程序执行前进行绑定，叫做静态绑定，也称作前期绑定。在面向过程的语言中是默认的绑定方式。

在Java中，**用private、static和final修饰的方法**（static和final之后会做出总结）或构造器能够准确地让编译器调用哪个方法，就是静态绑定（static binding）。

### 动态绑定
**在运行时根据对象的运行时类型进行绑定，叫做动态绑定**，也叫做后期绑定。当然在Java中，除了静态绑定的那些方法，其他的调用方式就是动态绑定啦。
```java
public class DynamicBinding {
    //Object是所有类的超类，根据向上转型，该方法可以接受任何类型的对象
    public static void test(Object x) {
        System.out.println(x.toString());
    }

    public static void main(String[] args) {
        test(new PrimaryStudent());//Student
        test(new Student());//Student
        test(new Person());//Person
        test(new Object());//java.lang.Object@1b6d3586
    }
}

class Person extends Object {
    @Override
    public String toString() {
        return "Person";
    }
    public void run(){}
    public void count(int a){}
}

class Student extends Person {
    @Override
    public String toString() {
        return "Student";
    }
    public void jump(){}
}

class PrimaryStudent extends Student {
}
```
- 四句调用方法的语句中的形参，编译时类型都是`Object`。注意：**引用变量只能调用编译时类型所具有的方法。**
- 它们运行时类型各不相同，所以解释运行器在运行时，会调用它们各自类型中重写的方法。
- **相同的类型的引用变量，在调用同一个方法时，表现出不同的行为特征**，这就是多态最直观的体现吧。
### 方法表
我们还可以发现，`test(new PrimaryStudent());`的运行结果是`Student`,，结果很明显，因为` PrimaryStudent`类中并没有重写父类的方法，如果采用动态绑定的方式调用方法，虚拟机会首先在本类中寻找适合的方法，如果没有，会一直向父类寻找，直到找到为止。
> 那么，每次调用时都要向上寻找，时间开销必然会很大。为此虚拟机预先为每个类都创建了**方法表**，其中列出了所有的**方法签名**（返回值类型不算）和**实际调用的方法**，这样子的话，在调用方法时直接查表就可以了。（值得一提的是，如果用super限定调用父类方法，那么将直接在实际类型的父类的表中查找）

- 下面是`Person`类的方法表：
```java
Person：
    //下面省略Object方法签名
    //xxx()-> Object.xxx()
    //方法签名->实际调用的方法
    toString()->Person.toString()
    run()->Person.run()
    count(int)->Person(int)
```
- 下面是`Student`类的方法表：
```java
Student:
    //下面省略Object方法签名
    //xxx()-> Object.xxx()
    //方法签名->实际调用的方法
    toString()->Student.toString()
    jump()->Student.jump()
    run()->Person.run()
    count(int)->Person(int)
```
- 下面是`PrimaryStudent`类的方法表(`PrimaryStudent`类为空，直接继承`Student`类）：
```java
PrimaryStudentt:
    //下面省略Object方法签名
    //xxx()-> Object.xxx()
    //方法签名->实际调用的方法
    toString()->Student.toString()
    jump()->Student.jump()
    run()->Person.run()
    count(int)->Person(int)
```
- 因此，在执行`test(new PrimaryStudent());`语句时，虚拟机将会提取`PrimaryStudent`的方法表。
- 虚拟机将会在表中搜索定义`toString`签名的类。这时虚拟机已经知道需要调用`Student`类型的`toString()`方法。
- 最后，调用方法，完毕。

> 动态绑定大大提升了程序的可扩展性，比如，我现在要新增一个`Teacher`类，可以直接让`Teache`r类继承于`Person`类，再用`Object`类的引用指向`Teacher`对象，而不用做其他的代码调整，动态绑定自动搞定，就相当舒服。