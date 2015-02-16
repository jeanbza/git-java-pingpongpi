package Activity;

import org.springframework.cloud.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.SQLException;

import static DatabaseUtils.Database.createInitialTables;

public class AppConfiguration {
    @Configuration
    @Profile("cloud")
    @EnableScheduling
    static class CloudConfiguration {
        @Bean
        public DataSource getDataSource() {
            CloudFactory cloudFactory = new CloudFactory();
            Cloud cloud = cloudFactory.getCloud();
            String serviceID = cloud.getServiceInfo("mysql").getId();
            return cloud.getServiceConnector(serviceID, DataSource.class, null);
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            createInitialTables(jdbcTemplate);
            return jdbcTemplate;
        }
    }

    @Configuration
    @Profile("default")
    @EnableScheduling
    static class LocalConfiguration {
        @Bean
        public DataSource dataSource() throws SQLException {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriver(new com.mysql.jdbc.Driver());
            dataSource.setUrl("jdbc:mysql://localhost/pingpong");
            dataSource.setUsername("root");
            dataSource.setPassword("");

            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            createInitialTables(jdbcTemplate);
            return jdbcTemplate;
        }
    }
}