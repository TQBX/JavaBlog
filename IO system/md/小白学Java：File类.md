[toc]
# 小白学Java：File类


我们可以知道，存储在程序中的数据是暂时的，在程序终止的时候，存储在程序中的数据就会丢失。我们通常为了永久地保存在程序中创建地数据，需要将它们**存储到磁盘上**或者其他永久存储设备的文件中，这些文件之后可以被其他的程序传输和读取。


而java.io包下的File实用类库就可以被运用操作这些文件，File封装了在用户机器上处理文件系统所需要的所有功能。

官方给的定义是：代表的是**文件**或者**目录路径名**的抽象。

> This class presents an abstract, system-independent view of hierarchical pathnames.

可以说，这个类提供了一个抽象的、与系统无关的层次路径名视图。


## 不同风格的分隔符
- 在windows系统中，用`\`间隔目录，用`;`间隔不同的路径。
- 在Linux或unix中，用`/`间隔目录，用`:`间隔不同的路径。

java为了屏蔽不同操作系统的差异性，提供了`File.separator`表示间隔符，提供了`File.pathSeparator`间隔不同路径。当然这两个是String类型，另外两个为Char类型，作用相同。

![1GU5xx.png](https://s2.ax1x.com/2020/02/01/1GU5xx.png)

## 绝对与相对路径

**绝对路径**（absolute）：以盘符或者/开头的路径。
**相对路径**（relative)：相较于某个路径下才指明的路径。

> 在IDEA的main()方法中测试，相对路径是当前的Project下。
```java
    File file = new File("newFile");
    System.out.println(file.getAbsolutePath());
    //E:\java_learning\project01\newFile
```

## File类常用方法
### 常用构造器
```java
File(String pathname) //传入一个路径名（文件or目录）
File(String parent, String child) //在目录parent下创建一个子路径对象
File(File parent, String child) //在目录parent下创建一个子路径对象，区别是parent本身就是File对象
```
下面是创建File对象的一个例子，需要注意的是，创建对象并不意味着创建了一个真实的文件，而是一种抽象，可以通过这个File对象进行相应的操作。
```java
//以绝对路径名作为参数，创建一个File对象。
File file = new File("D:\\newFile.txt");
```
我们之前提到，windows系统中目录层级之间的分隔符是反斜杠`\`，Java中反斜杠是一个转义字符，所以要表示`\`，需要写成`\\`。
### 创建方法
下面的三个方法用以创建文件或者创建目录，都有返回的boolean值。
- 创建文件 `public boolean createNewFile()`
用以创建文件，创建成功返回true。但是如果该文件已经存在，则返回false。
- 创建目录 `public boolean mkdir()`
用以创建文件目录，创建成功返回true。但是需要注意的是：同样的，如果目录已经存在，则返回false，如果该文件目录的上层目录不存在，也不会创建。
- 创建多级目录 `public boolean mkdirs()` 
同样是创建文件目录，如果已经存在，则返回false。但是和上面不同的是，如果上层目录不存在，它会**一并创建**。

需要注意：如果创建File对象时，传入的路径名没有指定盘符，则为相对路径名，在项目路径下。
### 判断方法
下面的方法都是以`public boolean`修饰的方法，用以判断。
![1GU4R1.png](https://s2.ax1x.com/2020/02/01/1GU4R1.png)

### 获取方法
![1GUTsK.png](https://s2.ax1x.com/2020/02/01/1GUTsK.png)

- 关于`getPath`和`getAbsolutePath`产生的疑惑？为啥这两个产生的结果相同呢？

于是我进行了新的测试：
```java
    File file = new File("relativeFile");
    file.getAbsolutePath(); //E:\java_learning\project01\relativeFile
    file.getPath();         //relativeFile
```
这次我以相对路径名作为参数传入，结果显而意见。`getAbsolutePath`返回的是默认带着项目路径的绝对路径名，而`getPath`只是将传入的路径名原原本本地返回。
- 关于产生的文件列表，其实还有list()方法，只不过它的返回值类型是`String[]`。list()和listFiles()都有传入参数是`FilenameFilter`接口类型的重载方法。
```java
public interface FilenameFilter {
    boolean accept(File dir, String name);
}
```
我们可以通过**匿名内部类**，对产生的列表进行操作：
```java
    //打印该目录下的java文件
    File path = new File("module01\\src\\com\\my\\FileTest");
    File[] list;
    //通过匿名内部类返回FilenameFilter的引用
    list = path.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            //选择接收.java结尾的文件
            return name.endsWith(".java");
        }
    });
    for(File l:list) 
        System.out.println(l.getName());
```
### 命名方法
`public boolean renameTo(File dest)`
把文件重命名为指定地文件路径，如果成功，返回true。
### 删除方法
`public boolean delete()`
删除文件或者目录。
- 一旦删除成功，无法在回收站复原，是**不可逆**的。
- 如果待删除的目录里面包含着文件或者子目录，则其不能被删除，我们可以通过下面的方法递归删除包含内容的目录。
- 删除完整的目录。
```java
    /**
     * 静态方法，删除完整的目录
     * @param file 传入File对象
     */
    public static void deleteDirectory(File file){
        //判断file对象是文件还是目录
        if(file.isDirectory()){
            //如果是目录，获取子文件和子目录列表
            File[] fs = file.listFiles();
            //遍历数组
            for(File f :fs){
                //递归删除
              deleteDirectory(f);
            }
        }
        //最后的无论是啥，都需要删除
        file.delete();
    }
```