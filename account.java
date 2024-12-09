public class account {
    private String username;
    private String password;
    private String accountType;

    public account(String username, String password, String accountType){
        this.username = username;
        this.password = password;
        this.accountType = accountType;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
  
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
