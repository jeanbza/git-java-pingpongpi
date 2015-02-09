package activity;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityController {
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> greeting() {
        return new ResponseEntity<String>("{'active': 1}", HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> greeting(@RequestParam(value = "active") boolean activity) {
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}
