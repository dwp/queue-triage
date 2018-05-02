package uk.gov.dwp.queue.triage.web.server.list;

import uk.gov.dwp.queue.triage.web.common.Page;

public class FailedMessageListPage extends Page {

    private final boolean popupRendered;

    public FailedMessageListPage(boolean popupRendered) {
        super("list.mustache");
        this.popupRendered = popupRendered;
    }

    public boolean isPopupRendered() {
        return popupRendered;
    }
}
