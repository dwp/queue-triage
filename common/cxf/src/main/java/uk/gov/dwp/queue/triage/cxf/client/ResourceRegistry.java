package uk.gov.dwp.migration.mongo.demo.cxf.client;

import java.util.ArrayList;
import java.util.List;

public class ResourceRegistry {

    private final List<Object> resources = new ArrayList<>();

    public <T> T add(T resource) {
        resources.add(resource);
        return resource;
    }

    public List<Object> getResources() {
        return resources;
    }
}
