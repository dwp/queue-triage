package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChainedFailedMessageActionTest {

    private final ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();

    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessageAction failedMessageAction1 = mock(FailedMessageAction.class);
    private final FailedMessageAction failedMessageAction2 = mock(FailedMessageAction.class);

    @Before
    public void setUp() {
        objectMapper.registerSubtypes(CustomFailedMessageAction.class);
    }

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
                objectMapper.writeValueAsString(
                        new ChainedFailedMessageAction(Collections.singletonList(new CustomFailedMessageAction()))),
                        FailedMessageAction.class
        );
        assertThat(underTest.getClass(), equalTo(ChainedFailedMessageAction.class));
    }

    @Test
    public void testToString() {
        when(failedMessageAction1.toString()).thenReturn("action1");
        when(failedMessageAction2.toString()).thenReturn("action2");
        ChainedFailedMessageAction underTest = new ChainedFailedMessageAction(Arrays.asList(
                failedMessageAction1, failedMessageAction2
        ));

        assertThat(underTest.toString(), Matchers.is("action1 AND action2"));
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