package org.gvfbla;

import java.lang.reflect.Type;

import com.google.gson.Gson;

import io.javalin.json.JsonMapper;

public class GsonJsonMapper implements JsonMapper {
    private final Gson gson;

    public GsonJsonMapper(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String toJsonString(Object obj, Type type) {  
        return gson.toJson(obj, type);
    }

    @Override
    public <T> T fromJsonString(String json, Type targetType) {
        return gson.fromJson(json, targetType);
    }
}