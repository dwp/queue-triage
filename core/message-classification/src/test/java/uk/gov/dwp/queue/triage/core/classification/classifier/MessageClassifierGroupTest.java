package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.action.LabelMessageAction;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.Builder;
import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.copyClassifierCollection;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class MessageClassifierGroupTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private MessageClassificationContext context;
    @Mock
    private MessageClassificationOutcome classifier1Outcome;
    @Mock
    private MessageClassificationOutcome classifier2Outcome;
    @Mock
    private MessageClassificationOutcome finalOutcome;
    @Mock
    private MessageClassifier messageClassifier1;
    @Mock
    private MessageClassifier messageClassifier2;

    private MessageClassifier underTest;

    @Before
    public void setUp() {
        underTest = newClassifierCollection().withClassifier(messageClassifier1).withClassifier(messageClassifier2).build();
    }

    @Test
    public void messageClassifiersExist() {
        underTest = newClassifierCollection().build();
        when(context.notMatched(new BooleanPredicate(false))).thenReturn(finalOutcome);

        assertThat(underTest.classify(context), is(finalOutcome));

        verify(context).notMatched(new BooleanPredicate(false));
        verifyNoMoreInteractions(context);
    }

    @Test
    public void noClassifiersMatch() {
        when(messageClassifier1.classify(context)).thenReturn(classifier1Outcome);
        when(messageClassifier2.classify(context)).thenReturn(classifier2Outcome);
        when(classifier1Outcome.isMatched()).thenReturn(false);
        when(finalOutcome.isMatched()).thenReturn(false);
        when(classifier1Outcome.or(classifier2Outcome)).thenReturn(finalOutcome);

        assertThat(underTest.classify(context), is(finalOutcome));

        verify(messageClassifier1).classify(context);
        verify(messageClassifier2).classify(context);
        verify(classifier1Outcome).or(classifier2Outcome);
    }

    @Test
    public void firstClassifierMatches() {
        when(messageClassifier1.classify(context)).thenReturn(classifier1Outcome);
        when(classifier1Outcome.isMatched()).thenReturn(true);

        assertThat(underTest.classify(context), is(this.classifier1Outcome));

        verify(messageClassifier1).classify(context);
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
        final ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();
        objectMapper.registerSubtypes(DoNothingMessageClassifier.class);

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

    @Test
    public void twoExecutingMessageClassifiers() {
        final MessageClassifierGroup classifier = newClassifierCollection()
                .withClassifier(new ExecutingMessageClassifier(new BooleanPredicate(false), new LabelMessageAction("for", null)))
                .withClassifier(new ExecutingMessageClassifier(new BooleanPredicate(true), new LabelMessageAction("bar", null)))
                .build();

        MessageClassificationOutcome classify = classifier.classify(new MessageClassificationContext(failedMessage));

        assertEquals("matched = true, ( false [false] OR true [true] )", classify.getDescription());

    }
    @Test
    public void anExecutingMessageClassifierThenDelegatingMessageClassifier() {
        final MessageClassifierGroup classifier = newClassifierCollection()
                .withClassifier(new ExecutingMessageClassifier(new BooleanPredicate(false), new LabelMessageAction("for", null)))
                .withClassifier(new DelegatingMessageClassifier(new BooleanPredicate(true), new ExecutingMessageClassifier(new BooleanPredicate(true), new LabelMessageAction("bar", null))))
                .build();

        MessageClassificationOutcome classify = classifier.classify(new MessageClassificationContext(failedMessage));

        assertEquals("matched = true, ( false [false] OR ( true [true] AND true [true] ) )", classify.getDescription());

    }

    @Test
    public void testToString() {
        when(messageClassifier1.toString()).thenReturn("classifier1");
        when(messageClassifier2.toString()).thenReturn("classifier2");

        final MessageClassifierGroup underTest = newClassifierCollection().withClassifier(messageClassifier1).withClassifier(messageClassifier2).build();

        assertThat(underTest.toString(), is("classifier1 OR classifier2"));
    }

    @JsonTypeName("doNothing")
    private static class DoNothingMessageClassifier implements MessageClassifier {

        @Override
        public MessageClassificationOutcome classify(MessageClassificationContext context) {
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