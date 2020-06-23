参考：[https://juejin.im/post/5d107037e51d45599e019de8](https://juejin.im/post/5d107037e51d45599e019de8)

每次生成mapper.xml时候不是覆盖原文件，而是追加新内容，导致项目运行出错。

解决方法：

### 一、升级MyBatis Generator的版本为1.3.7以上。

```xml
<!-- MyBatis 生成器 -->
    <dependency>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-core</artifactId>
    <version>1.3.7</version>
    </dependency>
```

### 二、修改generatorConfig.xml的配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <properties resource="generator.properties"/>
    <context id="MySqlContext" targetRuntime="MyBatis3" defaultModelType="flat">
        <property name="beginningDelimiter" value="`"/>
        <property name="endingDelimiter" value="`"/>
        <property name="javaFileEncoding" value="UTF-8"/>
        <!-- 为模型生成序列化方法-->
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
        <!-- 为生成的Java模型创建一个toString方法 -->
        <plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>
        <!--生成mapper.xml时覆盖原文件-->
        <plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin" />
        <!--可以自定义生成model的代码注释-->
        <commentGenerator type="com.smday.mall.mbg.CommentGenerator">
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true"/>
            <property name="suppressDate" value="true"/>
            <property name="addRemarkComments" value="true"/>
        </commentGenerator>
        <!--配置数据库连接-->
        <jdbcConnection driverClass="${jdbc.driverClass}"
                        connectionURL="${jdbc.connectionURL}"
                        userId="${jdbc.userId}"
                        password="${jdbc.password}">
            <!--解决mysql驱动升级到8.0后不生成指定数据库代码的问题-->
            <property name="nullCatalogMeansCurrent" value="true" />
        </jdbcConnection>
        <!--指定生成model的路径-->
        <javaModelGenerator targetPackage="com.smday.mall.mbg.model" targetProject="src\main\java"/>
        <!--指定生成mapper.xml的路径-->
        <sqlMapGenerator targetPackage="com.smday.mall.mbg.mapper" targetProject="src\main\resources"/>
        <!--指定生成mapper接口的的路径-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.smday.mall.mbg.mapper"
                             targetProject="src\main\java"/>
        <!--生成全部表tableName设为%-->
        <table tableName="pms_brand">
            <generatedKey column="id" sqlStatement="MySql" identity="true"/>
        </table>
    </context>
</generatorConfiguration>
```

主要是：`<plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin" />`

项目学习地址：[https://gitee.com/macrozheng/mall-learning/tree/master/mall-tiny-02/src/main](https://gitee.com/macrozheng/mall-learning/tree/master/mall-tiny-02/src/main)