# 【已解决】java.lang.ClassNotFoundException: org.springframework.dao.DataAccessException

![3H5J6U.png](https://s2.ax1x.com/2020/03/05/3H5J6U.png)

解决办法，修改目录如下，注意必须是WEB-INF和lib，（ps：我一开始目录为WEB_INF,死活找不出错误，太难了！)

![3H5GlT.png](https://s2.ax1x.com/2020/03/05/3H5GlT.png)

接着，注意右键点击lib，选择`Add as Library..`,将其添加进工作空间内即可。

![3H5wkR.png](https://s2.ax1x.com/2020/03/05/3H5wkR.png)

参考：[https://blog.csdn.net/iconhot/article/details/90319883](https://blog.csdn.net/iconhot/article/details/90319883)

