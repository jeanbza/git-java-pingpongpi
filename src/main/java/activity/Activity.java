package activity;

import java.time.LocalDateTime;

public class Activity {
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
