package uk.gov.dwp.queue.triage.web.server.api;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.apache.cxf.common.util.StringUtils.split;

public class LabelExtractor {

    public Set<String> extractLabels(String labels) {
        return Stream.of(split(labels, ","))
                .map(String::trim)
                .filter(label -> !label.isEmpty())
                .collect(toSet());
    }
}
