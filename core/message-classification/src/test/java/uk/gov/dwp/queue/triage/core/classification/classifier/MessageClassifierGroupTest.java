package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.Builder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.copyClassifierCollection;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class MessageClassifierGroupTest {


    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper(new InjectableValues.Std());

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock(answer = Answers.RETURNS_SELF)
    private Description<String> description;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private MessageClassificationOutcome<String> messageClassificationOutcome;
    @Mock
    private MessageClassifier messageClassifier1;
    @Mock
    private MessageClassifier messageClassifier2;

    private MessageClassifier underTest;

    @Before
    public void setUp() {
        underTest = newClassifierCollection().withClassifier(messageClassifier1).withClassifier(messageClassifier2).build();
        objectMapper.registerSubtypes(DoNothingMessageClassifier.class);
    }

    @Test
    public void noClassifiersMatch() {
        when(messageClassifier1.classify(failedMessage, description)).thenReturn(messageClassificationOutcome);
        when(messageClassifier2.classify(failedMessage, description)).thenReturn(messageClassificationOutcome);
        when(messageClassificationOutcome.isMatched()).thenReturn(false);

        final MessageClassificationOutcome outcome = underTest.classify(failedMessage, description);

        assertThat(outcome.isMatched(), is(false));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        verify(messageClassifier1).classify(failedMessage, description);
        verify(description, times(3)).append("( ");
        verify(description).append(" OR ");
        verify(description, times(3)).append(" )");
        verifyNoMoreInteractions(description);
        verify(messageClassifier2).classify(failedMessage, description);
        verify(messageClassificationOutcome, times(2)).isMatched();
    }

    @Test
    public void firstClassifierMatches() {
        when(messageClassifier1.classify(failedMessage, description)).thenReturn(messageClassificationOutcome);
        when(messageClassificationOutcome.isMatched()).thenReturn(true);

        final MessageClassificationOutcome outcome = underTest.classify(failedMessage, description);

        assertThat(outcome, is(messageClassificationOutcome));
        verify(description, times(2)).append("( ");
        verify(description, times(2)).append(" )");
        verifyZeroInteractions(messageClassifier2);
    }


    @Test
    public void buildClassifierCollection() {
        final MessageClassifierGroup messageClassifierGroup = newClassifierCollection()
                .withClassifier(messageClassifier1)
                .build();

        final Builder classifierCollectionBuilder = copyClassifierCollection(messageClassifierGroup)
                .withClassifier(messageClassifier2);

        assertThat(classifierCollectionBuilder.build().getClassifiers(), contains(messageClassifier1, messageClassifier2));
    }

    @Test
    public void canSerialiseAndDeserialiseMessageClassifier() throws IOException {
        underTest = newClassifierCollection()
                .withClassifier(new DoNothingMessageClassifier())
                .withClassifier(new DoNothingMessageClassifier())
                .build();
        final String json = objectMapper.writeValueAsString(underTest);

        assertThat(json, allOf(
                hasJsonPath("$._classifier", equalTo("collection")),
                hasJsonPath("$.classifiers[0]._classifier", equalTo("doNothing")),
                hasJsonPath("$.classifiers[1]._classifier", equalTo("doNothing"))
        ));

        assertThat(objectMapper.readValue(json, MessageClassifier.class), reflectionEquals(underTest));
    }

    @JsonTypeName("doNothing")
    private static class DoNothingMessageClassifier implements MessageClassifier {

        @Override
        public <T> MessageClassificationOutcome<T> classify(FailedMessage failedMessage, Description<T> description) {
            return null;
        }

        @Override
        public int hashCode() {
            return 32 * 17;
        }

        @Override
        public boolean equals(Object object) {
            return object == this || object instanceof DoNothingMessageClassifier;
        }
    }
}