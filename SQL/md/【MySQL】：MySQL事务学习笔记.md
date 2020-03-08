[toc]

# 一、事务的概念

>  什么是事务呢？

事务是由一步或几步数据库操作序列组成的逻辑执行单元，这系列操作**要么全部执行，要么全部放弃执行**。

# 二、事务的四大特性

原子性(Atomic)，一致性(Consistency)，隔离性(Isolation)，持续性(Durability)，简称ACID性。

## 1、原子性

原子性在多线程的时候学习过，通常表示不可再分的操作，表示**事务是应用中最小的执行单位**。

## 2、一致性

**事务操作前后，数据总量不变**。如A像B转账500，A得减少500，B得加上500，这样才算是保证了数据库的一致性。如果A减了，B没加上去，这不是耍流氓莫。

一致性是通过原子性来保证的。


## 3、隔离性

各个事务的执行**互不干扰**，任意一个事务的内部操作对其他并发的事务都是隔离的。

## 4、持续性

当事务提交或回滚后，数据库将会**持久化地保存数据**。

# 三、事务语句

数据库的语句由下列语句组成：

- 一组DML语句。
- 一条DDL语句。
- 一条DCL语句。

模拟转账：

```sql
CREATE TABLE account(
	id INT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(10),
	balance DOUBLE
	);
	
INSERT INTO account (NAME,balance) VALUES ('张三',1000),('李四',1000);
```

转账成功后，张三余额还剩500，李四余额还剩1500。

```sql
-- 张三向李四转账500
UPDATE account SET balance = balance - 500 WHERE NAME = '张三';
UPDATE account SET balance = balance + 500 WHERE NAME = '李四';
```

假设此时，在张三转账完毕，余额减去500之后，数据库出现了意料之外的异常，导致李四的余额并没有加上500，那么这个问题就非常严重辽，就像下面这样：

```sql
UPDATE account SET balance = balance - 500 WHERE NAME = '张三';
Something IS wrong...	-- 不是sql语句，将发生异常
UPDATE account SET balance = balance + 500 WHERE NAME = '李四';
```

就像是什么问题呢，就像是充话费，你充了100块钱，充完之后你一看，没充上，血亏。

这时就可以利用事务来处理这个问题：

## 1、开启事务：start transaction

```sql
-- 开启事务
START TRANSACTION;

UPDATE account SET balance = balance - 500 WHERE NAME = '张三';
Something IS wrong...
UPDATE account SET balance = balance + 500 WHERE NAME = '李四';
```

这时转账还是没有正常执行对吧，通过`select*from account;`查看一下，果然还是一个500，一个1000，但是其实这只是**临时的数据，并没有将数据永久修改**，可以再另外一个窗口查看，发现数据并没有变化，也就是说，当你发现临时数据不符合预期，就可以立即进行事务回滚。

## 2、事务回滚：rollback

```sql
-- 开启事务
START TRANSACTION;

UPDATE account SET balance = balance - 500 WHERE NAME = '张三';
Something IS wrong...
UPDATE account SET balance = balance + 500 WHERE NAME = '李四';

-- 回滚事务
ROLLBACK;
```

回滚之后，一切又回到最初的起点，记忆中两人的余额都是1000。

### 指定回滚点

相当于设置了个断点：`savepoint a;`。

回滚到该断点：`rollback to a;`

## 3、提交任务：commit

既然是临时数据，那么如何将他变成永久性的呢，这便是提交任务。

```sql
-- 开启事务
START TRANSACTION;

UPDATE account SET balance = balance - 500 WHERE NAME = '张三';
UPDATE account SET balance = balance + 500 WHERE NAME = '李四';

-- 提交任务
COMMIT;
```

ok，提交之后，表中数据就真正地被修改辽。

# 四、事务的提交

当事务所包含的任意一个数据库操作执行成功或者失败之后，都应该提交事务，无论是commit还是rollback。提交方式有两种，自动提交或者手动提交。

## 1、查询事务提交方式

```sql
SELECT @@autocommit; -- 1代表自动提交，0代表手动提交
```

**MySQL中事务提交方式默认是自动提交的。** 

## 2、修改事务提交方式

```sql
-- 关闭默认提交，即开启事务。
SET @@autocommit = 0;
```

需要注意的是，**一旦设置将提交方式设置成手动提交，相当于开启了一次事务**，那么所有的DML语句都需要显示地使用commit提交事务，或者使用rollback回滚结束事务。

> 当前会话窗口修改事务提交的方式，对其他的会话窗口没有影响。

# 五、事务的隔离级别

**事务具有隔离性**，多个事务之间相互独立。但多个事务操作同一批数据，将会引发一些问题，可**设置不同的隔离级别解决问题**。

## 1、存在问题

- 脏读(Dirty Read)：一个事务读取到另一个事务中没有提交的数据。
- 不可重复读(Nonrepeatale Read)：在同一个事务中，两次读取到的数据不同。
- 幻读(Phantom Read)：一个事务操作DML数据表中所有记录，另一个事务添加了一条数据，则第一个事务查询不到自己的修改。

## 2、查询与设置隔离级别

查询隔离级别

```sql
-- 数据库查询隔离级别 MySQL默认 repeatable read
SELECT @@tx_isolation;
```

设置隔离级别

```sql
-- 设置隔离级别为read-uncommited（需重启生效）
SET GLOBAL TRANSACTION ISOLATION LEVEL 隔离级别字符串;
```

## 3、隔离级别分类

### read uncommitted：读未提交

>  脏读、不可重复读、幻读都会发生

演示

```sql
-- 设置隔离级别为read-uncommited（需重启生效）
SET GLOBAL TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
```

其中事务A开启并转账，但是并没有提交，此时临时读取到的数据是500，1500：

```sql
-- 开启事务
START TRANSACTION;
UPDATE account SET balance = balance - 500 WHERE NAME = '张三';
UPDATE account SET balance = balance + 500 WHERE NAME = '李四';
```

这时事务B开启并查询账户，读取到了刚才事务A并未提交掉的数据，500，1500：

```sql
-- 开启事务
START TRANSACTION;
SELECT * FROM account;
```

这时就出现了脏读的情况，出现了虚晃，事务B以为事务A转账成功，其实并没有。

此时，如果A很鸡贼，将事务进行回滚rollback，双方的数据又回到最初的起点，两个1000，1000。

很明显，在同一个事务中读取到了不同的数据，也就是出现了不可重复读的问题。

### read committed：读已提交

>  不可重复读、幻读会发生

read committed可以解决脏读，也就是说，如果事务A没有提交事务，事务B读取的数据还是原来的数据，只有事务A提交事务了commit，事务B读取到的数据才会改变。

但此时，事务B在同一事务中读取到的两次数据显然又是不同的，因此不可重复读的问题依旧存在。

### repeatable read：可重复度

>  幻读会发生

repeatable read是MySQL的 默认事务隔离级别，确保同一个事务的多个实例在并发读取数据时，看到同样的数据行，解决了不可重复读和脏读的问题。但是幻读现象仍然存在：

举另外一个例子，现在事务A开启并查询一个叫王五的人，显然是查不到的。

```sql
-- 开启事务
START TRANSACTION;

SELECT * FROM account WHERE NAME="王五";
```

此时事务B开启，并插入王五这个人，并提交：

```sql
START TRANSACTION;
INSERT INTO account (NAME,balance) VALUES ("王五",1000);
COMMIT;
```

这时事务A仍然认为表中确实没有王五这个人，也想往里面添加。

```sql
INSERT * INTO account (NAME,balance) VALUES ("王五",1000);
```

这时，事务A将无法插入这条数据，因为事务B已经插入了这条数据，但是在事务A这就跟撞见了鬼一样，什么情况！明明搜了一下没有这条数据，却怎么也插不进去！就像产生了幻觉。

这就是幻读。

参考：[https://www.cnblogs.com/boboooo/p/12370770.html](https://www.cnblogs.com/boboooo/p/12370770.html)

### serializable：串行化

>  解决所有的问题

事务的最高级别，在每个读的数据行上加上锁，强制事务排序，使之不可能相互冲突，从而解决了幻读问题。但是，将会导致大量的超时现象和锁竞争，有点类似于线程中的同步锁，很安全但效率较低。



参考：《疯狂Java讲义》





