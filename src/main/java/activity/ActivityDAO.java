package Activity;

import java.util.List;

public interface ActivityDAO {
    public void createActivities(List<Activity> activitiesToPersist);

    public List<DailyActivity> getDailyActivities();
}
