## Hello, Lox
```
// Your first Lox program!
print "Hello, world!";
```

## 基本数据类型
### Booleans
```
true;  // Not false.
false; // Not *not* false.
```
### Numbers
```
1234;  // An integer.
12.34; // A decimal number.
```
### Strings
```
"I am a string";
"";    // The empty string.
"123"; // This is a string, not a number.
```
### Nil
```
nil;
```

## 表达式
### Arithmetic
```
add + me;
subtract - me;
multiply * me;
divide / me;
```
```
-negateMe;
```
### Comparison and equality
```
less < than;
lessThan <= orEqual;
greater > than;
greaterThan >= orEqual;
```
```
1 == 2;         // false.
"cat" != "dog"; // true.
```
```
314 == "pi"; // false.
```
```
123 == "123"; // false.
```
### Logical operators
```
!true;  // false.
!false; // true.
```
```
true and false; // false.
true and true;  // true.
```
```
false or false; // false.
true or false;  // true.
```
`and`和`or`本质上是控制流而不只是操作符，因为它们都具有**短路特性**。
### Precedence and grouping
All of these operators have the same precedence and associativity that you’d expect coming from C. In cases where the precedence isn’t what you want, you can use () to group stuff:
```
var average = (min + max) / 2;
```

## 语句
```
print "Hello, world!";  // print语句
```
```
"some expression";  // 表达式语句
```
```
{
  print "One statement.";
  print "Two statements.";
}
```

## 变量
```
var imAVariable = "here is my value";
var iAmNil;
```
```
var breakfast = "bagels";
print breakfast; // "bagels".
breakfast = "beignets";
print breakfast; // "beignets".
```

## 控制流
```
if (condition) {
  print "yes";
} else {
  print "no";
}
```
```
var a = 1;
while (a < 10) {
  print a;
  a = a + 1;
}
```
```
for (var a = 1; a < 10; a = a + 1) {
  print a;
}
```

## 函数
```
makeBreakfast(bacon, eggs, toast);
```
```
makeBreakfast();
```
```
fun printSum(a, b) {
  print a + b;  // implicitly returns nil.
}
```
```
fun returnSum(a, b) {
  return a + b;
}
```

## 闭包
```
fun addPair(a, b) {
  return a + b;
}

fun identity(a) {
  return a;
}

print identity(addPair)(1, 2); // Prints "3".
```
```
fun outerFunction() {
  fun localFunction() {
    print "I'm local!";
  }

  localFunction();
}
```
```
fun returnFunction() {
  var outside = "outside";

  fun inner() {
    print outside;
  }

  return inner;
}

var fn = returnFunction();
fn();
```