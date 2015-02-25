package Activity;

import DatabaseUtils.Database;
import org.junit.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.*;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

import static Activity.DailyActivityBuilder.dailyActivityBuilder;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;

public class MysqlActivityDAOTest {
    private ActivityDAO dao;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        Database testDatabase = new Database();
        jdbcTemplate = testDatabase.getJdbcTemplate();
        dataSource = jdbcTemplate.getDataSource();

        dao = new MysqlActivityDAO(jdbcTemplate);

        jdbcTemplate.update("DELETE FROM daily_activity");
    }

    @Test
    public void createActivities() throws Exception {
        List<Activity> activitiesToPersist = asList(
            new Activity(true, LocalDateTime.parse("2013-01-01T01:01:01")),

            new Activity(false, LocalDateTime.parse("2013-02-02T02:02:02")),

            // Active: [0, 2, 1, ...]
            // Inactive: [0, 1, ...]
            new Activity(true, LocalDateTime.parse("2013-03-03T01:03:03")),
            new Activity(false, LocalDateTime.parse("2013-03-03T01:04:03")),
            new Activity(true, LocalDateTime.parse("2013-03-03T01:05:03")),
            new Activity(true, LocalDateTime.parse("2013-03-03T02:03:03"))
        );

        dao.createActivities(activitiesToPersist);

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT date, total_active, total_inactive," +
            " active_hour_1, inactive_hour_1, active_hour_2, inactive_hour_2" +
            " FROM daily_activity ORDER BY date");
        ResultSet rs = preparedStatement.executeQuery();

        assertThat(rs.next(), equalTo(true));
        assertThat(rs.getDate("date"), equalTo(Date.valueOf("2013-01-01")));
        assertThat(rs.getInt("total_active"), equalTo(1));
        assertThat(rs.getInt("total_inactive"), equalTo(0));
        assertThat(rs.getInt("active_hour_1"), equalTo(1));
        assertThat(rs.getInt("inactive_hour_1"), equalTo(0));
        assertThat(rs.getInt("active_hour_2"), equalTo(0));
        assertThat(rs.getInt("inactive_hour_2"), equalTo(0));

        assertThat(rs.next(), equalTo(true));
        assertThat(rs.getDate("date"), equalTo(Date.valueOf("2013-02-02")));
        assertThat(rs.getInt("total_active"), equalTo(0));
        assertThat(rs.getInt("total_inactive"), equalTo(1));
        assertThat(rs.getInt("active_hour_1"), equalTo(0));
        assertThat(rs.getInt("inactive_hour_1"), equalTo(0));
        assertThat(rs.getInt("active_hour_2"), equalTo(0));
        assertThat(rs.getInt("inactive_hour_2"), equalTo(1));

        assertThat(rs.next(), equalTo(true));
        assertThat(rs.getDate("date"), equalTo(Date.valueOf("2013-03-03")));
        assertThat(rs.getInt("total_active"), equalTo(3));
        assertThat(rs.getInt("total_inactive"), equalTo(1));
        assertThat(rs.getInt("active_hour_1"), equalTo(2));
        assertThat(rs.getInt("inactive_hour_1"), equalTo(1));
        assertThat(rs.getInt("active_hour_2"), equalTo(1));
        assertThat(rs.getInt("inactive_hour_2"), equalTo(0));

        assertThat(rs.next(), equalTo(false));
    }

    @Test
    public void createActivities_withExistingData() throws Exception {
        jdbcTemplate.update("INSERT INTO daily_activity(date, total_active, total_inactive, active_hour_1, inactive_hour_1)" +
            " VALUES ('2013-01-01', 3, 5, 3, 5)");

        List<Activity> activitiesToPersist = asList(
            new Activity(true, LocalDateTime.parse("2013-01-01T01:01:01")),
            new Activity(true, LocalDateTime.parse("2013-01-01T01:02:01")),
            new Activity(false, LocalDateTime.parse("2013-01-01T01:03:01"))
        );

        dao.createActivities(activitiesToPersist);

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT date, total_active, total_inactive," +
            " active_hour_1, inactive_hour_1" +
            " FROM daily_activity ORDER BY date");
        ResultSet rs = preparedStatement.executeQuery();

        assertThat(rs.next(), equalTo(true));
        assertThat(rs.getDate("date"), equalTo(Date.valueOf("2013-01-01")));
        assertThat(rs.getInt("total_active"), equalTo(5));
        assertThat(rs.getInt("total_inactive"), equalTo(6));
        assertThat(rs.getInt("active_hour_1"), equalTo(5));
        assertThat(rs.getInt("inactive_hour_1"), equalTo(6));

        assertThat(rs.next(), equalTo(false));
    }

    @Test
    public void createActivities_withEmptyList() throws Exception {
        try {
            dao.createActivities(asList());
        } catch (BadSqlGrammarException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getDailyActivities() throws Exception {
        jdbcTemplate.update("INSERT INTO daily_activity(date, total_active, total_inactive, active_hour_1, inactive_hour_1)" +
            " VALUES ('2013-01-01', 3, 5, 3, 5)");

        List<DailyActivity> dailyActivities = dao.getDailyActivities();

        assertThat(dailyActivities, equalTo(asList(
            dailyActivityBuilder().build()
        )));
    }
}
