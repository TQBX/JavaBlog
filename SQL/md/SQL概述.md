# 一、数据库的定义

- 数据库是“按照数据结构来**组织**、**存储**和**管理**数据的仓库”。

- 是一个**长期存储**在计算机内的、有组织的、有共享的、统一管理的数据集合。

> 严格来说，**数据库**（Database）仅仅是存放用户数据的地方，用户进行访问、操作数据库内的数据时，还需要**数据库管理系统**的帮助（Database Management System），简称**DBMS**，通常把这俩称作数据库。

# 二、什么是关系型数据库

- 关系型数据库是数据库的其中一种，是指采用了关系模型来组织数据的数据库。
- 关系模型指的就是**二维表格**模型，而一个关系型数据库就是由二维表及其之间的联系所组成的一个数据组织。
- 常见的关系型数据库有MySQL、Oracle、SQL Server、DB2 等。（PS：当然我学习就是用MySQL了）
- 关系型数据库都可以使用通用的SQL语句进行管理。

# 三、SQL的定义

Structured Query Language：结构化查询语言，是一种非过程性的语言。

SQL是操作和检索关系型数据库的标准语言，**定义了操作所有关系型数据库的规则**。

当然，各个厂商为了加强SQL的语言能力，各自存在着不同的地方，操作方式存在不同。

# 四、SQL分类

- DDL(Data Definition Language)：数据定义语言
  - 用来定义数据库对象：数据库，表，列等。
  - 关键字：create，drop，alter，truncate等。

- DML(Data Manipulation Language)：数据操作语言
  - 用来对数据库中表的数据进行增删改。
  - 关键字：insert, delete, update 等。
- DQL(Data Query Language)：数据查询语言
  - 用来查询数据库中表的记录(数据)。
  - 关键字：**select**等。
-  DCL(Data Control Language)：数据控制语言
  - 用来定义数据库的访问权限和安全级别，及创建用户。
  - 关键字：grant、revoke 等 。

# 五、SQL基础语法

>  之后的内容将基于MySQL。

- SQL语句可以单行或多行书写，以分号结尾

- 可以使用空格和缩进来增强语句的可读性。

- MySQL数据库的SQL语句不区分大小写，关键字建议使用大写。

- ```SQL
  -- 注释：#为MySQL特有
  mysql> SHOW DATABASES; -- 查询所有数据库名称
  mysql> show databases; # 查询 
  mysql> show databases; /*查询数据库名称*/
  ```

---

参考链接：[简述关系型数据库和非关系型数据库](https://www.jianshu.com/p/fd7b422d5f93)

