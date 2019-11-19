package io.localhost.freelancer.statushukum.model.converter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.localhost.freelancer.statushukum.model.util.helper.IntegerHelper;
import io.localhost.freelancer.statushukum.model.util.helper.StringHelper;

public class JsonObjectToMeData implements GenericJsonObjectConverter<JSONObject> {
    private int category;
    private Pattern yearPattern = Pattern.compile("Tahun ([0-9]{4})", Pattern.CASE_INSENSITIVE);

    public JsonObjectToMeData(int category) {
        this.category = category;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public JSONObject to(JSONObject object) {
        if(object == null) return null;
        String rawYear = StringHelper.captureSingleValue(object.optString("NOMOR", ""), yearPattern);
        int year = IntegerHelper.parseIntOrDefault(rawYear, -1);
        if (year == -1) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", 0);
        map.put("year", year);
        map.put("no", StringHelper.trimOrNull(object.optString("NOMOR")));
        map.put("description", StringHelper.trimOrNull(object.optString("TENTANG")));
        map.put("status", StringHelper.trimOrNull(object.optString("STATUS")));
        map.put("category", category);
        map.put("reference", StringHelper.trimOrNull(object.optString("DOWNLOAD")));
        return new JSONObject(map);
    }

    @Override
    public JSONObject from(JSONObject object) {
        throw new RuntimeException("Not Implemented");
    }
}
