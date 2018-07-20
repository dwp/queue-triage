package uk.gov.dwp.queue.triage.core.classification.classifier;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.AndPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.OrPredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageClassificationOutcomeTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private Description<String> description;
    @Mock
    private FailedMessagePredicate matchedPredicate;
    @Mock
    private FailedMessagePredicate notMatchedPredicate;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private FailedMessageAction failedMessageAction;

    @Test
    public void constructMatchedOutcome() {
        final MessageClassificationOutcome underTest = matched();

        assertThat(underTest.isMatched(), is(true));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(failedMessageAction));
        assertThat(underTest.getFailedMessagePredicate(), is(matchedPredicate));

        underTest.execute();

        verify(failedMessageAction).accept(failedMessage);
    }

    @Test
    public void constructUnmatchedOutcome() {
        final MessageClassificationOutcome underTest = notMatched();

        assertThat(underTest.isMatched(), is(false));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(nullValue()));
        assertThat(underTest.getFailedMessagePredicate(), is(notMatchedPredicate));

        // Ensure that a NullPointerException is not thrown
        underTest.execute();
    }

    @Test
    public void getDefaultDescription() {
        when(matchedPredicate.describe(any(StringDescription.class))).thenAnswer((Answer<Description<String>>) invocation -> ((StringDescription)invocation.getArgument(0)).append("predicate1"));

        assertThat(matched().getDescription(), is(equalTo("matched = true, predicate1")));
    }


    @Test
    public void getDescription() {
        assertThat(matched().getDescription(description), is(notNullValue(Description.class)));
    }

    @Test
    public void notMatchedOrMatched() {
        final MessageClassificationOutcome underTest = notMatched().or(matched());

        assertThat(underTest.isMatched(), is(true));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(failedMessageAction));
        assertThat(underTest.getFailedMessagePredicate(), orPredicate(contains(notMatchedPredicate, matchedPredicate)));
    }

    @Test
    public void MatchedOrNotMatched() {
        final MessageClassificationOutcome underTest = matched().or(notMatched());

        assertThat(underTest.isMatched(), is(true));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(failedMessageAction));
        assertThat(underTest.getFailedMessagePredicate(), orPredicate(contains(matchedPredicate, notMatchedPredicate)));
    }

    @Test
    public void MatchedOrMatched() {
        final MessageClassificationOutcome underTest = matched().or(matched());

        assertThat(underTest.isMatched(), is(true));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(failedMessageAction));
        assertThat(underTest.getFailedMessagePredicate(), orPredicate(contains(matchedPredicate, matchedPredicate)));
    }

    @Test
    public void notMatchedOrNotMatched() {
        final MessageClassificationOutcome underTest = notMatched().or(notMatched());

        assertThat(underTest.isMatched(), is(false));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), nullValue());
        assertThat(underTest.getFailedMessagePredicate(), orPredicate(contains(notMatchedPredicate, notMatchedPredicate)));
    }

    @Test
    public void notMatchedAndMatched() {
        final MessageClassificationOutcome underTest = notMatched().and(matched());

        assertThat(underTest.isMatched(), is(false));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), nullValue());
        assertThat(underTest.getFailedMessagePredicate(), andPredicate(contains(notMatchedPredicate, matchedPredicate)));
    }

    @Test
    public void matchedAndNotMatched() {
        final MessageClassificationOutcome underTest = matched().and(notMatched());

        assertThat(underTest.isMatched(), is(false));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), nullValue());
        assertThat(underTest.getFailedMessagePredicate(), andPredicate(contains(matchedPredicate, notMatchedPredicate)));
    }

    @Test
    public void matchedAndMatched() {
        final MessageClassificationOutcome underTest = matched().and(matched());

        assertThat(underTest.isMatched(), is(true));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), is(failedMessageAction));
        assertThat(underTest.getFailedMessagePredicate(), andPredicate(contains(matchedPredicate, matchedPredicate)));
    }

    @Test
    public void notMatchedAndNotMatched() {
        final MessageClassificationOutcome underTest = notMatched().and(notMatched());

        assertThat(underTest.isMatched(), is(false));
        assertThat(underTest.getFailedMessage(), is(failedMessage));
        assertThat(underTest.getFailedMessageAction(), nullValue());
        assertThat(underTest.getFailedMessagePredicate(), andPredicate(contains(notMatchedPredicate, notMatchedPredicate)));
    }

    private TypeSafeMatcher<FailedMessagePredicate> orPredicate(Matcher<Iterable<? extends FailedMessagePredicate>> predicates) {
        return new TypeSafeMatcher<FailedMessagePredicate>() {
            @Override
            protected boolean matchesSafely(FailedMessagePredicate item) {
                return (item != null) && (OrPredicate.class.isInstance(item)) && predicates.matches(((OrPredicate)item).getPredicates());
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("predicates ").appendDescriptionOf(predicates);
            }
        };
    }

    private TypeSafeMatcher<FailedMessagePredicate> andPredicate(Matcher<Iterable<? extends FailedMessagePredicate>> predicates) {
        return new TypeSafeMatcher<FailedMessagePredicate>() {
            @Override
            protected boolean matchesSafely(FailedMessagePredicate item) {
                return (item != null) && (AndPredicate.class.isInstance(item)) && predicates.matches(((AndPredicate)item).getPredicates());
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("predicates ").appendDescriptionOf(predicates);
            }
        };
    }

    private Answer<Description> withDescription(String predicateDescription) {
        return invocation -> ((Description)invocation.getArgument(0)).append(predicateDescription);
    }


    private MessageClassificationOutcome notMatched() {
        return new MessageClassificationOutcome(false, notMatchedPredicate, failedMessage,null);
    }

    private MessageClassificationOutcome matched() {
        return new MessageClassificationOutcome(true, matchedPredicate, failedMessage, failedMessageAction);
    }

}