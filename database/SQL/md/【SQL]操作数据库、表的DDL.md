[toc]

# 一、DDL操作数据库

> 关键字理应大写，为便于记忆，本篇都采用小写形式。

DDL(DATA Definition Language)：数据定义语言，用于定义数据库对象。

## 1、创建数据库

```mysql
create database db1;	#创建一个数据库，名字叫mydb
create database if not exists db2;	#如果db2不存在，则创建数据库db2
create database db3 character set gbk;	#创建数据库db3并制定字符集为gbk

-- 综合：如果db4不存在，就创建它，并且设置字符集为utf8（！不是utf-8）
create database if not exists db4 character set utf8;
```
## 2、查看数据库

```mysql
show databases;			# 查询所有数据库的名称
show create database db1; #查询已创建数据库db1的字符集
```
## 3、修改数据库

```mysql
alter database db2 character set utf8;	#修改数据库db2字符集为utf8
```
## 4、删除数据库

```mysql
drop database db4;		#删除数据库db4
drop database if exists db3;	#判断是否存在并删除db3
```
## 5、使用数据库

```mysql
use db1;	# 使用数据库db1
select database(); #查看正在使用的数据库
```
# 二、DDL操作数据表

在使用某个数据库的前提下，才可以操作库里面的表结构。

## 1、创建表

### 建表格式

```mysql

create table 表名(
    列名1 数据类型1，## 此处可添加字段注释
    列名2 数据类型2，## 此处可添加字段注释
    ...
    列名n 数据类型n  ## 此处可添加字段注释
); ## 最后一列列不需要加逗号
```
如：

```mysql
create table student (
	id int, 	  -- 编号
	NAME varchar(20), -- 姓名
	gender char(8),	  -- 性别
	birthday date 	  -- 生日
);
```

### MySQL数据类型

> 数据类型有许多，以下为几个比较基础重要的。

### 文本相关

- varchar(m)：可变长度字符串 ，**使用几个字符就占用几个**，m为0-65535之间。
- char(m)：定长字符串，**无论使用几个字符都将占满全部**，多余的以空字符补充，m为0-255之间。

> 关于char和varchar推荐看这篇：[char与varchar类型区别的深度剖析](https://blog.csdn.net/lovemysea/article/details/82315514)

### 数相关

- int：整数类型。
- double：双精度浮点数类型。
- float：单精度浮点数类型。

### 时间相关

- date：日期类型，只包含年月日：`yyyy-MM-dd`。
- time：时间类型，只包含时分秒：`HH:mm:ss`。
- datetime：日期和时间类型都有，包含年月日时分秒：`yyyy-MM-dd HH:mm:ss`。

- timestamp：时间戳 格式和上相同，如果不给这个字段赋值，则自动用系统默认时间赋值。

### 复制表

```mysql
create table new_stu like old_stu;      # 复制表的操作
```

## 2、查看表

```mysql
show tables;   # 查询该数据库中所有的表名称
```
```mysql
desc student;	  # 查询表student的结构
```

![3oXs0S.png](https://s2.ax1x.com/2020/03/04/3oXs0S.png)

```mysql
show create table student; #查看student的创建表 SQL 语句
```

## 3、删除表

```mysql

drop table student;				#删除表student
drop table if exists student;	#判断student是否存在，并删除
```
## 4、修改表

### 修改表名

```mysql
alter table student rename to stu;  # 把student表改名为stu
rename table student to stu;
```
### 修改字符集

```mysql
alter table stu character set gbk;   # 修改字符集为gbk
```

### 添加表列

```mysql
alter table stu add address varchar(20);  # 增加一列，列名为address，数据类型varchar
```

### 修改列类型

```mysql
alter table stu modify address char(10);  # 修改address的数据类型，改为char
```

### 修改列名

```mysql
alter table stu change address addr char(10);  #修改列名为addr，数据类型改为char
```

### 删除列

```mysql
alter table stu drop addr; 		# 删除addr列
```







