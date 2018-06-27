package uk.gov.dwp.queue.triage.core.classification.classifier;

import java.io.IOException;

public class StringDescription implements Description<String> {

    private final Appendable output;

    public StringDescription() {
        this(new StringBuilder());
    }

    public StringDescription(String description) {
        this(new StringBuilder(description));
    }

    public StringDescription(Appendable output) {
        this.output = output;
    }

    @Override
    public Description append(Description description) {
        return append(description.getOutput());
    }

    @Override
    public Description append(String text) {
        try {
            output.append(text);
        } catch (IOException e) {
            throw new RuntimeException("Could not write description: " + text, e);
        }
        return this;
    }

    @Override
    public Description append(Object object) {
        return append(object.toString());
    }

    public String getOutput() {
        return output.toString();
    }

    @Override
    public String toString() {
        return getOutput();
    }
}
