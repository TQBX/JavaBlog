# EL   Expression Language

基本形式：`${el表达式}`

# 获取数据

只能获取域中的数据，不能设置只能获取，不能遍历。

【获取常量】

​	支持获取数字，字符串，布尔类型的常量。

【域中自动搜寻获取变量】

​	自动搜寻域，获取变量，在四大作用域中由小到大的顺序搜寻指定名称的属性。

​	如果搜寻到，直接获取并返回，如果没有就返回一个空字符串。

【指定域获取变量】

​	可以直接使用如下四个内置对象操纵指定域：

```js
${pageScope.name }
${requestScope.name }
${sessionScope.name }
${applicationScope.name }
```

【获取数组】

数据必须要在某一个域中，才可以获取

```jsp
<% 
String names [] = {"张三丰","张无忌","张翠山","谷丰硕"};
pageContext.setAttribute("names", names);
%>
${names[3] }
```

【获取集合】

和数组类似。

```jsp
<%
List<String> list = new ArrayList<String>();
list.add("天乔巴夏");
list.add("summerday");
pageContext.setAttribute("list", list);
%>
${list[0] }
```

【获取map】

```jsp
<%
Map<String,String> map = new HashMap<String,String>
();
map.put("name","天乔巴夏");
map.put("gender","男");
map.put("age",18)
pageContext.setAttribute("map", map);
%>
${map["name"] }
${map.gender }

```

【获取Javabean】

```jsp
<%
Person p = new Person("zs",19,"bj");
pageContext.setAttribute("p", p);
%>
${p.namex }
${p.age }
${p.addr }
${p["age"] }
```

# 执行运算

【算术运算】【关系运算】【逻辑运算】【三元表达式】

【Empty运算】

```jsp
<%
<hr><h1>EL执行运算 - empty运算</h1>
<h1>Empty运算规则：如果对象为null 字符串为空 集合数组没有任
何元素 empty操作都会返回true，否则false</h1>
String country = "China";
pageContext.setAttribute("country", country);
String province = "";
pageContext.setAttribute("province",province);
List<String> listx = new ArrayList<String>();
pageContext.setAttribute("listx",listx);
%>
${empty country}
${empty province}
${empty listx}
```

# 内置对象

【作用域】pageScope、requestScope、sessionScope、ApplicationScope

【请求参数】：param、paramValues

【web应用初始化参数】：initParam

【请求头】：header、headerValues

【所有cookie组成的Map】：cookie

【当前页面的对象】：pageContext

#  jstl标签技术

JavaServerPages Standard Tag Library

可以和 EL 配合来取代传统直接在页面上嵌入 Java 程序（Scripting）的做法，以提高程序可读性、维护性和方便性。

# 引入jstl

`<%@taglib uri="" prefix=""%>`

# jstl标签库的子库

核心标签库：`<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>`

# jstl常用标签

## set

【设置+修改】

```jsp
        <c:set var="name" value="天乔巴夏" scope="request"></c:set>
        ${name}
        <c:set var="name" value="summerday"></c:set>
        ${name}

```

天乔巴夏 summerday

【map】



