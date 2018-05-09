package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateRequestAdapterRegistry {

    private final Map<Class<? extends UpdateRequest>, UpdateRequestAdapter> updateRequestAdapters = new HashMap<>();
    private final UpdateRequestAdapter<UpdateRequest> defaultUpdateRequestAdapter;

    public UpdateRequestAdapterRegistry(UpdateRequestAdapter<UpdateRequest> defaultUpdateRequestAdapter) {
        this.defaultUpdateRequestAdapter = defaultUpdateRequestAdapter;
    }

    public <T extends UpdateRequest> UpdateRequestAdapterRegistry addAdapter(Class<T> clazz, UpdateRequestAdapter<T> adapter) {
        updateRequestAdapters.put(clazz, adapter);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends UpdateRequest> UpdateRequestAdapter<T> getAdapter(T updateRequest) {
        return updateRequestAdapters.getOrDefault(updateRequest.getClass(), defaultUpdateRequestAdapter);
    }

    Map<Class<? extends UpdateRequest>, UpdateRequestAdapter> getUpdateRequestAdapters() {
        return new HashMap<>(updateRequestAdapters);
    }
}
