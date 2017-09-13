package uk.gov.dwp.queue.triage.core.resend;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.HashMap;
import java.util.Map;

@Path("/admin/executor/message-resend/{brokerName}")
public class ResendScheduledExecutorsResource {

    private final Map<String, ResendScheduledExecutorService> resendScheduledExecutors;

    public ResendScheduledExecutorsResource(Map<String, ResendScheduledExecutorService> resendScheduledExecutors) {
        this.resendScheduledExecutors = resendScheduledExecutors;
    }

    @POST
    @Path("/start")
    public void start(@PathParam("brokerName") String brokerName) {
        getExecutor(brokerName).start();
    }

    @POST
    @Path("/execute")
    public void execute(@PathParam("brokerName") String brokerName) {
        getExecutor(brokerName).execute();
    }

    @PUT
    @Path("/pause")
    public void pause(@PathParam("brokerName") String brokerName) {
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
