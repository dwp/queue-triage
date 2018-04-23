package uk.gov.dwp.queue.triage.web.common.mustache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.web.common.Page;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import static javax.ws.rs.core.Response.serverError;

@Provider
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML})
public class PageMessageBodyWriter implements MessageBodyWriter<Page> {
    private static final Logger logger = LoggerFactory.getLogger(PageMessageBodyWriter.class);
    private static final String TEMPLATE_ERROR_MSG =
            "<html>" +
                    "<head><title>Template Error</title></head>" +
                    "<body><h1>Template Error</h1><p>Something went wrong rendering the page</p></body>" +
                    "</html>";

    @Context
    @SuppressWarnings("UnusedDeclaration")
    private HttpHeaders headers;

    private final MustachePageRenderer renderer;

    public PageMessageBodyWriter(MustachePageRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Page.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Page t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Page t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException {
        try {
            renderer.render(t, entityStream);
        } catch (Exception e) {
            logger.debug("Template Error", e);
            throw new WebApplicationException(serverError()
                    .type(MediaType.TEXT_HTML_TYPE)
                    .entity(TEMPLATE_ERROR_MSG)
                    .build());
        }
    }

    private Locale detectLocale(HttpHeaders headers) {
        final List<Locale> languages = headers.getAcceptableLanguages();
        for (Locale locale : languages) {
            if (!locale.toString().contains("*")) {
                return locale;
            }
        }
        return Locale.getDefault();
    }
}
