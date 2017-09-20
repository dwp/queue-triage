package uk.gov.dwp.queue.triage.core.client.label;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Api(value = "Label")
@Path("/failed-message/label")
public interface LabelFailedMessageClient {

    @ApiOperation("Add a label to a FailedMessage")
    @PUT
    @Path("/{failedMessageId}/{label}")
    void addLabel(@PathParam("failedMessageId") FailedMessageId failedMessageId, @PathParam("label") String label);

    @ApiOperation("Remove a label from a FailedMessage")
    @DELETE
    @Path("/{failedMessageId}/{label}")
    void removeLabel(@PathParam("failedMessageId") FailedMessageId failedMessageId, @PathParam("label") String label);
}
