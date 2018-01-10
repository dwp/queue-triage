package uk.gov.dwp.queue.triage.core.client.status;

import io.swagger.annotations.Api;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api(value = "status-history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/failed-message/${failedMessageId}/status-history")
public interface FailedMessageStatusHistoryClient {

    @GET
    List<StatusHistoryResponse> getStatusHistory(@PathParam("failedMessageId") FailedMessageId failedMessageId);
}
