package TestUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TestDatabase {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public TestDatabase() throws SQLException {
        dataSource = getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate = jdbcTemplate;

        createInitialTables(jdbcTemplate);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private static synchronized DataSource getDataSource() throws SQLException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new com.mysql.jdbc.Driver());
        dataSource.setUrl("jdbc:mysql://localhost/pingpong_test");
        dataSource.setUsername("root");
        dataSource.setPassword("");

        return dataSource;
    }

    public static void createInitialTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS activity(id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
            " active TINYINT(1) DEFAULT NULL, created_at TIMESTAMP DEFAULT NOW())");
    }
}
