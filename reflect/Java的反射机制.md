[toc]
# 一、反射机制

## 1、概述
反射机制：将类的各个组成部分封装成其他对象，在运行状态中，可以动态获取类信息和调用类对象的方法。

## 2、优缺点
- 优点
    - 可以在程序运行过程中，操作类的组成部分。
    - 可以解耦，提高程序的可扩展性。
- 缺点
    - 产生一系列的解释操作，性能较差。

## 3、类加载的过程

java语言有着”一处编译，处处运行“的优良特点，能够很好地适应不同的平台，过程大致如下：

- 我们通过`javac`命令将`.java`文件编译，会在磁盘上生成**不面向平台**的字节码文件`.class`。
- 类加载器通过一系列的操作，如加载、连接和初始化之后，将`.class`文件加载进内存中。
- 将.class字节码文件代表的静态存储结构转化为方法区的运行时数据结构。
- 类加载时还会再内存中创建一个`java.lang.Class`的对象，该对象包含类的信息，作为程序从方法区访问各种数据的接口。



# 二、获取Class对象的三种方式
这个Class对象包含类的所有信息，也就是说，我们获得了这个Class类的对象，就可以访问到JVM中的这个类。那么，如何获取呢？主要有以下三种方式。

## 1、Class.forName("全类名")
通过`Class的静态方法forName("全类名")`，返回和全类名对应类的Class对象。
```java
    //Class.forName("全类名");
    Class cls1 = Class.forName("com.my.base.Person");
    System.out.println(cls1);
```
- 多用于配置文件，将类名定义在配置文件中，便可以动态读取文件，加载类。
- 全类名指：完整包名.类名，如果找不到，会抛出ClassNotFoundException的异常。
##  2、类名.class
通过`类名的class属性`获取，返回该类对应的Class对象。

```java
    //类名.class;
    Class cls2 = Person.class;
    System.out.println(cls2);
```
- 该方式在程序编译阶段就可以检查需要访问的Class对象是否存在，相对来说更加安全。
- 该方式无需调用方法，程序性能方面相对较好。

## 3、对象.getClass() 
`getClass()`方法在Object中定义，返回该对象所属类对应得Class对象。


```java
    //对象.getClass();
    Person p = new Person();
    Class cls3 = p.getClass();
    System.out.println(cls3);
```
---
同一个字节码文件`.class`在一次程序运行过程中，只会被加载一次，不论通过哪种方式获取的Class对象都是同一个。
```java
    // == 比较三个对象,都是true

    System.out.println(cls1 == cls2);
    System.out.println(cls1 == cls3);
    System.out.println(cls3 == cls2);
```


# 三、反射相关的方法

Class对象包含着类的所有信息，而这些信息被封装在`java.lang.reflect`包下，成了一个个的类，不完全统计如下：


| Method | Field  | Annotation | Constructor | Package | Modifier | Parameter  |
| ------ | ------ | ---------- | ----------- | ------- | -------- | ---------- |
| 方法类 | 字段类 | 注解类     | 构造器类    | 包类    | 修饰符类 | 方法参数类 |

Class类中提供了相应的方法，获取这些信息，由于方法众多，具体的还需要参看JDK官方文档。

我在这里总结几点通用的：

- 调用对应的`getXxx`通常是获得对应public修饰的单个信息，`getDeclaredXxx`则不考虑修饰符，找不到，则往超类找。


- 调用对应的`getXxxs`通常获得对应的public修饰信息的数组，`getDeclaredXxxs`则不考虑修饰符，找不到，则往超类找。

- 上面两者遇到参数的情况不同，如果本身用于获取public修饰信息的方法强行去获取达不到的权限，则会抛出异常：
    - 对于Field而言，括号内是: `"参数名"`。
    - 对于Method而言，括号内是: `"方法名",参数类型对应的类`。
    - 对于Constructor而言，括号内是：`"参数对应的类"`。


- Field类中有获取值的方法：`Object get(Object obj) `，也有设置值的方法：`void set(Object obj, Object value)`。
- Method类可以调用方法：`Object invoke(Object obj, Object... args)`。
- Constructor类可以创建实例：`T newInstance(Object... initargs)`，如果类中有无参构造器，可以直接利用Class类中的newInstance()方法创建实例。

- 在反射中，没有什么是私有的，如果有，那就使用`xxx.setAccessible(true)`暴力破解。
- 判断修饰符时，`getModifiers()`返回的时int值，是各个修饰符表示数的和，可以用位运算判断，`(xx.getModifiers()&Modifier.STATIC)!=0`表示存在static修饰符。



ps：肯定有遗漏的API，到时候用的时候，翻翻API就完事了。

# 四、Demo×2
##  1、尝试自己写一个clone()方法

```java
package com.my.reflect.practice;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @auther Summerday
 */

@SuppressWarnings("unchecked")
public class ImplementClone {
    public Object clone(Object o) throws Exception{
        //获取对象的实际类型
        Class<Object> clz = (Class<Object>) o.getClass();

        //获取所有构造方法
        Constructor<Object>[] cs = (Constructor<Object>[]) clz.getDeclaredConstructors();
        //任取一个构造方法
        Constructor<Object> c = cs[0];

        //防止取出构造方法为私有
        c.setAccessible(true);

        //该构造方法参数有or无？

        //获取参数类型
        Class[] ps = c.getParameterTypes();
        //存储参数的数组
        Object[] os = new Object[ps.length];

        for(int i = 0;i<ps.length;i++){
            //判断是否为基本类型
            if(ps[i].isPrimitive()){
                if(ps[i] == boolean.class)
                    os[i] = false;
                else if(ps[i] == char.class)
                    os[i] = '\u0000';
                else
                    os[i] = 0;
            }else{
                os[i] = null;
            }
        }
        //执行构造方法创建对象
        Object obj = c.newInstance(os);

        //获取属性数组
        Field[] fs = clz.getDeclaredFields();

        for (Field f : fs){

            //如果final修饰则返回，无法修改
            if((f.getModifiers()&Modifier.FINAL)!=0){
                continue;
            }
            //暴力破解
            f.setAccessible(true);

            //取出原属性值
            Object value = f.get(o);

            //将取出的属性值赋值给新对象的属性
            f.set(obj, value);
            
        }
        return obj;
    }
}

```

## 2、利用配置文件动态加载
```java
package com.my.reflect.practice;

/**
 * @auther Summerday
 */

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 *
 *     参考黑马程序员教学视频
 *
 * 实现：1、配置文件  2、反射
 *
 * 一、将需要创建的对象的全类名和需要执行的方法定义在配置文件中
 *
 * 二、在程序中加载读取配置文件
 *
 * 三、使用反射来加载类文件进内存
 *
 * 四、创建对象
 *
 * 五、执行方法
 *
 */
public class ReflectTest {

    public static void main(String[] args) throws Exception {

        //1、加载配置文件

        //1.1 创建Properties对象
        Properties pro = new Properties();

        //1.2 加载配置文件，转换为一个集合

        //1.2.1获取class目录下的配置文件

        //创建类加载器
        ClassLoader classLoader = ReflectTest.class.getClassLoader();

        InputStream resourceAsStream = classLoader.getResourceAsStream("pro.properties");
        pro.load(resourceAsStream);

        //2、获取配置文件中定义的数据
        String className = pro.getProperty("className");
        String methodName = pro.getProperty("methodName");

        //3、加载该类进内存
        Class cls = Class.forName(className);

        //4、创建对象
        Object obj = cls.newInstance();

        //5、获取方法
        Method method = cls.getMethod(methodName);

        //6、执行方法
        method.invoke(obj);

    }
}
```
---

以后会有越来越多真实的场景需要用到反射这项灵魂技术，总之，好好看，好好学。