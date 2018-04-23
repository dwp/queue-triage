package uk.gov.dwp.queue.triage.web.server.api.status;

import uk.gov.dwp.queue.triage.core.client.status.FailedMessageStatusHistoryClient;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.server.w2ui.W2UIResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("api/failed-messages/status-history/{failedMessageId}")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class StatusHistoryResource {

    private final FailedMessageStatusHistoryClient failedMessageStatusHistoryClient;

    public StatusHistoryResource(FailedMessageStatusHistoryClient failedMessageStatusHistoryClient) {
        this.failedMessageStatusHistoryClient = failedMessageStatusHistoryClient;
    }

    @POST
    public W2UIResponse<StatusHistoryListItem> statusHistory(@PathParam("failedMessageId") FailedMessageId failedMessageId) {
        return W2UIResponse.success(failedMessageStatusHistoryClient.getStatusHistory(failedMessageId)
                        .stream()
                        .map(StatusHistoryListItem::new)
                        .collect(Collectors.toList())
        );
    }

}
