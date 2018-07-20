package uk.gov.dwp.queue.triage.core.classification.classifier;

public class StringDescription implements Description<String> {

    private final String output;

    public StringDescription() {
        this("");
    }

    StringDescription(String description) {
        this.output = description;
    }

    public Description<String> append(Description description) {
        return append(description.getOutput());
    }

    @Override
    public Description<String> append(String text) {
        return new StringDescription(output + text);
    }

    @Override
    public Description<String> append(Object object) {
        return append(object.toString());
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return output;
    }
}
