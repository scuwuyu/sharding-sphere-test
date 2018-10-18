# sharding-sphere-test
please checkout branch dev,use module spring-boot-data-mybatis-example.

for show this NullPointerException:
step 1:
    create two databases:userdb1,userdb2

step 2:
    create table user in each database,
    sql:
        CREATE TABLE `user` (
          `id` bigint(64) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
          `city_id` int(11) DEFAULT NULL COMMENT '城市ID',
          PRIMARY KEY (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

step 3:
    modify properties application.properties

step 4:
    run io.shardingsphere.example.spring.boot.mybatis.SpringBootDataMain.main()

you will see this:
   Caused by: java.lang.NullPointerException
	at io.shardingsphere.core.rewrite.SQLRewriteEngine.appendLimitRowCount(SQLRewriteEngine.java:205) ~[sharding-core-3.0.0.M3.jar:?]
	at io.shardingsphere.core.rewrite.SQLRewriteEngine.rewrite(SQLRewriteEngine.java:134) ~[sharding-core-3.0.0.M3.jar:?]
	at io.shardingsphere.core.routing.router.sharding.ParsingSQLRouter.route(ParsingSQLRouter.java:105) ~[sharding-core-3.0.0.M3.jar:?]
	at io.shardingsphere.core.routing.PreparedStatementRoutingEngine.route(PreparedStatementRoutingEngine.java:66) ~[sharding-core-3.0.0.M3.jar:?]
	at io.shardingsphere.core.jdbc.core.statement.ShardingPreparedStatement.sqlRoute(ShardingPreparedStatement.java:245) ~[sharding-jdbc-3.0.0.M3.jar:?]
	at io.shardingsphere.core.jdbc.core.statement.ShardingPreparedStatement.execute(ShardingPreparedStatement.java:188) ~[sharding-jdbc-3.0.0.M3.jar:?]
	at sun.reflect.GeneratedMethodAccessor136.invoke(Unknown Source) ~[?:?]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_73]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[?:1.8.0_73]
	at org.apache.ibatis.logging.jdbc.PreparedStatementLogger.invoke(PreparedStatementLogger.java:59) ~[mybatis-3.4.2.jar:3.4.2]
	at com.sun.proxy.$Proxy140.execute(Unknown Source) ~[?:?]
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.query(PreparedStatementHandler.java:63) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.query(RoutingStatementHandler.java:79) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.executor.SimpleExecutor.doQuery(SimpleExecutor.java:63) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.executor.BaseExecutor.queryFromDatabase(BaseExecutor.java:324) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.executor.BaseExecutor.query(BaseExecutor.java:156) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:109) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:83) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:148) ~[mybatis-3.4.2.jar:3.4.2]
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:141) ~[mybatis-3.4.2.jar:3.4.2]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_73]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_73]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_73]
	at java.lang.reflect.Method.invoke(Method.java:497) ~[?:1.8.0_73]
