package uk.gov.dwp.queue.triage.web.server.api.resend;

import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.fromString;

@Path("api/failed-messages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ResendFailedMessageResource {

    private final ResendFailedMessageClient resendFailedMessageClient;

    public ResendFailedMessageResource(ResendFailedMessageClient resendFailedMessageClient) {
        this.resendFailedMessageClient = resendFailedMessageClient;
    }

    @POST
    @Path("/resend")
    public String resendFailedMessages(ResendRequest request) {
        request.getSelected()
                .forEach(recid -> resendFailedMessageClient.resendFailedMessage(fromString(recid)));
        return "{ 'status': 'success' }";
    }
}
