# JavaScript中的BOM对象

BOM（Browser Object Model）：**浏览器对象模型**。

BOM可用于对浏览器窗口进行访问，但BOM没有相关的标准，所以根据浏览器的不同，其中定义的对象属性和方法可能会有所不同。

BOM对象也就是我们所说的**宿主对象**，总共分为五类：Window、Navigator、Screen、History、Location。



# 一、Window对象

Window 对象表示浏览器中打开的**窗口**。

## 1、特点

Window对象是**全局对象**，所有的表达式都可以再当前的环境中计算。因此，Window对象的属性和方法都可以直接使用，下面两种方式效果是一样的：

```js
window.alert(123);	//1
alert(123);			//2
```

## 2、方法

### 弹出框相关

1. alert()：显示带有一段消息和一个确认按钮的**警告框**。
2. confirm()：显示带有一段消息以及确认按钮和取消按钮的消息框（方法返回值：确定为true，取消为false）。
3. prompt()：显示可提示用户输入的对话框（返回值：用户输入的值）


### 打开关闭相关

1. close()：调用者是谁，就close谁的窗口。
2. open()：打开一个新的浏览器窗口，将会返回新的Window对象。

### 定时器相关

1. setTimeout(code,millisec)：在（millisec) 毫秒数后执行（code），只执行一次！
    - 参数
        - code：**js代码或方法对象**。
        - millisec：毫秒值。
    - 返回值：**返回唯一标识ID**，用于clearTimeout()方法取消指定的定时器。
2. clearTimeout(id_of_settimeout)：取消由 setTimeout() 方法设置的定时器。
    - 参数
      - id_of_settimeout ：传入setTimeout方法的返回值id，取消该定时器。
3. setInterval(code,millisec)：每隔（millisec）毫秒就执行（code），除非被clearInterval()取消，否则不会停。
4. clearInterval(id_of_setinterval)：取消由 setInterval() 设置的定时器。

> 下面两个参数、返回值和上面相同，区别在于：**前者是一次性的计时，后者是循环的计时**。

以下代码源自W3School：

```js
<script language=javascript>
var int=self.setInterval("clock()",50)
function clock()
  {
  var t=new Date()
  document.getElementById("clock").value=t
  }
</script>
<button onclick="int=window.clearInterval(int)">Stop interval</button>
```

## 3、属性

1. 其他BOM对象：history、location、Screen、Navigator
2. DOM对象：document

> 意思是：通过window对象可以方便的操纵其他BOM对象和DOM对象，我们之前见过的`document.write(123);`其中的DOM对象`document`就是通过Window全局对象来调用的。

# 二、Location对象

Location 对象包含有关当前 **URL** 的信息。

## 方法

reload()：重新加载当前文档，即刷新界面。

## 属性

href：可读可写，意味着既可以设置新的URL，也可以返回当前完整的URL。

# 三、History对象

History对象包含用户在浏览器窗口中**访问过的UR**L。

## 方法

1. back()：加载 history 列表中的前一个 URL。
2. forward()：加载 history 列表中的下一个 URL。
3. go()：加载 history 列表中的某个具体页面。

## 属性

length：返回当前窗口历史列表中的URL数量

# 四、Screen对象

Screen 对象包含有关客户端**显示屏幕**的信息。

# 五、Navigator

Navigator 对象包含有关**浏览器**的信息。



---

参考链接：[W3School--BOM](https://www.w3school.com.cn/jsref/dom_obj_screen.asp)