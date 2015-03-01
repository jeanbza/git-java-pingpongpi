package Activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;

@Repository
public class MysqlActivityDAO implements ActivityDAO {
    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter SQL_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
    private List<DailyActivity> dailyActivities;

    @Autowired
    public MysqlActivityDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // TODO: Refactor this...
    @Override
    public void createActivities(List<Activity> activitiesToPersist) {
        if (activitiesToPersist.isEmpty()) {
            return;
        }

        Map<LocalDate, List<Activity>> datesAndTheirActivities = activitiesToPersist.stream().collect(Collectors.groupingBy(activity -> activity.getCreatedAt().toLocalDate()));

        Map<LocalDate, Long> datesAndTheirTotalActive = new HashMap<>();
        Map<LocalDate, Long> datesAndTheirTotalInactive = new HashMap<>();
        Map<LocalDate, Map<Integer, Long>> datesAndTheirHourlyTotalActive = new HashMap<>();
        Map<LocalDate, Map<Integer, Long>> datesAndTheirHourlyTotalInactive = new HashMap<>();

        datesAndTheirActivities.forEach((date, dailyActivities) -> {
            long totalDailyActiveCount = dailyActivities.stream().filter(activity -> activity.isActive()).count();

            datesAndTheirTotalActive.put(date, totalDailyActiveCount);
            datesAndTheirTotalInactive.put(date, dailyActivities.size() - totalDailyActiveCount);

            Map<Integer, List<Activity>> activitiesByHour = dailyActivities.stream().collect(Collectors.groupingBy(activity -> activity.getCreatedAt().getHour()));

            Map<Integer, Long> hoursAndTheirTotalActive = hourlyMap();
            Map<Integer, Long> hoursAndTheirTotalInactive = hourlyMap();

            activitiesByHour.forEach((hour, hourlyActivities) -> {
                long totalHourlyActiveCount = hourlyActivities.stream().filter(activity -> activity.isActive()).count();

                hoursAndTheirTotalActive.put(hour, totalHourlyActiveCount);
                hoursAndTheirTotalInactive.put(hour, hourlyActivities.size() - totalHourlyActiveCount);
            });

            datesAndTheirHourlyTotalActive.put(date, hoursAndTheirTotalActive);
            datesAndTheirHourlyTotalInactive.put(date, hoursAndTheirTotalInactive);
        });

        String insertSql = "INSERT INTO daily_activity(date, total_active, total_inactive," +
            " active_hour_0, active_hour_1, active_hour_2, active_hour_3, active_hour_4, active_hour_5," +
            " active_hour_6, active_hour_7, active_hour_8, active_hour_9, active_hour_10, active_hour_11," +
            " active_hour_12, active_hour_13, active_hour_14, active_hour_15, active_hour_16, active_hour_17," +
            " active_hour_18, active_hour_19, active_hour_20, active_hour_21, active_hour_22, active_hour_23," +
            " inactive_hour_0, inactive_hour_1, inactive_hour_2, inactive_hour_3, inactive_hour_4, inactive_hour_5," +
            " inactive_hour_6, inactive_hour_7, inactive_hour_8, inactive_hour_9, inactive_hour_10, inactive_hour_11," +
            " inactive_hour_12, inactive_hour_13, inactive_hour_14, inactive_hour_15, inactive_hour_16, inactive_hour_17," +
            " inactive_hour_18, inactive_hour_19, inactive_hour_20, inactive_hour_21, inactive_hour_22, inactive_hour_23" +
            ") VALUES ";

        Collection<String> valueRows = new ArrayList<>();

        datesAndTheirTotalActive.keySet().iterator().forEachRemaining(date -> {
            String hourlyActiveValues = datesAndTheirHourlyTotalActive.get(date).values().stream().map(Object::toString).collect(Collectors.joining(","));

            String hourlyInactiveValues = datesAndTheirHourlyTotalInactive.get(date).values().stream().map(Object::toString).collect(Collectors.joining(","));

            String totalActive = datesAndTheirTotalActive.get(date).toString();
            String totalInactive = datesAndTheirTotalInactive.get(date).toString();

            valueRows.add(format("(\"{0}\",{1},{2},{3},{4})", date, totalActive, totalInactive, hourlyActiveValues, hourlyInactiveValues));
        });

        String valueSql = valueRows.stream().collect(Collectors.joining(","));
        String duplicateKeyUpdate = " ON DUPLICATE KEY UPDATE" +
            " total_active=total_active+VALUES(total_active)," +
            " total_inactive=total_inactive+VALUES(total_inactive)," +
            " active_hour_0=active_hour_0+VALUES(active_hour_0)," +
            " active_hour_1=active_hour_1+VALUES(active_hour_1)," +
            " active_hour_2=active_hour_2+VALUES(active_hour_2)," +
            " active_hour_3=active_hour_3+VALUES(active_hour_3)," +
            " active_hour_4=active_hour_4+VALUES(active_hour_4)," +
            " active_hour_5=active_hour_5+VALUES(active_hour_5)," +
            " active_hour_6=active_hour_6+VALUES(active_hour_6)," +
            " active_hour_7=active_hour_7+VALUES(active_hour_7)," +
            " active_hour_8=active_hour_8+VALUES(active_hour_8)," +
            " active_hour_9=active_hour_9+VALUES(active_hour_9)," +
            " active_hour_10=active_hour_10+VALUES(active_hour_10)," +
            " active_hour_11=active_hour_11+VALUES(active_hour_11)," +
            " active_hour_12=active_hour_12+VALUES(active_hour_12)," +
            " active_hour_13=active_hour_13+VALUES(active_hour_13)," +
            " active_hour_14=active_hour_14+VALUES(active_hour_14)," +
            " active_hour_15=active_hour_15+VALUES(active_hour_15)," +
            " active_hour_16=active_hour_16+VALUES(active_hour_16)," +
            " active_hour_17=active_hour_17+VALUES(active_hour_17)," +
            " active_hour_18=active_hour_18+VALUES(active_hour_18)," +
            " active_hour_19=active_hour_19+VALUES(active_hour_19)," +
            " active_hour_20=active_hour_20+VALUES(active_hour_20)," +
            " active_hour_21=active_hour_21+VALUES(active_hour_21)," +
            " active_hour_22=active_hour_22+VALUES(active_hour_22)," +
            " active_hour_23=active_hour_23+VALUES(active_hour_23)," +
            " inactive_hour_0=inactive_hour_0+VALUES(inactive_hour_0)," +
            " inactive_hour_1=inactive_hour_1+VALUES(inactive_hour_1)," +
            " inactive_hour_2=inactive_hour_2+VALUES(inactive_hour_2)," +
            " inactive_hour_3=inactive_hour_3+VALUES(inactive_hour_3)," +
            " inactive_hour_4=inactive_hour_4+VALUES(inactive_hour_4)," +
            " inactive_hour_5=inactive_hour_5+VALUES(inactive_hour_5)," +
            " inactive_hour_6=inactive_hour_6+VALUES(inactive_hour_6)," +
            " inactive_hour_7=inactive_hour_7+VALUES(inactive_hour_7)," +
            " inactive_hour_8=inactive_hour_8+VALUES(inactive_hour_8)," +
            " inactive_hour_9=inactive_hour_9+VALUES(inactive_hour_9)," +
            " inactive_hour_10=inactive_hour_10+VALUES(inactive_hour_10)," +
            " inactive_hour_11=inactive_hour_11+VALUES(inactive_hour_11)," +
            " inactive_hour_12=inactive_hour_12+VALUES(inactive_hour_12)," +
            " inactive_hour_13=inactive_hour_13+VALUES(inactive_hour_13)," +
            " inactive_hour_14=inactive_hour_14+VALUES(inactive_hour_14)," +
            " inactive_hour_15=inactive_hour_15+VALUES(inactive_hour_15)," +
            " inactive_hour_16=inactive_hour_16+VALUES(inactive_hour_16)," +
            " inactive_hour_17=inactive_hour_17+VALUES(inactive_hour_17)," +
            " inactive_hour_18=inactive_hour_18+VALUES(inactive_hour_18)," +
            " inactive_hour_19=inactive_hour_19+VALUES(inactive_hour_19)," +
            " inactive_hour_20=inactive_hour_20+VALUES(inactive_hour_20)," +
            " inactive_hour_21=inactive_hour_21+VALUES(inactive_hour_21)," +
            " inactive_hour_22=inactive_hour_22+VALUES(inactive_hour_22)," +
            " inactive_hour_23=inactive_hour_23+VALUES(inactive_hour_23)";

        jdbcTemplate.update(insertSql + valueSql + duplicateKeyUpdate);
    }

    @Override
    public List<DailyActivity> getDailyActivities() {
        return dailyActivities;
    }

    public void refreshDailyActivities() {
        dailyActivities = jdbcTemplate.query("SELECT date," +
            " active_hour_0, active_hour_1, active_hour_2, active_hour_3, active_hour_4, active_hour_5," +
            " active_hour_6, active_hour_7, active_hour_8, active_hour_9, active_hour_10, active_hour_11," +
            " active_hour_12, active_hour_13, active_hour_14, active_hour_15, active_hour_16, active_hour_17," +
            " active_hour_18, active_hour_19, active_hour_20, active_hour_21, active_hour_22, active_hour_23," +
            " inactive_hour_0, inactive_hour_1, inactive_hour_2, inactive_hour_3, inactive_hour_4, inactive_hour_5," +
            " inactive_hour_6, inactive_hour_7, inactive_hour_8, inactive_hour_9, inactive_hour_10, inactive_hour_11," +
            " inactive_hour_12, inactive_hour_13, inactive_hour_14, inactive_hour_15, inactive_hour_16, inactive_hour_17," +
            " inactive_hour_18, inactive_hour_19, inactive_hour_20, inactive_hour_21, inactive_hour_22, inactive_hour_23" +
            " FROM daily_activity", new RowMapper<DailyActivity>() {
            @Override
            public DailyActivity mapRow(ResultSet rs, int rowNum) throws SQLException {
                LocalDate date = rs.getDate("date").toLocalDate();
                List<Long> active = new ArrayList<>();
                List<Long> inactive = new ArrayList<>();

                for (int i = 0; i < 24; i++) {
                    active.add(rs.getLong(i+2));
                    inactive.add(rs.getLong(i+2+24));
                }

                return new DailyActivity(date, active, inactive);
            }
        });
    }

    @Scheduled(fixedDelay=1000*60*60)
    private void scheduledRefreshDailyActivities() {
        refreshDailyActivities();
    }

    private static Map<Integer, Long> hourlyMap() {
        Map<Integer, Long> map = new HashMap<>();

        for (int i = 0; i < 24; i++) {
            map.put(i, 0L);
        }

        return map;
    }
}
