package io.localhost.freelancer.statushukum.model.proceeder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.localhost.freelancer.statushukum.model.converter.GenericJsonObjectConverter;
import io.localhost.freelancer.statushukum.model.entity.ME_Data;

public class AirTableResponseProceeder implements GenericProceeder<JSONObject> {
    private GenericJsonObjectConverter<? extends ME_Data> objectConverter;
    private List<ME_Data> collector;

    public AirTableResponseProceeder(GenericJsonObjectConverter<? extends ME_Data> objectConverter, List<ME_Data> collector) {
        this.objectConverter = objectConverter;
        this.collector = collector;
    }

    public GenericJsonObjectConverter<? extends ME_Data> getObjectConverter() {
        return objectConverter;
    }

    public void setObjectConverter(GenericJsonObjectConverter<? extends ME_Data> objectConverter) {
        this.objectConverter = objectConverter;
    }

    @Override
    public void proceed(JSONObject object) {
        if (object == null) return;
        if (object.has("records")) {
            JSONArray records = object.optJSONArray("records");
            if (records != null)
                for (int i = -1, is = records.length(); ++i < is; ) {
                    ME_Data me = objectConverter.to(records.optJSONObject(i));
                    if (me != null)
                        collector.add(me);
                }
        }
    }
}
