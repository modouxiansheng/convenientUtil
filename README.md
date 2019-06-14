# convenientUtil(Java便捷工具)

自己写的或者收集的一些便捷小工具类

1. `TableNameParser`：获得表名工具类例子如下
2. `AutoTestMapper`: 自动测试项目中的Sql是否正确
3. `FindDuplicate`: 找出项目中同名类

## 导入Jar包

> 具体版本号可以去[中央仓库](https://search.maven.org/)查询modouxiansheng即可获得最新版本号

Maven

```
<dependency>
  <groupId>com.github.modouxiansheng</groupId>
  <artifactId>convenientUtil</artifactId>
  <version>最新版本号</version>
</dependency>
```

Gradle

```
compile 'com.github.modouxiansheng:convenientUtil:最新版本号'

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

> 自动测试Mapper除了传参为List和Set，其余都能测到。在xml中所有的if条件都会拼接到。

* 将`AutoTestMapper`拷贝到测试模块中。如图所示

`AutoTestMapper `文件存放在[github](https://github.com/modouxiansheng/convenientUtil/blob/master/src/main/java/com/github/autoTest/AutoTestMapper.java)

![](https://ws3.sinaimg.cn/large/006tNbRwly1fy1p500fgvj30ho03i3yp.jpg)

* 在`resources`模块中加入`mybatis-config.xml`文件，如图所示

![](https://ws2.sinaimg.cn/large/006tNbRwly1fy1p60n2vkj30hn08jt9g.jpg)
	
`mybatis-config.xml`内容如下
	
```
	<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="dev">
        <environment id="dev">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="连接地址"/>
                <property name="username" value="账号"/>
                <property name="password" value="密码"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
	
```

* 在根目录创建lib文件夹，并将测试的Mybatis版本放入其中，并在Gradle中引入此包

> compile files('../lib/mybatis-3.5.0-hupengfeiTest.jar')此处路径填写相对路径
	
如果目录结构如下，那么就`compile files('lib/mybatis-3.5.0-hupengfeiTest.jar')`

`mybatis-3.5.0-hupengfeiTest.jar`在[github](https://github.com/modouxiansheng/convenientUtil/blob/master/src/main/java/com/github/autoTest/AutoTestMapper.java)下面的lib目录中
	
```
-lib
	-- mybatis-3.5.0-hupengfeiTest.jar
-build.gradle
```
	
如果目录结构如下，那么就`compile files('../lib/mybatis-3.5.0-hupengfeiTest.jar')`
	
```
-lib
	-- mybatis-3.5.0-hupengfeiTest.jar
-service
	-- build.gradle
```
	
![](https://ws1.sinaimg.cn/large/006tNbRwly1fy1p7ylfmwj30dc03iwem.jpg)

* 在单元测试中编写代码，进行测试

```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AirApplication.class })//此处AirApplication.class为项目中的启动类，自行修改
public class TestMapper {

    @Test
    public void testCeshi()
            throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IOException, ClassNotFoundException {
        //读取Mybatis配置
        Reader resourceAsReader = Resources.getResourceAsReader("mybatis-config.xml");
        //生成SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsReader);
        resourceAsReader.close();
        AutoTestMapper autoTestMapper = new AutoTestMapper(存放Mapper的Java文件夹的全路径名);
        //执行测试方法
        autoTestMapper.openSqlSession(sqlSessionFactory);
    }
}
```
	
就会在控制台中打印出执行失败的Mapper以及其原因。如下图所示
	
![](https://ws1.sinaimg.cn/large/006tNbRwly1fy1pgmgf25j31c106qjt5.jpg)
