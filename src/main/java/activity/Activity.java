package Activity;

import TimeUtils.Temporals;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public class Activity implements Delayed {
    private final static int SECONDS_RECENT = 30;

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

    @Override
    public long getDelay(TimeUnit unit) {
        return LocalDateTime.now().until(createdAt.plusSeconds(SECONDS_RECENT), Temporals.chronoUnit(unit));
    }

    @Override
    public int compareTo(Delayed o) {
        long comparison = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);

        if (comparison > 0) {
            return 1;
        } else if (comparison == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Activity{" +
            "active=" + active +
            ", createdAt=" + createdAt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Activity activity = (Activity) o;

        if (active != activity.active)
            return false;
        if (createdAt != null ? !createdAt.equals(activity.createdAt) : activity.createdAt != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (active ? 1 : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}
