package uk.gov.dwp.queue.triage.core.client.resend;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api(value = "Resend")
@Path("/resend")
@Produces(APPLICATION_JSON)
public interface ResendFailedMessageClient {

    @ApiOperation("Mark a FailedMessage with given FailedMessageId ready for resending")
    @PUT
    @Path("/{failedMessageId}")
    void resendFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
