package activity;

import org.junit.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ActivityControllerTest {
    @InjectMocks
    ActivityController controller;
    @Mock
    ActivityDAO activityDAO;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testGet() throws Exception {
        doReturn(asList(
            new Activity(false, LocalDateTime.parse("2015-01-01T00:00:03")),
            new Activity(true, LocalDateTime.parse("2015-01-01T00:00:05"))
        )).when(activityDAO).getActivities();

        mockMvc.perform(get("/activity"))
            .andExpect(status().isOk())
            .andExpect(content().string("[{\"active\":false,\"created_at\":\"2015-01-01T00:00:03\"},{\"active\":true,\"created_at\":\"2015-01-01T00:00:05\"}]"));
    }
}
