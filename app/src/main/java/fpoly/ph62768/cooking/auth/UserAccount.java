package fpoly.ph62768.cooking.auth;

public class UserAccount {

    // Properties
    private String name;
    private String password;
    private long createdAt;
    // Constructors
    private String phone;
    private String bio;
    private String address;
    private String website;
    private String avatarUrl;

    public UserAccount(String name, String password, long createdAt, String phone, String bio, String address, String website, String avatarUrl) {
        this.name = name;
        this.password = password;
        this.createdAt = createdAt;
        this.phone = phone;
        this.bio = bio;
        this.address = address;
        this.website = website;
        this.avatarUrl = avatarUrl;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

