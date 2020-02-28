[toc]

# 一、DOM的概念

>  DOM的英文全称是 **Document Object Model**，即文档对象模型，是用以操作**HTML文档和XML文档**的API。

W3C将DOM分为三个不同的部分，分别是：核心DOM，XML DOM，HTML DOM。

# 二、DHTML与HTML DOM

所谓`DHTML`即`Dynamic HTML`，是**动态的HTML**。

这门技术可以使用客户端脚本语言，**利用HTML DOM 控制HTML的元素**，将静态的HTML内容变成动态。



---

>  本篇主要是对HTML DOM的学习总结。

HTML DOM可以把HTML文档看作一棵**节点树**，可以利用DOM对象的属性或方法对这些节点进行增删改查的操作。

# 三、Document对象

**Document对象代表载入浏览器的HTML文档**，可以通过全局对象Window获得Document对象。

## 获取元素方法

getElementById（id值）：根据id值获取一个元素。

getElementsByName（name值）：根据name值获取一个元素数组。

getElementsByTagName（tagName值）：根据标签名获取一个元素数组。

---

> 在HTML DOM中，每个部分都是节点：文档本身，元素，属性，注释都是节点。

# 四、Element对象

Element对象代表文档中的元素，可以通过Document的许多方法获取。

## 与属性相关方法

setAttribute(属性名，属性值)：将第一个input标签的type属性设置为button。

```js
document.getElementByTagName("input")[0].setAttribute("type","button");
```

removeAttribute(属性名)：将id为"city"的元素的style属性移除。

```js
document.getElementById("city").removeAttribute("style");
```

# 五、对DOM对象的操作

>  增删改查。

## 1、DOM对象的属性

innerHTML：元素（节点）的文本值。

parentNode：元素（节点）的父节点。

childNodes：元素（节点）的子节点。

## 2、创建元素

```js
document.createElement("节点类型");//为指定的标签创建一个元素的实例。
```

## 3、挂载元素

```js
parentElement.appendChild(childElement);//在父元素最后位置添加子元素。
parentElement.insertBefore(newElement,oldElement);//将元素作为父对象的子元素插入其中。
```

## 4、删除元素

```js
parentElement.removeChild(chileElement);//删除父元素的指定子元素。
```

## 5、克隆元素

```js
newElement = oldElement.cloneNode(boolean);//默认为false，即不克隆节点中的子节点。
```

# 六、调整元素样式的方式

直接利用元素的style属性节点设置。

```js
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
        <script>
            function changeStyle() {
                document.getElementById("p1").style.border= "red 1px solid";
            }
        </script>
    </head>
    <body>
        <p id="p1">hello</p>
        <input type="button" onclick="changeStyle()" value="点击更改样式">
    </body>
```

实现定义类选择器的样式，点击事件触发时，更改其className值即可。

```js
    <head>
        <meta charset="UTF-8">
        <title>Title</title>

        <style>
            .change {
                border: red 1px solid;
            }
        </style>
        <script>
            function changeStyle() {
                document.getElementById("p1").className = "change";
            }
        </script>
    </head>
    <body>
        <p id="p1">hello</p>
        <input type="button" onclick="changeStyle()" value="点击更改样式">
    </body>
```

# 七、Event对象

Event对象代表事件的状态：如鼠标的位置，鼠标是否点击，键盘是否按下等等。

通常与函数结合使用，且函数不会再事件发生前执行。

## 1、常见的几类事件

### 点击事件
- onclick：单击事件
- ondbclick：双击事件

### 焦点事件
- onblur：失去焦点，一般用于表单校验。
- onfocus：获得焦点。

### 加载事件
- onload：图片或页面加载完成。

### 鼠标事件

- onmousedown：鼠标按下。
- onmouseup：鼠标松开。
- ommousemove：鼠标移动。
- onmouseover：鼠标移到元素上。
- onmouseout：鼠标从元素上移开。

### 键盘事件

- onkeydown：键盘按下。
- onkeyup：键盘松开。
- onkeypress：键盘按下并松开。

### 选中和改变事件
- onchange：域的内容被改变
- onselect：文本被选中
### 表单事件
- onsubmit ：提交表单按钮被点击。（事件绑定的函数返回false则表单将会被阻止提交）


- onreset：重置按钮被点击。

## 2、绑定事件的几种方法

直接在HTML标签上，指定事件的属性，属性值即为js代码）。

```js
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
        <script>
            function fun() {
                alert("!!");
            }
        </script>
    </head>
    <body>
        <p id="p1">hello</p>
        <input type="button" onclick="fun()" value="点击触发">
    </body>
</html>
```

通过DOM元素对象与匿名方法指定实现属性。

```js
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
        <script>
            window.onload = function () {
                var ele = document.getElementsByTagName("input")[0];
                ele.onclick = function () {
                    alert("!!");
                }
            }
        </script>
    </head>
    <body>
        <p id="p1">hello</p>
        <input type="button" value="点击触发">
    </body>
</html>
```



还有其他的方法，以后遇到的时候再做总结。

---

参考：[W3School](https://www.w3school.com.cn/jsref/dom_obj_event.asp)