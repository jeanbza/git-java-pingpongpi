package Activity;

import TestUtils.TestDatabase;
import org.junit.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.*;

import java.time.*;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;

public class MysqlActivityDAOTest {
    private ActivityDAO dao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        TestDatabase testDatabase = new TestDatabase();
        jdbcTemplate = testDatabase.getJdbcTemplate();

        dao = new MysqlActivityDAO(jdbcTemplate);

        jdbcTemplate.update("DELETE FROM activity");
    }

    @Test
    public void createActivities() throws Exception {
        List<Activity> activitiesToPersist = asList(
            new Activity(true, LocalDateTime.parse("2013-01-01T01:01:01")),
            new Activity(false, LocalDateTime.parse("2013-02-02T02:02:02")),
            new Activity(true, LocalDateTime.parse("2013-03-03T03:03:03"))
        );

        dao.createActivities(activitiesToPersist);

        List<Activity> resultantActivities = jdbcTemplate.query(
            "SELECT active, created_at FROM activity ORDER BY created_at",
            (rs, rowNum) -> new Activity(
                rs.getBoolean(1),
                LocalDateTime.ofInstant(rs.getTimestamp(2).toInstant(), ZoneId.systemDefault())
            )
        );

        for (int i = 0; i < activitiesToPersist.size(); i++) {
            assertThat(activitiesToPersist.get(i), equalTo(resultantActivities.get(i)));
        }

        assertThat(activitiesToPersist.size(), equalTo(resultantActivities.size()));
    }

    @Test
    public void createActivities_withEmptyList() throws Exception {
        try {
            dao.createActivities(asList());
        } catch (BadSqlGrammarException e) {
            fail(e.getMessage());
        }
    }
}
