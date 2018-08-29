package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.format.BooleanFormatter;
import org.hamcrest.Matchers;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationOutcomeResponse;
import uk.gov.dwp.queue.triage.jgiven.ThenStage;

import static org.hamcrest.MatcherAssert.assertThat;

public class MessageClassificationThenStage extends ThenStage<MessageClassificationThenStage> {

    @ExpectedScenarioState
    private MessageClassificationOutcomeResponse messageClassificationOutcome;

    public MessageClassificationThenStage theFailedMessageWas$Matched(@Format(value = BooleanFormatter.class, args = {"", " not"}) boolean matched) {
        assertThat(messageClassificationOutcome.isMatched(), Matchers.is(matched));
        return self();
    }

    public MessageClassificationThenStage producedDescription$(String description) {
        assertThat(messageClassificationOutcome.getDescription(), Matchers.is(description));
        return self();
    }

    public MessageClassificationThenStage failedMessageActionWas$(String actionName) {
        assertThat(messageClassificationOutcome.getAction(), Matchers.is(actionName));
        return self();
    }
}
