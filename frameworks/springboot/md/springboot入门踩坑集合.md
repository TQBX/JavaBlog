[toc]

# 一、Error:(8,26) java: 编码EUC_CN的不可映射字符

解决方法：在pom.xml文件中添加以下即可：

```xml
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
```

# 二、Web server failed to start. Port 8080 was already in use.（springboot解决8080端口被占用的方法）

参考：[https://blog.csdn.net/hk376755485/article/details/103121936](https://blog.csdn.net/hk376755485/article/details/103121936)

【方法一】

- 在cmd窗口中输入：`netstat -ano|findstr "8080"`，回车，会显示占用8080的进程号PID。
- 打开任务管理器，点击服务，找到对应PID的进程，停止服务即可。

【方法二】

- SpringBoot内嵌tomcat默认的端口号是8080，可以在application.properties或application.yml中修改。

![image-20200510100930331](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200510100930331.png)

# 二、启动SpringBoot的可执行jar 报错：target\spring-boot-hello-1.0-SNAPSHOT.jar中没有主清单属性

参考：[https://cloud.tencent.com/developer/article/1393574](https://cloud.tencent.com/developer/article/1393574)

解决办法：修改pom.xml

```java
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
        					//指定执行项
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

![image-20200509231756653](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200509231756653.png)