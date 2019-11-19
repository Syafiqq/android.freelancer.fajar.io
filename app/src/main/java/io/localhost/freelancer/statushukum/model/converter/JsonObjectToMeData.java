package io.localhost.freelancer.statushukum.model.converter;

import org.json.JSONObject;

import java.util.regex.Pattern;

import io.localhost.freelancer.statushukum.model.entity.ME_Data;
import io.localhost.freelancer.statushukum.model.util.helper.IntegerHelper;
import io.localhost.freelancer.statushukum.model.util.helper.StringHelper;

public class JsonObjectToMeData implements GenericJsonObjectConverter<ME_Data> {
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
    public ME_Data to(JSONObject object) {
        String rawYear = StringHelper.captureSingleValue(object.optString("NOMOR", ""), yearPattern);
        int year = IntegerHelper.parseIntOrDefault(rawYear, -1);
        if (year == -1) return null;
        return new ME_Data(
                0,
                year,
                StringHelper.trimOrNull(object.optString("NOMOR")),
                StringHelper.trimOrNull(object.optString("TENTANG")),
                StringHelper.trimOrNull(object.optString("STATUS")),
                1,
                StringHelper.trimOrNull(object.optString("DOWNLOAD"))
        );
    }

    @Override
    public JSONObject from(ME_Data object) {
        throw new RuntimeException("Not Implemented");
    }
}
