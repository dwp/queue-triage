package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.RESEND;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequestMatcher.aStatusUpdateRequest;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class ResendFailedMessageActionTest {

    private static final Duration TEN_SECONDS = Duration.ofSeconds(10);
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final ArgumentCaptor<StatusUpdateRequest> statusUpdateRequest = ArgumentCaptor.forClass(StatusUpdateRequest.class);

    private ObjectMapper objectMapper;


    @Before
    public void setUp() {
        final JacksonConfiguration jacksonConfiguration = new JacksonConfiguration();
        objectMapper = JacksonConfiguration.defaultObjectMapper().setInjectableValues(
                jacksonConfiguration.jacksonInjectableValues().addValue(FailedMessageService.class, failedMessageService)
        );
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);
    }

    @Test
    public void jsonCanBeDeserialised() throws IOException {
        final FailedMessageAction failedMessageAction = objectMapper.readValue("{ \"_action\": \"resend\", \"resendDelay\": \"PT10S\" }", FailedMessageAction.class);

        assertThat(failedMessageAction, reflectionEquals(new ResendFailedMessageAction(TEN_SECONDS, failedMessageService)));
    }

    @Test
    public void jsonCanBeDeserialisedWithNullResendDelay() throws IOException {
        final FailedMessageAction failedMessageAction = objectMapper.readValue("{ \"_action\": \"resend\", \"resendDelay\": null }", FailedMessageAction.class);

        assertThat(failedMessageAction, reflectionEquals(new ResendFailedMessageAction(null, failedMessageService)));
    }

    @Test
    public void resendFailedMessageActionWithNullDelay() {
        ResendFailedMessageAction underTest = resendFailedMessageActionWithFixedClock(null);

        underTest.accept(failedMessage);

        verify(failedMessageService).update(eq(FAILED_MESSAGE_ID), statusUpdateRequest.capture());
        assertThat(statusUpdateRequest.getValue(), aStatusUpdateRequest(RESEND).withUpdatedDateTime(FIXED_CLOCK.instant()));
    }

    @Test
    public void resendFailedMessageActionWithGivenDelay() {
        ResendFailedMessageAction underTest = resendFailedMessageActionWithFixedClock(Duration.ofSeconds(10));

        underTest.accept(failedMessage);

        verify(failedMessageService).update(eq(FAILED_MESSAGE_ID), statusUpdateRequest.capture());
        assertThat(statusUpdateRequest.getValue(), aStatusUpdateRequest(RESEND).withUpdatedDateTime(FIXED_CLOCK.instant().plusSeconds(10)));
    }

    @Test
    public void testToString() {
        ResendFailedMessageAction underTest = resendFailedMessageActionWithFixedClock(Duration.ofDays(2).plusMinutes(15).plusSeconds(10).plusMillis(5));

        assertThat(underTest.toString(), Matchers.is("resend in 48:15:10.005"));
    }

    private ResendFailedMessageAction resendFailedMessageActionWithFixedClock(final Duration resendDelay) {
        return new ResendFailedMessageAction(resendDelay, failedMessageService, FIXED_CLOCK);
    }
}