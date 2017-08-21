package uk.gov.dwp.migration.mongo.demo.cxf.client;

import org.apache.cxf.feature.Feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureRegistry {

    private final List<Feature> features = new ArrayList<>();

    public <T extends Feature> T add(T feature) {
        features.add(feature);
        return feature;
    }

    public List<Feature> getFeatures() {
        return features;
    }

}
