package Activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Repository
public class MysqlActivityDAO implements ActivityDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter SQL_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");

    @Autowired
    public MysqlActivityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createActivities(List<Activity> activitiesToPersist) {
        if (activitiesToPersist.isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO activity(active, created_at) VALUES ";
        String valuesSql = activitiesToPersist.stream()
            .map(activity -> format("({0},\"{1}\")", activity.isActive(), activity.getCreatedAt().format(SQL_FORMAT)))
            .collect(Collectors.joining(","));

        jdbcTemplate.update(insertSql + valuesSql);
    }
}
