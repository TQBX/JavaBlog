# where和if标签

```xml
    <!--根据条件查询-->
    <select id="findUserByCondition" resultMap="userMap">
        select * from user
        <where>
            <if test="userName != null">
                and username = #{userName}
            </if>
            <if test="userSex!=null">
                and sex = #{userSex}
            </if>
        </where>
    </select>
```

`<where>`元素只会在子元素有返回内容的时候才会插入where子句，`<if>`元素代表查询的条件，`test`为条件内容，因此：该语句的效果是，如果username和usersex参数都不传的话，将会查询所有user。username和usersex参数有就带上，都有就都带上，作为查询的条件。

传入两个参数时：`select * from user WHERE username = ? and sex = ? `。

# choose、when、otherwise标签

类似于switch case语句，不同于if标签的是，这次传入哪个参数就按哪个参数查找，如果没有传入参数，可以通过`<otherwise>`标签进行指定默认。

```xml
    <!--从多个条件中选一个使用,查询用户-->
    <select id="findUserByOneOfCondition" resultMap="userMap">
        <include refid="defaultUser"></include>
        <where>
            <choose>
                <when test="userName!=null">
                    and username = #{userName}
                </when>
                <when test="userId!=null">
                    and id = #{userId}
                </when>
                <otherwise>
                    and sex = '女'
                </otherwise>

            </choose>
        </where>
    </select>
```

如果两个参数都为null，那就直接返回所有性别为女的user：`select * from user WHERE sex = '女' `。

如果传入username，sql语句就变成了这样：`select * from user WHERE username = ? `。

# foreach标签

对集合进行遍历的时候可以使用foreach标签，在构建in条件语句的时候相当有用。

```xml
<!--根据queryvo中的id集合实现查询用户列表-->
<select id="findUserInIds" resultMap="userMap" parameterType="queryvo">
    select * from user
    <where>
        <if test="ids!=null and ids.size()>0">
            <foreach collection="ids" index = "index" open="and id in (" close=")" item="uid" separator=",">
                <!-- #{内容} 与item内容统一 -->
                #{uid}
            </foreach>
        </if>
    </where>
</select>
```

可以发现`<if test="ids!=null and ids.size()>0">`表示`ids!=null`和`ids.size>0`两个条件同时满足，可以用and连接 。

另外，我们可以看到`<foreach>`标签有许多属性：

`collection`：表示遍历的集合id，从参数中获取。

`open`：指定的开头字符。

`close`：指定的结尾字符。

`item`：即将遍历的集合中的元素名。

`index`：遍历的索引序号。

`separator`：分隔符。

当我们传入size为3的List时，它的sql语句就动态生成为：`select * from user WHERE id in ( ? , ? , ? ) `。

写到这里，不仅大叹一声，不愧是动态SQL。

# set标签

利用set标签动态地在行首插入set关键字。

```xml
    <!--根据条件动态更新用户信息-->
    <update id="updateUserByCondition">
        update user
        <set>
            <if test="userName != null">username=#{userName},</if>
            <if test="userSex != null">sex=#{userSex},</if>
            <if test="userAddress != null">address=#{userAddress},</if>
        </set>
        where id=#{userId}
    </update>
```

当我们只传入usernam和sex参数时，sql语句是这样的：`update user SET username=?, sex=? where id=? `。

# 抽取重复sql语句

在一顿操作之后，我们可以发现，`select * from user`将会出现非常多次，因此，我们可以想办法将重复的语句进行抽取。

```xml
    <!--抽取重复的sql语句-->
    <sql id="defaultUser">
        select * from user
    </sql>
    <!-- 查询所有 -->
    <select id="findAll" resultMap="userMap">
        <!-- 引入抽取的语句 -->
        <include refid="defaultUser"></include>
    </select>
```

这样依赖，如果之后需要对该句进行修改，只需要改一个地方就好了，方便维护。