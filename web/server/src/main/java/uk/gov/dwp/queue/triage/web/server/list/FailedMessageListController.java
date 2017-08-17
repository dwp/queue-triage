package uk.gov.dwp.queue.triage.web.server.list;

import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.server.w2ui.BaseW2UIRequest;
import uk.gov.dwp.queue.triage.web.server.w2ui.W2UIResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;

@Path("/failed-messages")
public class FailedMessageListController {

    private final SearchFailedMessageClient searchFailedMessageClient;
    private final FailedMessageListItemAdapter failedMessageListItemAdapter;

    public FailedMessageListController(SearchFailedMessageClient searchFailedMessageClient,
                                       FailedMessageListItemAdapter failedMessageListItemAdapter) {
        this.searchFailedMessageClient = searchFailedMessageClient;
        this.failedMessageListItemAdapter = failedMessageListItemAdapter;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(TEXT_HTML)
    public FailedMessageListPage getFailedMessages() {
        return new FailedMessageListPage();
    }

    @POST
    @Path("/data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public W2UIResponse<FailedMessageListItem> getData(BaseW2UIRequest request) {
        LoggerFactory.getLogger(FailedMessageListController.class).info("Getting data");
        Collection<SearchFailedMessageResponse> failedMessages = searchFailedMessageClient
                .search(newSearchFailedMessageRequest().build());
        return new W2UIResponse<>(
                "success",
                failedMessages.size(),
                failedMessageListItemAdapter.adapt(failedMessages)
        );
    }
}
