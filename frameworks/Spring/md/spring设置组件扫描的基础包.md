`@ComponentScan`默认扫描当前目录及以下目录的类，可设置`@ComponentScan("com.my.demo")`。

![componentscan](E:\1JavaBlog\frameworks\Spring\pic\componentscan.png)

如图，只能扫描config目录下的程序。

【解决一】：`@ComponentScan("com.my.demo")`

【解决二】：`@ComponentScan(basePackages = {"com.my.demo.web","com.my.demo.service"})`

【解决三】：`@ComponentScan(basePackageClasses = {UserController.class, UserService.class, UserDao.class})`