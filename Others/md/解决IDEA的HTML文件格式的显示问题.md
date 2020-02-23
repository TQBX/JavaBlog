# 解决IDEA的HTML文件格式的显示问题

用IDEA在显示HTML文件的时候，格式非常奇怪，比如body标签下面不会自动缩进，每次都需要手动缩进，怪麻烦的。解决办法如下：



`settings `> `Editor `> `Code Style `>` HTML`



选择框框标出来的`Other`，注意下方的`Do not indent children of`，存在的标签表明不会自动缩进，需要点击小文件夹，将`<html>`、`<head>`、`<body>`给取消，就ok了。



![33KB6K.png](https://s2.ax1x.com/2020/02/23/33KB6K.png)