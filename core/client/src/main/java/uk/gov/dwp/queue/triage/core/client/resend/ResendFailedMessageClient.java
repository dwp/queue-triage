package uk.gov.dwp.queue.triage.core.client.resend;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/resend")
@Produces(APPLICATION_JSON)
public interface ResendFailedMessageClient {

    @PUT
    @Path("/${failedMessageId}")
    void resendFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
