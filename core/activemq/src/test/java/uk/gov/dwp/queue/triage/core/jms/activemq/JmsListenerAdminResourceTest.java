package uk.gov.dwp.queue.triage.core.jms.activemq;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.stubbing.defaultanswers.GloballyConfiguredAnswer;
import org.mockito.internal.stubbing.defaultanswers.ReturnsDeepStubs;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JmsListenerAdminResourceTest {

    private final DefaultMessageListenerContainer defaultMessageListenerContainer = mock(DefaultMessageListenerContainer.class);
    private final JmsListenerAdminResource underTest = new JmsListenerAdminResource(
            singletonMap("internal-broker", defaultMessageListenerContainer)
    );

    static {
        System.setProperty("javax.ws.rs.ext.RuntimeDelegate", StubRuntimeDelegate.class.getName());
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
    public void stopDefaultMessageListenerContainer() throws Exception {
        underTest.stop("internal-broker");

        verify(defaultMessageListenerContainer).stop();
    }

    public static class StubRuntimeDelegate extends RuntimeDelegate {

        @Override
        public UriBuilder createUriBuilder() {
            return null;
        }

        @Override
        public Response.ResponseBuilder createResponseBuilder() {
            Response.ResponseBuilder responseBuilder = mock(Response.ResponseBuilder.class);
            when(responseBuilder.status((Response.StatusType)Response.Status.NOT_FOUND)).thenReturn(responseBuilder);
            Response response = mock(Response.class);
            when(responseBuilder.build()).thenReturn(response);
            when(response.getStatusInfo()).thenReturn(Response.Status.NOT_FOUND);
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