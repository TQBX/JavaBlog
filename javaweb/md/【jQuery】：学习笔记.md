# jQuery概述



# jQuery的使用方式

```js
$("div").text("hello,jquery!");//$() jQuery对象
```

# JS对象和jQuery对象的转换

```js

var div = document.getElementById("test");//js对象
$(div).text("bbb");//js对象--> jquery对象

var div = $("div")[0];//jquery对象-->js对象
div.innerText = "ccc";
var div1 = $("div").get(0);//jquery对象-->js对象


<div id="test" class="test"></div>
```

