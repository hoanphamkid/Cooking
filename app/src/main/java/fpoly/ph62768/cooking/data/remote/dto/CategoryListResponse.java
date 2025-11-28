package fpoly.ph62768.cooking.data.remote.dto;

import java.util.List;

public class CategoryListResponse {

    private int status;
    private String messenger;
    private List<CategoryResponse> data;

    public int getStatus() {
        return status;
    }

    public String getMessenger() {
        return messenger;
    }

    public List<CategoryResponse> getData() {
        return data;
    }
}



