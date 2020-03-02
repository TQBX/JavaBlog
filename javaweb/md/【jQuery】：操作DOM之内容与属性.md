# DOM操作之内容与属性

# 一、内容操作

## 1、html()

与JS中的interHTML属性类似，用以读取或者设置某个元素的HTML内容。

```js
	<p><span>城市</span></p>
    
	//获取p元素的HTML内容：<span>城市</span>
	$("p").html() 	
	//设置p元素的HTML内容：此时为：<p><strong>城市</strong></p>
	$("p").html("<strong>城市</strong>") 
```

## 2、text()

与JS中innerText属性类似，用以读取或设置某个元素中的文本内容。

```js
	<p><span>城市</span></p>
        
    //获取p元素的文本内容：城市
    $("p").text()
	//设置p元素的文本内容，此时为：<p><span>新城市</span></p>
	$("p").text("新城市")
```

## 3、val()

与JS中的Value属性类似，用以设置和获取元素的value属性值。

```js
	<input type="text" value="请输入你的名字">

	//获取input元素的value值：请输入你的名字
	$("input").val()
	//设置input元素的value值，此时为：<input type="text" value="请输入">
    $("input").val("请输入")

```

需要注意的是，val()还可以用于下拉框，多选框，单选框的选项选择，以下拉框为例：

```js
<select id="single_select">
    <option >1</option>
	<option >2</option>
	<option >3</option>
</select>
<select id="multiple_select" multiple>
    <option >1</option>
	<option >2</option>
	<option >3</option>
</select>
//选择2
$("#single_select").val("2");
//同时选择2和3
$("#multiple_select").val(["1","2"]);
```



# 二、属性操作

## 1、获取与设置属性

```js
<div id="city" title="this is city">city</div>
```

```js
$("#city").attr("title")	//获取该元素的title属性值：this is a city


$("#city").attr("title","new city");	//设置title属性的值为new city
$("#city").attr({						//设置多组属性值的格式
    "title":"newCity",
    "name":"city"
})									
```

## 2、删除属性

```js
$("#city").removeAttr("title");		//删除title属性，此时为：<div id="city">city</div>
```

# 三、样式操作

## 1、追加样式

事先定义指定的class样式，使用addClass操作。

```js
<style>
    .myClass{
        background: #00ffFF;
    }
</style>
$("#city").addClass("myClass");
```

## 2、设置样式

同样可以事先定义class样式，使用attr()设置属性。

```js
$("#city").attr("class","myClass");
```

## 3、移除样式

使用removeClass移除class样式，或利用removeAttr移除class属性。

```js
$("#city").removeClass("myClass");	//移除指定值为myClass的class
$("#city").removeAttr("class");		//移除class属性

```

removeClass（）：括号内不加参数代表删除所有的class属性。

removeClass（"myClass yourClass“）：指定多个属性值时，用空格隔开。



## 4、切换样式

使用toggleClass方法进行样式切换。

```js
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
        <script src="../jquery-1.4.2.min.js"></script>
        <script>
            $(function () {
                $("input").click(function () {
                    //如果该元素有值为myClass的class，则移除该class，如果没有则添加
                    $("#city").toggleClass("myClass");
                })
            })
        </script>
        <style>
            .myClass {
                background: #00ffFF;
            }
        </style>
    </head>
    <body>
        <div id="city" title="this is city" class="myClass">city</div>
        <input type="button" value="点击切换样式">
    </body>
```



## 5、判断是否含有样式

```js
$("#city").hasClass("myClass")	//如果含有myClass，返回true
```



# 四、样式操作CSS补充

利用CSS-DOM，通过设置或读取style属性来操作样式。

## 1、设置样式

```js
$("#city").css("background","red")	//设置”background“ 为red
```

```js
$("#city").css(
    {
        "background":"red",
        "width":"500px",
        "fontSize":"50px"	//font-size --> fontSize
    });
```

## 2、获取样式

```js
$("#city").css("width")		//获取width 的值
```

