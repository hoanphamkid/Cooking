package fpoly.ph62768.cooking.data.remote;

import androidx.annotation.NonNull;

public class DistributorRequest {
    private final String name;

    public DistributorRequest(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }
}

