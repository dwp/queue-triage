package uk.gov.dwp.queue.triage.web.server.home;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

@Path("/home")
@Produces(TEXT_HTML)
public class HomeController {

    @GET
    public HomePage getHomePage() {
        return new HomePage();
    }
}
