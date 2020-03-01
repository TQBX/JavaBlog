# jQuery实现轮播图无缝连接

参考链接地址：[https://blog.csdn.net/qq_36996271/article/details/82015950](https://blog.csdn.net/qq_36996271/article/details/82015950)

```js

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Document</title>
        <style>

            * {
                margin: 0;
                padding: 0;
            }

            .banner {
                border: #0066FF 5px solid;
                width: 600px;
                height: 400px;
                /*auto：浏览器计算外边距*/
                margin: 100px auto;
                overflow: hidden;
                /*cursor:设置或检索在对象上移动的鼠标指针采用的光标形状。*/
                cursor: pointer;
                position: relative;

            }

            .banner .slide {

                width: 4000px;
                height: 400px;
                /*要激活对象的绝对(absolute)定位，必须指定 left ， right ， top ， bottom 属性中的至少一个*/
                position: absolute;
                left: -600px;
            }

            .banner .slide .pic {
                width: 600px;
                height: 400px;
                background: #00ffFF;
                line-height: 400px;
                text-align: center;
                float: left;
                font-size: 72px;
                color: #ffffff;
            }

            .banner .slide .a {
                background-color: black;
            }

            .banner .slide .b {
                background-color: green;
            }

            .banner .slide .c {
                background-color: purple;
            }

            .banner .dots {
                width: 100px;
                height: 30px;
                position: absolute;
                bottom: 0px;
                /*固定到中间*/
                left: 50%;
                margin-left: -50px;
                /*z-index的值大的会覆盖在小的上面*/
                z-index: 2;

            }

            .banner .dots .dot {
                /*圆点样式*/
                width: 20px;
                height: 20px;
                float: left;
                border-radius: 50%;
                background-color: rgba(7, 17, 27, 0.4);
                margin: 5px 6px;
                box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.8) inset;
                cursor: pointer;
            }

            .banner .dots .active {
                /*小圆点高亮的样式*/
                box-shadow: 0 0 0 2px rgba(7, 17, 27, 0.4) inset;
                background-color: #fff;
            }

            .banner .arrow {
                /*左箭头*/
                width: 0;
                height: 0;
                border-right: 30px solid rgba(255, 255, 255, .5);
                border-top: 30px solid transparent;
                border-bottom: 30px solid transparent;
                position: absolute;
                left: 0;
                top: 50%;
                margin-top: -30px;
                z-index: 2;
            }

            .banner .arrow:hover {
                /*鼠标移动到箭头时候的样式*/
                border-right-color: white;
            }

            .banner .next {
                /*右箭头*/
                left: auto;
                right: 0;
                top: 50%;
                margin-top: -30px;
                transform: rotate(180deg);
                z-index: 2;
            }

        </style>
    </head>
    <body>
        <div class="banner">
            <div class="slide">
                <!-- 当切换到A（副本）的时候,立马用JS将图片替换成图片A，
				然后，再从第二张开始轮播,这个瞬间就是0ms，肉眼无法察觉。-->
                <div class="pic c">C</div><!-- C(副本)-->
                <!-- 真正要轮播的就这三个ABC图片 -->
                <div class="pic a">A</div>
                <div class="pic b">B</div>
                <div class="pic c">C</div>
                <!-- 这边也是同样的道理，无缝轮播要添加的副本 -->
                <div class="pic a">A</div><!-- A(副本)-->
            </div>
            <div class="dots">
                <div class="dot active"></div>
                <div class="dot"></div>
                <div class="dot"></div>
            </div>
            <div class="arrow next"></div>
            <div class="arrow prev"></div>
        </div>
        <!-- 这里引用jQuery的源码 -->
        <script type="text/javascript" src="../jquery-1.4.2.min.js"></script>
        <script type="text/javascript">

            //获取对应的jQuery元素
            var $pic = $(".pic");
            var $banner = $(".banner");
            var $slide = $(".slide");
            var $dot = $(".dot");
            var $next = $(".next");
            var $prev = $(".prev");
            //定时器
            var timer = null;
            //控制图片索引
            var index = 1;
            //获取图片宽度
            var picWidth = $pic.width();
            //获取图片的张数
            var size = $pic.length;
            //鼠标移上去的时候轮播暂停
            $banner.mouseover(function () {
                //关闭定时器
                clearInterval(timer);
            });
            //鼠标移走的时候，轮播继续
            $banner.mouseleave(function () {
                //自动播放
                timer = setInterval(function () {
                    //图片切换
                    index++;
                    changeImg();
                    changeDots();

                }, 1000)
            });

            //触发鼠标移走事件，初始化轮播状态
            $slide.mouseleave();

            //改变图片函数
            function changeImg() {
                //计算移动的距离: - 图片宽度*索引
                var slideWidth = -1 * picWidth * index;
                $slide.animate({
                    "left": slideWidth + "px"
                }, "1500");

                //索引从A开始，而不是副本开始

                //此时移动到最后一张图片是，设置animate函数的值0，瞬间切换成第一张
                if (index > size - 2) {
                    $slide.animate({
                            "left": -picWidth + "px"
                        }, 0
                    );
                    //索引切换为A的索引
                    index = 1;
                }
                //index<1的情况将会发生在点击上一张时
                if (index < 1) {
                    $slide.animate({
                            "left": -picWidth * (size - 2) * +"px"
                        }, 0
                    );
                    //索引切换为C的索引
                    index = size - 2;
                }

            }

            // 小圆点切换函数
            function changeDots() {
                //圆点索引始终为图片索引-1
                //将当前圆点设置active样式，其他圆点取消active样式
                $dot.eq(index-1).addClass("active").siblings().removeClass("active");
            }
            //点击小圆点事件
            $dot.click(function (event) {
                //返回触发事件的元素，获取的是dom对象
                var target = event.target;

                //转为jQuery对象，索引加一
                index = $(target).index()+1;

                changeImg();
                changeDots();
            });
            //点击切换下一张
            $next.click(function () {
                index++;
                changeImg();
                changeDots();
            });
            //点击切换上一张
            $prev.click(function () {
                index--;
                changeImg();
                changeDots();
            })

        </script>
    </body>
</html>
​```
```

自我总结：

- 本身对CSS样式不太熟悉，这段代码参考自其他博主，一步步测试CSS样式属性，加深了对CSS定位的理解。

  - `position：absolute`：生成绝对定位的元素，相对于static定位以外的第一个父元素进行定位，如上面的banner。
  - `position：relative`：相对定位，相对于其原本的位置进行定位，如上面的几个div块。

  - `z-index: 2`：设置优先级，z-index值大的将会覆盖在上面。

- 其次是关于定时器的实现，通过`setInterval(函数，毫秒值）`和`clearInterval(定时器id)`控制循环执行函数。

- 还有就是jQuery实现动画效果的函数：`$(selector).animate({params},speed,callback);`

  - 需要注意的是，所有的元素的position属性值默认都是静态的，无法移动，如果希望移动，需要设置position的值。
  - {params}：必须的参数，代表形成动画的css属性。
  - speed：可选参数，代表效果时长，可以指定：`"slow"`,`"fast"`或毫秒值。
  - callback：可选参数，代表动画完成之后执行的函数名称。

```js
    $slide.animate({
        "left": -picWidth + "px"
    }, 0);
	//动画执行效果持续0ms，瞬间完成，向左移动一张图片。
```

- 还有一个比较重要的**DOM事件属性**，叫做target，它可以返回触发事件的元素，原博主代码如下：

```js
    //点击小圆点事件
    $dot.click(function (event) {
        //返回触发事件的元素，获取的是dom对象
        var target = event.target;

        //转为jQuery对象，索引加一
        index = $(target).index()+1;

        changeImg();
        changeDots();
    });
```

- 我做了些改动，用this关键字代表当前触发事件的元素，仍然是DOM对象，需要向jQuery转化。

```js
    //点击小圆点事件
    $dot.click(function () {
        //this为DOM对象，需要$(this)转化为jQuery
        index = $(this).index()+1;
        changeImg();
        changeDots();
    });
```

---

另：如有错误，还望各位前辈评论区指出！万分感谢！