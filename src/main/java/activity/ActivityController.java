package activity;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityController {
    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public ResponseEntity<String> greeting() {
        return new ResponseEntity<>("{'active': 1}", HttpStatus.OK);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public ResponseEntity<String> greeting(@RequestParam(value = "active") boolean activity) {


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
