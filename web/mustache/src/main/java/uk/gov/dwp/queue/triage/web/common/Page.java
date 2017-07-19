package uk.gov.dwp.queue.triage.web.common;

public class Page {

    private final String template;

    public Page(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
