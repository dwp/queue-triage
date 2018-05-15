package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.update.PropertiesUpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class PropertiesUpdateRequestAdapterTest {

    private static final String SOME_KEY = "some-key";
    private static final String SOME_VALUE = "some-value";
    private final FailedMessageBuilder failedMessageBuilder = mock(FailedMessageBuilder.class);
    private final PropertiesUpdateRequestAdapter underTest = new PropertiesUpdateRequestAdapter();

    @Test
    public void propertiesAreDeleted() {
        underTest.adapt(new PropertiesUpdateRequest(Collections.singleton(SOME_KEY), Collections.emptyMap()), failedMessageBuilder);

        verify(failedMessageBuilder).removeProperty(SOME_KEY);
        verifyNoMoreInteractions(failedMessageBuilder);
    }

    @Test
    public void propertiesAreUpdated() {
        underTest.adapt(new PropertiesUpdateRequest(Collections.emptySet(), Collections.singletonMap(SOME_KEY, SOME_VALUE)), failedMessageBuilder);

        verify(failedMessageBuilder).withProperty(SOME_KEY, SOME_VALUE);
        verifyNoMoreInteractions(failedMessageBuilder);
    }
}