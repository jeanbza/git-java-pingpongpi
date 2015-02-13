package activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@RestController
public class ActivityController {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ActivityDAO activityDAO;

    public ActivityController(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> activity() {
        List<Activity> activityList = activityDAO.getActivities();

        String json = "[{" + activityList.stream()
            .map(activity -> format("\"active\":{0},\"created_at\":\"{1}\"", activity.isActive(), activity.getCreatedAt()))
            .collect(Collectors.joining("},{")) + "}]";

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> activity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Boolean> activityRequest = jsonMapper.readValue(request.getReader(), HashMap.class);

        if (activityRequest.containsKey("active")) {
            activityDAO.addActivity(activityRequest.get("active"));

            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
