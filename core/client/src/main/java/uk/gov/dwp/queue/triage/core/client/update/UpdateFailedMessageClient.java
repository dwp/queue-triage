package uk.gov.dwp.queue.triage.core.client.update;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api(value = "Update")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/failed-message/update/{failedMessageId}")
public interface UpdateFailedMessageClient {

    @ApiOperation("Update an existing FailedMessage")
    @PUT
    void update(@PathParam("failedMessageId") FailedMessageId failedMessageId, FailedMessageUpdateRequest failedMessageUpdateRequest);
}
