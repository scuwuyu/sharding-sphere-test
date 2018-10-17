/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.example.transaction;

import com.google.common.base.Optional;
import io.shardingsphere.example.transaction.fixture.DatasourceType;
import io.shardingsphere.example.transaction.fixture.ShardingDatasourceUtil;
import io.shardingsphere.transaction.api.SoftTransactionManager;
import io.shardingsphere.transaction.api.config.NestedBestEffortsDeliveryJobConfiguration;
import io.shardingsphere.transaction.api.config.SoftTransactionConfiguration;
import io.shardingsphere.transaction.bed.BEDSoftTransaction;
import io.shardingsphere.transaction.constants.SoftTransactionType;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BEDTransactionMain {
    
    private static boolean useNestedJob = true;
    
    public static void main(final String[] args) throws SQLException {
        DataSource dataSource = ShardingDatasourceUtil.getShardingDataSource(DatasourceType.LOCAL);
        dropTable(dataSource);
        createTable(dataSource);
        insert(dataSource);
        updateFailure(dataSource);
    }
    
    private static void createTable(final DataSource dataSource) throws SQLException {
        executeUpdate(dataSource, "CREATE TABLE IF NOT EXISTS t_order (order_id BIGINT NOT NULL, user_id INT NOT NULL, status VARCHAR(50), PRIMARY KEY (order_id))");
        executeUpdate(dataSource, "CREATE TABLE IF NOT EXISTS t_order_item (order_item_id BIGINT NOT "
            + "NULL AUTO_INCREMENT, order_id BIGINT NOT NULL, user_id INT NOT NULL, PRIMARY KEY (order_item_id))");
    }
    
    private static void dropTable(final DataSource dataSource) throws SQLException {
        executeUpdate(dataSource, "DROP TABLE IF EXISTS t_order_item");
        executeUpdate(dataSource, "DROP TABLE IF EXISTS t_order");
    }
    
    private static void executeUpdate(final DataSource dataSource, final String sql) throws SQLException {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }
    
    private static void insert(final DataSource dataSource) throws SQLException {
        String sql = String.format("INSERT INTO t_order VALUES (%s, %s, 'INIT');", 21474843647L, 10);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
    }
    
    private static void updateFailure(final DataSource dataSource) throws SQLException {
        String sql1 = "UPDATE t_order SET status='UPDATE_1' WHERE user_id=10 AND order_id=21474843647";
        String sql2 = "UPDATE t_order SET not_existed_column=1 WHERE user_id=1 AND order_id=?";
        String sql3 = "UPDATE t_order SET status='UPDATE_2' WHERE user_id=10 AND order_id=21474843647";
        SoftTransactionManager transactionManager = new SoftTransactionManager(getSoftTransactionConfiguration(dataSource));
        transactionManager.init();
        BEDSoftTransaction transaction = (BEDSoftTransaction) transactionManager.getTransaction(SoftTransactionType.BestEffortsDelivery);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            transaction.begin(connection);
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.setObject(1, 21474843647L);
            PreparedStatement preparedStatement3 = connection.prepareStatement(sql3);
            preparedStatement1.executeUpdate();
            preparedStatement2.executeUpdate();
            preparedStatement3.executeUpdate();
        } finally {
            transaction.end();
            if (null != connection) {
                connection.close();
            }
        }
    }
    
    private static DataSource createTransactionLogDataSource() {
        BasicDataSource result = new BasicDataSource();
        result.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
        result.setUrl("jdbc:mysql://localhost:3306/trans_log");
        result.setUsername("root");
        result.setPassword("");
        return result;
    }
    
    private static SoftTransactionConfiguration getSoftTransactionConfiguration(final DataSource dataSource) {
        SoftTransactionConfiguration result = new SoftTransactionConfiguration(dataSource);
        if (useNestedJob) {
            result.setBestEffortsDeliveryJobConfiguration(Optional.of(new NestedBestEffortsDeliveryJobConfiguration()));
        }
        result.setTransactionLogDataSource(createTransactionLogDataSource());
        return result;
    }
}
