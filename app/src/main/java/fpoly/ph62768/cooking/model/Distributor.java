package fpoly.ph62768.cooking.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Distributor {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("contactEmail")
    private String contactEmail;

    @SerializedName("phone")
    private String phone;

    @SerializedName("notes")
    private String notes;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public Distributor() {
        // Required empty constructor for Gson
    }

    public Distributor(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name != null ? name : "";
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(@Nullable String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    @Nullable
    public String getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public String getUpdatedAt() {
        return updatedAt;
    }
}

