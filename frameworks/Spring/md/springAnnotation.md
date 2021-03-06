# 一、用于创建对象 `<bean></bean>`

Component

 * 作用：将当前类对象存入spring容器中
 * 属性:value:用于指定bean的id,不指定value时，默认值为当前类名首字母小写


Controller：表现层

Service：业务层

Respository：持久层

以上三个注解的作用与Component作用相同，这三个时spring框架提供明确的三层使用的注释，是三层对象更加清晰


# 二、用于注入数据 `<property></property>`
Autowired:

 * 作用：自动按照类型注入，只要容器中有唯一的一个bean对象类型和要注入的变量类型匹配，就可以注入成功
 * 如果ioc容器中没有任何bean类型和要注入的变量类型匹配，则报错，如果ioc容器中有多个类型匹配时
 * 出现位置：可以是变量，也可以是方法
 * 细节：在使用注解注入时,set方法就不是必须的了。

Qualifier:

 * 作用：在按照类中注入的基础之上再按照名称注入，它在给类成员注入时不能单独使用，但是在给方法参数注入时可以
 * 属性：value,用于指定注入bean的id

Resource:

 * 作用：直接按照bean的id注入，可以独立使用
 * 属性：name,用于指定bean的id
 * 以上三个注入都只能注入其他bean类型的数据，基本类型和string类型无法使用上述注解实现，另外，集合类型的注入只能通过xml来实现

Value:

 * 作用：用于基本类型和string类型的数据
 * 属性：value,指定数据的值，可以使用spring中的SpEL,spring的el表达式
 * SpEL的写法：${表达式}

# 三、用于改变作用范围的 scope=""
Scope:

 * 作用：指定bean的作用范围
 * 属性：value,指定范围的取值， 常用： singleton prototype

# 四、与生命周期相关 init-method="" destroy-method=""

 * PreDestroy：指定销毁方法
 * PostConstruct：指定初始化方法

# 五、其他注解

Configuration

 * 作用：指定当前类是一个配置类
 * 细节：当配置类作为AnnotationConfigApplicationContext对象创建的参数时，该注解可以不写。

ComponentScan

 * 作用：用于通过注解指定spring在创建容器时要扫描的包
 * 属性：value,和basePackages的作用相同，指定创建容器时要扫描的包
 * 使用此注解就等同于在xml中配置了。

Bean:

 * 作用：用于把当前方法的返回值作为bean对象存入spring的ioc容器中
 * 属性：name,指定bean的id,当不写时，默认为当前方法的名称
 * 细节：当使用注解配置方法时，如果方法有参数,spring框架会去容器中查找有无可用的bean对象
 * 查找的方式和Autowired注解的作用是一样的

Import:

 * 作用：用于导入其他的配置类
 * 属性：value，指定其他配置类的字节码，使用Import注解后，有该注解的类为父配置类，导入的都是子配置类

PropertySource:

 *      作用：作用于指定properties文件的位置
 *      属性：value，指定文件的名称和路径 关键字:classpath:表示类路径下

