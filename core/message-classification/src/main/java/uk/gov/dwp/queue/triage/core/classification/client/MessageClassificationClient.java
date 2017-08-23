package uk.gov.dwp.queue.triage.core.classification.client;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/message-classification")
public interface MessageClassificationClient {

    @POST
    void addMessageClassifier(MessageClassifier messageClassifier);

    @GET
    List<MessageClassifier> listAllMessageClassifiers();

    @DELETE
    void removeAllMessageClassifiers();
}
