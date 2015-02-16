package Activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // TODO: Refactor this...
    @Override
    public void createActivities(List<Activity> activitiesToPersist) {
        if (activitiesToPersist.isEmpty()) {
            return;
        }

        Map<LocalDate, List<Activity>> datesAndTheirActivities = activitiesToPersist.stream()
            .collect(Collectors.groupingBy(activity -> activity.getCreatedAt().toLocalDate()));

        Map<LocalDate, Long> datesAndTheirTotalActive = new HashMap<>();
        Map<LocalDate, Long> datesAndTheirTotalInactive = new HashMap<>();
        Map<LocalDate, Map<Integer, Long>> datesAndTheirHourlyTotalActive = new HashMap<>();
        Map<LocalDate, Map<Integer, Long>> datesAndTheirHourlyTotalInactive = new HashMap<>();

        datesAndTheirActivities.forEach((date, dailyActivities) -> {
            long totalDailyActiveCount = dailyActivities.stream()
                .filter(activity -> activity.isActive())
                .count();

            datesAndTheirTotalActive.put(date, totalDailyActiveCount);
            datesAndTheirTotalInactive.put(date, dailyActivities.size()-totalDailyActiveCount);

            Map<Integer, List<Activity>> activitiesByHour = dailyActivities.stream()
                .collect(Collectors.groupingBy(activity -> activity.getCreatedAt().getHour()));

            Map<Integer, Long> hoursAndTheirTotalActive = hourlyMap();
            Map<Integer, Long> hoursAndTheirTotalInactive = hourlyMap();

            activitiesByHour.forEach((hour, hourlyActivities) -> {
                long totalHourlyActiveCount = hourlyActivities.stream()
                    .filter(activity -> activity.isActive())
                    .count();

                hoursAndTheirTotalActive.put(hour, totalHourlyActiveCount);
                hoursAndTheirTotalInactive.put(hour, hourlyActivities.size()-totalHourlyActiveCount);
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
            String hourlyActiveValues = datesAndTheirHourlyTotalActive.get(date).values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

            String hourlyInactiveValues = datesAndTheirHourlyTotalInactive.get(date).values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

            String totalActive = datesAndTheirTotalActive.get(date).toString();
            String totalInactive = datesAndTheirTotalInactive.get(date).toString();

            valueRows.add(format("(\"{0}\",{1},{2},{3},{4})",
                date, totalActive, totalInactive, hourlyActiveValues, hourlyInactiveValues)
            );
        });

        String valueSql = valueRows.stream().collect(Collectors.joining(","));

        jdbcTemplate.update(insertSql + valueSql);
    }

    private static Map<Integer, Long> hourlyMap() {
        Map<Integer, Long> map = new HashMap<>();

        for (int i = 0; i < 24; i++) {
            map.put(i, 0L);
        }

        return map;
    }
}
