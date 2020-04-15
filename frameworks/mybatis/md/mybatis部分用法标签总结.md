# typeAliases类型别名

别名的意思很好理解，就是起一个更加简短的名称，降低全限定类名的抒写冗余。Mybatis内置已经定义了许多的别名与映射的类型，比如int对应Integer等等，具体可以查看官方文档。 

当然，我们也可以自定义类型别名，告别冗余：比如，我们之前如果要写一条根据id查找用户的语句，可能会在映射文件中这样写：

```xml
<mapper namespace="com.smday.dao.UserDao">    
	<!-- 根据id查询用户 -->
    <select id="findById" parameterType="int" resultType="com.smday.domain.User">
        select * from user where id = #{id}
    </select>
</mapper>
```

该语句的意思是，每当调用UserDao的`findById`方法，就会执行这条sql语句，此时接收一个int参数，返回一个`com.smday.domain.User`的类型。

我们可以为`com.smday.domain.User`起一个别名，在主配置文件中配置如下：

```xml
    <!--使用typeAliases配置别名,只能配置domain中类的别名-->
    <typeAliases>
        <!--<typeAlias type="com.smday.domain.User" alias="user"></typeAlias>-->
        <package name="com.smday.domain"/>
    </typeAliases>
```

配置别名的方式有两种：

- 指定类：`<typeAlias type="com.smday.domain.User" alias="user"></typeAlias>`，type指定实体类全类名,alias指定别名,不区分大小写。

- 指定包：`<package name="com.smday.domain"/>`，name用于指定要配置别名的包,指定之后,包下的所有实体类都会注册别名,类名即为别名,不区分大小写。

至此，所有`com.smday.domain.User`存在的地方就可以使用user代替。

# 结果映射

数据库中的字段名命名规范和JavaBean属性命名规范不一致，就会导致两者不能一一对应，解决的办法就是使用结果映射（当然还有一种方法是在sql语句中使用别名，就不赘述了）：

```xml
<mapper namespace="com.smday.dao.UserDao">
    <!-- id是映射结果集的唯一标识 ，type为实体类，此处已指定别名为user -->
    <resultMap id="userResultMap" type="User">
        <!--id:主键字段的对应  column:指定数据库列名 property:指定实体属性的名称-->
        <id property="id" column="user_id"></id>
        <!--result:非主键字段的对应-->
        <result property="username" column="user_name"></result>
        <result property="birthday" column="user_birthday"></result>
        <result property="sex" column="user_sex"></result>
        <result property="address" column="user_address"></result>
    </resultMap>
    <!-- 返回指定结果集类型，如果在不同mapper.xml中，需要指定namespace -->
    <select id="findAll" resultMap="userResultMap">
        select * from user
    </select>

</mapper>
```

# 占位符

`${}`采用的是参数拼接的方式，可以通过日志信息直观地发现，相当于我们之前学的statement执行sql语句的过程，可能会有sql注入的风险。${}可以接收简单类型和pojo属性值，如果是简单类型的值，需要写成${value}。

```xml
    <select id="findById" resultMap="userResultMap">
        select * from user where user_id = ${id}
    </select>
```

![image-20200415134502786](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415134502786.png)

`#{}`采用的预编译sql语句，相当于我们学习的preparedstatement，使用？作为占位符，自动进行java类型和jdbc类型的转换。可以防止sql注入。如果#{}可以接收简单类型和pojo属性值，如果是单个简单类型的值，则#{}中的可以是任意名称。

```xml
<select id="findById" resultMap="userResultMap">
    select * from user where user_id = #{id}
</select>
```
![image-20200415134609858](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415134609858.png)

# ognl表达式

```xml
<mapper namespace="com.smday.dao.UserDao">
	<!-- 保存用户 -->
    <insert id="saveUser" parameterType="User">
        insert into user(username, address, sex, birthday) values (#{username}, #{address}, #{sex}, #{birthday})
    </insert>
</mapper>    
```

>  #{}中利用ognl（Object Graphic Navigation Language）表达式获取数据，格式为#{对象.对象}。

例如：`#{user.username}`，将会去寻找user实体类对象，找到username属性，并调用`getUsername()`方法将值取出，当我们指定了`parameterType="User"`，可以省略user，直接来username。

# Mybatis事务

Mybatis默认事务是开启的，因此我们要想修改数据奏效，得手动提交一波。

SqlSession类中包含事务相关的方法，像commit,rollback等。

# 获取新增用户id的返回值

新增用户后，同时还要返回当前新增用户的 id 值，因为 id 是由数据库的自动增长来实现的，所以就相
当于我们要在新增后将自动增长 auto_increment 的值返回。

```xml
<insert id="saveUser" parameterType="USER">
<!-- 配置保存时获取插入的 id --> 
    <selectKey keyColumn="id" keyProperty="id" resultType="int">
		select last_insert_id();
	</selectKey>
    	insert into user(username,birthday,sex,address) 
    	values(#{username},#{birthday},#{sex},#{address})
</insert>
```

