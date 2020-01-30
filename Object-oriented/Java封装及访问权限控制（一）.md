[toc]
# Java面向对象之封装（一） 

对于封装的概念，我总觉得自己还是挺了解的，但是真要我说，还真说不出个啥来。我只能默默地通过身边的例子加上书本理论完善我对封装的认识。
> 就比如，我们在玩游戏的时候，我们只能通过完成指定任务获得金币，并不能直接修改金币的值，作为玩家的我们，如果轻易就能修改机密，那岂不是乱套啦。设计者明显不想让我们这么做，他们允许我们享受游戏，但是这些禁忌碰不得。这就是封装的一个例子。

## 封装的概念

**封装是面向对象三大特征之一。**
将对象的状态信息隐藏再对象内部，不允许外部程序直接访问对象内部信息，而是通过该类所提供的方法对内部信息进行操作和访问。

**优点**：

- 隐藏类的实现细节。 
- 使用者只能通过预定的方法访问数据，可以控制方法逻辑，限制不合理访问。 
- 可进行数据检查，利于保证对象信息的完整性。
- 便于修改，提高代码的可维护性。

**需要考虑**：
- 将对象的成员变量和实现细节隐藏起来，不允许外部直接访问。
- 把方法暴露出来，让方法来控制对这些成员变量进行安全的访问操作。

> **将该隐藏的隐藏起来，将该暴露的暴露出来。**


## 访问控制符
**控制级别**
public > protected >缺省> private

**访问控制级别表**

|            | private | default | protected | public |
| ---------- | ------- | ------- | --------- | ------ |
| 同一个类中 | √       | √       | √         | √      |
| 同一个包中 |         | √       | √         | √      |
| 子类中     |         |         | √         | √      |
| 全局范围内 |         |         |           | √      |

**private(当前类访问权限)**:被修饰的成员只能在该类的内部被访问。

**缺省(包访问权限)** ：缺省就是没有任何修饰符所修饰，缺省的成员可以被同一个包中的其他类所访问，关于包的概念，之后再提。

**protected(子类访问权限)** ：被修饰的成员既可以被同一个包中的其他类访问，也可以被不同包中的子类所访问。关于子类和父类之后将会总结~

**public(公共访问权限)** ：被修饰的成员可以被其他所有类访问，不论是否在同一包，不论是否具有继承关系。


**注意**

- **外部类只能由public或缺省两种修饰方式**，其他两个修饰没啥太大意义。
- Java源文件的命名问题：
    - 定义的所有类中没有用public修饰，文件名随意取，合法就行，但不建议这样。
    - <u>如果定义了public修饰的类，文件名必须与public修饰的类类名相同，所以一个java源文件中只能有一个public修饰的类。</u>

- private用来修饰成员变量非常合适，可以很好实现隐藏和封装的目的。
- 通常来说，用protected修饰一个方法，是希望子类来重写这个方法。

## 属性私有化
先来看看我们原先写过的简单的类及测试：
```java
package com.my.pac08;

public class People {
    public static void main(String[] args) {
        Man man = new Man();
        man.age = -4;
        man.name = "12345";
        man.run();
    }
}

class Man {
    int age;
    String name;

    void run() {
        System.out.println("running..");
    }
}

```
- 一切都是那么普通，可以不加修饰符的地方都没有加。
- 我们还是普通地创建了对象，普通地通过对象访问其属性。
- 但是这样子会产生一个很明显的问题:要是和上面一样，**赋上很离谱的值，就会产生同样离谱的结果**。
- 于是我们采用了以下的解决办法。
```java
package com.my.pac07;

public class Person {
    //private 修饰符对成员变量 进行隐藏
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() > 5) {
            System.out.println(name+"的长度太长，取名失败！");
            return;
        }
        System.out.println("取名成功！");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            System.out.println("输入年龄不合法！");
            return;
        }
        this.age = age;
    }
}

```
- 将name和age两个实例变量用**private**修饰。
- 加上与之匹配的一组方法，setter与getter方法用来设置与获取属性。
- 在设置方法处，加入了逻辑判断，限制非法或无效赋值。

```java
package com.my.pac07;

public class PersonTest {
    public static void main(String[] args) {
        Person p = new Person();

        /*private修饰属性，不在同一个类中无法直接访问，需要
          使用对应的getter和setter方法。
          错误 p.name = 5;
          错误 p.age = 10;
         */

        //名字长度超过限制，通过加入逻辑控制输入
        p.setName("Longname");

        System.out.println(p.getName());
        //赋符合标准的名字
        p.setName("Dady");

        System.out.println("p的名字是："+p.getName());
        //年龄超出限制，不能小于0
        p.setAge(-4);

        System.out.println(p.getAge());
        //赋正常年龄值
        p.setAge(10);

        System.out.println("p的年龄是："+p.getAge());
    }
}

```
- 无法再用对象.属性的方式直接访问。
- 需要通过setter方法设置合理值，用getter方法获取值。
- 两个方法的形式：``set+首字母大写的属性``，例如`setName`。getter方法同理。

以上简单的例子，就是**私有化属性**，隐藏需要隐藏的，并提供可以访问属性的方法，展示需要展示的，这就是封装性的体现之一。