package uk.gov.dwp.queue.triage.core.jms.activemq.browser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

public class QueueBrowserServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private BrowserCallback<Long> browserCallback;

    private QueueBrowserService<Long> underTest;

    @Before
    public void setUp() {
        underTest = new QueueBrowserService<>(browserCallback, jmsTemplate, "some-broker", "some-queue");
    }

    @Test
    public void browseDelegatesToJmsTemplate() {
        underTest.browse();

        verify(jmsTemplate).browse("some-queue", browserCallback);
    }

    @Test
    public void brokerNameIsAvailable() {
        assertThat(underTest.getBrokerName(), is("some-broker"));
    }
}