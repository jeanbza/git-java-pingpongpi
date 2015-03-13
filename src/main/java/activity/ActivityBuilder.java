package Activity;

import java.time.LocalDateTime;

public class ActivityBuilder {
    private boolean active;
    private LocalDateTime createdAt;

    public ActivityBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public ActivityBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public static ActivityBuilder activityBuilder() {
        return new ActivityBuilder();
    }

    public Activity build() {
        return new Activity(active, createdAt);
    }
}
