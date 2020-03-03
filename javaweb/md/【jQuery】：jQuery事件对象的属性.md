【jQuery】：事件对象的类型



# event.type

> 获取事件的类型。

```js
        <a href="https://www.baidu.com">hello world</a>
```

```js
        $(function () {
            $("a").click(function (event) {
                alert(event.type);  //获取事件类型 click
                return false;       //阻止事件跳转
            })
        })
```

# event.preventDefault()

> 用于阻止默认的事件行为。

```js
        $(function () {
            $("a").click(function (event) {
                event.preventDefault();//点击超链接后，无法跳转
            })
        })
```

# event.stopPropagation()

> 用于阻止事件的冒泡。

冒泡的意思就是：多个元素响应同一个事件（嵌套），事件将会按照DOM的层次向上发生。举个例子：

```js
      //  定义两个div块，嵌套着。
		<style>
            .div1 {
                background-color: yellow;
                width: 100px;
                height: 100px;
            }
            .div2 {
                background-color: pink;
                width: 50px;
                height: 50px;
                margin: 0 auto;
            }
        </style>

        <body>
            <div class="div1">
                <div class="div2">
                </div>
            </div>
        </body>
```

```js
	//div1和div2都绑定了click事件
        <script>
            $(function () {
                $(".div2").click(function () {
                    alert("div2");
                })
                $(".div1").click(function () {
                    alert("div1");
                })
            })
        </script>

```

当点击里面那个div2时，就会依次弹出div2，div1的提示信息，这就是冒泡。

处理冒泡的其中一种方法就是利用事件对象的stopPropagation()方法：

```js
        $(".div2").click(function (event) {
            alert("div2");
            event.stopPropagation();
        })
```

# event.target

> 获取触发事件的元素。

## event.target与this的区别

event.target不会冒泡，永远指向触发事件的DOM元素本身。

this会冒泡，指向的元素在冒泡中会发生变化。





