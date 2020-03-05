> DML(Data Manipulation Language)：数据操作语言，用以操作表中数据。

假设现在已经在数据库中创建好了一个表，结构如下：

![3TbHc6.png](https://s2.ax1x.com/2020/03/05/3TbHc6.png)

# 一、添加表记录

## 1、添加全部的字段值

```sql
INSERT INTO stu (id,score,addr) VALUES (1,67.7,'china'); -- 插入全部字段

INSERT INTO stu VALUES (1,67.7,'china');				--  插入全部字段,同上
```

## 2、添加部分字段值

```sql
INSERT INTO stu (id,score) VALUES (2,85.9);	-- 插入部分字段的值，没有添加数据的字段以NULL填充
```

## 3、注意事项

列名和值需要一一对应。

```sql
INSERT INTO stu VALUES (43,34);	-- 会报错，列名和值没有匹配
```

字符和日期型数据需要用引号引起。

```sql
INSERT INTO stu (id,score,addr) VALUES (1,67.7,china);-- 会报错，字符类型未用引号引起
```

# 二、复制表中数据

```sql
CREATE TABLE new_stu LIKE stu; 	-- 创建和stu结构相同的表new_stu
```

## 1、复制所有数据

```sql
INSERT INTO new_stu SELECT * FROM stu;	-- 将stu表中的所有数据添加到new_stu中
```

## 2、复制指定字段的数据

```sql
-- 将stu的id,score字段数据对应添加到new_stu的id,score中，未添加字段依旧以NULL填充，
INSERT INTO new_stu(id,score) SELECT id,score FROM stu;
```

# 三、删除表记录

> 只是对表中数据进行删除，表结构还是存在着的。

## 1、删除表中所有数据

```sql
DELETE FROM stu;	-- 删除stu表所有数据，删除操作的次数和记录数有关

TRUNCATE TABLE stu;	-- 直接删除表，然后创建一张结构相同的表，效率较高
```

## 2、删除指定数据

```sql
DELETE FROM stu WHERE id=1; -- 删除stu表中id字段值为1的一条记录
```

# 四、更新表记录

## 1、修改所有行的数据

```sql
UPDATE stu SET score = 99.9; 	-- 将stu表中所有的score的值改为99.9
```

## 2、修改指定行的数据

```sql
UPDATE stu SET score = 88.8 WHERE id = 1;	--将id为1的行中score值改为88.8
```

## 3、修改多个字段的数据

```sql
UPDATE stu SET score = 77.7,addr = 'English' WHERE id = 1;--修改id为1的行的score和addr字段值
```



