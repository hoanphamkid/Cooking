package fpoly.ph62768.cooking.auth;

public class UserAccount {

    // Properties
    private String name;
    private String password;
    private long createdAt;
    // Constructors


    public UserAccount() {
    }

    public UserAccount(String name, String password) {
        this(name, password, System.currentTimeMillis());
    }

    public UserAccount(String name, String password, long createdAt) {
        this.name = name;
        this.password = password;
        this.createdAt = createdAt;
    }
    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

