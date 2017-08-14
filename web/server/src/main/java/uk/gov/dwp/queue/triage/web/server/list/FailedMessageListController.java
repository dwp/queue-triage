package uk.gov.dwp.queue.triage.web.server.list;

import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.server.w2ui.BaseW2UIRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;

@Path("/failed-messages")
@Produces(TEXT_HTML)
public class FailedMessageListController {

    private final SearchFailedMessageClient searchFailedMessageClient;
    private final FailedMessagesJsonSerializer failedMessagesJsonSerializer;

    public FailedMessageListController(SearchFailedMessageClient searchFailedMessageClient,
                                       FailedMessagesJsonSerializer failedMessagesJsonSerializer) {
        this.searchFailedMessageClient = searchFailedMessageClient;
        this.failedMessagesJsonSerializer = failedMessagesJsonSerializer;
    }

    @GET
    public FailedMessageListPage getFailedMessages() {
        return new FailedMessageListPage(Collections.emptyList(), failedMessagesJsonSerializer);
    }

//    @GET
//    @Path("/{brokerName}")
//    public FailedMessageListPage getFailedMessages(@PathParam("brokerName") String brokerName) {
//        return getFailedMessageListPage(aSearchRequest().withBrokerName(of(brokerName)));
//    }
//
//    @GET
//    @Path("/{brokerName}/{destination}")
//    public FailedMessageListPage getFailedMessages(@PathParam("brokerName") String brokerName, @PathParam("destination") String destinationName) {
//        return getFailedMessageListPage(aSearchRequest().withBrokerName(of(brokerName)).withQueueName(of(destinationName)));
//    }
//
//    @POST
//    @Path("/delete")
//    @Consumes("application/json")
//    public String deleteFailedMessages(BaseW2UIRequest request) {
//        failedMessageResource.delete(toFailedMessageIds(request));
//        return "{ 'status': 'success' }";
//    }

//    private List<FailedMessageId> toFailedMessageIds(BaseW2UIRequest request) {
//        return request.getSelected().stream().map(FailedMessageId::fromString).collect(toList());
//    }

//    @POST
//    @Path("/labels")
//    @Consumes("application/json")
//    public String updateLabelsOnFailedMessages(LabelRequest request) {
//        request.getChanges()
//                .forEach(change -> failedMessageResource.setLabels(fromString(change.getRecid()), extractLabels(change)));
//        return "{ 'status': 'success' }";
//    }
//
//    private Set<String> extractLabels(LabelRequest.Change change) {
//        return Stream.of(split(change.getLabels(), ","))
//                .map(String::trim)
//                .collect(toSet());
//    }

//    @POST
//    @Path("/resend")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public String resendFailedMessages(BaseW2UIRequest request) {
//        // Resend
//        return  "{ 'status': 'success' }";
//    }

    @POST
    @Path("/data")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getData(BaseW2UIRequest request) {
        Collection<SearchFailedMessageResponse> failedMessages = searchFailedMessageClient.search(newSearchFailedMessageRequest().withBroker("broker-name").build());
        return getJson(failedMessages);
    }

    private String getJson(Collection<SearchFailedMessageResponse> failedMessages) {
        return new StringBuilder("{ ")
                .append("'status': 'success'")
                .append(", 'total': ").append(failedMessages.size())
                .append(", 'records': ").append(failedMessagesJsonSerializer.asJson(failedMessages))
                .append(" }")
                .toString();
    }
}
