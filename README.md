# convenientUtil(Java便捷工具)

自己写的或者收集的一些便捷小工具类

1. `TableNameParser`：获得表名工具类例子如下

## 导入Jar包

Maven

```
<dependency>
  <groupId>com.github.modouxiansheng</groupId>
  <artifactId>convenientUtil</artifactId>
  <version>1.0-release</version>
</dependency>
```

Gradle

```
compile 'com.github.modouxiansheng:convenientUtil:1.0-release'

```

## `TableNameParser `使用介绍

解析字符串，返回字符串中的表名存放在`HashSet`中

```
HashSet<String> tableName = new TableNameParser("select * from A").tables();

```

如果想要解析一个项目中所使用了哪些表

```
String path="";//存放xml的全路径名
HashSet<String> tableName = TableNameParser.getTableName(path);

```
