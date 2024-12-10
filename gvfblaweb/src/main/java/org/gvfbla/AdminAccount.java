package org.gvfbla;


/**
 * Represents an administrator account with elevated privileges. 
 * Administrators have permission to perform all actions and may manage multiple departments.
 */
public class AdminAccount extends account {

    /**
     * Creates a new AdminAccount with a given username, password, and email.
     *
     * @param username the admin account's username
     * @param password the admin account's password
     * @param email    the admin account's email address
     */
    public AdminAccount(String username, String password, String email) {
        super(username, password, email, "ADMIN");
    }

    /**
     * Determines if the administrator has permission to perform a given action.
     * Administrators have all permissions.
     *
     * @param action the action to check
     * @return true, as administrators have all permissions
     */
    @Override
    public boolean hasPermission(String action) {
        return true;
    }

    /**
     * Returns the account type.
     *
     * @return the string "Administrator"
     */
    @Override
    public String getAccountType() {
        return "Administrator";
    }



 


}
