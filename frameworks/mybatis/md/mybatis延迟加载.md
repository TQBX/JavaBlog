前篇学习了多表查询，能够满足我们在复杂的场景下的查询，但不可否认的是，<u>单表查询在数据库查询的性能上是要优于多表查询的。</u>

因此，如果单表查询可以满足我们的需求，我们就应该查询单表，当需要关联多表信息的时候，再进行关联查询，这就是一个**延迟加载**的策略。

# 延迟加载的启动配置

我们需要在主配置文件中`<setting>`中配置`lazyLoadingEnabled`和`aggressiveLazyLoading`两个属性

```xml
    <settings>
        <!--开启mybatis支持延迟加载-->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!--false,每个延迟加载属性会按需加载,在 3.4.1 及之前的版本中默认为 true-->
        <setting name="aggressiveLazyLoading" value="false"/>
    </settings>
```

## 【一对一关系延迟加载】

```xml
<!-- com.itheima.dao.IAccountDao -->
<mapper namespace="com.itheima.dao.IAccountDao">    
	<!--定义封装account和user的resultmap-->
    <resultMap id="accountUserMap" type="account">
        <!--account对象的封装-->
        <id property="id" column="id"></id>
        <result property="uid" column="uid"></result>
        <result property="money" column="money"></result>
        <!--一对一的关系映射,配置封装user-->
        <!--
        select属性指定的内容:查询用户的唯一标识.即延迟加载执行的sql所在的statement的id
        column属性指定的内容:用户根据id查询时,所需要的参数值
        property属性指定的内容:将查询到的信息封装到account中的User属性里
        -->
        <!-- 只有需要user的时候，才会根据column指定的uid传给com.itheima.dao.IUserDao.findById，来查询user-->
        <association property="user" column="uid" javaType="user" select="com.itheima.dao.IUserDao.findById">
        </association>
    </resultMap>
    <!-- 查询所有 -->
    <select id="findAll" resultMap="accountUserMap">
        SELECT * FROM account;
    </select>
</mapper>  

<!-- com.itheima.dao.IUserDao -->
<mapper namespace="com.itheima.dao.IUserDao">
    <!-- 根据id查询用户 -->
    <select id="findById" parameterType="INT" resultType="user">
        select * from user where id = #{uid}
    </select>
</mapper>
```

【进行测试一】

```java
    @Test
    public void testFindAll(){
        //只是单纯地查询account，并没有需要user
        List<Account> accounts = accountDao.findAll();
    }
```

【输出结果一】

![image-20200417145254520](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200417145254520.png)

【进行测试二】

```java
    @Test
    public void testFindAll(){
        List<Account> accounts = accountDao.findAll();
        //尝试获取user，此时执行延迟加载sql语句，查询user信息
        for (Account account : accounts) {
            System.out.printf("account ==> %s \n",account);
        }
    }
```

【输出结果二】

![image-20200417145341987](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200417145341987.png)

## 【一对多关系延迟加载】

一对一学习之后，一对多就没什么难题了。

一、首先还是开启全局支持懒加载的设置。

二、配置mapper.xml

```xml
<!-- com.itheima.dao.IUserDao -->
<mapper namespace="com.itheima.dao.IUserDao">
    <!--定义User的resultmap-->
    <resultMap id="userAccountMap" type="user">
        <id property="id" column="id"></id>
        <result property="username" column="username"></result>
        <result property="address" column="address"></result>
        <result property="sex" column="sex"></result>
        <result property="birthday" column="birthday"></result>
        <!--配置user中accounts集合的映射-->
        <collection property="accounts" ofType="account" select="com.itheima.dao.IAccountDao.findAccountByUid" column="id">
        </collection>
    </resultMap>
    <!-- 查询所有 -->
    <select id="findAll" resultMap="userAccountMap" >
        SELECT * FROM USER
    </select>
</mapper>


<!-- com.itheima.dao.IAccountDao -->
<mapper namespace="com.itheima.dao.IAccountDao">
    <!--根据用户id查询账户列表-->
    <select id="findAccountByUid" resultType="account">
        select * from account where UID = #{uid}
    </select>
</mapper>
```

