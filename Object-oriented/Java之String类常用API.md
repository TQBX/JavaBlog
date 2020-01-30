[toc]
# Java之String类常用API
## 字符串与内存
- `public final class String`，String类被final修饰，代表的是最终类。

- 字符串在内存中以字符数组的形式来存储` private final char value[];`

- 字符串是常量，本身被存储在方法区的常量池中，只要字符串的实际值是一样的，那么用的就是同一个字符串，意思是可以被共享。

- 字符串都是String类的实例。


```java
public class StringDemo {
    public static void main(String[] args) {

        String str = "abc";
        str = "def";
        str = "abc";
        //栈内存直接指向方法区
        String str2 = "abc";
        //栈内存指向堆内存，堆内存指向方法区
        String str3 = new String("abc");
        //"a"和"b"都是字面量（数字，字符，字符串，布尔值），因此在编译的时候会对字面量进行运算
        //相当于编译完之后就是：String str4 = "abc";
        String str4 = "a" + "b" + "c";
        System.out.println(str == str2);
        System.out.println(str == str4);
        //因为字符串变量进行拼接操作，所以在底层并不是直接的拼接
        //而是调用了StringBuilder中的append方法进行拼接

        String str5 = "a";
        //str5 = new StringBuilder(str5).append("bc").toString();
        //toString方法在底层就是return new String(value, 0, count);
        str5 = str5 + "bc";
        System.out.println(str == str5);

    }

}
```
```java


String str1 = "abc";  一个对象 在方法区
String str2 = new String("abc"); 两个对象，一个堆，一个方法区
 
  
  
 * 拼接大量字符串 使用+还是stringbuilder：
 * 结论：如果需要拼接多个字符串，使用stringbuilder

 
  String[] arr = new String[100];
 // 方式1  使用'+', 产生301个对象
  String result = "";   1个对象
  for(String str:arr) //result = new StringBuilder(str).append(str).toString();
  result +=str; 每拼接依次产生三个对象
 
  // 方式2 StringBuilder 102个对象
 
  StringBuilder sb = new StringBuilder();1个对象
  for(String str:arr)
   sb.append(str); //每拼接一次，产生1个对象
  String result = sb.toString(); 1个对象
```

## 方法
以下所有方法均省略**public**。
### char chatAt(int index)
```java
/*通过下标获取指定位置上的字符*/
//源码
public char charAt(int index) {
    if ((index < 0) || (index >= value.length)) {
        throw new StringIndexOutOfBoundsException(index);
    }
    return value[index];
}


```
### int length()
```java
/*获取字符串的长度*/
//源码：
public int length() {
    return value.length;
}
```
### char[] toCharArray()
```java
/*将字符串转换成对应的字符数组*/
//源码：
public char[] toCharArray() {
    // Cannot use Arrays.copyOf because of class initialization order issues
    char result[] = new char[value.length];
    System.arraycopy(value, 0, result, 0, value.length);
    return result;
}
```
### String(char value[])
### String(char value[], int offset, int count)
```java
/*将字符数组转换为字符串*/
//源码：
//public String(char value[])
public String(char value[]) {
    this.value = Arrays.copyOf(value, value.length);
}
//public String(char value[], int offset, int count)
public String(char value[], int offset, int count) {
    if (offset < 0) {
        throw new StringIndexOutOfBoundsException(offset);
    }
    if (count <= 0) {
        if (count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        }
        //count=0且offset小于等于字符串的长度，构造空字符串
        if (offset <= value.length) {
            this.value = "".value;
            return;
        }
    }
    // Note: offset or count might be near -1>>>1.
    if (offset > value.length - count) {
        throw new StringIndexOutOfBoundsException(offset + count);
    }
    this.value = Arrays.copyOfRange(value, offset, offset+count);
}

//示例：
char[] ch = {'a','b','c','d','e'};
String str = new String(ch);
System.out.println(ch);//abcde
String str1 = new String(ch,3,2);
System.out.println(str1);//de
value - 要操作的字符数组
offset - 偏移量，即起始下标
count - 拼接字符的个数
```
### int compareTo(String anotherString) 
```java
/* 比较两个字符串*/
//compareTo 源码
public int compareTo(String anotherString) {
    int len1 = value.length;
    int len2 = anotherString.value.length;
    int lim = Math.min(len1, len2);
    char v1[] = value;
    char v2[] = anotherString.value;

    int k = 0;
    while (k < lim) {
        char c1 = v1[k];
        char c2 = v2[k];
        if (c1 != c2) {
            return c1 - c2;
        }
        k++;
    }
    return len1 - len2;
}
/*
 1.两个字符串在底层都是以字符数组的形式来存储
 2.遍历两个字符数组，然后去依次比较每一位上的字符是否相等
 3.如果对应位置不相等 那么返回两个字符之差
 4.如果相等，则比较下一位
 5.如果一直都相等，直到有一个字符串结束，返回两个字符串之差
*/

 String s3 = "abcde";
 String s4 = "ABCDE";
 System.out.println(s3.compareTo(s4));//32
 
 //compareToIgnoreCase 忽视大小写
 System.out.println(s3.compareToIgnoreCase(s4));//0
```
### String concat(String str)
```java
 /*将指定的参数拼接到字符串的末尾不改变原先的字符串
 底层就是进行了数组的合并*/
 //源码
 public String concat(String str) {
    int otherLen = str.length();
    if (otherLen == 0) {
        return this;
    }
    int len = value.length;
    char buf[] = Arrays.copyOf(value, len + otherLen);
    str.getChars(buf, len);
    return new String(buf, true);
}
 //示例：
String str1 = "abc";
String str2 = "abc";
System.out.println(str2.concat(str1));//abcabc
```
### boolean contains(CharSequence s)
```java
/*判断是否包含指定的子字符串*/
contains
//源码：
public boolean contains(CharSequence s) {
    return indexOf(s.toString()) > -1;
}
//示例：
String str1 = "ab";
String str2 = "abcd";
System.out.println(str2.contains(str1));//true
System.out.println(str1.contains(str2));//false
```
### boolean endsWith(String suffix)
```java
/*判断是否是指定的结尾 */
String str1 = "ab";
String str2 = "abcd";
System.out.println(str2.endsWith(str1));//false
```
### startsWith(String prefix)
```java
/*判断是否是指定的开头*/
String str1 = "ab";
String str2 = "abcd";
System.out.println(str2.startsWith(str1));//true
```
### boolean equals(Object anObject)
```java
/*判断字符串值是否相等*/
//源码：
public boolean equals(Object anObject) {
    if (this == anObject) {
        return true;
    }
    if (anObject instanceof String) {
        String anotherString = (String)anObject;
        int n = value.length;
        if (n == anotherString.value.length) {
            char v1[] = value;
            char v2[] = anotherString.value;
            int i = 0;
            while (n-- != 0) {
                if (v1[i] != v2[i])
                    return false;
                i++;
            }
            return true;
        }
    }
    return false;
}
//示例
String str1 = "ab";
String str2 = new String("ab");
String str2 = "a"+"b";
System.out.println(str1.equals(str2));//true
System.out.println(str1.equals(str3));//true

/*忽略字母大小写的字符串值比较*/
String str1 = "ab";
String str4 = "AB";
System.out.println(str4.equalsIgnoreCase(str1));//true


```
### byte[] getBytes()
```java
/*将字符串转换为字节数组，在转化为字节数组的过程中，如果没有指定编码，默认时当前工程的编码，意味着代码中的编码将会跟着工程编码一起改变*/
//示例
String s = "abcd";
byte[] bs = s.getBytes();
System.out.println(Arrays.toString(bs));//[97, 98, 99, 100]
//避免编码方式不确定，可以强制编码的名字 （需要在方法中抛出异常）
//public byte[] getBytes(String charsetName) throws UnsupportedEncodingException 
String ss = "字节数组";
byte[] bssGBK = ss.getBytes("gbk");
byte[] bssUTF8 = ss.getBytes("utf-8");

```
### String(byte bytes[])
```java
/*将字节数组转换为字符串--String构造器*/
//示例
byte[] newBs = {43,43,54,65,76};
String s0 = new String(newBs);
System.out.println(s0);//++6AL
/*指定编码，编码不对应，则转换不成功*/
//public String(byte bytes[], String charsetName) throws UnsupportedEncodingException
String s2 = new String(bssUTF8);
String s3 = new String(bssGBK,"gbk");
System.out.println(s2+","+s3);   //字节数组,字节数组
/*构造字符串时截取*/
//public String(byte bytes[], int offset, int length, String charsetName) throws UnsupportedEncodingException
String str = new String(bssUTF8,0,6,"utf-8");
System.out.println(str);//字节
```
### int hashCode()
```java
/*字符串的哈希码 hashCode*/
//对Object中的hashCode方法进行了重写
//示例
String str = "abc";
System.out.println(str.hashCode());//96354=（97*31+98）*31+99
```
### int indexOf(int ch)
### int indexOf(String str)
### int indexOf(int ch, int fromIndex)
```java
/*获取字符或子字符串在字符串中第一次出现的下标位置*/
//示例
String s = "abcdabcdabcd";
System.out.println(s.indexOf('a'));//0
System.out.println(s.indexOf("aa"));//-1

//从fromIndex开始，字符出现的位置
System.out.println(s.indexOf('a',4));//4
```

### int lastIndexOf(String str)
### int lastIndexOf(int ch)
### int lastIndexOf(String str, int fromIndex)
```java
/*获取字符或子字符串在字符串中最后出现的位置*/
System.out.println(s.lastIndexOf("abcd"));//8
System.out.println(s.lastIndexOf('d'));//11
//从fromIndex向前，字符最后出现的位置
System.out.println(s.lastIndexOf("abc",3));//0
```
### native String intern()
```java
/*强制返回字符串的常量池地址*/

String s1 = "abc";
String s2 = new String("abc");

System.out.println(s1==s2);//false

System.out.println(s1==s2.intern());//true
System.out.println(s1.equals(s2));//true 一般用equals
```
### boolean isEmpty()
```java
/*判断字符串的长度是否为0*/
//源码：
public boolean isEmpty(){
    return value.length ==0;
}
/*空串和字符串为空的区别*/
//空串：长度为0，但是有常量池地址，相当于char[] value = new char[0];

String str1 = "";

//字符串为空：没有地址  并没有区内存中实际开辟空间
// 可以认为内存中对象并不存在，所以调用isEmpty()方法会报错
String str2 = null;

System.out.println(str2.isEmpty());//!false java.lang.NullPointerException
System.out.println(str1.isEmpty());//true
```
### String replace(char oldChar, char newChar)
```java
/*将字符串的字符替换，用newChar替换oldChar*/
String str1 = "abc";
String str2 = str1.replace('a','e');
System.out.println(str2);//ebc

System.out.println(str1);//abc
```

### String substring(int beginIndex)
### String substring(int beginIndex, int endIndex)
```java
/*截取子字符串*/
String str = "abcdefg";
//从beginIndex到最后一位
String str1 = str.substring(2);//cdefg

//从beginIndex开始，到endIndex的前一位
String str2 = str.substring(3,4);//d
```

### String toUpperCase()
### String toLowerCase()
```java
/*大小写转换*/
String s = "abc";
String s1 = s.toUpperCase();
String s2 = s1.toLowerCase();
System.out.println(s1);//ABC
System.out.println(s2);//abc
```
### String toString()
```java
/*重写了Object类的toString方法，返回对象本身*/
//源码
public String toString() {    
    return this;
}
```
### String trim()
```java
/*去掉字符串的前导空白和尾部空白--> 去掉头部和尾部的空白字符*/
//空白字符：空格，\t制表符  \r \n \f
String s = "\t\n \t \t \t dwead \f  \r";
String s1 = s.trim();
System.out.println(s);
System.out.println(s1);
```
### static String valueOf(Object obj)
```java

/*将传入的参数转换为字符串*/

//源码：
public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
}
//System.out.println(Object obj)底层就是调用String.valueof(obj)，将传入对象转换为字符串
//示例：
String.valueOf(3);
//如果将对象转换为字符串，底层调用对象的toString方法
String str = String.valueOf(new Object());
System.out.println(str);//java.lang.Object@1b6d3586

int[] arr = {1,2,34};
String str1 = String.valueOf(arr);
System.out.println(str);//java.lang.Object@1b6d3586


/*如果是字符数组，打印的是拼接之后的字符串而不是地址*/
//源码：
public static String valueOf(char data[]) {   
    return new String(data);
}
//示例：
char[] arrChar = {'q','w','e'};
String str2 = String.valueOf(arrChar);
//return new String(data);
System.out.println(str2);//qwe
```

如有错误，欢迎大家评论区指正！