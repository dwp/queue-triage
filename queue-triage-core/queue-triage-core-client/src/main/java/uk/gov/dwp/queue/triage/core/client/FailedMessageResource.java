package uk.gov.dwp.queue.triage.core.client;

import javax.ws.rs.*;

@Consumes("application/json")
@Produces("application/json")
@Path("/failed-message")
public interface FailedMessageResource {

    @GET
    @Path("/{failedMessageId}")
    FailedMessage getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessgeId);

    @POST
    void create(FailedMessage failedMessage);
}
