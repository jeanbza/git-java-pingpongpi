package Activity;

import java.util.List;

public interface ActivityDAO {
    public List<Activity> getRecentActivities();

    public void createActivity(boolean active);

    public void createActivities(List<Activity> activitiesToPersist);

    public List<DailyActivity> getDailyActivities();

    public void refreshDailyActivities();
}
