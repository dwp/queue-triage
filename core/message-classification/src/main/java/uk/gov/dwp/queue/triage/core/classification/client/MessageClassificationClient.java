package uk.gov.dwp.queue.triage.core.classification.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api("Message Classification")
@Path("/message-classification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MessageClassificationClient {

    @ApiOperation("Add a new MessageClassifier")
    @POST
    void addMessageClassifier(MessageClassifier messageClassifier);

    @ApiOperation("List all MessageClassifiers")
    @GET
    MessageClassifier listAllMessageClassifiers();

    @ApiOperation("Remove all MessageClassifiers")
    @DELETE
    void removeAllMessageClassifiers();

    @ApiOperation("Attempt to classify the FailedMessage corresponding to the given FailedMessageId")
    @PUT
    @Path("/classify/{failedMessageId}")
    MessageClassificationOutcomeResponse classifyFailedMessage(@PathParam("failedMessageId") FailedMessageId failedMessageId);

    @ApiOperation("Attempt to classify the provided FailedMessage")
    @PUT
    @Path("/classify")
    MessageClassificationOutcomeResponse classifyFailedMessage(FailedMessage failedMessage);
}
