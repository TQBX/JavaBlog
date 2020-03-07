[toc]

借着上回外键约束的例子，这篇将对多表查询的一些概念和操作进行学习。

二话不说，直接创建一个例子：

```sql
CREATE TABLE dept(
id INT PRIMARY KEY AUTO_INCREMENT,
NAME VARCHAR(20)
);
INSERT INTO dept VALUES (NULL,'法师'),(NULL,'坦克'),(NULL,'剑客'),(NULL,'战士');

CREATE TABLE emp(
id INT PRIMARY KEY AUTO_INCREMENT,
NAME VARCHAR(20),
dept_id INT
);
INSERT INTO emp VALUES (NULL,'佐伊',1),(NULL,'扎克',2),(NULL,'亚索',3),(NULL,'盖伦',4)
```

# 一、笛卡尔积查询

```sql
-- 直接书写两张表的名称进行查询即可获取笛卡尔积查询的结果
SELECT * FROM dept,emp;	-- 两张表数据相乘

-- 在笛卡尔积查询的结果中需要筛选出正确的数据(类似内连接查询)
SELECT * FROM dept,emp WHERE emp.dept_id = dept.id;
```

![image-20200306232158339](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306232158339.png)

# 二、内连接查询

```sql
-- 在笛卡尔积查询的结果之上，获取两边表都有的记录。
SELECT * FROM dept INNER JOIN emp 
ON dept.id = emp.dept_id ;
```

![image-20200306232223841](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306232223841.png)

```sql
-- 选择指定内容显示
SELECT emp.NAME AS 英雄,dept.NAME AS 职业 FROM dept INNER JOIN emp
ON dept.id = emp.dept_id;
-- 给表起别名，所有的字段需要用别名获取
SELECT e.NAME AS 英雄,d.NAME AS 职业 FROM dept d INNER JOIN emp e
ON d.id = e.dept_id;
```

![image-20200306232632653](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306232632653.png)

# 三、外连接查询

## 左外连接

在内连接查询的基础上获取【左边表有而右边没有的数据】。

首先先添加一下这样的数据测试一下：

```sql
INSERT INTO dept VALUES (NULL,'射手');

-- 左外连接
SELECT * FROM dept LEFT JOIN emp 
ON dept.id = emp.dept_id;
```

![image-20200306233422196](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306233422196.png)

## 右外连接

在内连接查询的基础上获取【左边表没有而右边有的数据】。

还是再添加一个测试数据：

```sql
INSERT INTO emp VALUES(NULL,'菲兹',6);

-- 右外连接
SELECT * FROM dept RIGHT JOIN emp 
ON dept.id = emp.dept_id;
```

![image-20200306234428961](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306234428961.png)

## 全外连接

在内连接查询的基础上获取【左边表没有而右边有的数据+左边表有而右边没有的数据】。

MySQL中没有全外连接的关键字 FULL JOIN ,只能通过`UNION`实现全外连接的查询效果。

> 再UNION中，如果结果有相同的数据，则只会保留一份。

```sql
-- 全外连接
SELECT * FROM dept LEFT JOIN emp ON dept.id = emp.dept_id 
UNION	-- 全外连接关键字
SELECT * FROM dept RIGHT JOIN emp ON dept.id = emp.dept_id ;
```

![image-20200306235028335](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306235028335.png)

