# 分组查询

之前学习聚合函数，知道聚合函数在默认情况下，将会把所有的记录当成一组，让我们在对列求值，计算时更方便了一些。

但是，在某些情况下，我们需要显式地对记录进行分组，使用的是group by [column1,column2..]。

这样，**查询结果将会根据group by后面的字段，将值相同的记录分成一组**。举个例子，我有一份管理学生信息的表，这时候我想查一下男生和女生各多少人，男生总分最高是谁等等，我们完全可以通过`group by 性别字段`进行操作。

# 具体应用

暂时忽略数据的正确性，先创建一个表，如下：

```sql
CREATE TABLE tab(
	NAME VARCHAR(10),
	score INT,
	gender CHAR(2),
	salary INT
);
	
INSERT INTO tab VALUES('马超',80,'男','1000');
INSERT INTO tab VALUES('小乔',60,'女','1000');
INSERT INTO tab VALUES('曹操',90,'男','2000');
INSERT INTO tab VALUES('貂蝉',55,'女','1000');

SELECT * FROM tab;
```

由于截图过于丑陋，索引直接写出来吧：

```js
| name |  score  | gender | salary|
| 马超  |  80     |  男    |  1000 |
| 小乔  |  60     |  女    |  1000 |
| 曹操  |  90     |  男    |  2000 |
| 貂蝉  |  55     |  女    |  1000 |
```

先来看一个错误案例：

```sql
-- 按性别分组，name的位置将会只显示一个，name不具有共性
SELECT NAME,gender FROM tab GROUP BY gender;
```

结果如下：

```sql
| name | gender |
| 马超  |  男    |
| 小乔  |  女    |
```

可以看到，确实按照性别分成了两组，但是曹操和貂蝉都从表中消失了，是因为如果某个记录没有出现在group by之后或者没有用聚合函数扩起，就会输出该列的第一条记录的值。

---

分组查询通常和聚合函数配套使用，效果俱佳，比如下面：

```sql
-- 依据性别分组，分别查询男或女的人群中各自的平均分，各自的人数，各自里的最大工资
SELECT gender,AVG(score),COUNT(gender),MAX(salary) FROM tab GROUP BY gender;
```

```c
| gender |  avg(score)  | count(gender) | max(salary) |
|   男  	|   85.0000    |       2   	   |    2000     |
|   女  	|   57.5000    |       2       |    1000     |
```



# where和having

同样的之前学习过where关键字表示条件，分组查询中还有个having关键字用于筛选，来细品。

```sql
-- 在上面的基础上加一个条件，如果分数不是60或80，就不参与分组，貂蝉和曹操就尴尬离去了。
SELECT gender,AVG(score),COUNT(gender),MAX(salary) FROM tab WHERE score IN (60,80) GROUP BY gender;
```

```sql
| gender |  avg(score)  | count(gender) | max(salary) |
|   男  	|   80.0000    |       1   	   |    1000     |
|   女  	|   60.0000    |       1       |    1000     |
```



```sql
-- 加上的条件为，如果查询出来的结果平均分大于60才会被显示出来
SELECT gender,AVG(score),COUNT(gender),MAX(salary) FROM tab GROUP BY gender HAVING AVG(score)>60;
```

```sql
| gender |  avg(score)  | count(gender) | max(salary) |
|   男  	|   85.0000    |       2   	   |    2000     |
```



总结：

- where子句用于过滤行，在分组之前进行限定，且子句不能使用聚合函数。
- having子句用于过滤组，在分组之后对结果进行限定，且可以配合使用聚合函数。

