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

> 需要注意的是，这些标签的顺序需要按照当前排列的顺序定义，否则在解析xml配置的时候，导致解析失败，抛出异常：`元素类型为 "configuration" 的内容必须匹配 "(properties?,settings?,typeAliases?,typeHandlers?,objectFactory?,objectWrapperFactory?,reflectorFactory?,plugins?,environments?,databaseIdProvider?,mappers?)"。`

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

![image-20200419120601820](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200419120601820.png)

# typeAliases类型别名设置

![image-20200415144701359](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415144701359.png)

# environments设置

environments可以配置多种环境，通过default属性指定使用环境。

![image-20200415144856488](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415144856488.png)

# databaseIdProvider设置

![image-20200419195604111](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200419195604111.png)

# mappers设置

![image-20200415145153233](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415145153233.png)

