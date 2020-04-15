# Configuration配置总览

- properties（属性）
  - property
- settings（全局配置参数）
  - setting
- typeAliases（类型别名）
  - typeAliase
  - package
- typeHandles（类型处理器）
- objectFactory（对象工厂）
- plugins（插件）
- environments（环境集合属性对象）
  - environment（环境子属性对象）
    - transactionManager（事务管理）
    - dataSource（数据源）
- mappers（映射器）
  - mapper
  - package

# properties标签设置

![image-20200415144218024](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415144218024.png)

在classpath下定义jdbc.properties文件

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///spring
jdbc.username=root
jdbc.password=123456
```

# settings全局信息设置

详情见：[https://mybatis.org/mybatis-3/zh/configuration.html#settings](https://mybatis.org/mybatis-3/zh/configuration.html#settings)

# typeAliases类型别名设置

![image-20200415144701359](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415144701359.png)

# environments设置

![image-20200415144856488](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415144856488.png)

# mappers设置

![image-20200415145153233](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415145153233.png)

