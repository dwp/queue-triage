package uk.gov.dwp.queue.triage.core.classification.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Api("Message Classification")
@Path("/message-classification")
public interface MessageClassificationClient {

    @ApiOperation("Add a new MessageClassifier")
    @POST
    void addMessageClassifier(MessageClassifier messageClassifier);

    @ApiOperation("List all MessageClassifiers")
    @GET
    List<MessageClassifier> listAllMessageClassifiers();

    @ApiOperation("Remove all MessageClassifiers")
    @DELETE
    void removeAllMessageClassifiers();
}
