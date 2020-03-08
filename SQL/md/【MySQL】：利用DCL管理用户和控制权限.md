>  DCL(Data Control Language)：数据控制语言，用来定义数据库的访问权限和安全级别，及创建用户。

# 一、管理用户

## 1、创建用户

```sql
-- 创建用户
CREATE USER '用户名'@'主机名' IDENTIFIED BY '密码';

CREATE USER 'Summerday'@'localhost' IDENTIFIED BY '123456';
```

ps：如果出现了`[The MySQL server is running with the --skip-grant-tables option so it cannot execute this statement]`的错误，可以先执行`FLUSH PRIVILEGES;`语句。

## 2、修改用户

```sql
-- 修改密码
SET PASSWORD FOR '用户名'@'主机名' = PASSWORD('新密码');

SET PASSWORD FOR 'Summerday'@'localhost' = PASSWORD('hyh123');
```

## 3、查询用户

```sql
-- 1. 切换到mysql数据库
USE mysql;
-- 2. 查询user表
SELECT * FROM USER;
```

>  %通配符匹配所有。

## 4、删除用户

```sql
-- 删除用户
DROP USER '用户名'@'主机名';

DROP USER 'Summerday'@'localhost';
```

# 二、权限管理

## 1、查询权限

```sql
-- 查询权限
SHOW GRANTS FOR '用户名'@'主机名';

SHOW GRANTS FOR 'Summerday'@'localhost';
```

## 2、授予权限

```sql
-- 授予权限
GRANT 权限列表 ON 数据库名.表名 TO '用户名'@'主机名';

GRANT SELECT ON mydb2.account TO 'Summerday'@'localhost';

-- 授予所有权限
GRANT ALL ON *.* TO 'Summerday'@'localhost';
```

## 3、撤销权限

```sql
-- 撤销权限
REVOKE 权限列表 ON 数据库名.表名 FROM '用户名'@'主机名';

REVOKE SELECT ON mydb2.account TO 'Summerday'@'localhost';

-- 撤销所有权限
REVOKE ALL ON *.* FROM 'Summerday'@'localhost';
```

