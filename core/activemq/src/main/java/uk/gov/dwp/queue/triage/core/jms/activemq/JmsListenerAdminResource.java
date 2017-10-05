package uk.gov.dwp.queue.triage.core.jms.activemq;

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

@Path("/admin/jms-listener")
public class JmsListenerAdminResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsListenerAdminResource.class);

    private final Map<String, DefaultMessageListenerContainer> defaultMessageListenerContainers;

    public JmsListenerAdminResource(Map<String, DefaultMessageListenerContainer> defaultMessageListenerContainers) {
        this.defaultMessageListenerContainers = defaultMessageListenerContainers;
    }

    @POST
    @Path("/start/{brokerName}")
    public void start(@PathParam("brokerName") String brokerName) {
        LOGGER.info("Starting consuming messages of the {} broker", brokerName);
        Optional.ofNullable(defaultMessageListenerContainers.get(brokerName))
                .orElseThrow(NotFoundException::new)
                .start();
    }

    @POST
    @Path("/stop/{brokerName}")
    public void stop(@PathParam("brokerName") String brokerName) {
        LOGGER.info("Stopping consuming messages of the {} broker", brokerName);
        Optional.ofNullable(defaultMessageListenerContainers.get(brokerName))
                .orElseThrow(NotFoundException::new)
                .stop();
    }

    @GET
    @Path("/running/{brokerName}")
    public String isRunning(@PathParam("brokerName") String brokerName) {
        return Boolean.toString(Optional.ofNullable(defaultMessageListenerContainers.get(brokerName))
                .orElseThrow(NotFoundException::new)
                .isRunning());
    }
}
