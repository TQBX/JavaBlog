【组件扫描】：

@Component：表示这个类需要在应用程序中被创建

@ComponentScan：自动发现应用程序中创建的类

【自动装配】：

@Autowired：自动满足bean之间的依赖

@Autowired（required=false）

【解决自动装配歧义】：

@Primary

@Qualifier

@Resource

【定义配置类】：

@Configuration：表示当前类是一个配置类

【分层架构】：

@Controller

@Service

@Respository

【Spring测试环境】

@RunWith：`@RunWith(SpringJUnit4ClassRunner.class)`（这里使用log4j搭建测试环境）

@ContextConfiguration：`@ContextConfiguration(classes = Appconfig.class)`

【使用xml启动组件扫描】