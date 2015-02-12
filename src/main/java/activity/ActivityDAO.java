package activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.time.*;
import java.util.List;

public class ActivityDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ActivityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Activity> getActivities() {
        return jdbcTemplate.query("SELECT active, created_at FROM activity", new RowMapper<Activity>() {
            @Override
            public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Activity(rs.getBoolean(1), LocalDateTime.ofInstant(rs.getTimestamp(2).toInstant(), ZoneId.of("UTC")));
            }
        });
    }

    public void addActivity(boolean active) {
        jdbcTemplate.update("INSERT INTO activity(active) VALUES (?)", active);
    }
}
