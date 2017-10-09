package uk.gov.dwp.queue.triage.core.resend;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;

@Api("Resend Executor")
@Path("/admin/executor/message-resend/{brokerName}")
public class ResendScheduledExecutorsResource {

    private final Map<String, ResendScheduledExecutorService> resendScheduledExecutors;

    public ResendScheduledExecutorsResource(Map<String, ResendScheduledExecutorService> resendScheduledExecutors) {
        this.resendScheduledExecutors = resendScheduledExecutors;
    }

    @ApiOperation("Start the resend executor for the given broker")
    @POST
    @Path("/start")
    public void start(
            @ApiParam(value = "name of the broker as defined in application.yml", required = true)
            @PathParam("brokerName") String brokerName) {
        getExecutor(brokerName).start();
    }

    @ApiOperation("Synchronously execute the resend job for the given broker")
    @POST
    @Path("/execute")
    public void execute(
            @ApiParam(value = "name of the broker as defined in application.yml", required = true)
            @PathParam("brokerName") String brokerName) {
        getExecutor(brokerName).execute();
    }

    @ApiOperation("Pause the resend executor for the given broker")
    @PUT
    @Path("/pause")
    public void pause(
            @ApiParam(value = "name of the broker as defined in application.yml", required = true)
            @PathParam("brokerName") String brokerName) {
        getExecutor(brokerName).pause();
    }

    private ResendScheduledExecutorService getExecutor(String brokerName) {
        if (brokerName == null) {
            throw new BadRequestException("A broker must be specified");
        }
        ResendScheduledExecutorService resendScheduledExecutorService = resendScheduledExecutors.get(brokerName);
        if (resendScheduledExecutorService == null) {
            throw new BadRequestException("Cannot find a ResendScheduledExecutorService for broker: " + brokerName);
        }
        return resendScheduledExecutorService;
    }
}
