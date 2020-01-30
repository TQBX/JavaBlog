# Java数组使用以及foreach循环

二话不说，先甩一个简单的程序：

```java
final int NUM= 10;
int[] arrays = new int[NUM];
System.out.println(arrays.length);//10
for(int i = 0;i<5;i++){
    arrays[i] = i;//赋值
}
//foreach
for(int element:arrays){
    System.out.print(element+" ");
}
// 0 1 2 3 4 0 0 0 0 0
```

- 动态创建一个数组arrays,指定数组长度为10。
- 索引从0开始，以长度减1结束，越界会报错。
- **用`length`属性可以得到数组的长度**，例如`arrays.length`.
- `数组名[index]`访问数组元素,即可对其进行赋值操作。
- 由于动态初始化，没有进行赋值的部分为系统默认值0。
- 输出打印运用了foreach循环，是Java中为数组和集合而生的循环。

# foreach循环

格式：

```java
for(元素类型 元素:数组名){...}
```

- 可以看出，相比for循环，foreach不关心数组的长度，也不需要靠索引值来访问数组元素，而是**遍历所有的元素**。
- 元素类型和数组类型总该一样吧，不然咋放进去。
- 书上看到一种说法，让我印象深刻：**for each element in array**，意思就是数组中的每一个元素。

**注意：**

```java
int []arrays = {1,2,3,4,5};
// int i;
System.out.println("使数组中每个元素加1");
for(int i:arrays){
    i+=1;
    System.out.print(i+" ");
}
System.out.println();
System.out.println("修改后：");
for(int j:arrays){
    System.out.print(j+" ");
}	
/**
使数组中每个元素加1
2 3 4 5 6
修改后：
1 2 3 4 5*/
```

- 在foreach中对数组元素赋值并没有作用。
- 在进行foreach循环时，其实系统将数组的元素赋给了临时变量，所以并不会改变原来数组。