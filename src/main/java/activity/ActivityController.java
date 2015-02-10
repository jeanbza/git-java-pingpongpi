package activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> greeting() {
        return new ResponseEntity<>("{'active': 1}", HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> greeting(@RequestParam(value = "active") boolean activity) {
        jdbcTemplate.update("insert into activity(active) values (?)", activity);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
