package Activity;

import org.apache.commons.collections.IteratorUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Controller
public class ActivityController {
    private BlockingQueue<Activity> activitiesAwaitingPersist = new LinkedBlockingQueue<>();
    private BlockingQueue<Activity> recentActivities = new DelayQueue<Activity>();

    private final ObjectMapper jsonMapper = new ObjectMapper();
    @Autowired
    private ActivityDAO activityDAO;

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> activity() {
        List<Activity> recentActivities = IteratorUtils.toList(this.recentActivities.iterator());
        String json = "[{" + recentActivities.stream()
            .map(activity -> format("\"active\":{0},\"created_at\":\"{1}\"", activity.isActive(), activity.getCreatedAt()))
            .collect(Collectors.joining("},{")) + "}]";

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> activity(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        Map<String, Boolean> activityRequest = jsonMapper.readValue(request.getReader(), HashMap.class);

        if (activityRequest.containsKey("active")) {
            recentActivities.put(new Activity(activityRequest.get("active"), LocalDateTime.now()));

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Scheduled(fixedDelay=1000)
    public void drainRecentActivities() {
        recentActivities.drainTo(activitiesAwaitingPersist);
    }

    @Scheduled(fixedDelay=1000*60*5)
    public void drainActivitiesAwaitingPersist() {
        List<Activity> activitiesToPersist = new ArrayList<>();
        activitiesAwaitingPersist.drainTo(activitiesToPersist);
        activityDAO.createActivities(activitiesToPersist);
    }
}
