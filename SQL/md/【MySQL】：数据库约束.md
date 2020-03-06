所有的关系型数据库都支持对数据表使用约束，在表上强制执行数据校验，保证数据的完整性。

MySQL数据库支持以下四种约束形式：

# 非空约束 NOT NULL

所有数值类型的值都可以为null。

空字符串和0都不等于null。

![非空约束](E:\1myblog\JavaBlog\JavaBlog\SQL\pic\非空约束.png)





# 唯一约束 UNIQUE

保证指定的列不允许出现重复值，但是可以存在多个null值。

![唯一约束](E:\1myblog\JavaBlog\JavaBlog\SQL\pic\唯一约束.png)



# 主键约束 PRIMARY KEY

主键约束的功能相当于非空＋唯一约束，既不允许出现重复，也不允许出现null。

每个表中最多允许有一个主键，唯一确定一行记录的字段。

![主键约束](E:\1myblog\JavaBlog\JavaBlog\SQL\pic\主键约束.png)

## 配合主键的自动增长

![自动增长](E:\1myblog\JavaBlog\JavaBlog\SQL\pic\自动增长.png)

# 外键约束  FOREIGN KEY







