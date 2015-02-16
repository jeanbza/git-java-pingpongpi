package DatabaseUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Database {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public Database() throws SQLException {
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
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS daily_activity(date DATE NOT NULL PRIMARY KEY," +
            " total_active INT(11), total_inactive INT(11), active_hour_0 INT(11), inactive_hour_0 INT(11)," +
            " active_hour_1 INT(11), inactive_hour_1 INT(11), active_hour_2 INT(11), inactive_hour_2 INT(11)," +
            " active_hour_3 INT(11), inactive_hour_3 INT(11), active_hour_4 INT(11), inactive_hour_4 INT(11)," +
            " active_hour_5 INT(11), inactive_hour_5 INT(11), active_hour_6 INT(11), inactive_hour_6 INT(11)," +
            " active_hour_7 INT(11), inactive_hour_7 INT(11), active_hour_8 INT(11), inactive_hour_8 INT(11)," +
            " active_hour_9 INT(11), inactive_hour_9 INT(11), active_hour_10 INT(11), inactive_hour_10 INT(11)," +
            " active_hour_11 INT(11), inactive_hour_11 INT(11), active_hour_12 INT(11), inactive_hour_12 INT(11)," +
            " active_hour_13 INT(11), inactive_hour_13 INT(11), active_hour_14 INT(11), inactive_hour_14 INT(11)," +
            " active_hour_15 INT(11), inactive_hour_15 INT(11), active_hour_16 INT(11), inactive_hour_16 INT(11)," +
            " active_hour_17 INT(11), inactive_hour_17 INT(11), active_hour_18 INT(11), inactive_hour_18 INT(11)," +
            " active_hour_19 INT(11), inactive_hour_19 INT(11), active_hour_20 INT(11), inactive_hour_20 INT(11)," +
            " active_hour_21 INT(11), inactive_hour_21 INT(11), active_hour_22 INT(11), inactive_hour_22 INT(11)," +
            " active_hour_23 INT(11), inactive_hour_23 INT(11))");
    }
}
