XMLStatementBuilder

![image-20200408161310811](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200408161310811.png)

关于parameterType

![image-20200408161227994](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200408161227994.png)

关于SqlCommandType如何得到？

```java
SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
```

