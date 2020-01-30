# Java封装与访问权限控制（二）

**访问权限控制是具体实现的隐藏**，是封装性的一部分体现。前面提到几个访问控制修饰符，是访问权限控制的一部分。接下来要探讨这块另一个重要的概念，**包（package）**。

## 包：库单元

**包解决了什么问题？**

Java作为面向对象程序设计语言，"**高内聚，低耦合**"是设计的目标。既然这样，如何能做到高内聚，如何有效管理这些内聚的构件，包就是充当这一角色。包机制提供了类的多层**命名空间**（类似于C++中的命名空间namespace），很好解决了类命名冲突及类文件管理的问题。可以说，**包确保了类名的唯一性**。

> Java编译：
> - 在编译一个.java文件时，其中每个类都会有一个输出文件。
>  - 输出文件名和对应类地文件名相同，只是多了个后缀名`.class `。
>  - Java的可运行程序是一组可以打包并压缩为一个Java文档文件（JAR）的`.class`文件。
>  - Java解释器负责查找、装载和解释这些文件。

如何理解呢？类库实际就是一组类文件，每个文件中都有一个public类和若干个非public类，所以每个文件都有一个构件，package可以让这些构件从属于同一个群组。

package语句必须是除注释以外的第一句程序代码：**package+包名。包名格式是一串由`.`分隔的小写英文单词**，为了取一个独一无二的包名，一般以**域名（显然独一无二）逆序**作为包名。

值得注意的是，在给定包名时就已经隐含地指定了目录结构。
> Java解释器的运行过程：
> - 在环境变量CLASSPATH中查找.class文件的根目录。
> - 从根目录开始，将包名中的`.`替换成反斜杠（依据操作系统不同而不同），得到路径。
> - 将路径与CLASSPATH中的各个不同的项相连。
> - 在这些目录中查找.class文件。

但是需要注意：编译器在编译源文件的时候不会检查目录结构，也就是说，如果源文件没有在指定的目录下，编译不会出现错误，但是无法运行程序，因为包与目录不匹配，虚拟机找不到对应的类。
- 建议将`.java`源文件和`.class`文件分开存放，利于管理。
- 如果没有显示指定package语句，则处在默认包下，但是不建议。
- 同一个包下的类可以自由访问，但是假如在com再创建一个sub子包，那么这时处在两个包下的类是不能直接访问的，而需要带上类的全名（包名+类名），**也就是说，嵌套的包之间毫无关系，每个都拥有独立的集合。**

> 说实话，关于包这部分还是有些稀里糊涂，等待后续补充~
## import
> **不同包之间的类相互访问时，为了解决每次都需要带上类的全名的繁杂难题**，import应运而生。

- import语句需要出现在package语句之后，类定义之前。
- import可以向某个Java文件中导入指定包层次下某个类或全部类。
    - 假如现在想导入com.my.pac06包下的Overload类：`import com.my.pac06.Overload;`
    - 假如想导入还是这个包中的所有类(是类！不是包！）：`import com.my.pac06.*;`
- import导入类之后，在使用类时就可以省略包前缀（包名），直接用类名。



**特殊情况：**
- 如果两个包中含有名字相同的类，且这两个包都要用到，例如`java.sql`中和`java.util`中都有`Date`类，我们在同时导入时，系统就不知道该怎么办了。这时就**需要重新使用类的全名**：`java.sql.Date date = new java.sql.Date(6);`，没办法，import也救不了。

```java
package com.my.pac08;

import java.sql.*;
import java.util.*;

public class Tesr {
    /*Reference to'Date' is ambiguous,both
    * 'java.sql.Date'and'java.util.Date'match*/
//    Date date = new Date();
    java.sql.Date date = new java.sql.Date(6);
}

```
- **Java默认为所有源文件导入java.lang包下的所有类**，我们常用的String和System类再用的时候就没有需要import导入的情况。



## import static
> 静态导入，与import功能类似，不同在于import static用于导入指定类的某个静态成员变量、方法或全部的静态成员变量、方法。
```java
package com.my.pac08;
//静态导入 java.lang.System类的静态成员变量out
import static java.lang.System.out;
//同理的，导入所有静态成员变量
//import static java.lang.System.*;
public class Test {
    public static void main(String[] args) {
    //静态导入之后，可以直接省略类名
        out.println("Hello,World!");
    }
}

```
## Java常用包

> Java的核心类位于java包及其子包下。
> Java扩展的很多类位于javax包及其子包下。

以下罗列Java常用包：
- java.lang:包含Java许多核心类，诸如**String**，Math，System，**Thread**。无需import导入，因为系统自动导入该包。


- java.util：Java大量工具类/接口和**集合框架类**/接口，诸如Arrays，List，Set等。

- java.net：包含Java**网络编程**相关类/接口。
- java.io：包含Java输入输出编程相关的类/接口。
- java.text：包含Java格式化相关的类。
- java.sql：包含**Java进行JDBC数据库编程**的相关类/接口。

参考书籍：《疯狂Java讲义》、《Java编程思想》、《Java核心技术卷I》



​                      