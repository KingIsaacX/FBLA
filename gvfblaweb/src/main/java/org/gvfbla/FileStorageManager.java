package org.gvfbla;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.gvfbla.util.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileStorageManager {
    private static final String DATA_DIR = "data";
    private static final String POSTINGS_FILE = DATA_DIR + "/postings.json";
    private static final String APPLICATIONS_FILE = DATA_DIR + "/applications.json";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accounts.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    static {
        initializeStorage();
    }

    public static void initializeStorage() {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
            initializeFileIfNotExists(POSTINGS_FILE);
            initializeFileIfNotExists(APPLICATIONS_FILE);
            initializeFileIfNotExists(ACCOUNTS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }

    private static void initializeFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("[]");
            }
        }
    }

    public static List<posting> loadPostings() {
        return loadFromFile(POSTINGS_FILE, new TypeToken<List<posting>>() {});
    }

    public static void savePostings(List<posting> postings) {
        saveToFile(postings, POSTINGS_FILE);
    }

    public static List<application> loadApplications() {
        return loadFromFile(APPLICATIONS_FILE, new TypeToken<List<application>>() {});
    }

    public static void saveApplications(List<application> applications) {
        saveToFile(applications, APPLICATIONS_FILE);
    }

    public List<account> loadAccounts() {
        return loadFromFile(ACCOUNTS_FILE, new TypeToken<List<account>>() {});
    }

    public void saveAccounts(List<account> accounts) {
        saveToFile(accounts, ACCOUNTS_FILE);
    }

    private static <T> void saveToFile(List<T> items, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save to file: " + filePath, e);
        }
    }

    private static <T> List<T> loadFromFile(String filePath, TypeToken<List<T>> typeToken) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(filePath)) {
            List<T> items = gson.fromJson(reader, typeToken.getType());
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load from file: " + filePath, e);
        }
    }
}
