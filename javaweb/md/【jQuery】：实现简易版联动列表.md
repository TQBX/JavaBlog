# jQuery实现简易版列表联动

> 本文为学习过程中的总结，也许许多地方可能过于片面，考虑不周全，还望前辈们评论区批评指正呐！

简易版三级联动效果如下：

![3yotoR.png](https://s2.ax1x.com/2020/02/29/3yotoR.png)

代码如下：

```js

<!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>二级联动下拉框</title>
        <!--引入jquery的js库-->
        <script src="../js/jquery-1.4.2.js"></script>
        <script>
            $(function () {
                //定义国家+城市
                var Country_Province = {
                    "中国": ["北京", "浙江"],
                    "美国": ["A城", "B城"]
                };
                //定义城市+地区
                var Province_city = {
                    "北京": ["朝阳", "海淀"],
                    "浙江": ["杭州", "温州"],
                    "A城": ["C地区", "D地区"],
                    "B城": ["E地区", "F地区"]
                };
                //省份对象
                var $province = $("#province");
                //城市对象
                var $city = $("#city");

                //绑定onchange事件
                $("#country").change(function () {
                    //获取选中的国家
                    var country = $(this).val();
                    //根据国家得到对应的省份
                    var provinces = Country_Province[country];
                    //重置省份对应的元素
                    $province.html("<option>--请选择省份--</option>");
                    //遍历填写省份
                    for (var i = 0; i < provinces.length; i++) {
                        $("#province").append("<option>" + provinces[i] + "</option>");
                    }
                });

                $province.change(function () {
                    //获取选中的省份
                    var province = $(this).val();
                    //根据省份获取对应的城市
                    var cities = Province_city[province];
                    //找到城市对应的元素，清空

                    $city.html("<option>--请选择城市--</option>");
                    //遍历填写城市
                    for (var i = 0; i < cities.length; i++) {
                        $city.append("<option>" + cities[i] + "</option>");
                    }
                })
            })

        </script>
    </head>

    <body>

        <select id="country">
            <option >--请选择国家-</option>
            <option value="中国">中国</option>
            <option value="美国">美国</option>
        </select>

        <select id="province">
            <option>--请选择省份--</option>
        </select>

        <select id="city">
            <option>--请选择城市--</option>
        </select>
    </body>

</html>
```

总结一下注意的点：

- jQuery的引入确实简化了JS代码，JS操纵DOM对象，而jQuery操纵jQuery对象，`$()`。

- this关键字仍然代表的是DOM对象，所以如果希望它调用jQuery的函数，可以转化一下：`$(this)`。
- JS与jQuery操纵不同的对象，因此函数或者事件之类的都是不相同的！比如像下拉框的绑定事件是`change`，而不是`onchange`，获取HTML内容为`html()`，而不是`innerHTML`属性，获取value值为`val()`等等。
- `html()`可以获取HTML内容，`html(String)`可以设置HTML内容。
- jQuery的文档绑定事件可以简写成：`$(function () {...})`。

