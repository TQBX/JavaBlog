# 初识Java的Arrays类

**Arrays类**包括很多用于操作数组的静态方法（例如排序和搜索），且静态方法可以通过类名Arrays直接调用。用之前需要导入Arrays类：

```java
 import java.util.Arrays;
```

本篇记录几个基础的操作，后续等待技术精进，再进行补充。

## 二分查找

- **public static int binarySearch(type[]a,type key)**

```java
int[] a = {1,2,3,4};
System.out.println(Arrays.binarySearch(a,1));//输出0  即1的索引值
System.out.println(Arrays.binarySearch(a,7));//输出-1 找不到就负数
```

二分法查找key在a数组中的索引值，找不到就返回负数；需要数组按升序排列。

- **public static int binarySearch(type[]a,int fromIndex,int toIndex,type key)**

```java
int[] b = {5,6,7,8};
System.out.println(Arrays.binarySearch(b,0,3,6));//b中0-3里有6就返回6的索引
System.out.println(Arrays.binarySearch(b,0,4,8));//b中0-3里没6就返回负数
```

类似法一，但是搜索fromIndex到toIndex的索引值，索引范围需要注意。

## 数组拷贝

- **public static type[] copyOf(type[] original,int length)**

```java
int[] a = {1,2,3,4};
int[] c = Arrays.copyOf(a,5);
int[] d = Arrays.copyOf(a,3);
for(int m:d) System.out.print(m);//123
for(int m:c) System.out.print(m);//12340
String[] b = Arrays.copyOf(a,4)//报错，类型不符
```

把original数组复制成一个新的数组，length是新数组的长度，三种情况：
           1. length>original.length，多的部分整数类型补0，引用类型补null，布尔类型补false。
           2. length==original.length，刚好复制。
           3. length<original.length，就复制前length长的元素。

- **public static type[] copyOfRange(type[]original,int from,int to)**

```java
int[] a ={1,2,3,4,5};
int[] b =Arrays.copyOfRange(a,1,3);
int[] c = Arrays.copyOfRange(a,0,6);
System.out.println(Arrays.toString(b));//[2,3]
System.out.println(Arrays.toString(c));//[1,2,3,4,5,0]
```

类似，只复制original数组的from索引到to索引的元素。

## 转为字符串

- **public static String toString(type[]a)**

```java
char[]b = {'a','b','c'};
System.out.println(Arrays.toString(b));//[a,b,c]
```

将数组转换成字符串类型，用逗号和空格把数组元素按顺序隔开。

## 数组填充

- **public static void fill(type[]a,type val)**

```java
String[] stringArray = new String[3];
Arrays.fill(stringArray,"he");
System.out.println(Arrays.toString(stringArray));//[he,he,he]
```

把a数组的所有元素用val填充.

- **public static void fill(type[]a,int fromIndex,int toIndex,type val)**

```java
String[] stringArray = {"he","he","he"};
Arrays.fill(stringArray,0,1,"m");
System.out.println(Arrays.toString(stringArray));//[m,he,he]
```

把fromIndex到toIndex的元素用val填充，填充到toIndex的前一个数为止（不包括toIndex）。

## 数组比较

- **public static boolean equals(type[]a,type[]a2)**

```java
int[] a = {5,6,7,8};
int[] b = {5,6,7,8};
int[] c = {5,6,7};
System.out.println(Arrays.equals(a, b));//true
System.out.println(Arrays.equals(a, c));//false
```

如果a数组和a2数组的长度相等且里面的元素也都相同，返回true。

## 数组排序

- **public static void sort(type[] a)**

```java
char[] d = {'d','a','c','b'};
Arrays.sort(d);
System.out.println(Arrays.toString(d))
```

将原数组按照升序的顺序排列。

- **public static void sort(type[]a,int fromIndex,int toIndex)**

```java
int[] a ={3,1,2,4};
Arrays.sort(a,0,2);
System.out.println(Arrays.toString(a));
```

将原数组按照升序的顺序，在fromIndex到toIndex的范围内排列，到toIndex之前的数为止（不包括toIndex）。

**综上：**

- 数组类型与元素类型一定要匹配，数组之间的操作也需要匹配类型。
- fromIndex一定小于等于toIndex。
- fromIndex需要大于零。
- toIndex需要小于数组的长度。

