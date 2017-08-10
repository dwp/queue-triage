package uk.gov.dwp.queue.triage.core.client.delete;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/failed-message")
public interface DeleteFailedMessageClient {

    @DELETE
    @Path("/{failedMessageId}")
    void deleteFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
