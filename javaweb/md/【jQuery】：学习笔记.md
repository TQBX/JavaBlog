[toc]

# 一、jQuery概述

> write less , do more .

轻量级的JavaScript函数库，在使用时需要引入jQuery库文件。引用方式如下，两种都可。

```js
<script src="../jquery-1.4.2.min.js"></script>	 <!--生产版-->
<script src="../jquery-1.4.2.js"></script>		 <!--开发版-->
```

# 二、jQuery对象的创建方式

1. `$(expr , [context])`：接收一个css选择器的字符串，去匹配一组元素，如`$("div > p")`。

2. `$(html , [ownerDoc])`：接收一个原始的HTML标记字符串，如`$("<span></span>")`。

3. `$(html , props)`：接收原始HTML字符串，并设置属性，事件，如`$("<div>",{text:"hello!"})`

4. `$(elements)`：将DOM对象转化为jQuery对象，如：`$(document.body)`

4. `$()`：jQuery 1.4中，如果不提供任何参数，则返回一个空jQuery对象。

6. `$(callback)`：接收一个DOM文档载入完成后执行的函数，例如文档就绪事件：`$(function(){……})`

# 三、JS对象和jQuery对象的转换

$符号等价于jQuery，$()相当于调用jQuery()，该函数会返回一个jQuery对象。

DOM对象和jQuery对象为不同对象，因此属性和方法调用上是不相同的，但是可以进行相互转化。

## 1、JS->jQuery

```js

var div = document.getElementById("test");
var $div = $(div);				//$div是一个变量名，只是为了提高可读性。
```

## 2、jQuery->JS

```js
var div = $("div")[0];			//方式1
var div1 = $("div").get(0);		//方式2
```

# 四、jQuery的选择器

## 1、基本选择器

 ```js
$("#test") 			//选取id为test的元素。
$(".city")			//选取所有class为city的元素。
$("p")				//选取所有<p>元素。
$("*")				//选取所有的元素。
$("div,span,p.city")//选取所有<div>,<span>,class为city的<p>
 ```



## 2、层级选择器

```js
$("div span") 	//匹配div下所有的span元素
$("div>span") 	//匹配div下所有的span子元素
$("div+span")	//匹配div后面紧邻的span兄弟元素
$("div~span") 	//匹配div后面所有的span兄弟元素 
```



## 3、基本过滤选择器

```js
$("div:first")		//匹配所有div中的第一个div元素
$("div:last") 		//匹配所有div中的最后一个div元素
$("div:even") 		//匹配所有div中索引值为偶数的div元素，0开始
$("div:odd")		//匹配所有div中索引值为奇数的div元素，0开始
$("div:eq(n)")		//匹配所有div中索引值为n的div元素，0开始
$("div:lt(n)") 		//匹配所有div中索引值小于n的div元素，0开始
$("div:gt(n)") 		//匹配所有div中索引值大于n的div元素，0开始
$("div:not(.one)") 	//匹配所有class值不为one的div元素
```



## 4、内容过滤选择器

```js
$("div:contains('abc')") //匹配所有div中包含abc内容的div元素,如: <div>xxxabcxx</div>
$("div:has(p)") 		 //匹配所有包含p元素的div元素,如: <div><p></p></div>
$("div:empty") 			 //匹配所有内容为空的div元素,如: <div></div>
$("div:parent") 		 //匹配所有内容不为空的div元素,如: <div>xxxxx</div>
```



## 5、可见性过滤选择器

```js
$("div:hidden") 		// 匹配所有隐藏的div元素
$("div:visible") 		// 匹配所有可见的div元素
```



## 6、属性过滤选择器

```js
$("div[id]") 			//匹配所有具有id属性的div元素
$("div[id='d1']") 		//匹配所有具有id属性并且值为d1的div元素
$("div[id!='d1']") 		//匹配所有id属性值不为d1的div元素
$("div[id^='d1']") 		//匹配所有id属性值以d1开头的div元素
$("div[id$='d1']") 		//匹配所有id属性值以d1结尾的div元素
//等等……
```



## 7、子元素过滤选择器

```js
$("div:nth-child(n)") 	//n可以取even、odd、关于m的表达式（m从1开始）、
$("div:first-child") 	//匹配div中第1个子元素。
$("div:last-child") 	//匹配div中最后一个子元素。。。
```



## 8、表单对象属性过滤选择器

```js
$(":selected") 		//匹配所有被选中的option选项
$(":checked") 		//匹配所有的被选中的单选框/复选框/option
$(":enabled")		//匹配所有可用的元素
$(":disabled")		//匹配所有不可用的元素
//例
$("input:checked") 	//匹配所有的被选中的单选框/复选框
```



## 10、表单选择器

```js
$(":input") 		//匹配所有的input文本框、密码框、单选框、复选框、select框、textarea、button。
$(":password") 		//匹配所有的密码框
$(":radio") 		//匹配所有的单选框
$(":checkbox") 		//匹配所有的复选框
$(":submit")		//匹配所有的提交按钮
$(":reset")			//匹配所有的重置按钮
$(":hidden")		//匹配所有的不可见元素
//等等……
```

---

参考链接：[W3School](https://www.w3school.com.cn/jquery/index.asp)、《锋利的jQuery》