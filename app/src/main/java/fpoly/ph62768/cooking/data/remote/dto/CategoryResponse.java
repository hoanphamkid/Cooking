package fpoly.ph62768.cooking.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CategoryResponse {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}



