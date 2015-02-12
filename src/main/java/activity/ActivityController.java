package activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.*;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@RestController
public class ActivityController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> greeting() {
        List<Activity> activityList = jdbcTemplate.query("SELECT active, created_at FROM activity", new RowMapper<Activity>() {
                @Override
                public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Activity(rs.getBoolean(1), LocalDateTime.ofInstant(rs.getTimestamp(2).toInstant(), ZoneId.of("UTC")));
                }
            });

        String json = "[{"+activityList.stream()
            .map(activity -> format("\"active\":{0},\"created_at\":\"{1}\"", activity.isActive(), activity.getCreatedAt()))
            .collect(Collectors.joining("},{"))+"}]";

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> greeting(@RequestParam(value = "active") boolean activity) {
        jdbcTemplate.update("INSERT INTO activity(active) VALUES (?)", activity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private class Activity {
        private boolean active;
        private LocalDateTime createdAt;

        public Activity(boolean active, LocalDateTime createdAt) {
            this.active = active;
            this.createdAt = createdAt;
        }

        public boolean isActive() {
            return active;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
