[toc]

# Java面向对象之继承

> **继承**是面向对象的第二大特征，是实现软件复用的重要手段，也是面向对象的基石。接下的篇目将会围绕着继承这一特征展开探究。

## 引言 
 继承这个概念，在我们生活中也存在。
 - 就比如，老王和他的儿子小王是父亲与儿子的关系，小王以后是要继承老王的衣钵的！
 - 很显然，小王很多方面都和老王一样，有一样的姓氏，一样的地址……
 - 老王教会小王很多为人处世的道理，小王在很多行为方面效仿老王。
 - 老王爱喝酒，小王却不喜欢，反而喜欢可乐，小王的儿子小小王喜欢喝雪碧。
 - 甚至，符合伦理的话，小王不能有两个爸爸，但是老王可以有两个儿子。

 老刘是老王的好朋友，老刘通过老王就能够知道小王的许多特点……
 哈哈哈，说了一大堆，里面说出了许多可以类比于Java继承的特点。
 下面的程序，其实略有不妥，因为创建类的时候格局太小。。但是自己理解起来相对会容易一些，后续将会完善例子。
 ```java
package com.my.pac11;

/**
 * @author Summerday
 * @date 2019/12/9 19:45
 */
//老王类
public class LaoWang {
    private String lastName;//姓氏
    public String address;//家庭住址
    //super类构造器
    public LaoWang() {
        System.out.println("未知信息老王创建");
    }

    public LaoWang(String address) {
        this.address = address;
        System.out.println("创建家住在" + this.address + "王xx");
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }


    public void doGoodThings() {
        System.out.println(this.getLastName() + "做好事……");
    }

    public void drink() {
        System.out.println("老王酗酒……");
    }
    //重写Object类的toString方法
    public String toString(){
        return "Object类是所有类的直接父类或间接父类";
    }
}

//老李类
class LaoLi {
    public LaoLi() {
        System.out.println("我是老李！");
    }
}
//LaoWang 是LittleXiaoWang的间接父类
class LittleXiaoWang extends XiaoWang {
    //没有构造器会报错，子类必须第一步调用父类构造器
    LittleXiaoWang(String n, String a) {
        super(n, a);
    }
    //重写XiaoWang的方法，继承谁就改谁的
    public void drink() {
        System.out.println(this.getLastName() + "不喜欢喝可乐,喜欢雪碧……");
    }
}
//一个类最多只有一个直接父类 ，可以有很多间接父类
//false: class XiaoWang extends LaoLi,LaoWang{...}
class XiaoWang extends LaoWang {
    //子类构造器
    XiaoWang(String lastName, String address) {
        //super调用父类构造器
        super(address);
        setLastName(lastName);
    }

    //重写父类的方法
    public void drink() {
        System.out.println(this.getLastName() + "不酗酒，喜欢喝可乐……");
    }

    public static void main(String[] args) {
        //先调用父类适合的构造器
        //创建家住在浙江王xx
        XiaoWang xw = new XiaoWang("小王", "浙江");
        //false:System.out.println(l.lastName);
        System.out.println(xw.getLastName() + "," + xw.address);//小王,浙江
        //做好事……
        xw.doGoodThings();
        //小王不酗酒，喜欢喝可乐……
        xw.drink();
        //自动调用toString方法
        System.out.println(xw);
        System.out.println("********************");
        //依旧先调用父类适合的构造器
        //创建家住在浙江王xx
        LittleXiaoWang lxw = new LittleXiaoWang("小小王", "浙江");
        System.out.println(lxw.getLastName() + "," + lxw.address);//小小王,浙江
        //小小王做好事……
        lxw.doGoodThings();
        //小小王不喜欢喝可乐,喜欢雪碧……
        lxw.drink();
    }
}
 ```
```java
//测试
创建家住在浙江王xx
小王,浙江
小王做好事……
小王不酗酒，喜欢喝可乐……
Object类是所有类的直接父类或间接父类
********************
创建家住在浙江王xx
小小王,浙江
小小王做好事……
小小王不喜欢喝可乐,喜欢雪碧……
```
## 继承的特点

### 语法格式
```java
[修饰符] class Subclass extends Superclass
{
    //类定义部分
}
```
### 父子类的关系
- extends是继承的关键字，Subclass是**子类**，也叫派生类，Superclass是**父类**，也叫基类，超类。
- **子类一旦继承父类，将会获得父类所有的结构**，也就是属性和方法。但是父类中用private修饰的成员(lastName)，子类无法直接访问，但是可以通过父类提供的可访问的方法来证明，如`xw.getLastName()`。
```java
XiaoWang xw = new XiaoWang("小王", "浙江");
//false:System.out.println(l.lastName);
System.out.println(xw.getLastName() + "," + xw.address);//小王,浙江
```
- **一个子类最多有一个直接父类**，但可以有无限多个间接父类；一个父类可以有很多子类。
```java
//下面的语句错误！
class XiaoWang extends LaoLi,LaoWang{...}
```
- 子类父类是相对的概念，一个类可以是父类，也可以是子类，比如例中的XiaoWang。
- 如果并没有显式指定一个类的直接父类，那么这个类将默认扩展java.lang.Object类，也就是说**Object类是所有类的直接或间接父类**。关于Object类之后会花一定篇幅进行总结。
```java
//重写Object类的toString方法
public String toString(){
    return "Object类是所有类的直接父类或间接父类";
}
```
## 继承要点
除了下面谈到的几个，还有好多好多，待补充！
### 重写父类方法
子类可以以父类为基础，额外增加新的成员变量或者方法。也可以**重写父类的方法**，就是相同方法名加参数列表，但是要定义不同的行为。（重写在下篇讲述……）
```java
//父类的方法
public void drink() {
    System.out.println("老王酗酒……");
}
//重写父类的方法
public void drink() {
    System.out.println(this.getLastName() + "不酗酒，喜欢喝可乐……");
}
```
### 继承中的构造器
关于继承中的**构造器**，接下来将做详细讲解，暂时空缺。
```java
//super类构造器
public LaoWang() {
    System.out.println("未知信息老王创建");
}

public LaoWang(String address) {
    this.address = address;
    System.out.println("创建家住在" + this.address + "王xx");
}

 //子类构造器
XiaoWang(String lastName, String address) {
    //super调用父类构造器
    super(address);
    setLastName(lastName);
}
```
### 继承中的super关键字
**super**关键字用于限定该对象调用它的父类继承得到的实例变量或方法。**super不能出现再static语句中**。(之后补充。。）
```java
 //super调用父类构造器
    LittleXiaoWang(String n, String a) {
        super(n, a);
    }
```

### ...

注：本文有一些内容待补充，虽然后面的内容都有涉猎，想着一点一点蚕食知识点，所以大家见谅，一定第一时间补充！


参考书籍：《疯狂Java讲义》