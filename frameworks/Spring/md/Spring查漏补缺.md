# bean

初始化和销毁方法：init-method和destroy-method

bean的作用域：scope

- singleton：单例
- prototype：原型，每次调用getBean()返回一个实例

bean的后置处理器：

```java
@Component
public class MyPostProcessor implements BeanPostProcessor {

    /**
     * 初始化之前调用
     * @param bean
     * @param beanName
     * @return 
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName+" ==> bean将要进行初始化了");

        //返回传入的bean
        return bean;
    }

    /**
     * 初始化方法之后调用
     * @param bean
     * @param beanName bean在xml中配置的id
     * @return 
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName+" ==> bean初始化完毕了");
        return bean;
    }
}
```

> 添加bean后置处理器后bean的生命周期

[1]通过构造器或工厂方法**创建bean实例**

[2]为bean的**属性设置值**和对其他bean的引用

[3]将bean实例传递给bean后置处理器的**postProcessBeforeInitialization()**方法

[4]调用bean的**初始化**方法

[5]将bean实例传递给bean后置处理器的**postProcessAfterInitialization()**方法

[6]bean可以使用了

[7]当容器关闭时调用bean的**销毁方法**

# 自动装配

①自动装配的概念

[1]手动装配：以value或ref的方式***\*明确指定属性值\****都是手动装配。

[2]自动装配：根据指定的装配规则，***\*不需要明确指定\****，Spring***\*自动\****将匹配的属性值***\*注入\****bean中。

②装配模式

[1]根据***\*类型\****自动装配：将类型匹配的bean作为属性注入到另一个bean中。若IOC容器中有多个与目标bean类型一致的bean，Spring将无法判定哪个bean最合适该属性，所以不能执行自动装配

[2]根据***\*名称\****自动装配：必须将目标bean的名称和属性名设置的完全相同

[3]通过构造器自动装配：当bean中存在多个构造器时，此种自动装配方式将会很复杂。不推荐使用。

③选用建议

相对于使用注解的方式实现的自动装配，在XML文档中进行的自动装配略显笨拙，在项目中更多的使用注解的方式实现。

