package uk.gov.dwp.queue.triage.health;

import org.junit.Test;

import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PingResponseWriterTest {

    private PingResponseWriter underTest = new PingResponseWriter();
    private PrintWriter printWriter = mock(PrintWriter.class);

    @Test
    public void writeResponse() throws Exception {
        underTest.write(printWriter);

        verify(printWriter).write("pong");
    }
}