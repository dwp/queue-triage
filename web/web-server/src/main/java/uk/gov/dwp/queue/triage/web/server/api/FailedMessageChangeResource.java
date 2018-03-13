package uk.gov.dwp.queue.triage.web.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageChangeResource.class);

    private final LabelFailedMessageClient labelFailedMessageClient;
    private final LabelExtractor labelExtractor;
    private final DeleteFailedMessageClient deleteFailedMessageClient;

    public FailedMessageChangeResource(LabelFailedMessageClient labelFailedMessageClient,
                                       LabelExtractor labelExtractor,
                                       DeleteFailedMessageClient deleteFailedMessageClient) {
        this.labelFailedMessageClient = labelFailedMessageClient;
        this.labelExtractor = labelExtractor;
        this.deleteFailedMessageClient = deleteFailedMessageClient;
    }

    @POST
    @Path("/labels")
    public String updateLabelsOnFailedMessages(LabelRequest request) {
        LOGGER.info("Updating the labels on {} failedMessages", request.getChanges().size());
        request.getChanges()
                .forEach(change -> {
                    LOGGER.info("Setting labels on FailedMessage: {} to '{}'", change.getRecid(), change.getLabels());
                    labelFailedMessageClient.setLabels(
                            fromString(change.getRecid()),
                            labelExtractor.extractLabels(change.getLabels())
                    );
                });
        return "{ 'status': 'success' }";
    }

    @POST
    @Path("/delete")
    public String deleteFailedMessages(DeleteRequest request) {
        request.getSelected()
                .forEach(recid -> deleteFailedMessageClient.deleteFailedMessage(fromString(recid)));
        return "{ 'status': 'success' }";
    }
}
