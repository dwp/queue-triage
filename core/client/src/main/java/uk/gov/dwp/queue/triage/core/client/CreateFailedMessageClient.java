package uk.gov.dwp.queue.triage.core.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/failed-message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CreateFailedMessageClient {

    @POST
    void create(CreateFailedMessageRequest failedMessageRequest);

}
