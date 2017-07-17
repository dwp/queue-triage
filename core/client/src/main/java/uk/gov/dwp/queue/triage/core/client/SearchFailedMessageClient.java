package uk.gov.dwp.queue.triage.core.client;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/failed-message")
public interface SearchFailedMessageClient {

    @GET
    @Path("/{failedMessageId}")
    FailedMessageResponse getFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);

    @POST
    @Path("/search")
    Collection<SearchFailedMessageResponse> search(SearchFailedMessageRequest request);
}
