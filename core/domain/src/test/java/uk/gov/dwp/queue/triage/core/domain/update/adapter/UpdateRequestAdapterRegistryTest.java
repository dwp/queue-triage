package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

public class UpdateRequestAdapterRegistryTest {

    @Rule
    public MockitoRule mockitoJUnit = MockitoJUnit.rule();
    @Mock
    private UpdateRequestAdapter<UpdateRequest> defaultUpdateRequestAdapter;
    @Mock
    private UpdateRequestAdapter<ExampleUpdateRequest> exampleUpdateRequestAdapter;
    @Mock
    private UpdateRequest updateRequest;

    private UpdateRequestAdapterRegistry underTest;

    @Before
    public void setUp() {
        underTest = new UpdateRequestAdapterRegistry(defaultUpdateRequestAdapter);
    }

    @Test
    public void addNewAdapter() {
        underTest.addAdapter(ExampleUpdateRequest.class, exampleUpdateRequestAdapter);

        assertThat(underTest.getUpdateRequestAdapters(), hasEntry(equalTo(ExampleUpdateRequest.class), is(exampleUpdateRequestAdapter)));
    }

    @Test
    public void defaultAdapterIsReturnedWhenAdapterNotFoundForClass() {
        assertThat(underTest.getAdapter(updateRequest), is(defaultUpdateRequestAdapter));
    }

    @Test
    public void adapterAssociatedWithClassIsReturned() {
        assertThat(underTest.addAdapter(ExampleUpdateRequest.class, exampleUpdateRequestAdapter).getAdapter(new ExampleUpdateRequest()), is(exampleUpdateRequestAdapter));
    }

    private static class ExampleUpdateRequest implements UpdateRequest {

    }
}