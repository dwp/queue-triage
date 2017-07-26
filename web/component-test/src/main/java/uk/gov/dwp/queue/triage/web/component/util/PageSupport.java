package uk.gov.dwp.queue.triage.web.component.util;

import org.springframework.core.env.Environment;

import static com.codeborne.selenide.Selenide.open;

public class PageSupport {

    public static <T> T navigateTo(Environment environment, Class<T> clazz, String path) {
        String url = "http://localhost:" + environment.getProperty("local.server.port") + "/web/" + path;
        return open(url, clazz);
    }
}
