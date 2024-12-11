package org.gvfbla;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages user accounts, including creation, authentication, and administrative operations.
 * Provides methods for registering student, employer, and admin accounts, as well as 
 * activating/deactivating accounts, and retrieving account information.
 */
public class AccountManager {
    private List<account> accounts;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private final FileStorageManager fileManager;
    
    /**
     * Constructs an AccountManager and initializes the account list from storage.
     * If no default administrator account exists, it creates one.
     */
    public AccountManager() {
        this.fileManager = new FileStorageManager();
        this.accounts = fileManager.loadAccounts();
        createDefaultAdminIfNeeded();
    }
    
    /**
     * Creates the default admin account if none exists.
     */
    private void createDefaultAdminIfNeeded() {
        if (accounts.stream().noneMatch(a -> a.getUsername().equals(DEFAULT_ADMIN_USERNAME))) {
            AdminAccount adminAccount = new AdminAccount(
                DEFAULT_ADMIN_USERNAME,
                "admin123", 
                "admin@school.edu"
            );
            accounts.add(adminAccount);
            saveAccounts();
        }
    }
    
    /**
     * Registers a new student account.
     *
     * @param username  the username for the new account
     * @param password  the password for the new account
     * @param email     the email for the new account
     * @param firstName the student's first name
     * @param lastName  the student's last name
     * @return the newly created StudentAccount
     * @throws AccountException if validation fails or username is taken
     */
    public StudentAccount registerStudent(String username, String password, String email,
                                          String firstName, String lastName) throws AccountException {
        validateNewAccount(username, password, email);
        StudentAccount student = new StudentAccount(username, password, email, firstName, lastName);
        accounts.add(student);
        saveAccounts();
        return student;
    }
    
    /**
     * Registers a new employer account.
     *
     * @param username    the username for the new account
     * @param password    the password for the new account
     * @param email       the email for the new account
     * @param companyName the employer's company name
     * @return the newly created EmployerAccount
     * @throws AccountException if validation fails or username is taken
     */
    public EmployerAccount registerEmployer(String username, String password, String email,
                                            String companyName) throws AccountException {
        validateNewAccount(username, password, email);
        EmployerAccount employer = new EmployerAccount(username, password, email, companyName);
        accounts.add(employer);
        saveAccounts();
        return employer;
    }
    
    /**
     * Creates a new admin account. Only an admin with the appropriate permission can create another admin account.
     *
     * @param username the username for the new admin account
     * @param password the password for the new admin account
     * @param email    the email for the new admin account
     * @param creator  the admin account creating the new admin account
     * @return the newly created AdminAccount
     * @throws AccountException if validation fails, username is taken, or permissions are insufficient
     */
    public AdminAccount createAdminAccount(String username, String password, String email,
                                           AdminAccount creator) throws AccountException {
        if (creator == null || !creator.hasPermission("CREATE_ADMIN")) {
            throw new AccountException("Insufficient permissions to create admin account");
        }
        validateNewAccount(username, password, email);
        AdminAccount admin = new AdminAccount(username, password, email);
        accounts.add(admin);
        saveAccounts();
        return admin;
    }
    
    /**
     * Authenticates a user by username and password.
     * 
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return the authenticated account, or null if authentication fails or the account is inactive
     */
    public account login(String username, String password) {
        Optional<account> accOpt = accounts.stream()
            .filter(a -> a.getUsername().equals(username))
            .findFirst();
            
        if (accOpt.isPresent() && accOpt.get().verifyPassword(password)) {
            account acc = accOpt.get();
            if (!acc.isActive()) {
                return null;
            }
            acc.setLastLoginDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            saveAccounts();
            return acc;
        }
        return null;
    }
    
    /**
     * Deactivates an account, preventing it from being used.
     *
     * @param username the username of the account to deactivate
     * @param admin    the admin account performing the deactivation
     * @throws AccountException if the admin does not have permission or the account is not found
     */
    public void deactivateAccount(String username, AdminAccount admin) throws AccountException {
        if (!admin.hasPermission("MANAGE_ACCOUNTS")) {
            throw new AccountException("Insufficient permissions to deactivate accounts");
        }
        account acc = findAccountByUsername(username);
        if (acc != null) {
            acc.setActive(false);
            saveAccounts();
        } else {
            throw new AccountException("Account not found");
        }
    }
    
    /**
     * Reactivates a previously deactivated account.
     *
     * @param username the username of the account to reactivate
     * @param admin    the admin account performing the reactivation
     * @throws AccountException if the admin does not have permission or the account is not found
     */
    public void reactivateAccount(String username, AdminAccount admin) throws AccountException {
        if (!admin.hasPermission("MANAGE_ACCOUNTS")) {
            throw new AccountException("Insufficient permissions to reactivate accounts");
        }
        account acc = findAccountByUsername(username);
        if (acc != null) {
            acc.setActive(true);
            saveAccounts();
        } else {
            throw new AccountException("Account not found");
        }
    }
    
    /**
     * Returns a list of all student accounts.
     *
     * @return a list of StudentAccount objects
     */
    public List<StudentAccount> getAllStudents() {
        return accounts.stream()
            .filter(a -> a instanceof StudentAccount)
            .map(a -> (StudentAccount) a)
            .collect(Collectors.toList());
    }
    
    /**
     * Returns a list of all employer accounts.
     *
     * @return a list of EmployerAccount objects
     */
    public List<EmployerAccount> getAllEmployers() {
        return accounts.stream()
            .filter(a -> a instanceof EmployerAccount)
            .map(a -> (EmployerAccount) a)
            .collect(Collectors.toList());
    }
    
    /**
     * Finds an account by username.
     *
     * @param username the username to search for
     * @return the account with the given username, or null if not found
     */
    public account findAccountByUsername(String username) {
        return accounts.stream()
            .filter(a -> a.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Finds an account by its unique ID.
     *
     * @param id the unique ID to search for
     * @return the account with the given ID, or null if not found
     */
    public account findAccountById(String id) {
        return accounts.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Validates the details of a new account before creation.
     *
     * @param username the desired username
     * @param password the chosen password
     * @param email    the email for the account
     * @throws AccountException if validation fails (username taken, password too short, invalid email)
     */
    private void validateNewAccount(String username, String password, String email) 
            throws AccountException {
        if (isUsernameTaken(username)) {
            throw new AccountException("Username already exists");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new AccountException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (!isValidEmail(email)) {
            throw new AccountException("Invalid email format");
        }
    }
    
    /**
     * Checks if an email address is valid.
     * 
     * @param email the email address to validate
     * @return true if the email is in a valid format, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Saves the current list of accounts to storage.
     */
    private void saveAccounts() {
        fileManager.saveAccounts(accounts);
    }

    /**
     * Adds a new account to the system.
     *
     * @param account the account to add
     * @throws AccountException if the username is already taken
     */
    public void addAccount(account account) throws AccountException {
        if (isUsernameTaken(account.getUsername())) {
            throw new AccountException("Username already exists");
        }
        accounts.add(account);
        saveAccounts();
    }

    /**
     * Checks if a username is already taken.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return accounts.stream()
            .anyMatch(a -> a.getUsername().equals(username));
    }

    /**
     * Retrieves all accounts.
     *
     * @return a list of all account objects
     */
    public List<account> getAllAccounts() {
        return accounts;
    }
}
