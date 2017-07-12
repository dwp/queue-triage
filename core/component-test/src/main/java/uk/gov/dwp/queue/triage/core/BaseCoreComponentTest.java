package uk.gov.dwp.queue.triage.core;

import com.mongodb.MongoClient;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EnableJGiven
@SpringBootTest(webEnvironment= RANDOM_PORT)
@ActiveProfiles(value = "component-test")
public class BaseCoreComponentTest<STAGE> extends SimpleSpringRuleScenarioTest<STAGE> {

//    static {
//        if (StringUtils.isEmpty(System.getProperty(ACTIVE_PROFILES_PROPERTY_NAME))) {
//            System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, "component-test");
//        }
//    }

    @Autowired
    protected MongoClient mongoClient;

}
