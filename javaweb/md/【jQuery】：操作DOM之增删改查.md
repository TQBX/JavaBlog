[toc]

```js
//案例
<p title="location"> 你住在哪? </p>
<ul>
    <li title="温州">温州</li>
    <li title="杭州">杭州</li>
    <li title="广州">广州</li>
</ul>
```

# 一、获取节点

## 1、获取节点文本内容

```js
var $li = $("ul li:eq(1)"); //获取<ul>里的第二个li节点
var li_text = $li.text();   //获取其文本内容
```

## 2、获取节点属性

```js
var $para = $("p");
var attr = $para.attr("title");//获取title属性值
```

# 二、创建节点

创建节点不等同于添加节点，创建的节点还不在文档节点树中。

## 1、创建元素

```js
var $li_0 = $("<li></li>");    	//创建一个<li>元素，方式1
var $li_1 = $("<li/>");    		//创建一个<li>元素，方式2
```

## 2、创建文本节点

```js
var $li_text = $("<li>苏州</li>");
```

## 3、创建属性节点

```js
var $li_attr = $("<li title='苏州'>苏州</li>");
```

# 三、添加节点

添加操作都两两对应：

- append()与appendTo()：在元素内部追加新内容。（内部的末尾）

- prepend()与prependTo()：在元素内部前置新内容。（内部的开头）
- after()与insertAfter()：在元素之后插入新内容。（两元素为兄弟关系）
- before()与insertBefore()：在元素之前插入新内容。（两元素为兄弟关系）

```js
$("ul").append($("<li> 新元素 </li>"));	//向ul内部追加新元素
$("<li> 新元素 </li>").appendTo($("ul"));	//效果相同
```

> 添加节点的操作，经过组合，可以转变为移动节点的操作，如下：

```js
$("ul").append($("li:eq(1)"));//将第二个li移出，并添加到ul
```



# 四、删除节点

- remove()：删除该节点及所有后代节点，并返回删除节点的引用，可以二次使用。

```js
var $ul = $("ul");
var $li = $("li:eq(1)").remove(); 	//将第二个li元素移除
$li.appendTo($ul);					//将移除的li元素再次添加到ul中
```

- empty()：清除元素的所有后代节点，但是会保留当前对象的属性。

# 五、克隆节点

```js
$("ul").clone(true).insertAfter($("ul"));//将ul列表复制一份添加到原表后面
```

clone方法里的参数，默认为false，复制后的新节点不具有任何行为 ，如果需要新元素能够拥有事件行为，需要将参数设为true。

# 六、替换节点

```js
$("p").replaceWith($("<strong>你住在哪?</strong>"));//用指定的DOM元素或HTML替换p元素
$("<strong>你住在哪?</strong>").replaceAll("p");	//与上相同
```

替换之后，原本元素绑定的事件也就随之消失，如果需要的话，要再绑定。

# 七、包裹节点

```js
$("li").wrap("<strong></strong>");	//将li元素加粗
```

