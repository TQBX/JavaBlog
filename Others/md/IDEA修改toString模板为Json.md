Alt+Insert，选择toString()。

![image-20200415211631710](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415211631710.png)

点击settings。

![image-20200415211741553](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415211741553.png)

点击Templates，点击加号。

![image-20200415211822794](C:\Users\13327\AppData\Roaming\Typora\typora-user-images\image-20200415211822794.png)

将下面的代码复制进自己创建的模板即可。

```java
public java.lang.String toString() {
final java.lang.StringBuilder sb = new java.lang.StringBuilder("{");
#set ($i = 0)
#foreach ($member in $members)#if ($i == 0)
sb.append("#####
#else
sb.append(",####
#end#if ($member.string || $member.date)
\"$member.name\":\"")
#else
\"$member.name\":")
#end#if ($member.primitiveArray || $member.objectArray)
.append(java.util.Arrays.toString($member.name));
#elseif ($member.string || $member.date)
.append($member.accessor).append('\"');
#else
.append($member.accessor);
#end#set ($i = $i + 1)
#end
sb.append('}');
return sb.toString();
}
```

结果如下：

```java
{"id":45,"username":"hyh","address":"hangzhou","sex":"男","birthday":"Sun Mar 04 12:04:06 CST 2018","accounts":null}
```

