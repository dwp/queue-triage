package uk.gov.dwp.queue.triage.web.server.list;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Path("/failed-messages")
public class FailedMessageListController {

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_HTML)
    public FailedMessageListPage getFailedMessages() {
        return new FailedMessageListPage();
    }
}
