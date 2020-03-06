>  所有的关系型数据库都支持对数据表使用约束，在表上强制执行数据校验，保证数据的完整性。

MySQL数据库支持以下四种约束形式：

# 非空约束 NOT NULL

所有数值类型的值都可以为null。

空字符串和0都不等于null。

```sql
-- 头铁之后的报错信息：Column 'name' cannot be null
-- 1、创建表时添加约束

CREATE TABLE stu(
	id INT,
	NAME VARCHAR(20) NOT NULL
	);
SELECT * FROM stu;

-- 2、表创建完毕之后，追加约束

ALTER TABLE stu MODIFY NAME VARCHAR(20) NOT NULL;
-- 3、 删除name的非空约束
ALTER TABLE stu MODIFY NAME VARCHAR(20);
```

# 唯一约束 UNIQUE

保证指定的列不允许出现重复值，但是可以存在多个null值。

```sql
-- 头铁之后的报错信息：Duplicate entry '1' for key 'phone_number'

-- 1、创建表时添加约束 
CREATE TABLE stu(
	id INT,
	phone_number VARCHAR(20) UNIQUE
	);
-- mySql中，唯一约束限定的列可以有多个null

-- 2、建表后追加唯一约束
ALTER TABLE stu MODIFY phone_number VARCHAR(20) UNIQUE;

-- 3、删除唯一约束
ALTER TABLE stu DROP INDEX phone_number;
```

# 主键约束 PRIMARY KEY

主键约束的功能相当于非空＋唯一约束，既不允许出现重复，也不允许出现null。

每个表中最多允许有一个主键，唯一确定一行记录的字段。

```sql
-- 头铁之后的报错信息：Duplicate entry '1' for key 'PRIMARY'

-- 1、创建表时，添加主键约束
CREATE TABLE student(
	id INT PRIMARY KEY,
	NAME VARCHAR(20)
	);
SELECT * FROM student;
-- 2、建表后，追加主键
ALTER TABLE student MODIFY id INT PRIMARY KEY;

-- 3、删除主键
ALTER TABLE student DROP PRIMARY KEY;
```

## 配合主键的自动增长

```sql
-- 自动增长，建议配合int类型的主键(如果不指定值，当前值为上一值加一）    
CREATE TABLE student(
	id INT PRIMARY KEY AUTO_INCREMENT,
	phone_number VARCHAR(20)
	);	
-- 创建表之后，追加自动增长
ALTER TABLE student MODIFY id INT AUTO_INCREMENT;
-- 删除自动增长
ALTER TABLE student MODIFY id INT;
```

# 外键约束  FOREIGN KEY

外键约束用以通知数据库与表字段之间的对应关系，以维护数据的完整性。

```sql
-- 首先创建不含外键约束的“一对多”关系的两张表
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
INSERT INTO emp VALUES (NULL,'佐伊',1),(NULL,'扎克',2),(NULL,'亚索',3),(NULL,'盖伦',4);
```

一对多的关系是，通过emp表中的dept_id字段关联到dept表中的id字段，这样我们一看到这个英雄是什么序号，就可以通过多表查询，得到这个英雄具体的职业是啥。

在没有添加外键约束的情况下，可能会出现以下几种不合理的情况：

```sql
-- 不合理情况1：可以任意插入英雄，选择不存在的职业
INSERT INTO emp VALUES(NULL,'艾希',5);

-- 不合理情况2：在仍有英雄存在的情况下，删除这个职业
DELETE FROM dept WHERE id = 4;
```

很明显，都是非常无理取闹的请求，如何去解决呢，通过**外键约束**。

再来试着创建两个表，并且添加上外键约束：

```sql
-- 新建表，添加外键
CREATE TABLE dept(
id INT PRIMARY KEY AUTO_INCREMENT,
NAME VARCHAR(20)
);
INSERT INTO dept VALUES (NULL,'法师'),(NULL,'坦克'),(NULL,'剑客'),(NULL,'战士');

CREATE TABLE emp(
id INT PRIMARY KEY AUTO_INCREMENT,
NAME VARCHAR(20),
dept_id INT,	-- 不是最后一行，注意加上逗号
FOREIGN KEY(dept_id) REFERENCES dept(id)-- 添加外键约束（emp中的dep_id字段依赖于dept表中的id字段）
);
INSERT INTO emp VALUES (NULL,'佐伊',1),(NULL,'扎克',2),(NULL,'亚索',3),(NULL,'盖伦',4);
```

这个时候再搞一搞无理取闹的要求试试，这个时候就不可了，会报错呢。

```sql
-- Cannot add or update a child row: a foreign key constraint fails (xxx省略)
-- Cannot delete or update a parent row: a foreign key constraint fails (xxx省略)
```

（ps：我发现数据库的报错信息都非常直接，每次都能一眼看出来错出在哪）

这时，如果想删除这个职业，也不是不可以，你需要把这个职业里面代表的英雄全给删咯，这样就可以删除职业了。

```sql
-- 1.需要先删除战士英雄，也就是dept_id = 4的记录
DELETE FROM emp WHERE dept_id = 4;
-- 2.接着删除这个职业，成功删除
DELETE FROM dept WHERE id = 4;
```

当然，外键约束这玩意儿，其实还是蛮危险的，因为在数据量很大的情况下，由于错误设置外键，可能会导致很多数据进不了数据库，也有可能会导致很多功能难以扩展。

