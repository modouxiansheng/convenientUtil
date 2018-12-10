# convenientUtil(Java便捷工具)

自己写的或者收集的一些便捷小工具类

1. `TableNameParser`：获得表名工具类例子如下

## 导入Jar包

Maven

```
<dependency>
  <groupId>com.github.modouxiansheng</groupId>
  <artifactId>convenientUtil</artifactId>
  <version>1.2-release</version>
</dependency>
```

Gradle

```
compile 'com.github.modouxiansheng:convenientUtil:1.1-release'

```

## `TableNameParser `使用介绍

解析字符串，返回字符串中的表名存放在`HashSet`中

```
HashSet<String> tableName = new TableNameParser("select * from A").tables();

```

如果想要解析一个项目中所使用了哪些表

```
String path="";//存放xml的全路径名
HashSet<String> tableName = GetTableName.getTableName(path);

```

## 自动测试Mapper

> 记得引入Jar包`compile("mysql:mysql-connector-java")`


1. 将文件导入到测试模块中，其中`AutoTestMapper`放在测试模块中，并且新建`mybatis-config.xml`文件
	
	![](https://ws4.sinaimg.cn/large/006tNbRwly1fxov5vfgjyj30bo0bh0tj.jpg)

2. 在项目的`resource`文件夹下创建`mybatis-config.xml`文件，里面内容如下，里面value值为想要测的数据库的连接信息。
	
```
	<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="dev">
        <environment id="dev">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="UNPOOLED">
                <property name="driver" value=""/>
                <property name="url" value=""/>
                <property name="username" value=""/>
                <property name="password" value=""/>
            </dataSource>
        </environment>
    </environments>
</configuration>
	
```
	
3. 在测试类中编写如下代码

```
Reader resourceAsReader = Resources.getResourceAsReader("mybatis-config.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsReader);
resourceAsReader.close();
AutoTestMapper autoTestMapper = new AutoTestMapper("想要测试的Mapper文件夹全路径名");
autoTestMapper.openSqlSession(sqlSessionFactory); 
	
```
	
4. 然后会打印出执行成功的Sql，执行失败的Sql。如果失败的话会有原因。

5. 如果想要在项目的测试中要测试到所有的`if`分支的话，那么就引入在`github`中的lib文件夹下的`mybatis`包，将其放入自己的项目中，然后进行引用，这样就能够测到所有的`if`分支了
