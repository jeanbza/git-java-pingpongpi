package Activity;

import org.junit.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static Activity.DailyActivityBuilder.dailyActivityBuilder;

public class ActivityControllerTest {
    @Mock ActivityDAO activityDAO;
    @InjectMocks ActivityController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ActivityController(activityDAO);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testGet() throws Exception {
        List<DailyActivity> dailyActivityList = asList(
            dailyActivityBuilder()
                .date(LocalDate.parse("2013-01-01"))
                .hourActive(1, 3L)
                .hourInactive(1, 5L)
                .hourActive(5, 7L)
                .hourInactive(5, 8L)
                .build(),
            dailyActivityBuilder()
                .date(LocalDate.parse("2014-01-01"))
                .hourActive(1, 2L)
                .hourInactive(1, 2L)
                .hourActive(5, 1L)
                .hourInactive(5, 4L)
                .build()
        );

        doReturn(dailyActivityList).when(activityDAO).getDailyActivities();

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("dailyActivities", equalTo(dailyActivityList)));
    }

    @Test
    public void testPost() throws Exception {
        mockMvc.perform(post("/activity").content("{\"active\":true}"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void testPost_withBadRequest() throws Exception {
        mockMvc.perform(post("/activity").content("{\"boom\":true}"))
            .andExpect(status().isBadRequest());
    }
}
