# IDEA配置WEB项目

现在网上很多视频都是eclipse，学起来可真是相当难受，有时候这个配置吧，你不动吧总缺点啥，动了吧，又怕待会崩了改不回去，纠结的很。

算是小心翼翼了这么久，决定总结以下。

> 环境：IDEA2019.3    JDK1.8     Tomcat 8.5.31

首先，创建项目或者模块，其实差不太多，我这里就创建module了。

![IDEA1](E:\1myblog\JavaBlog\JavaBlog\Others\pic\IDEA1.png)

选择JavaEnterprise，选择Web Application，servlet3.0之后支持注解配置web.xml，我这边就没有选择create web.xml了。

![idea2](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea2.png)

填写项目名和路径，这个不多bb。

![idea3](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea3.png)

在创建好的Web目录下，清晰可见一个src，一个web目录和一个jsp文件。

接着新建一个WEB-INF，里面创建两个目录：classes和lib。（当然这步其实不要也可以继续，只不过为了目录结构的完整性，就按照这样子创建）

![WEB-INF](E:\1myblog\JavaBlog\JavaBlog\Others\pic\WEB-INF.png)

对这个web应用进行配置，选择左上角File，点击Project Structure。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea5.png)

点击Modules，点击创建的module，接着选择path，点击Use module compile output path 使用自定义的模块输出目录。将下面两个目录都改为刚才创建好的classes目录，这里还是为了目录结构的完整性，存放web应用发布编译的.class文件。

不改的话，也没多大关系，IDEA会默认在project的目录out下面存放编译后的文件。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea6.png)

接着点击path旁边的Dependencies配置一些依赖的jar包啥的。点击右边的＋，选择第一个JARS or directories。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea7.png)

选择刚刚创建的lib目录，以后相关jar包就存放在lib中就ok。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea8.png)

接着选择Jar Directory。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea9.png)

# 配置Tomcat

点击Run，选择Edit Configurations。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\ida10.png)

选择Tomcat Server的local。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea11.png)

映入眼帘的是一些关于Tomcat的配置，After lauch勾选之后，在Tomcat部署完成会自动访问下面URL，URL可以不用动，因为它会自动智能调整。HTTP port和JMX port两个看着改，也可以不动。

习惯将http port改成80。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea12.png)

上图中点击Configure..，选择Tomcat的安装目录，以及相关jar包。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea13.png)

Deployment中，右边的加减可以设置要部署的项目，可增可减。Application是虚拟路径，可以设置成项目的名称，也可以设置缺省。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea14.png)

ok，配置完毕，可以在index.jsp中修改内容，然后启动了，正常启动和debug都ok。

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea15.png)

成功！

![6](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea16.png)

# 配置虚拟主机

下面这个问题困扰了我一段时间，网上搜了很久都没搜着，可能是我太菜了。我还是总结以下，不然以后遇到类似的问题，又歇菜了。

现在有一个小小的需求，要求在浏览器地址栏中输入：www.mmall.com就能跳转到定义的主页。

知道是配置虚拟主机的问题，但在idea中不知怎么结合起来，今天终于成功，总结一波。

一、在D盘下新建一个目录：www.mmall.com作为我们的web目录。

二、在Tomcat安装目录的conf/server.xml中配置虚拟主机和虚拟路径映射：

```xml
		<Host name="www.mmall.com" appBase="D:/www.mmall.com">
			<Context path="/" docBase="D:/www.mmall.com"/>
		</Host>
```

三、在`C:\Windows\System32\drivers\etc`目录的hosts文件中，增加：127.0.0.1 www.mmall.com。

四、将IDEA创建好的项目的输出目录更改为www.mmall.com目录。

![配置虚拟主机](E:\1myblog\JavaBlog\JavaBlog\Others\pic\配置虚拟主机.png)

至此，大功告成，石头落地。

![idea17](E:\1myblog\JavaBlog\JavaBlog\Others\pic\idea17.png)