package uk.gov.dwp.queue.triage.core.client.delete;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Api(value = "Delete")
@Path("/failed-message")
public interface DeleteFailedMessageClient {

    @ApiOperation("Remove a FailedMessage with a given FailedMessageId")
    @DELETE
    @Path("/{failedMessageId}")
    void deleteFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
