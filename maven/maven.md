# maven仓库

本地仓库：默认在`${user.home}/.m2/repository`，可以通过`<localRepository>E:\Java\maven_repository</localRepository>`指定。

远程仓库：

中央仓库：

默认：根据maven工程中jar包的坐标，先本地->中央仓库，如果不能联网，就连接不到中央仓库

公司中：根据maven工程中jar包的坐标去本地仓库寻找，找不到->远程仓库（私服）->中央

# maven安装

解压maven安装包到一个没有中文字符的路径`E:\Java\apache-maven-3.5.2`，maven中目录结构如下：

![image-20200324110609679](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200324110609679.png)

配置环境变量：点击此电脑->右键属性->高级系统设置->环境变量

MAVEN_HOME：`E:\Java\apache-maven-3.5.2`，maven的安装目录。

![image-20200324110914714](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200324110914714.png)

path添加`%MAVEN_HOME%\bin`。

最后在cmd窗口中进行测试：`mvn -v`，出现下图代表maven已经配置成功。

![mvn -v](E:\1JavaBlog\maven\pic\mvn -v.png)

# maven标准目录结构

`src/main/java`：核心代码

`src/main/resources`：配置文件

`src/test/java`：测试代码

`src/test/resources`：测试配置文件

`src/main/webapp`：页面资源：js、css、图片等资源

# maven命令

mvn clean：将原先编译后的文件目录target清空（编译，打包，安装所有执行过程都会在target文件夹中生成对应文件）

mvn compile：将源码和配置文件编译输出到项目根目录target/classes中，test包中所有内容在编译、打包、安装过程都不参加，但会运行测试。

mvn test：测试代码，测试报告会打印到target文件夹中，生成报告文件。

mvn package ：将当前项目所有运行资源打包称java工程的jar/war包，打包类型由pom.xml定义：`<packaging>war</packaging>`。

mvn install：将项目生成到maven仓库，作为可以使用的资源文件。

# maven配置文件

conf/settings.xml，默认的本地仓库

![image-20200324111333758](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200324111333758.png)

jar包坐标

```java
		<dependency>
			<groupId>javax.servlet.jsp</groupId>公司组织的名称
			<artifactId>jsp-api</artifactId>项目名
			<version>2.0</version>版本号
			<scope>provided</scope>
		</dependency>
```



# 安装过程遇到的问题：

#### 【一】创建完之后没有绿色三角，需要自动导入

#### 【二】IDEA无法识别mvn命令，需要在用户path添加maven目录的bin。