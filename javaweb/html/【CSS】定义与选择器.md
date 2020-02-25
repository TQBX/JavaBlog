[toc]

`Cascading Style Sheets` 层叠样式表

是一种用于表现HTML或XML等文件样式的计算机语言。

可以静态修饰网页，也可以配合各种脚本语言动态地对网页元素进行格式化。



# 一、CSS与HTML结合方式

以下三种方式，css代码的作用范围越来越大，但内联样式的优先权最高。

## 1、内联样式

再HTML标签内利用style属性指定CSS代码。

如：`<div style="color: red">hello world</div>`

> 不推荐，因为html和css代码耦合度较高。

## 2、内部样式

在`<head>`标签内定义`<style>`标签，在style标签内指定CSS代码。

```css
<head>
    <style>
        div{
            color:red;
        }
    </style>
</head>
```



## 3、外部样式

在`<head>`标签内定义`<link>`标签，通过href属性引入外部地`.css`文件。

```css
<head>
	<link rel="stylesheet" href="css/a.css" /> 
</head>
```

另一种写法：

```css
<head>
    <style>
		@import "css/a.css";
    </style>
</head>
```

> 有效地实现css代码的复用。

# 二、CSS定义格式

内联样式的格式："属性名：属性值"。（不推荐）

其他两种格式如下：

```css
选择器{
    属性名：属性值；
    属性名：属性值；
    ……
}
```

选择器用于筛选具有相似特征的元素们。

属性名和属性值用冒号`:`分隔，每个属性以分号`;`分隔。

# 三、CSS选择器

## 1、基本选择器
- id选择器：选择具体id属性值的元素 ，建议在一个html种id值唯一
  - `#id属性值{}`

- 元素选择器：选择具有相同标签名称的元素
  - `标签名称{}`
  - id选择器优先级高于元素选择器

- 类选择器：选择具有想用的class属性值的元素
  - `.class属性值{}`
  - 类选择器优先级高于元素


## 2、扩展选择器
- 选择所有元素：`*{}`

- 分组选择器：将多个选择器的结果取并集，并操作，如：`div,#span1,class{}`

- 后代选择器：选中div中的全部后代span  ，如：` div span{}`

- 子选择器：选中div中的儿子级别span ，如：  ` div>span{}`

- 相邻选择器：div的下一个相邻兄弟元素span被选中，如： `div+span{}`

- 属性选择器：选择具有指定属性，或指定属性的值和指定值相等的选择器。
  - `div[name]{}`
  - `div[name='time']{}`

- 伪元素选择器：预先定义好的一些选择器。
  - link：未点击的状态。
  - visited：被点击过的状态（在不设置visited状态时active生效，否则会出现visited覆盖active效果）
  - hover：鼠标悬浮但没被点击
  - active：被激活的状态   

