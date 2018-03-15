package uk.gov.dwp.queue.triage.core.jms.activemq;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.dwp.queue.triage.core.jms.activemq.browser.QueueBrowserScheduledExecutorService;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageConsumerManagerResourceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final MessageConsumerManager defaultMessageListenerContainer = mock(MessageListenerManager.class);
    private final QueueBrowserScheduledExecutorService queueBrowserScheduledExecutorService = mock(QueueBrowserScheduledExecutorService.class);

    private final MessageConsumerManagerResource underTest = new MessageConsumerManagerResource(
            new MessageConsumerManagerRegistry()
                    .with("internal-broker", messageConsumerManager)
                    .with("readonly-broker", messageConsumerManager)

    );

    static {
        System.setProperty("javax.ws.rs.ext.RuntimeDelegate", StubRuntimeDelegate.class.getName());
    }

    @Before
    public void setUp() {
        StubRuntimeDelegate.setExpectedStatus(Response.Status.NOT_FOUND);
    }

    @Test(expected = NotFoundException.class)
    public void startThrowsANotFoundExceptionIfBrokerIsUnknown() throws Exception {
        underTest.start("unknown-broker");
    }

    @Test(expected = NotFoundException.class)
    public void stopThrowsANotFoundExceptionIfBrokerIsUnknown() throws Exception {
        underTest.stop("unknown-broker");
    }

    @Test
    public void startDefaultMessageListenerContainer() throws Exception {
        underTest.start("internal-broker");

        verify(defaultMessageListenerContainer).start();
    }

    @Test
    public void stopDefaultMessageListenerContainer() {
        underTest.stop("internal-broker");

        verify(defaultMessageListenerContainer).stop();
    }

    @Test(expected = NotFoundException.class)
    public void readMessagesThrowsANotFoundExceptionIfBrokerIsUnknown() {
        underTest.readMessages("unknown-broker");
    }

    @Test
    public void readMessagesThrowsAServerErrorExceptionIfBrokerIsNotReadOnly() {
        StubRuntimeDelegate.setExpectedStatus(Response.Status.NOT_IMPLEMENTED);
        expectedException.expect(ServerErrorException.class);
        expectedException.expectMessage("HTTP 501 Not Implemented");

        underTest.readMessages("internal-broker");
    }

    @Test
    public void readMessagesForAReadOnlyBroker() {
        underTest.readMessages("readonly-broker");

        verify(queueBrowserScheduledExecutorService).execute();
    }

    public static class StubRuntimeDelegate extends RuntimeDelegate {

        private static Response.Status expectedStatus;

        @Override
        public UriBuilder createUriBuilder() {
            return null;
        }

        @Override
        public Response.ResponseBuilder createResponseBuilder() {
            Response.ResponseBuilder responseBuilder = mock(Response.ResponseBuilder.class);
            when(responseBuilder.status((Response.StatusType) expectedStatus)).thenReturn(responseBuilder);
            Response response = mock(Response.class);
            when(responseBuilder.build()).thenReturn(response);
            when(response.getStatusInfo()).thenReturn(expectedStatus);
            return responseBuilder;
        }

        @Override
        public Variant.VariantListBuilder createVariantListBuilder() {
            return null;
        }

        @Override
        public <T> T createEndpoint(Application application, Class<T> aClass) throws IllegalArgumentException, UnsupportedOperationException {
            return null;
        }

        @Override
        public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> aClass) throws IllegalArgumentException {
            return null;
        }

        @Override
        public Link.Builder createLinkBuilder() {
            return null;
        }

        public static void setExpectedStatus(Response.Status status) {
            expectedStatus = status;
        }
    }
}