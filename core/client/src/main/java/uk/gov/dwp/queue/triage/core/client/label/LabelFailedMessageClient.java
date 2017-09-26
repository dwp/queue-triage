package uk.gov.dwp.queue.triage.core.client.label;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api(value = "Label")
@Path("/failed-message/label")
public interface LabelFailedMessageClient {

    @ApiOperation("Add a label to a FailedMessage")
    @PUT
    @Path("/{failedMessageId}/{label}")
    void addLabel(@PathParam("failedMessageId") FailedMessageId failedMessageId, @PathParam("label") String label);

    @ApiOperation("Replace the labels on a given FailedMessage")
    @PUT
    @Path("/{failedMessageId}")
    @Consumes(APPLICATION_JSON)
    void setLabels(@PathParam("failedMessageId") FailedMessageId failedMessageId, Set<String> labels);

    @ApiOperation("Remove a label from a FailedMessage")
    @DELETE
    @Path("/{failedMessageId}/{label}")
    void removeLabel(@PathParam("failedMessageId") FailedMessageId failedMessageId, @PathParam("label") String label);
}
