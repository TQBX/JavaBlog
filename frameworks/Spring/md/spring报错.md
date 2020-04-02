# IDEA解决：Unmapped Spring configuration files found.Please configure Spring facet.

## 问题如下：

一个友好的提示，不会阻止你正常运行，但是强迫症患者看着很难受，查找资料后，成功消除，记录一下。

![image-20200402130244012](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200402130244012.png)

产生原因，spring的配置文件没有被IDEA所管理导致。

## 解决方法

打开Project Strucure

![image-20200402130358546](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200402130358546.png)

选择报错的Module，点击+。

![image-20200402130433138](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200402130433138.png)

全选配置文件，完成。

![image-20200402130452119](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200402130452119.png)

重启，提示消失。

[IntelliJ IDEA 2017 提示"Unmapped Spring configuration files found.Please configure Spring facet."解决办法](https://blog.csdn.net/flyingnet/article/details/78009254)