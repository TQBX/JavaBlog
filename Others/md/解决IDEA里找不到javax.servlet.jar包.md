# 解决IDEA找不到javax.servlet.jar包的问题

配置完tomcat，准备照着资料实现Servlet接口，发现IDEA中并不能找到javax.servlet.jar包，一番查找之后，解决了问题，解决方法如下：

一、右键点击项目，选择`Open Module Settings`。

![34ZSc4.png](https://s2.ax1x.com/2020/03/03/34ZSc4.png)

二、选择`Libraries`，点击加号`+`，选择`Java`。

![34Vz3F.png](https://s2.ax1x.com/2020/03/03/34Vz3F.png)

三、浏览目录，找到tomcat安装目录下的lib里的`servlet-api.jar`包，点击ok。

![34Z1Et.png](https://s2.ax1x.com/2020/03/03/34Z1Et.png)

四、最后选择项目即可。

![34Vx9U.png](https://s2.ax1x.com/2020/03/03/34Vx9U.png)