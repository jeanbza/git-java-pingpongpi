package Activity;

import java.time.LocalDate;
import java.util.*;

import static java.text.MessageFormat.format;

public class DailyActivity {
    private final LocalDate date;
    private final List<Long> hourlyActive, hourlyInactive;

    public DailyActivity(LocalDate date, List<Long> hourlyActive, List<Long> hourlyInactive) {
        if (hourlyActive.size() != 24 && hourlyInactive.size() != 24) {
            throw new UnsupportedOperationException(format(
                "HourlyActive and HourlyInactive must be 24 length lists. Their lengths are: {0} and {1}, respectively",
                hourlyActive.size(), hourlyInactive.size()));
        }

        this.date = date;
        this.hourlyActive = hourlyActive;
        this.hourlyInactive = hourlyInactive;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Long> getHourlyActive() {
        return hourlyActive;
    }

    public List<Long> getHourlyInactive() {
        return hourlyInactive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DailyActivity that = (DailyActivity) o;

        if (date != null ? !date.equals(that.date) : that.date != null)
            return false;
        if (hourlyActive != null ? !hourlyActive.equals(that.hourlyActive) : that.hourlyActive != null)
            return false;
        if (hourlyInactive != null ? !hourlyInactive.equals(that.hourlyInactive) : that.hourlyInactive != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (hourlyActive != null ? hourlyActive.hashCode() : 0);
        result = 31 * result + (hourlyInactive != null ? hourlyInactive.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DailyActivity{" +
            "date=" + date +
            ", hourlyActive=" + hourlyActive +
            ", hourlyInactive=" + hourlyInactive +
            '}';
    }
}
