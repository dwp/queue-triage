package uk.gov.dwp.queue.triage.web.server.api;

import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.fromString;

@Path("api/failed-messages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class FailedMessageChangeResource {

    private final LabelFailedMessageClient labelFailedMessageClient;
    private final LabelExtractor labelExtractor;

    public FailedMessageChangeResource(LabelFailedMessageClient labelFailedMessageClient,
                                       LabelExtractor labelExtractor) {
        this.labelFailedMessageClient = labelFailedMessageClient;
        this.labelExtractor = labelExtractor;
    }

    @POST
    @Path("/labels")
    public String updateLabelsOnFailedMessages(LabelRequest request) {
        request.getChanges()
                .forEach(change -> labelFailedMessageClient.setLabels(
                        fromString(change.getRecid()),
                        labelExtractor.extractLabels(change.getLabels())));
        return "{ 'status': 'success' }";
    }

}
