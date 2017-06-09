package uk.gov.dwp.queue.triage.core.health;

import java.io.PrintWriter;

public class PingResponseWriter {

    public void write(PrintWriter writer) {
        writer.write("pong");
    }
}
