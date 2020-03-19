【Servlet端】

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        List<String> userNames = new ArrayList<>();
        userNames.add("天乔巴夏");
        userNames.add("summerday");

        String username = request.getParameter("username");
        PrintWriter writer = response.getWriter();
        if (userNames.contains(username)){
            writer.write("用户名已经存在");
        }else {
            writer.write("用户名可使用");
        }
    }
```



# $.get()

> $.get(url, [data], [callback], [type])

```js
<%--
  Date: 2020/3/19
  Time: 9:00
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>ajax测试</title>
        <script src="<%=request.getContextPath()%>/js/jquery-1.4.2.min.js"></script>
        <script>
            $(function () {
                $("input[type='button']").click(function () {
                    var username = $("#username").val();
                    $.get(                                          //GET请求方式
                        "<%=request.getContextPath()%>/AjaxServlet",//URI
                        {
                            "username":username,
                            "password":"123456"  					//发送请求的数据
                        },  
                        		
                        function (result,status) {                  //回调函数
                            alert("result:"+result+"\nstatus:"+status)
                        }
                    )
                })
            })
        </script>
    </head>
    <body>
        姓名：<input type="text" id="username" name="username" placeholder="请输入姓名">
        <input type="button"  value="判断用户名是否存在">
    </form>
    </body>
</html>
```



![get](E:\1myblog\JavaBlog\JavaBlog\tomcat_servlet\pic\get.png)

# $.post()

> $.post(url, [data], [callback], [type])

```js
$.post(                                          //POST请求方式
    "<%=request.getContextPath()%>/AjaxServlet", //URI
    {
        "username":username,                     //发送请求的数据
        "password":"123456"
    },
    function (result,status) {                   //回调函数
        alert("result:"+result+"\nstatus:"+status)
    }
)
```

post请求方式的参数不拼接在URL中，而是在请求体中，格式：`username=summer&password=123456`。

# $.ajax()

> $.ajax({key:value}); 键值对处用于设置属性

```js
<script>
    $(function () {
        $("input[type='button']").click(function () {
            var username = $("#username").val();
            $.ajax({                                                    //POST请求方式
                "url": "<%=request.getContextPath()%>/AjaxServlet", //URL
                "data": {
                    "username": username,                           //发送请求的数据
                    "password": "123456"
                },
                "async": true,                                      //异步
                "type": "POST",                                     //"POST"
                "success": function (result, status) {              //回调函数(响应成功)
                    alert("result:" + result + "\nstatus:" + status)
                },
                "error": function () {                              //回调函数(响应失败)
                    alert("wrong!")
                },
                dataType: "text"                                    //接收响应数据格式
            }
                  )
        })
    })
</script>
```

