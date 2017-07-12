package uk.gov.dwp.queue.triage.core.jms;

import javax.jms.Message;
import java.util.Map;

public interface MessagePropertyExtractor {

    Map<String, Object> extractProperties(Message message);
}
