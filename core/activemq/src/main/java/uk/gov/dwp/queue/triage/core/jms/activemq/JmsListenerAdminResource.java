package uk.gov.dwp.queue.triage.core.jms.activemq;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;
import java.util.Optional;

@Api(value = "Manage JMS Listeners")
@Path("/admin/jms-listener")
public class JmsListenerAdminResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsListenerAdminResource.class);

    private final Map<String, DefaultMessageListenerContainer> defaultMessageListenerContainers;

    public JmsListenerAdminResource(Map<String, DefaultMessageListenerContainer> defaultMessageListenerContainers) {
        this.defaultMessageListenerContainers = defaultMessageListenerContainers;
    }

    @ApiOperation("Start consuming messages from the DLQ on the given broker")
    @ApiResponses({
            @ApiResponse(code = 204, message = "DLQ consumption started successfully"),
            @ApiResponse(code = 404, message = "brokerName not found")
    })
    @POST
    @Path("/start/{brokerName}")
    public void start(
            @ApiParam(value = "name of the broker as defined in application.yml", required = true)
            @PathParam("brokerName") String brokerName) {
        LOGGER.info("Starting consuming messages of the {} broker", brokerName);
        Optional.ofNullable(defaultMessageListenerContainers.get(brokerName))
                .orElseThrow(NotFoundException::new)
                .start();
    }

    @ApiOperation("Stop consuming messages from the DLQ on the given broker")
    @ApiResponses({
            @ApiResponse(code = 204, message = "DLQ consumption stopped successfully"),
            @ApiResponse(code = 404, message = "brokerName not found")
    })
    @POST
    @Path("/stop/{brokerName}")
    public void stop(
            @ApiParam(value = "name of the broker as defined in application.yml", required = true)
            @PathParam("brokerName") String brokerName) {
        LOGGER.info("Stopping consuming messages of the {} broker", brokerName);
        Optional.ofNullable(defaultMessageListenerContainers.get(brokerName))
                .orElseThrow(NotFoundException::new)
                .stop();
    }

    @ApiOperation("Check the status of DLQ consumption on the given broker")
    @ApiResponses({
            @ApiResponse(code = 200, message = "true - if the listener has been started, false - if the listener has been stopped"),
            @ApiResponse(code = 404, message = "brokerName not found")
    })
    @GET
    @Path("/running/{brokerName}")
    public String isRunning(
            @ApiParam(value = "name of the broker as defined in application.yml", required = true)
            @PathParam("brokerName") String brokerName) {
        return Boolean.toString(Optional.ofNullable(defaultMessageListenerContainers.get(brokerName))
                .orElseThrow(NotFoundException::new)
                .isRunning());
    }
}
