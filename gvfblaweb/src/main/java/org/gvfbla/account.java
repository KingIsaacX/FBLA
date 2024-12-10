package org.gvfbla;

import java.util.UUID;

/**
 * Represents a user account with a username, password, and account type.
 * Each account is assigned a unique ID upon creation.
 */
public class account {
    private String username;
    private String password;
    private String accountType;
    private final String id;

    /**
     * Creates a new account with the specified username, password, and account type.
     *
     * @param username    the account's username
     * @param password    the account's password
     * @param accountType the type of the account (e.g., "admin", "user")
     */
    public account(String username, String password, String accountType) {
        this.username = username;
        this.password = password;
        this.accountType = accountType;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Returns the unique ID of this account.
     *
     * @return the account's unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the username of this account.
     *
     * @return the account's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of this account.
     *
     * @param username the account's new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of this account.
     *
     * @return the account's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of this account.
     *
     * @param password the account's new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the type of this account.
     *
     * @return the account type
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Sets the type of this account.
     *
     * @param accountType the account's new type
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
