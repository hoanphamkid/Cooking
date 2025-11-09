package fpoly.ph62768.cooking.model;

public enum RecipeCategory {
    ALL("Tất cả"),
    LOW_CAL("Ít calo"),
    HEALTHY("Healthy"),
    QUICK("Nhanh"),
    TRADITIONAL("Truyền thống"),
    DESSERT("Tráng miệng"),
    DRINK("Đồ uống");

    private final String displayName;

    RecipeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

