package uk.gov.dwp.queue.triage.web.server.search;

import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.server.list.FailedMessageListItem;
import uk.gov.dwp.queue.triage.web.server.w2ui.W2UIResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/failed-messages/search")
public class SearchFailedMessageController {

    private final SearchFailedMessageClient searchFailedMessageClient;
    private final SearchFailedMessageRequestAdapter searchFailedMessageRequestAdapter;
    private final FailedMessageListItemAdapter failedMessageListItemAdapter;

    public SearchFailedMessageController(SearchFailedMessageClient searchFailedMessageClient,
                                         SearchFailedMessageRequestAdapter searchFailedMessageRequestAdapter,
                                         FailedMessageListItemAdapter failedMessageListItemAdapter) {
        this.searchFailedMessageClient = searchFailedMessageClient;
        this.searchFailedMessageRequestAdapter = searchFailedMessageRequestAdapter;
        this.failedMessageListItemAdapter = failedMessageListItemAdapter;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public W2UIResponse<FailedMessageListItem> search(SearchW2UIRequest request) {
        Collection<SearchFailedMessageResponse> failedMessages = searchFailedMessageClient.search(
                searchFailedMessageRequestAdapter.adapt(request)
        );
        return W2UIResponse.success(failedMessageListItemAdapter.adapt(failedMessages));
    }

    @GET
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Path("/{failedMessageId}")
    public FailedMessageResponse getFailedMessageById(@PathParam("failedMessageId") FailedMessageId failedMessageId) {
        return searchFailedMessageClient.getFailedMessage(failedMessageId);
    }
}
