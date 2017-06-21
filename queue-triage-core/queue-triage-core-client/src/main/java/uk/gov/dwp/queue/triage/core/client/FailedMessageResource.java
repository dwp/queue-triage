package uk.gov.dwp.queue.triage.core.client;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.*;

@Consumes("application/json")
@Produces("application/json")
@Path("/failed-message")
public interface FailedMessageResource {

    @GET
    @Path("/{failedMessageId}")
    FailedMessageResponse getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessgeId);
}
