package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import java.util.Optional;

public class DestinationDocumentConverter implements DocumentConverter<Destination> {

    public static final String BROKER_NAME = "brokerName";
    public static final String NAME = "name";

    @Override
    public Destination convertToObject(Document document) {
        return new Destination(document.getString(BROKER_NAME), Optional.ofNullable(document.getString(NAME)));
    }

    @Override
    public Document convertFromObject(Destination item) {
        return new Document()
                .append(BROKER_NAME, item.getBrokerName())
                .append(NAME, item.getName().orElse(null));
    }
}