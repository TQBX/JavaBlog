[toc]

# 什么是Tomcat

Tomcat是一个网络服务器软件，它支持全部jsp和servlet规范，可以用以部署web项目。

安装了服务器软件的计算机，就是服务器。

# 安装注意点

安装目录不要包含中文和空格。（养成好习惯）

配置环境变量`JAVA_HOME`到Tomcat运行需要的jdk。

![image-20200313203840952](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200313203840952.png)

安装完毕，可以进入安装目录`D:\apache-Tomcat-8.5.31\bin`，bin目录下双击`startup.bat`文件。

![Tomcatstartup](E:\1myblog\JavaBlog\JavaBlog\Tomcat_servlet\pic\Tomcatstartup.png)

上述操作之后，出现黑窗口，然后打开浏览器，输入`http://localhost:8080/`，就可以看见可爱的Tomcat了。

如果要正常关闭的话：可以进入bin目录下双击`shutdown.bat`，也可以在黑窗口按`ctrl+c`。

非正常关闭，就直接叉掉黑窗口。

##  过程中可能出现的问题

- 没有正确导致环境变量`JAVA_HOME`导致黑窗口一闪而过，正确配置环境变量即可。

- 8080端口号被占用导致启动失败，有两种解决办法：

  - cmd打开，找到正在占用8080端口号的进程，在任务管理器里关掉它。

  - 修改Tomcat目录下的`conf/server.xml`中，找到下面这段，把修改端口号。

    ```xml
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
    ```

- 一般来说，习惯将端口改为80，即` port="80"`,因为80端口号是http协议的默认端口号，设置之后就不需要输入端口了。

# Tomcat结构目录

bin：存放Tomcat启动关闭所用的批处理文件

conf：Tomcat的配置文件

lib：Tomcat运行所需jar包

logs：Tomcat运行时产生的日志文件

temp：Tomcat运行时使用的临时目录

webapps：web应用所应存放的目录

work：Tomcat工作目录，用于存放jsp、servlet编译后生成的`.java`，`.class`文件。

# 虚拟主机与web应用

虚拟与真实相对，一台真实的主机可以配置多个站点，多个站点运行在各自的虚拟主机之中，一个网站可以看作是一个虚拟主机。

虚拟主机中可以存放许多web资源，但是这些web资源不能直接给虚拟主机管理，需要按照一定的方式组织web应用。也就是说，web应用就是把功能相关的所有web资源（静态资源如html，css，js文件，动态资源如jsp，java程序等）组织了起来。

# 利用Tomcat部署项目

## 方式一

在webapps目录下新建一个目录，目录名即为虚拟目录，在里面存放资源文件，如html等资源。`D:\apache-Tomcat-8.5.31\webapps\web\hell.html`。

在网址栏输入：`http://localhost:8080/web/hello.html`即可访问资源。

### URL组成部分

http：协议

localhost：主机（域名）

8080：端口号

web：当前web应用

hello.html：资源

web/hello.html：URI统一资源标识符

http://localhost:8080/web/hello.html：统一资源定位符，是URI的子集

/hello.html：资源名称。

### 简化部署的方式

将项目打包成`.war`的压缩包，复制到webapps目录下，将会自动解压缩生成项目目录，删去war包，目录也会自动删去。

### 虚拟目录的缺省

webapps中，ROOT目录所在的位置即为虚拟目录缺省，也就是说，将web目录改为ROOT就可以让当前的web的虚拟目录缺省。但是，Tomcat的webapps中，本身就存在着ROOT目录，想要完成此操作，需要先将它改名。

# 虚拟目录配置

Tomcat中，webapps目录用于存放web应用，如果多个web站点的目录都放在webapps目录下，既不利于web站点目录的管理，也会导致磁盘空间不够。

因此我们可以将web站点的目录分散到其他磁盘管理，这时就需要配置虚拟目录，默认情况下，只有webapps下的目录才能被Tomcat自动管理成一个站点。

把web应用所在目录交给web服务器管理，这个过程就叫做虚拟目录的映射，就是虚拟路径与真实路径的关系。

## 方式二

首先我随便在一个磁盘中（就E盘吧）创建一个目录，里面写一个hello.html文件。接着，在Tomcat的conf/server.xml文件中`<Host>`标签体内写上下面的内容。

```xml
<!-- 部署项目-->
<Context path="/virtual" docBase="E:\web"/>
```

- docBase：项目存放的真实路径，注意真实路径一定不能中间加空格，血淋淋的教训啊，建议直接在地址栏复制地址。

- path：虚拟目录

映射完成之后，我们就不用将web的目录都放在webapps目录下了，在浏览器中输入：`http://localhost:8080/virtual/hello.html`，就可以访问资源了。

### 虚拟目录的缺省方式

如果将path的路径完全删除，改成下面这样：

```xml
<!-- 部署项目-->
<Context path="" docBase="E:\web"/>
```

这时当前web目录的虚拟目录即为缺省，浏览器中输入`http://localhost:8080/hello.html`即可不用写虚拟目录。

---

ps：server.xml是Tomcat的核心配置，一般不建议在里面进行配置。

## 方式三

在`D:\apache-Tomcat-8.5.31\conf\Catalina\localhost`目录下创建任意名称的xml文件，我给他命名为`virtual.xml`，编写以下内容：

```xml
<Context docBase="E:\web"/>
```

此时docBase指向真实web应用的目录，而虚拟目录就是xml文件的名称。

在浏览器输入路径：`http://localhost:8080/virtual/hello.html`，一样可以访问资源，当然，我这里新建的xml文件名叫hello.xml。

> 如果希望虚拟目录为多级目录，如`http://localhost:8080/virtual/second/hello.html`，则需要用#将目录分级，xml文件命名为：`virtual#second.xml`

### 虚拟目录的缺省方式

即将xml文件的名称改为`ROOT.xml`，此时，虚拟目录即缺省。

---

这三种方式中，方式1和方式3支持热部署，即在不重启Tomcat的情况下，使应用的最新代码生效。

# Web应用目录结构

![](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200313232030244.png)

# 配置缺省主页

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
  version="3.1">

      <welcome-file-list>
        <welcome-file>index.html</welcome-file>
        <welcome-file>index.htm</welcome-file>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

</web-app>
```

`<welcome-file-list>`标签中的页面将作为缺省页面，则在地址栏中就不需要写页面名称就可以直接访问，可参照Tomcat的首页。

如果我们想设置主页是我们自己的页面，我们需要将`<welcome-file>index.html</welcome-file>`标签内的内容改为自己的，比如`<welcome-file>hello.html</welcome-file>`，注意代码执行自上而下，如果有两个主页文件，那么第一个会生效。

这样，我们在访问的时候，就不要指定资源名称了。

# 配置虚拟主机

一、在tomcat中`D:\apache-tomcat-8.5.31\conf`下的server.xml，增添以下信息。

![host](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\host.png)

二、重启tomcat后，自动生成summerday目录。

![summerday](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\summerday.png)

三、`C:\Windows\System32\drivers\etc`目录下找到hosts文件，在文件的最后一行添加`127.0.0.1 www.summerday.com`。

四、浏览器输入`http://www.summerday.com:8080/tqbx/hello.html`，即可访问资源。

# 浏览器访问Web资源流程图

![image-20200314125422732](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200314125422732.png)

参考：[Tomcat就是这么简单](https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484755&idx=2&sn=b09e747bd0af5e1899a47911f92d1afe&chksm=ebd74452dca0cd44ebbcdacab7373a72d0c769746eaa2bfa454d1dbc8295e3f93b0645c0ac58###rd)