package Activity;

import java.time.LocalDate;
import java.util.*;

public class DailyActivityBuilder {
    private LocalDate date;
    private List<Long> hourlyActive, hourlyInactive = sizedList(24);

    public DailyActivityBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public DailyActivityBuilder hourActive(int hour, Long active) {
        hourlyActive.set(hour, active);
        return this;
    }

    public DailyActivityBuilder hourInactive(int hour, Long inactive) {
        hourlyInactive.set(hour, inactive);
        return this;
    }

    public DailyActivityBuilder hourlyActive(List<Long> hourlyActive) {
        this.hourlyActive = hourlyActive;
        return this;
    }

    public DailyActivityBuilder hourlyInactive(List<Long> hourlyInactive) {
        this.hourlyInactive = hourlyInactive;
        return this;
    }

    public DailyActivity build() {
        return new DailyActivity(date, hourlyActive, hourlyInactive);
    }

    public static DailyActivityBuilder dailyActivityBuilder() {
        return new DailyActivityBuilder();
    }

    private static List<Long> sizedList(int listSize) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            list.add(0L);
        }
        return list;
    }
}
