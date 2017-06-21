package uk.gov.dwp.queue.triage.core.health;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PingServlet extends HttpServlet {

    private PingResponseWriter responseWriter;

    public PingServlet(PingResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        responseWriter.write(resp.getWriter());
    }
}
