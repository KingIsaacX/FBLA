package org.gvfbla;

import java.time.LocalDateTime;

import org.gvfbla.util.LocalDateTimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonProvider {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(account.class, new AccountDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public static Gson getGson() {
        return gson;
    }
}