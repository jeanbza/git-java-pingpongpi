package activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@RestController
public class ActivityController {
    private final ActivityDAO activityDAO;

    @Autowired
    public ActivityController(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> greeting() {
        List<Activity> activityList = activityDAO.getActivities();
        String json = "[{" + activityList.stream().map(activity -> format("\"active\":{0},\"created_at\":\"{1}\"", activity.isActive(), activity.getCreatedAt())).collect(Collectors.joining("},{")) + "}]";

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> greeting(@RequestParam(value = "active") boolean active) {
        activityDAO.addActivity(active);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
