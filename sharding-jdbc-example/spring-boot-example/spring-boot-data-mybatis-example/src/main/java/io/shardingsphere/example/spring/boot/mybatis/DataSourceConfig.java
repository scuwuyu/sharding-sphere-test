package io.shardingsphere.example.spring.boot.mybatis;

import io.shardingsphere.core.api.ShardingDataSourceFactory;
import io.shardingsphere.core.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.api.config.TableRuleConfiguration;
import io.shardingsphere.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.core.jdbc.core.datasource.ShardingDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConditionalOnClass(ShardingDataSource.class)
@MapperScan(basePackages = {"io.shardingsphere.example.spring.boot.mybatis.repository"}, sqlSessionFactoryRef = "mSqlSessionFactory")
public class DataSourceConfig {


    @Resource
    private Environment env;

    public static final String SHARDING_PREFIX = "userdb";
    public static final String SHARDING_TABLE = "user";
    public static final String JDBC_TEMPLATE_ENAME = "mShardingJdbcTemplate";
    public static final String TRANSACTION_MANAGER_NAME = "mshardingTransactionManager";

    @Bean(name = "mShardingDataSource")
    @Primary
    public DataSource initDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put(SHARDING_PREFIX + "1", user1());
        dataSourceMap.put(SHARDING_PREFIX + "2", user2());
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();
        tableRuleConfig.setLogicTable(SHARDING_TABLE);
        tableRuleConfig.setActualDataNodes(SHARDING_PREFIX + "${1..2}." + SHARDING_TABLE);
        // 配置分库策略
        tableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("city_id", "userdb${city_id % 2 + 1}"));

        // 配置路由规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfig);
        Properties properties = new Properties();
        properties.setProperty("sql.show", "true");
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap<String, Object>(), properties);
    }

    @Bean(name = "userDataSource1")
    @ConfigurationProperties(prefix = "spring.datasource.user1")
    public DataSource user1() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "userDataSource2")
    @ConfigurationProperties(prefix = "spring.datasource.user2")
    public DataSource user2() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = TRANSACTION_MANAGER_NAME)
    @Primary
    public DataSourceTransactionManager transactionManager() {
        try {
            return new DataSourceTransactionManager(initDataSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean(name = "mSqlSessionFactory")
    @Primary
    public SqlSessionFactory initSqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(initDataSource());
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(env.getProperty("mybatis.sharding.mapper-locations")));
        return sessionFactory.getObject();
    }

    @Bean(name = JDBC_TEMPLATE_ENAME)
    public JdbcTemplate initJdbcTemplate() {
        try {
            return new JdbcTemplate(initDataSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
