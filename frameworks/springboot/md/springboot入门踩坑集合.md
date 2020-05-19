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

# 四、IDEA解决国际化中文乱码问题

解决方法：Settings设置，搜索File Encodings，做出如下修改：

![image-20200515145714809](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200515145714809.png)

# 五、日期格式转换问题(H5日期控件)

日期转换的问题出现的问题有很多，解决的方法也需要对症下药，但是需要明确，SpringBoot中关于日期格式的默认配置：

WebMvcProperties

```java
	/**
	 * Date format to use. For instance, `dd/MM/yyyy`.
	 */
	private String dateFormat;
```

如果我们需要yyyy-MM-dd的格式，只需要在配置文件中修改即可：

```properties
spring.mvc.date-format=yyyy-MM-dd
```

如果日期数据需要在H5date日期控件中回显，那么数据格式需要是yyyy-MM-dd。

```html
<input name="birth" type="date" class="form-control" placeholder="zhangsan" th:value="${#dates.format(emp.birth, 'yyyy-MM-dd')}">
```

# 六、Rest风格请求之hiddenHttpMethodFilter

之前学习SpringMVC时已经了解过Rest风格的PUT、DELETE请求如何发送，一个主要的类就是hiddenHttpMethodFilter，SpringBoot2.2.7版本的自动配置定义如下：

```java
	@Bean
	@ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
	@ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled", matchIfMissing = false)
	public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new OrderedHiddenHttpMethodFilter();
	}
```

matchIfMissing为false，也就是说，如果需要Rest风格的请求发送，需要在Spring配置文件中将enabled属性改为true。

```properties
spring.mvc.hiddenmethod.filter.enabled=true
```

# 七、SpingBoot2.0以上版本EmbeddedServletContainerCustomizer被弃用的替代方法

主要是利用嵌入式的Servlet容器定制器，来修改Servlet容器的配置，当然可以直接在SpringBoot配置文件中直接修改，如：

```properties
# tomcat 配置
server.tomcat.uri-encoding=UTF-8
```

还有一种使用代码方式配置，Spring2.0版本以上使用：WebServerFactoryCustomizer。

```java
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            //定制嵌入式的Servlet容器相关的规则
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                factory.setPort(8084);
            }
        };
    }
```

