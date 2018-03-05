package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class OrPredicateTest {

    private final FailedMessagePredicate alwaysTruePredicate = new AlwaysTruePredicate();
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessagePredicate failedMessagePredicate1 = mock(FailedMessagePredicate.class);
    private final FailedMessagePredicate failedMessagePredicate2 = mock(FailedMessagePredicate.class);

    private final OrPredicate underTest = new OrPredicate(Arrays.asList(failedMessagePredicate1, failedMessagePredicate2));

    @Test
    public void shortCutEvaluationWhenFirstPredicateIsTrue() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(true);

        assertThat(underTest.test(failedMessage), is(true));

        verify(failedMessagePredicate1).test(failedMessage);
        verifyZeroInteractions(failedMessagePredicate2);
    }

    @Test
    public void bothPredicatesAreEvaluatedWhenFirstPredicateReturnsFalse() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(false);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(true);

        assertThat(underTest.test(failedMessage), is(true));

        verify(failedMessagePredicate1).test(failedMessage);
        verify(failedMessagePredicate2).test(failedMessage);
    }

    @Test
    public void allPredicatesReturnFalse() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(false);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(false);

        assertThat(underTest.test(failedMessage), is(false));

        verify(failedMessagePredicate1).test(failedMessage);
        verify(failedMessagePredicate2).test(failedMessage);
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();
        objectMapper.registerSubtypes(AlwaysTruePredicate.class);

        final OrPredicate underTest = objectMapper.readValue(
                objectMapper.writeValueAsString(new OrPredicate(singletonList(alwaysTruePredicate))),
                OrPredicate.class
        );

        assertThat(underTest.getPredicates(), contains(alwaysTruePredicate));
    }

    @JsonTypeName("alwaysTrue")
    public static class AlwaysTruePredicate implements FailedMessagePredicate {

        @Override
        public boolean test(FailedMessage failedMessage) {
            return true;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            return object instanceof AlwaysTruePredicate;
        }
    }
}