package io.localhost.freelancer.statushukum.model.converter;

import org.json.JSONObject;

public interface GenericJsonObjectConverter<T> {
    T to(JSONObject object);
    JSONObject from(T object);
}
