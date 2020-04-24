[toc]

# 基于xml的spring-aop配置

1. 把通知bean也交给spring管理
2. 使用`aop:config`标签表明开始AOP的配置
3. 使用`aop:aspect`标签表明开始配置切面
    id:给切面提供唯一标识
    ref:指定通知类bean的id
4. 在`aop：aspect`标签的内部使用对应的标签来配置通知的类型
    让printLog方法在切入点方法执行之前执行,前置通知   aop:before
        method属性：指定Logger类中哪个方法是前置通知。
        pointcut属性：指定切入点表达式,该表达式的含义指的是对业务层中哪些方法增强。

## 切入点表达式

- 写法：execution(访问修饰符 返回值 包名.包名……类名.方法名(参数列表))
- 例：`execution(public void com.smday.service.impl.AccountServiceImpl.saveAccount())`

- 访问修饰符可以省略，返回值可以使用通配符*匹配。
- 包名也可以使用`*`匹配，数量代表包的层级，当前包可以使用`..`标识，例如`* *..AccountServiceImpl.saveAccount()`
- 类名和方法名也都可以使用`*`匹配：`* *..*.*()`

- 参数列表使用`..`可以标识有无参数均可，且参数可为任意类型。

> 全通配写法：* *..*.*(..)

通常情况下，切入点应当设置再业务层实现类下的所有方法：`* com.smday.service.impl.*.*(..)`。

## 配置通知

```xml
	<!--spring-ioc配置-->
    <bean id="accountService" class="com.smday.service.impl.AccountServiceImpl"></bean>
    <!--spring-aop配置-->
    <!--配置Logger类-->
    <bean id="logger" class="com.smday.utils.Logger"></bean>

    <!--配置aop-->
    <aop:config>

        <!--配置切入点表达式 id 指定表达式的唯一标志- expression属性指定表达式内容
        (此标签写在aop:aspect标签内部只能当前切面使用) 还可以写在aop:aspect,此时所有切面可用

        -->
        <aop:pointcut id="pt1" expression="execution(* com.smday.service.impl.*.*(..))"></aop:pointcut>
        <aop:aspect id="logAdvise" ref="logger">
            <!--配置通知的类型并且通知方法和切入点方法的关联-->
            <aop:before method="printLog" pointcut="execution(* com.smday.service.impl.*.*(..))"></aop:before>

            <!--前置通知：在切入点方法执行之前执行-->
            <aop:before method="beforePrintLog" pointcut-ref="pt1" ></aop:before>

            <!--后置通知：在切入点方法正常执行之后执行,他和异常通知永远只能执行一个-->
            <aop:after-returning method="afterReturningPrintLog" pointcut-ref="pt1" ></aop:after-returning>

            <!--异常通知：在切入点方法执行产生异常之后执行-->
            <aop:after-throwing method="afterThrowingPrintLog" pointcut-ref="pt1"></aop:after-throwing>

            <!--最终通知：无论切入点方法是否正常执行它都会在其后面执行-->
            <aop:after method="afterPrintLog" pointcut-ref="pt1"></aop:after>


            <!--环绕通知-->
            <aop:around method="aroundPrintLog" pointcut-ref="pt1"></aop:around>
        </aop:aspect>

    </aop:config>
```

## 环绕通知

```xml
<!--环绕通知--><aop:around method="aroundPrintLog" pointcut-ref="pt1"></aop:around>
```

```java
    /**
     * 环绕通知
     *
     * 问题：配置环绕通知后,切入点的方法没有执行,而通知方法执行而来.
     * 分析：通过对比动态代理中的环绕通知代码,发现动态代理的环绕通知有明确的切入点方法调用,而我们代码中没有.
     * 解决:spring框架提供了ProceedingJoinPoint接口,接口中的proceed()方法,相当于明确调用切入点方法.
     * 该接口可以作为环绕通知的方法参数,在程序执行时,spring框架我会为我们提供该接口的实现类供我们调用
     *
     * spring中的环绕通知：
     *      spring框架为我们提供的一种可以在代码中手动控制增强方法何时执行的方式
     */
    public Object aroundPrintLog(ProceedingJoinPoint pjp){
        Object rtValue = null;
        try{

            //得到方法执行的所需参数
            Object[] args = pjp.getArgs();
            System.out.println("前置");
            //明确调用业务层的方法(切入点方法)
            rtValue = pjp.proceed(args);
            System.out.println("后置");
        }catch (Throwable t){
            System.out.println("异常");
            throw new RuntimeException(t);
        }finally {
            System.out.println("最终");
        }
        return rtValue;
    }
```

# 基于注解+xml的spring-aop配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!--配置spring创建容器时要扫描的包-->
    <context:component-scan base-package="com.smday"></context:component-scan>

    <!--配置spring开启注解aop的支持-->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>

</beans>
```

```java
@Component("logger")
@Aspect
public class Logger {
    @Pointcut("execution(* com.smday.service.impl.*.*(..))")
    private void pt1(){}

    @Before("pt1()")
    public void beforePrintLog() {
        System.out.println("Logger.beforePrintLog");
    }

    @AfterReturning("pt1()")
    public void afterReturningPrintLog() {
        System.out.println("Logger.afterReturningPrintLog");
    }

    @AfterThrowing("pt1()")
    public void afterThrowingPrintLog() {
        System.out.println("Logger.afterThrowingPrintLog");
    }

    @After("pt1()")
    public void afterPrintLog() {
        System.out.println("Logger.afterPrintLog");
    }
}
```

> 基于注解的方式，最终通知的调用将会出现在后置或者异常通知前，建议使用环绕通知。

## 环绕通知

```java
@Component("logger")
@Aspect
public class Logger {
    @Pointcut("execution(* com.smday.service.impl.*.*(..))")
    private void pt1(){}

    @Around("pt1()")
    public Object aroundPrintLog(ProceedingJoinPoint pjp) {
        Object rtValue = null;
        try {

            //得到方法执行的所需参数
            Object[] args = pjp.getArgs();
            System.out.println("前置");
            //明确调用业务层的方法(切入点方法)
            rtValue = pjp.proceed(args);
            System.out.println("后置");
        } catch (Throwable t) {
            System.out.println("异常");
            throw new RuntimeException(t);
        } finally {
            System.out.println("最终");
        }
        return rtValue;
    }
}
```

# 基于纯注解的spring-aop配置

## 创建配置类

```java
@Configuration
@ComponentScan(basePackages = "com.smday")
@EnableAspectJAutoProxy
public class SpringConfiguration {

}
```

## 获取容器

```java
public class aopTest {
    public static void main(String[] args) {
        //获取容器
        ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        //获取对象
        AccountService as = ac.getBean("accountService", AccountService.class);
        //执行方法
        as.saveAccount();
    }
}
```

