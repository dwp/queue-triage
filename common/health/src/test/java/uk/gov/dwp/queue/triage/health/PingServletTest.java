package uk.gov.dwp.queue.triage.health;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PingServletTest {

    private final PingResponseWriter responseWriter = mock(PingResponseWriter.class);
    private final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    private final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    private final PrintWriter printWriter = mock(PrintWriter.class);

    private final PingServlet underTest = new PingServlet(responseWriter);

    @Test
    public void name() throws Exception {
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        underTest.doGet(httpServletRequest, httpServletResponse);

        verify(responseWriter).write(printWriter);
        verifyZeroInteractions(httpServletRequest);
    }
}