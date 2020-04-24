[toc]

# 一、mybatis处理参数源码

- 单个参数：mybatis不会做特殊处理。
  - #{参数名/任意名}：取出参数值。	

- 多个参数：mybatis会做特殊处理。
  - 多个参数会被封装成 一个map，
    - key：param1...paramN,或者参数的索引也可以
    - value：传入的参数值
    - #{}就是从map中获取指定的key的值

- 【命名参数】：明确指定封装参数时map的key；@Param("id")
  - 多个参数会被封装成 一个map，
    - key：使用@Param注解指定的值
    - value：参数值
    - #{指定的key}取出对应的参数值

`(@Param("id")Integer id,@Param("lastName")String lastName);`

解析参数封装map的主要逻辑在ParamNameResolver中。

```java
/*确定流程：
1.获取每个标了param注解的参数的@Param的值：id，lastName；  赋值给name;
2.每次解析一个参数给map中保存信息：（key：参数索引，value：name的值）
	name的值：
		标注了param注解：注解的值
		没有标注：
			1.全局配置：useActualParamName（jdk1.8）：name=参数名
			2.name=map.size()；相当于当前元素的索引
		{0=id, 1=lastName,2=2}
		args【1，"Tom",'hello'】:*/
public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    //1、参数为null直接返回
    if (args == null || paramCount == 0) {
      return null;
    //2、如果只有一个元素，并且没有Param注解；args[0]：单个参数直接返回
    } else if (!hasParamAnnotation && paramCount == 1) {
      return args[names.firstKey()];

    //3、多个元素或者有Param标注
    } else {
      final Map<String, Object> param = new ParamMap<Object>();
      int i = 0;

      //4、遍历names集合；{0=id, 1=lastName,2=2}
      for (Map.Entry<Integer, String> entry : names.entrySet()) {

        //names集合的value作为key;  names集合的key又作为取值的参考args[0]:args【1，"Tom"】:
        //eg:{id=args[0]:1,lastName=args[1]:Tom,2=args[2]}
        param.put(entry.getValue(), args[entry.getKey()]);
        // add generic param names (param1, param2, ...)param
    	//额外的将每一个参数也保存到map中，使用新的key：param1...paramN
    	//效果：有Param注解可以#{指定的key}，或者#{param1}
        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }
}
```

# 二、占位符比较

大多情况下，我们去参数的值都应该去使用#{}，其采用预编译的方式，可以防止sql注入，少数情况下需要${}，比如**原生jdbc不支持占位符的地方**。

```sql
-- 比如分表、排序。。。；按照年份分表拆分
select * from ${year}_salary where xxx;
select * from tbl_employee order by ${f_name} ${order}
```

#{}包含更丰富的用法，如规定参数的一些规则：

> javaType、 jdbcType、 mode（存储过程）、 numericScale、resultMap、 typeHandler、 jdbcTypeName、 expression（未来准备支持的功能）；

以jdbcType为例，它通常需要在某种特定的条件下被设置：

数据为null的时候，<u>有些数据库可能不能识别mybatis对null的默认处理</u>，比如Oracle，就会出现错误，因为mybatis对所有的null都映射的是原生Jdbc的OTHER类型，oracle不能正确处理;

由于oracle不支持全局配置中：jdbcTypeForNull=OTHER；

就有以下两种解决办法：

1. 使用占位符的规则：`#{email,jdbcType=OTHER}`;
2. 设置jdbcTypeForNull=NULL，即全局配置中：`<setting name="jdbcTypeForNull" 。value="NULL"/>`

# 三、多表查询

```xml
<!-- 
场景一：
	查询Employee的同时查询员工对应的部门
	Employee===Department
	一个员工有与之对应的部门信息；
	id  last_name  gender    d_id     did  dept_name (private Department dept;)
 -->

<!--
	联合查询：级联属性封装结果集
  -->
<resultMap type="com.atguigu.mybatis.bean.Employee" id="MyDifEmp">
	<id column="id" property="id"/>
	<result column="last_name" property="lastName"/>
	<result column="gender" property="gender"/>
	<result column="did" property="dept.id"/>
	<result column="dept_name" property="dept.departmentName"/>
</resultMap>
```


```xml
<!-- 
	使用association定义关联的单个对象的封装规则；
 -->
<resultMap type="com.atguigu.mybatis.bean.Employee" id="MyDifEmp2">
	<id column="id" property="id"/>
	<result column="last_name" property="lastName"/>
	<result column="gender" property="gender"/>
	
	<!--  association可以指定联合的javaBean对象
	property="dept"：指定哪个属性是联合的对象
	javaType:指定这个属性对象的类型[不能省略]
	-->
	<association property="dept" javaType="com.atguigu.mybatis.bean.Department">
		<id column="did" property="id"/>
		<result column="dept_name" property="departmentName"/>
	</association>
</resultMap>
<!--  public Employee getEmpAndDept(Integer id);-->
<select id="getEmpAndDept" resultMap="MyDifEmp">
	SELECT e.id id,e.last_name last_name,e.gender gender,e.d_id d_id,
	d.id did,d.dept_name dept_name FROM tbl_employee e,tbl_dept d
	WHERE e.d_id=d.id AND e.id=#{id}
</select>

<!-- 使用association进行分步查询：
	1、先按照员工id查询员工信息
	2、根据查询员工信息中的d_id值去部门表查出部门信息
	3、部门设置到员工中；
 -->
 
 <!--  id  last_name  email   gender    d_id   -->
 <resultMap type="com.atguigu.mybatis.bean.Employee" id="MyEmpByStep">
 	<id column="id" property="id"/>
 	<result column="last_name" property="lastName"/>
 	<result column="email" property="email"/>
 	<result column="gender" property="gender"/>
 	<!-- association定义关联对象的封装规则
 		select:表明当前属性是调用select指定的方法查出的结果
 		column:指定将哪一列的值传给这个方法
 		
 		流程：使用select指定的方法（传入column指定的这列参数的值）查出对象，并封装给property指定的属性
 	 -->
 	  		<association property="dept" 
	 		select="com.atguigu.mybatis.dao.DepartmentMapper.getDeptById"
	 		column="d_id">
 		</association>
	 </resultMap>
	 <!--  public Employee getEmpByIdStep(Integer id);-->
	 <select id="getEmpByIdStep" resultMap="MyEmpByStep">
	 	select * from tbl_employee where id=#{id}
	 	<if test="_parameter!=null">
	 		and 1=1
	 	</if>
	 </select>
	 
	  <!-- 可以使用延迟加载（懒加载）；(按需加载)
 	Employee==>Dept：
 		我们每次查询Employee对象的时候，都将一起查询出来。
 		部门信息在我们使用的时候再去查询；
 		分段查询的基础之上加上两个配置：
  -->
```
# 四、鉴别器

```xml
<!-- =======================鉴别器============================ -->
<!-- <discriminator javaType=""></discriminator>
	鉴别器：mybatis可以使用discriminator判断某列的值，然后根据某列的值改变封装行为
	封装Employee：
		如果查出的是女生：就把部门信息查询出来，否则不查询；
		如果是男生，把last_name这一列的值赋值给email;
 -->
 <resultMap type="com.atguigu.mybatis.bean.Employee" id="MyEmpDis">
 	<id column="id" property="id"/>
 	<result column="last_name" property="lastName"/>
 	<result column="email" property="email"/>
 	<result column="gender" property="gender"/>
 	<!--
 		column：指定判定的列名
 		javaType：列值对应的java类型  -->
 	<discriminator javaType="string" column="gender">
 		<!--女生  resultType:指定封装的结果类型；不能缺少。/resultMap-->
 		<case value="0" resultType="com.atguigu.mybatis.bean.Employee">
 			<association property="dept" 
		 		select="com.atguigu.mybatis.dao.DepartmentMapper.getDeptById"
		 		column="d_id">
	 		</association>
 		</case>
 		<!--男生 ;如果是男生，把last_name这一列的值赋值给email; -->
 		<case value="1" resultType="com.atguigu.mybatis.bean.Employee">
	 		<id column="id" property="id"/>
		 	<result column="last_name" property="lastName"/>
		 	<result column="last_name" property="email"/>
		 	<result column="gender" property="gender"/>
 		</case>
 	</discriminator>
 </resultMap>
```
# 五、trim标签

```xml
	 <!--public List<Employee> getEmpsByConditionTrim(Employee employee);  -->
	 <select id="getEmpsByConditionTrim" resultType="com.atguigu.mybatis.bean.Employee">
	 	select * from tbl_employee
	 	<!-- 后面多出的and或者or where标签不能解决 
	 	prefix="":前缀：trim标签体中是整个字符串拼串 后的结果。
	 			prefix给拼串后的整个字符串加一个前缀 
	 	prefixOverrides="":
	 			前缀覆盖： 去掉整个字符串前面多余的字符
	 	suffix="":后缀
	 			suffix给拼串后的整个字符串加一个后缀 
	 	suffixOverrides=""
	 			后缀覆盖：去掉整个字符串后面多余的字符
	 			
	 	-->
	 	<!-- 自定义字符串的截取规则 -->
	 	<trim prefix="where" suffixOverrides="and">
	 		<if test="id!=null">
		 		id=#{id} and
		 	</if>
		 	<if test="lastName!=null &amp;&amp; lastName!=&quot;&quot;">
		 		last_name like #{lastName} and
		 	</if>
		 	<if test="email!=null and email.trim()!=&quot;&quot;">
		 		email=#{email} and
		 	</if> 
		 	<!-- ognl会进行字符串与数字的转换判断  "0"==0 -->
		 	<if test="gender==0 or gender==1">
		 	 	gender=#{gender}
		 	</if>
		 </trim>
	 </select>
```

# 六、动态sql 内置参数+bind

不只是方法传递过来的参数可以被用来判断，取值，mybatis默认还有两个内置参数：

- _parameter：代表整个参数
  - 单个参数：_parameter就是这个参数
  - 多个参数：参数会被封装为一个map；_parameter就是代表这个map
- _databaseId：数据库id
  - 如果配置了databaseIdProvider标签，_databaseId就是代表当前数据库的别名。

bind：可以将OGNL表达式的值绑定到一个变量中，方便后来引用这个变量的值。

```xml
<!--public List<Employee> getEmpsTestInnerParameter(Employee employee);  -->
	  <select id="getEmpsTestInnerParameter" resultType="com.atguigu.mybatis.bean.Employee">
	  		<!-- bind：可以将OGNL表达式的值绑定到一个变量中，方便后来引用这个变量的值 -->
	  		<bind name="_lastName" value="'%'+lastName+'%'"/>
	  		<if test="_databaseId=='mysql'">
	  			select * from tbl_employee
	  			<if test="_parameter!=null">
	  				where last_name like #{lastName}
	  			</if>
	  		</if>
	  		<if test="_databaseId=='oracle'">
	  			select * from employees
	  			<if test="_parameter!=null">
	  				where last_name like #{_parameter.lastName}
	  			</if>
	  		</if>
	  </select>
```

# 七、缓存相关

一级缓存 sqlsession级别，一直开启。 与数据库同一次会话期间查询到的数据会放到本地缓存中，获取相同的数据直接从缓存中获取，而没必要查询数据库。

一级缓存失效的情况：

- 两次查询sqlsession不同。
- sqlsession相同，查询条件不同，当前一级缓存中没由该数据。
- sqlsession相同，两次查询之间执行了增删改操作。

- sqlsession相同，手动清除了一级缓存。

一级缓存范围：

默认为session，一个会话范围内，可以指定statement，为当前语句。

---

二级缓存，基于namespace，一个namespace对应一个二级缓存。

二级缓存工作机制

- 一个会话查询一条数据，存放在当前会话的一级缓存中。
- 如果会话关闭，一级缓存中的数据会被保存到二级缓存中，新的会话查询信息，就可以参照二级缓存。不同的namespace查询出的数据会保存自己的缓存中。
  - 查出的数据都会被默认先放在entriesToAddOnCommit中，只有会话提交或者关闭以后，一级缓存中的数据才会转移到二级缓存中。

二级缓存开启：

- 主配置文件settings中enableCache设置为true，默认为true。

- mapper映射配置文件设置cache。

```xml
	<!--<cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>-->
	<cache eviction="FIFO" flushInterval="60000" readOnly="false" size="1024">	</cache> -->

```

- pojo实现serializable接口

cache的标签属性：

- eviction:缓存的回收策略：
  - LRU – 最近最少使用的：移除最长时间不被使用的对象。
  - FIFO – 先进先出：按对象进入缓存的顺序来移除它们。
  - SOFT – 软引用：移除基于垃圾回收器状态和软引用规则的对象。
  - WEAK – 弱引用：更积极地移除基于垃圾收集器状态和弱引用规则的对象。
  - 默认的是 LRU。
- flushInterval：缓存刷新间隔
  - 缓存多长时间清空一次，默认不清空，设置一个毫秒值
- readOnly:是否只读：
  - true：
    - 只读，mybatis认为所有从缓存中获取数据的操作都是只读操作，不会修改数据。
    - mybatis为了加快获取速度，直接就会将数据在缓存中的引用交给用户。不安全，速度快
  - false：
    - 非只读：mybatis觉得获取的数据可能会被修改。
    - mybatis会利用序列化&反序列的技术克隆一份新的数据给你（pojo实现serializable接口）。安全，速度慢
- size：缓存存放多少元素；
- type=""：指定自定义缓存的全类名；
  - 实现Cache接口即可；

---

和缓存相关的配置和属性

- cacheEnabled=true：false：关闭缓存（二级缓存关闭）(一级缓存一直可用的)。
- 每个select标签都有useCache="true"：
  - false：不使用缓存（一级缓存依然使用，二级缓存不使用）
- 【每个增删改标签的：flushCache="true"：（一级二级都会清除）】查询标签：flushCache="false"：
  - 增删改执行完成后就会清除缓存；flushCache="true"：一级缓存就清空了；二级也会被清除；
  - 如果flushCache=true;每次查询之后都会清空缓存；缓存是没有被使用的；
- sqlSession.clearCache();只是清楚当前session的一级缓存；
- localCacheScope：本地缓存作用域：（一级缓存SESSION）；当前会话的所有数据保存在会话缓存中；STATEMENT：可以禁用一级缓存；	

