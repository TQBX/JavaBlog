[toc]

# 一、JavaScript的实现

`JavaScript = ECMAScript+BOM+DOM`

ECMAScript：ECMA（欧洲计算机制造协会）指定的全新脚本语言，用于规定当时三足鼎立的三家Script语言（网景的JavaScript、Nombas的ScriptEase和微软的JScript），描述了该语言的语法和基本对象。

在此基础上，JavaScript还拥有自己的组成部分：

BOM（Browser Object Model）：**浏览器对象模型**，描述了与浏览器进行交互的方法与接口。

DOM（Document Object Model）：**文档对象模型**，描述了处理网页内容的方法和接口。





# 二、JavaScript语言的特点

JS是一门**弱类型**的**客户端脚本语言**，无需编译，**直接解释执行**。

每个浏览器都有JS的解析引擎，客户提交的数据可以在客户端就进行校验。

拥有：交互性、安全性和跨平台性。

# 三、JS与HTML如何结合

也就是如何在HTML文件中书写JS代码呢？在哪里写呢？

- 可以在内部任何的地方定义`<script></script>`标签，在标签的内部书写JS代码，需要注意的是：解析顺序是逐行进行的，JS代码定义的位置将会影响执行的顺序。
- 可以引入外部文件，如`<script src="xxx"></script>`，src的值即为外部链接。

JS解析器将会自动地给没加分号的语句添加分号，但是建议加上分号。

将script标签写成自闭和或者js代码出现错误，可能会导致全部的JS代码无法正常执行。

# 四、JS中的数据类型

## 四、JS的原始数据类型

原始数据类型（primitive type）也就是基本数据类型，在ES6之前，主要有以下五种：

- Number：代表数字（不区分整型和浮点型，底层实现都是浮点型）。

- String：表示字符串类型，用引号包裹起来，单双引号都可。
- Boolean：包含true或者false两个值。
- Null：只包含一个null值 ，一个不存在的值，常作为返回值使用。
- Undefined：变量未初始化时，默认值为undefined。

> 关于大小写，众说纷纭，但是要记住，只要是原始数据类型，就这几种。
>
> PS：ES6引入新的基础数据类型：Symbol。（当然这是刷题的时候见到的，没用过）

## 2、JS的引用数据类型

引用数据类型与基本数据类型的区别与Java类似，引用类型变量存储地址，而基本数据类型存储值。

典型的引用类型有：函数、数组、对象，关于这三者的详细学习在下面。

> ps:函数和数组本质上都是JS中的对象，它们具有对象的属性和方法。

# 五、JS引用数据类型之函数

- 一段可执行代码的合集，在需要执行的时候可以在方法名之后添加一对小括号执行方法，是一段可执行的字符串。
- 函数可以看成功能完整的对象Function。

## 1、函数定义


>函数定义时形参类型不用写。

普通函数的定义：

```js
//普通方法定义函数
function mx(a,b){
    return a+b;
}
//执行函数
mx(1,2);
```
动态函数定义：参数列表中先书写全部参数，最后一个参数需要书写方法体。
```js
//动态函数定义
var mx = new Function("a","b","return a+b");
```
匿名函数定义（直接函数定义）：

```js
var mx = function(a,b){
    return a+b;
};
```


## 2、arguments对象
> 在函数中有一个隐藏的对象`arguments`，其中保存的是用户输入的全部参数，可以通过`arguments.length`获取用户输入参数的长度。

```js
function add(){
    alert(arguments[0]);
}
```

## 3、函数属性

Function对象具有length属性，表示函数期望的参数个数。

## 4、函数的另类之处

> 与其他程序设计语言不同，ECMAScript 不会验证传递给函数的参数个数是否等于函数定义的参数个数。

- 如果用户输入的参数长度大于规定长度，多余的参数并没有被抛弃，不会报错。

- 如果用户的输入的参数长度小于规定长度，则缺少的参数会使用undefined来赋值。


> 以上两点可以利用arguments对象及其属性来检验。


在js函数中可以认为函数是一种特殊的变量，这个变量既可以作为参数使用，也可以作为方法使用。

- 作为参数使用：直接书写方法名即可。
- 作为方法使用：在方法名之后添加小括号，即为方法使用。

# 六、JS引用数据类型之数组

数组也可以看成是JS中的Array对象。

### 1、数组特点
> 在JS中，数组的本质就是用一个中括号括起来，其中添加任意类型的元素，每个元素用逗号隔开的字符串。

- 数组的长度是任意的。

- 数组存储的元素类型是任意的。

### 2、数组创建
普通数组定义

```js
var arr = new Array();//返回数组为空，length为0
```

加上初始容量的普通数组定义

```js
var arr = new Array(3);//如果传入参数为数字，则表示指定容量，返回长度为3，元素为undefined的数组
```

含有初始值的数组定义

```js
var arr = new Array(1,"a",false,new Object());
```

直接量数组定义

```js
var arr = [1,true,"bb"];
```

### 3、数组方法
join（参数）：将数组中的元素按照指定的分隔符拼接为字符串
Pop（）：删除最后一个元素，并返回
push（）：向末尾添加元素，并返回新的长度
shift（）：删除第一个元素，并返回

> 部分API，详情见API参考文档。

### 4、数组属性

length：数组长度

# 七、JS引用数据类型之对象

关于对象的类型的题目，在牛客网遇到之后，评论区简直神仙打架，啥观点都有。关于这部分总结，将参考自：

[ECMAScript 对象类型](https://www.w3school.com.cn/js/pro_js_object_types.asp)

该教程中说明：可以创建并使用的对象可以分为三种：本地对象、内置对象、宿主对象。

## 1、本地对象（native object）

Object：和Java中的Object类相似，所有的ECMAScript对象都继承自Object对象，也具有Object对象的属性和方法。如constructor属性和prototype属性，Valueof()方法等。

Boolean：Boolean原始类型的包装对象。

Number：Number原始类型的包装对象。

String：String原始类型的包装对象。

Function：函数类型的包装对象，意思是我们定义的函数可以看成功能完整的对象。

```js
var mx = new Function("a","b","return a+b");//动态创建Function对象，但不建议这样创建

//下面这条语句看似将函数传给一个变量很奇怪，其实是有说法的：
var mt = mx;			//mt与mx指向同一函数，它们都可以执行该函数代码。
```

Array：数组类型的包装对象，意思是我们创建的数组可以看成功能完整的对象。

Date：日期事件对象，封装了许多和日期实现相关的方法，如`toLocaleString()`等。

RegExp：正则对象，保存有关正则表达式模式匹配信息的固有全局对象。

```js
var reg = new RegExp(pattern,attributes);//pattern匹配模式，attributes参数可选，指定匹配方式
var reg = /pattern/attributes;
//测试方法
var result = reg.test(str);//判断str是否符合正则匹配
```

Error以及各种错误类型，如：URIError、SyntaxError、TypeError、ReferenceError、RangeError、EvalError等。

## 2、内置对象（built-in object）

其实也是本地对象，只是不需要我们显式地实例化内置对象。

### Global

全局对象，它多拥有的函数和属性可用于所有内建的JS对象。

以下为部分Global全局对象的函数：

- `parseInt()`：将字符串转换为数字，这个方法很奇特，它会逐一判断每一个字符是否位数字，直到不是数字位置，将前面的数字部分转换为Number。如`parseInt("123aba123")`的结果是123，Number类型。

- `isNaN()`：该方法用于判断该值是否为NaN。（ps：是因为不能单纯通过`xx==NaN`的值来判断xx是否为NaN，因为NaN和任何值做==运算都为false，包括自己，无法以此作为判断依据）

- `eval()`：该方法将计算JS的字符串，将字符串转换为脚本执行。如：`eval("alert(123)");`将会执行alert语句。

### Math

用于执行数学任务，内置对象无需构造函数Math()，直接使用即可。如：`Math.max(1,2);`

## 3、宿主对象（host object）

由ECMAScript实现的宿主环境提供的对象，我们之后学习的BOM和DOM对象都是宿主对象。

## 4、关于对象的补充

除了上述的对象，我们也可以自定义对象。定义的格式如下：

### 自定义对象的方法

无参构造

```js
function Person() {
}
var p = new Person();
p.name = "天乔巴夏";
p.age = 18;
p.gender = "male";
//定义方法
p.say = function () {
    return this.name + " say......";
};
//打印对象
console.log(p);
```

含参构造

```js
//含参构造
function Student(name, age) {
    this.name = name;
    this.age = age;
}

var student = new Student("天乔巴夏", 18);
//直接添加属性
student.addr = "hangzhou";
console.log(student);
```

直接量构造

```js
var p = {
    name: "天乔巴夏",
    age: 18,
    say: function () {
        return this.name + " say......";
    }
};
console.log(p);
```

### 自定义对象的操作

delete语句：删除对象的属性或函数

```js
delete p.say;
```

with语句：定义了某个对象的作用域，域中可以直接调用该对象的成员。

```js
with(student){
    alert(name);
}
```

for……in语句：遍历对象所有属性的名称

```js
for(var prop in p){
    alert(prop);
}
```



# 八、JS中的自动类型转换

JS中的自动类型转换与Java对比，还是有很多不同之处的，JS在需要的时候会对以下类型进行相应的类型转化：

## 1、数字类型

- 可转为字符串形式，比如在和字符串拼接的时候。
- 可转为布尔类型，0为false，其他都为true。
- 转化为Number对象（这点可以参考Java的自动装箱与拆箱）。

## 2、字符串类型

- 可转化为对应的数值。但是需要注意的是：字符串和数字进行相加，将会转为字符串拼接。当字符串和数字进行减运算时，如果可以的话，字符串会转化成对应的数值，然后进行减法运算。否则，将会返回一个NaN的值，如果可以的话，可以尝试使用parseFloat() 与 parseInt()方法对字符串先进行处理。

```js
	console.log("123"-1);//122
	console.log("abc123"-1);//NaN
```

- 亦可转为布尔类型，空字符串为false，非空则为true。
- 转化为String对象（这点可以参考Java的自动装箱与拆箱）。

```js
<script> 
    var a = "123abc"; 
    document.write(typeof(a)); 		//string 
    document.write(a+1); 			//123abc1 
    document.write(typeof(a+1)); 	//string 
    document.write(parseInt(a)); 	//123 
</script>
```

## 3、布尔类型

- 需要的时候可转为数字类型，true为1，false为0。
- 需要的时候可以转化为对应字符串。
- 转化为Boolean对象（这点可以参考Java的自动装箱与拆箱）。

## 4、对象类型

- 可转为布尔类型，对象为null，则为false，其余都是true。

## 5、其他情况

- null和undefined都是false。

# 九、JS中的运算符

JavaScript中的运算符大致上和Java相同，需要注意一些特别的点：

## 1、特殊运算符typeof

将会返回一个操作表达式的数据类型的字符串。

对于Undefined，Boolean，Number，String等基本类型的变量，typeof的结果是这四个的小写形式。

对于引用类型的变量，或者Null基本类型的变量，typeof的结果是object。

## 2、==与===

两者都是比较两边是否相等，区别在于：==比较的时候，会进行类型转换（划重点，类型转换），而===则先判断类型是否相等，再判断值是否相等。

```js
console.log(123 == "123");//true
console.log(123 === "123");//false
```

## 3、加减乘除运算

浮点数运算时，由于底层存储问题，将会导致出现精度缺失的情况。

```js
console.log(1.1+2.2);//3.3000000000000003
```

运算之后，如果结果可以转换成整型，将会以整型的形式显示。

```js
console.log(1.2+1.8);//3
```

整型/整型的结果不是整型。

```js
console.log(3/2*100);//150
```

## 4、三元运算符

```js
2+3>4 ? console.log(true):console.log(false)//true
```

# 十、JS中的流程控制语句

一些和Java相同的流程控制语句就不多说了，如for循环，while循环，do……while循环语句等。

值得注意的有以下两个：

## 1、switch语句

在Java中：switch条件可以接收byte，int，short，char，枚举和字符串类型。

在JS中：switch条件可以接收任意的原始数据类型。

## 2、if语句

我们知道，if语句中的条件需要是一个布尔值。下面的代码，在Java中是不允许的，而在JS中允许：意思是，将4赋值给变量x，此时变为if(4)，而4会自动转换成true，这样子的话程序原本的语义将会被破坏。我们可以将条件稍作修改，改为`if(4 == x)`，可以有效地规避粗心导致的错误。

```js
if(x = 4){
    //do 1
}else{
    //do 2
}
```

## 3、其他

和上面一样，有了许多类型和布尔类型的转换之后，可以利用数字或对象作为判断条件。

```js
while(1){
    //do 
}//死循环
```

```js
if(obj){
    //do
}//空指针判断
```

# 十一、JS中的变量

## 1、变量的定义

使用关键字`var`来定义变量，定义的变量为**弱类型**的变量，可以为这个类型的变量赋任意数据类型的值。

> 区别于Java，弱类型意思是：在开辟变量存储空间 时，不定义空间将来存储的数据类型，可以存放任意类型的值。

```java
var a = 1;
a = "abc";
```
使用var定义，在没有给定初始值时，其类型为undefined。

```js
var a;
console.log(typeof(a));//undefined
```

## 2、全局变量和局部变量

用var定义的变量即为局部变量，没有用var定义的变量为全局变量。

```js
var a = 4;	//局部变量
a = 4;		//全局变量
```

---

参考：[W3School](https://www.w3school.com.cn/js/pro_js_syntax.asp)