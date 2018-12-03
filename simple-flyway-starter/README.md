其他项目引入flyway的步骤

1.pom.xml 加入依赖
```
<dependency>
    <groupId>com.fpi.flyway</groupId>
    <artifactId>simple-flyway-starter</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

2.在application.yml中加入flyway配置
```
flyway:
  enabled: false
```

3.在resources目录新增对应数据库目录和存放版本脚本
```
##脚本名称格式：V1_1_0__description (description前连续两个下划线)

resources
   db/primary/V1_1_0__Test.sql
   db/secondary/V1_1_1__Test.sql

V1_1_0__description.sql 代表version 1.1.0
注：V1默认为flyway自动创建scheme_version，项目脚本需要以V1_1开头
```

4.修改application数据库配置类

```
spring 加载DataDource之前，flyway自动执行脚本

/**
 * mysql数据源
 * 需要使用flyway的数据库加@DependsOn("flywayBootDb")
 * @return
 */
@Bean(name = "primaryDataSource")
@Qualifier("primaryDataSource")
@Primary
@ConfigurationProperties(prefix="spring.datasource.primary")
@DependsOn("flywayBootDb")
public DataSource primaryDataSource() {
    return DataSourceBuilder.create().type(DruidDataSource.class).build();
}

@Bean
public FlywayBootDb flywayBootDb() {
    return new FlywayBootDb();
}
```

5.当启动flyway报错时

```
修改当前版本脚本V1_1_0__Test.sql
修改好脚本后重启application启动类
```
