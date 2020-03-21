# JSP学习+el+jstl

```
  Date: 2020/3/19
  Time: 19:16
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  hello,tomcat!
  </body>
</html>

```

编译后的目录在 CATALINA_BASE

![image-20200320170714713](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200320170714713.png)

```java
package org.apache.jsp;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent,
                 org.apache.jasper.runtime.JspSourceImports
```

