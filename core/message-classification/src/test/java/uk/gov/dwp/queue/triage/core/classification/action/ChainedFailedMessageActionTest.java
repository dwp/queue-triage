package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ChainedFailedMessageActionTest {

    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessageAction failedMessageAction1 = mock(FailedMessageAction.class);
    private final FailedMessageAction failedMessageAction2 = mock(FailedMessageAction.class);
    private final CustomFailedMessageAction customFailedMessageAction = new CustomFailedMessageAction();

    @Test
    public void allActionsInTheListAreExecuted() {
        ChainedFailedMessageAction underTest = new ChainedFailedMessageAction(Arrays.asList(
                failedMessageAction1, failedMessageAction2
        ));

        underTest.accept(failedMessage);

        verify(failedMessageAction1).accept(failedMessage);
        verify(failedMessageAction2).accept(failedMessage);
    }

    @Test
    public void canSerialiseAndDeserialiseAction() throws Exception {
        objectMapper.registerSubtypes(CustomFailedMessageAction.class);

        FailedMessageAction underTest = objectMapper.readValue(
                objectMapper.writeValueAsString(customFailedMessageAction),
                FailedMessageAction.class
        );
        assertThat(underTest.getClass(), equalTo(CustomFailedMessageAction.class));
    }

    @JsonTypeName("custom")
    public static class CustomFailedMessageAction implements FailedMessageAction {

        @JsonCreator
        public CustomFailedMessageAction() {}

        @Override
        public void accept(FailedMessage failedMessage) {
            // Do Nothing
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this);
            return (obj instanceof ChainedFailedMessageAction);
        }
    }
}