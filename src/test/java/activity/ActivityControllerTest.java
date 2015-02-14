package Activity;

import org.junit.*;
import org.mockito.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ActivityControllerTest {
    @InjectMocks ActivityController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(get("/activity"))
            .andExpect(status().isOk());
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
