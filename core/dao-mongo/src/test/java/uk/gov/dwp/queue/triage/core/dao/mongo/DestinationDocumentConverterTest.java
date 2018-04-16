package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.domain.DestinationMatcher.aDestination;

public class DestinationDocumentConverterTest {

    private final DestinationDocumentConverter underTest = new DestinationDocumentConverter();

    @Test
    public void convertDestinationToDocumentAndBack() {
        Document document = underTest.convertFromObject(new Destination("broker.name", Optional.of("queue.name")));

        assertThat(underTest.convertToObject(document), is(aDestination().withBrokerName("broker.name").withName("queue.name")));
    }

    @Test
    public void convertDestinationWithMissingNameToDocumentAndBack() {
        Document document = underTest.convertFromObject(new Destination("broker.name", Optional.empty()));

        assertThat(underTest.convertToObject(document), is(aDestination().withBrokerName("broker.name").withNoName()));
    }
}