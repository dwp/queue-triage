package uk.gov.dwp.queue.triage.core.client;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Consumes("application/json")
@Produces("application/json")
@Path("/failed-message")
public interface SearchFailedMessageClient {

    @GET
    @Path("/{failedMessageId}")
    FailedMessageResponse getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);

    @GET
    @Path("/{broker}/count")
    long getNumberOfFailedMessages(@PathParam("broker") String broker);
}
