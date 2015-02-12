package activity;

import org.junit.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ActivityControllerTest {
    @InjectMocks
    ActivityController controller;
    @Mock
    JdbcTemplate jdbcTemplate;

    @Test
    public void testGet() throws Exception {
        initMocks(this);

        MockMvc mockMvc = standaloneSetup(controller).build();

        mockMvc.perform(get("/activity")).andExpect(status().isOk());
    }
}
