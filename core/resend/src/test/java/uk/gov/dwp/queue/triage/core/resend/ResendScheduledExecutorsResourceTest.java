package uk.gov.dwp.queue.triage.core.resend;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResendScheduledExecutorsResourceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final ResendScheduledExecutorService resendScheduledExecutorService = mock(ResendScheduledExecutorService.class);

    private final ResendScheduledExecutorsResource underTest = new ResendScheduledExecutorsResource(
            new HashMap<String, ResendScheduledExecutorService>() {{
                put("internal-broker", resendScheduledExecutorService);
            }}
    );

    static {
        System.setProperty("javax.ws.rs.ext.RuntimeDelegate", StubRuntimeDelegate.class.getName());
    }

    @Test(expected = BadRequestException.class)
    public void startThrowsABadRequestExceptionIfBrokerIsUnknown() {
        underTest.start("unknown-broker");
    }

    @Test(expected = BadRequestException.class)
    public void executeThrowsABadRequestExceptionIfBrokerIsUnknown() {
        underTest.execute("unknown-broker");
    }

    @Test(expected = BadRequestException.class)
    public void pauseThrowsABadRequestExceptionIfBrokerIsUnknown() {
        underTest.pause("unknown-broker");
    }

    @Test
    public void startResendScheduledExecutorService() {
        underTest.start("internal-broker");

        verify(resendScheduledExecutorService).start();
    }

    @Test
    public void executeResendScheduledExecutorService() {
        underTest.execute("internal-broker");

        verify(resendScheduledExecutorService).execute();
    }

    @Test
    public void pauseResendScheduledExecutorService() {
        underTest.pause("internal-broker");

        verify(resendScheduledExecutorService).pause();
    }

    public static class StubRuntimeDelegate extends RuntimeDelegate {

        @Override
        public UriBuilder createUriBuilder() {
            return null;
        }

        @Override
        public Response.ResponseBuilder createResponseBuilder() {
            Response.ResponseBuilder responseBuilder = mock(Response.ResponseBuilder.class);
            when(responseBuilder.status((Response.StatusType)Response.Status.BAD_REQUEST)).thenReturn(responseBuilder);
            Response response = mock(Response.class);
            when(responseBuilder.build()).thenReturn(response);
            when(response.getStatusInfo()).thenReturn(Response.Status.BAD_REQUEST);
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
    }


}