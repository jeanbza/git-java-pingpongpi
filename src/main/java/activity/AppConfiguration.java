package activity;

import org.springframework.cloud.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class AppConfiguration {
    @Configuration
    @Profile("cloud")
    static class CloudConfiguration {
        @Bean
        public DataSource dataSource() {
            CloudFactory cloudFactory = new CloudFactory();
            Cloud cloud = cloudFactory.getCloud();
            String serviceID = cloud.getServiceInfo("mysql").getId();
            return cloud.getServiceConnector(serviceID, DataSource.class, null);
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            createInitialTables(jdbcTemplate);
            return jdbcTemplate;
        }
    }

    @Configuration
    @Profile("default")
    static class LocalConfiguration {
        @Bean
        public DataSource dataSource() throws SQLException {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriver(new com.mysql.jdbc.Driver());
            dataSource.setUrl("jdbc:mysql://localhost/spring_test");
            dataSource.setUsername("root");
            dataSource.setPassword("");

            return dataSource;
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            createInitialTables(jdbcTemplate);
            return jdbcTemplate;
        }
    }

    private static void createInitialTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS activity(id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            " active TINYINT(1) DEFAULT NULL, created_at TIMESTAMP DEFAULT NOW())");
    }
}