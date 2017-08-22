package uk.gov.dwp.queue.triage.core.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api(value = "search")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/failed-message")
public interface SearchFailedMessageClient {

    @ApiOperation("Find a FailedMessage by FailedMessageId")
    @GET
    @Path("/{failedMessageId}")
    FailedMessageResponse getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);

    @ApiOperation("Search for FailedMessages")
    @POST
    @Path("/search")
    Collection<SearchFailedMessageResponse> search(@NotNull SearchFailedMessageRequest request);
}
