package org.gvfbla;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Manages file-based storage of postings, applications, and accounts.
 * Provides methods to initialize storage, save, and load data.
 */
public class FileStorageManager {
    private static final String POSTINGS_FILE = "data/postings.json";
    private static final String APPLICATIONS_FILE = "data/applications.json";
    private static final String ACCOUNTS_FILE = "data/accounts.json";
    private static final Gson gson = new Gson();

    /**
     * Initializes the storage environment by ensuring data directories and files exist.
     */
    public static void initializeStorage() {
        createDirectoryIfNotExists("data");
        createFileIfNotExists(POSTINGS_FILE);
        createFileIfNotExists(APPLICATIONS_FILE);
        createFileIfNotExists(ACCOUNTS_FILE);
    }

    /**
     * Creates a directory if it does not exist.
     *
     * @param dirPath the path of the directory to create
     */
    private static void createDirectoryIfNotExists(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Creates a file if it does not exist and initializes it with an empty JSON array.
     *
     * @param filePath the path of the file to create
     */
    private static void createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves a list of items to the specified file in JSON format.
     *
     * @param items    the list of items to save
     * @param filePath the file path to save to
     * @param <T>      the type of items in the list
     */
    private static <T> void saveToFile(List<T> items, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a list of items from the specified file, returning an empty list if an error occurs.
     *
     * @param filePath  the file path to load from
     * @param typeToken the TypeToken representing the type of the list to be loaded
     * @param <T>       the type of items in the list
     * @return a list of items of type T
     */
    private static <T> List<T> loadFromFile(String filePath, TypeToken<List<T>> typeToken) {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, typeToken.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves a list of postings to the postings file.
     *
     * @param postings the list of postings to save
     */
    public static void savePostings(List<posting> postings) {
        saveToFile(postings, POSTINGS_FILE);
    }

    /**
     * Loads all postings from the postings file.
     *
     * @return a list of postings
     */
    public static List<posting> loadPostings() {
        return loadFromFile(POSTINGS_FILE, new TypeToken<List<posting>>() {});
    }

    /**
     * Finds a posting by its ID.
     *
     * @param id the ID of the posting to find
     * @return the posting with the given ID, or null if not found
     */
    public static posting findPostingById(String id) {
        List<posting> postings = loadPostings();
        return postings.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * Saves a list of applications to the applications file.
     *
     * @param applications the list of applications to save
     */
    public static void saveApplications(List<application> applications) {
        saveToFile(applications, APPLICATIONS_FILE);
    }

    /**
     * Loads all applications from the applications file.
     *
     * @return a list of applications
     */
    public static List<application> loadApplications() {
        return loadFromFile(APPLICATIONS_FILE, new TypeToken<List<application>>() {});
    }

    /**
     * Finds all applications associated with a given posting ID.
     *
     * @param postingId the ID of the posting
     * @return a list of applications linked to the given posting ID
     */
    public static List<application> findApplicationsByPosting(String postingId) {
        List<application> applications = loadApplications();
        return applications.stream()
            .filter(a -> a.getPostingId().equals(postingId))
            .collect(Collectors.toList());
    }

    /**
     * Saves a list of accounts to the accounts file.
     *
     * @param accounts the list of accounts to save
     */
    public static void saveAccounts(List<account> accounts) {
        saveToFile(accounts, ACCOUNTS_FILE);
    }

    /**
     * Loads all accounts from the accounts file.
     *
     * @return a list of accounts
     */
    public static List<account> loadAccounts() {
        return loadFromFile(ACCOUNTS_FILE, new TypeToken<List<account>>() {});
    }
}
