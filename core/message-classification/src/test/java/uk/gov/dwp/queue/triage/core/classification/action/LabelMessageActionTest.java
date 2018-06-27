package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageLabelService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LabelMessageActionTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper(new InjectableValues.Std());

    private final FailedMessageLabelService failedMessageLabelService = mock(FailedMessageLabelService.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    @Before
    public void setup() {
        objectMapper.setInjectableValues(
                new InjectableValues.Std()
                        .addValue(FailedMessageLabelService.class, failedMessageLabelService)
        );
    }

    @Test
    public void canSerialiseAndDeserialiseAction() throws Exception {
        LabelMessageAction underTest = objectMapper.readValue(
                objectMapper.writeValueAsString(new LabelMessageAction("foo", failedMessageLabelService)),
                LabelMessageAction.class
        );
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);

        underTest.accept(failedMessage);

        verify(failedMessageLabelService).addLabel(FAILED_MESSAGE_ID, "foo");
    }

    @Test
    public void testToString() {
        LabelMessageAction underTest = new LabelMessageAction("foo", failedMessageLabelService);

        assertThat(underTest.toString(), Matchers.is("set label = 'foo'"));
    }
}