package uk.gov.dwp.queue.triage.core.client;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(value = "Create")
@Path("/failed-message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CreateFailedMessageClient {

    @ApiOperation("Create a new FailedMessage")
    @POST
    void create(@NotNull CreateFailedMessageRequest failedMessageRequest);

}
