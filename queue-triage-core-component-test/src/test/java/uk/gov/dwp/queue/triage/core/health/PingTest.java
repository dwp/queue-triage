package uk.gov.dwp.queue.triage.core.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PingTest {

    @LocalServerPort
    private int port;

    @Test
    public void name() throws Exception {
        System.out.println(port);
    }
}
