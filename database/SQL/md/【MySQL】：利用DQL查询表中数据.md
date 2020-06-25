>  DQL(Data Query Language)：数据查询语言，用以查询数据库中表的记录(数据)。

```sql
CREATE TABLE exam(
	id INT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(20) NOT NULL,
	chinese DOUBLE,
	math DOUBLE,
	english DOUBLE
);

INSERT INTO exam VALUES(NULL,'关羽',85,76,70);
INSERT INTO exam VALUES(NULL,'张飞',70,75,70);
INSERT INTO exam VALUES(NULL,'赵云',90,65,95);
INSERT INTO exam VALUES (NULL,'黄忠',NULL,88,76);
```

![image-20200306144034658](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200306144034658.png)

# 一、基本查询

>  select关键字后面跟着的是字段名，*表示查询结果显示所有字段。

## 1、查询所有信息与指定信息

```sql
-- 查询表中所有学生的信息
SELECT * FROM exam;
SELECT id,NAME,chinese,math,english FROM exam;

-- 查询表中所有学生的姓名和英语成绩
SELECT NAME,english FROM exam;

```

## 2、清除重复值

```sql
-- 过滤重复的英语成绩
SELECT DISTINCT english FROM exam;
```

## 3、查询结果参与运算

> 参与运算的需要是数值类型。

```sql
-- 计算总分并以score显示 也可省去 AS (如果有null值，结果也会为null，可用ifnull(x,y))
SELECT NAME,math+chinese+english AS score FROM exam;

-- 查询所有成绩的总和，如果null用0代替。
SELECT NAME,IFNULL(math,0)+IFNULL(chinese,0)+IFNULL(english,0) AS score FROM exam;
```



# 二、条件查询

> SELECT 字段名 FROM 表名 WHERE 条件

## 1、比较运算符

```sql
-- 查询姓名为张飞的成绩
SELECT * FROM exam WHERE NAME='张飞';

-- 查询英语成绩大于90的同学
SELECT * FROM exam WHERE english>90;

-- 查询数学成绩为75，76的同学  IN（集合）
SELECT NAME,math FROM exam WHERE math IN(75,76);

-- 查询英语成绩为null的同学 is null 而不是 = null
SELECT NAME,chinese FROM exam WHERE chinese IS NULL;

-- 查询英语成绩[70,95]区间内的同学  BETWEEN..AND
SELECT NAME,english FROM exam WHERE english BETWEEN 70 AND 95;

-- 查询所有姓张的同学  模糊查询 LIKE pattern 
SELECT * FROM exam WHERE NAME LIKE '张%';

```

> MySQL通配符
>
> - %：匹配任意多个字符串。
> - _：匹配一个字符。

## 2、逻辑运算符

```sql
-- 查询数学大于70，语文大于80的同学
SELECT * FROM exam WHERE math>70 AND chinese>80;

-- 查询语文成绩不大于80的同学
SELECT * FROM exam WHERE NOT(chinese>80);

-- 查询数学成绩大于90，或者语文成绩大于80的同学
SELECT * FROM exam WHERE math>90 OR chinese>80;
```

# order by排序

## 1、单列排序

```sql
-- 排序查询 select column1,column2.. from table_name order by column_name asc|desc

SELECT NAME,IFNULL(chinese,0) FROM exam ORDER BY chinese;	-- 默认升序asc
SELECT NAME,IFNULL(chinese,0) FROM exam ORDER BY chinese DESC;	-- 指定降序desc
```

## 2、组合排序

```sql
-- 查询所有同学信息，英语默认升序，英语成绩相同的情况下，按数学成绩降序
SELECT * FROM exam ORDER BY english,math DESC;
```

# 聚合函数

> 聚合函数将会忽略空值，与此前不同，聚合函数可处理一列的值：求和，求平均等等。

## 1、COUNT：计算个数

```sql
-- 统计班级共有几个学生
SELECT COUNT(id) id_count FROM exam;
SELECT COUNT(1) FROM exam;
SELECT COUNT(*) FROM exam;

-- 统计数学成绩大于80的学生个数
SELECT COUNT(math) FROM exam WHERE math>80;
```

## 2、SUM：求和

```SQL
-- 统计数学总成绩
SELECT SUM(math) FROM exam;

-- 统计语文、英语、数学各科的总成绩
SELECT SUM(math),SUM(chinese),SUM(english) FROM exam;

-- 统计语文、英语、数学各科的总成绩
SELECT SUM(IFNULL(math,0)+IFNULL(chinese,0)+IFNULL(english,0)) AS all_sum FROM exam;
```

## 3、AVG：求平均

```sql
-- 查询数学平均分
SELECT AVG(math) FROM exam;
```

## 4、MAX/MIN：最大/最小值

```sql
-- 查询最小的语文成绩
SELECT NAME,MIN(IFNULL(chinese,0)) FROM exam;
-- 查询最大的英语成绩
SELECT NAME,MAX(english) FROM exam;
```

