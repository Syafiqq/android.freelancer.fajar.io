package io.localhost.freelancer.statushukum.model.proceeder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.localhost.freelancer.statushukum.model.converter.GenericJsonObjectConverter;

public class AirTableResponseProceeder implements GenericProceeder<JSONObject> {
    private GenericJsonObjectConverter<? extends JSONObject> objectConverter;

    private List<JSONObject> collector;

    public AirTableResponseProceeder(GenericJsonObjectConverter<? extends JSONObject> objectConverter, List<JSONObject> collector) {
        this.objectConverter = objectConverter;
        this.collector = collector;
    }

    public GenericJsonObjectConverter<? extends JSONObject> getObjectConverter() {
        return objectConverter;
    }

    public void setObjectConverter(GenericJsonObjectConverter<? extends JSONObject> objectConverter) {
        this.objectConverter = objectConverter;
    }

    public List<JSONObject> getCollector() {
        return collector;
    }

    public void setCollector(List<JSONObject> collector) {
        this.collector = collector;
    }

    @Override
    public void proceed(JSONObject object) {
        if (object == null) return;
        if (object.has("records")) {
            JSONArray records = object.optJSONArray("records");
            if (records != null)
                for (int i = -1, is = records.length(); ++i < is; ) {
                    JSONObject me = objectConverter.to(records.optJSONObject(i));
                    if (me != null)
                        collector.add(me);
                }
        }
    }
}
