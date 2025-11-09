package fpoly.ph62768.cooking.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.model.RecipeCategory;
import fpoly.ph62768.cooking.model.RecipeStep;

public class RecipeRepository {

    private static RecipeRepository instance;
    private final List<Recipe> recipes = new ArrayList<>();
    private int counter = 0;

    private RecipeRepository() {
        seedData();
    }

    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    public List<Recipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    public Recipe getRecipeById(String id) {
        if (id == null) {
            return null;
        }
        for (Recipe recipe : recipes) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }

    private void seedData() {
        recipes.clear();
        counter = 0;
        addRecipe("Trứng luộc", "10 phút", 2.2, RecipeCategory.LOW_CAL, "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2024/6/28/trung-luoc-17195964533531755427733.jpg");
        addRecipe("Cá hấp gừng", "25 phút", 3.6, RecipeCategory.LOW_CAL, "https://haisanloccantho.com/wp-content/uploads/2024/10/nguyen-lieu-chuan-bi-cho-mon-ca-tram-hap-gung.jpg");
        addRecipe("Canh bí đỏ", "30 phút", 1.4, RecipeCategory.LOW_CAL, "https://cdn.tgdd.vn/2021/05/CookProduct/1-1200x676-41.jpg");
        addRecipe("Salad rau củ", "15 phút", 0.1, RecipeCategory.LOW_CAL, "https://cdn.zsoft.solutions/poseidon-web/app/media/Kham-pha-am-thuc/04.2024/120424-3-mon-salad-buffet-poseidon-04.jpg");
        addRecipe("Đậu hũ hấp hành", "20 phút", 4.3, RecipeCategory.LOW_CAL, "https://storage.googleapis.com/onelife-public/blog.onelife.vn/2021/11/cach-lam-djau-hu-hap-hanh-huong-mon-chay-523692620962.jpg");

        addRecipe("Ức gà nướng rau củ", "35 phút", 4.7, RecipeCategory.HEALTHY, "https://cdn.tgdd.vn/2020/08/CookProduct/Untitled-4-1200x676-5.jpg");
        addRecipe("Bánh chuối yến mạch", "40 phút", 4.4, RecipeCategory.HEALTHY, "https://gimmedelicious.com/wp-content/uploads/2021/12/Oatmeal-Banana-Bread-9.jpg");
        addRecipe("Súp rau củ", "30 phút", 4.2, RecipeCategory.HEALTHY, "https://cdn.tgdd.vn/2021/09/CookProduct/thum-1200x676-2.jpg");
        addRecipe("Miến xào nấm", "25 phút", 4.3, RecipeCategory.HEALTHY, "https://img-global.cpcdn.com/recipes/f5813392126d249c/400x400cq80/photo.jpg");
        addRecipe("Salad cá ngừ", "18 phút", 4.6, RecipeCategory.HEALTHY, "https://cdn.tgdd.vn/2020/07/CookRecipe/Avatar/salad-ca-ngu-ngam-dau-voi-trung-luoc-thumbnail.jpg");

        addRecipe("Bánh mì trứng ốp la", "10 phút", 4.1, RecipeCategory.QUICK, "https://img-global.cpcdn.com/recipes/01914f4be6cc4786/1200x630cq80/photo.jpg");
        addRecipe("Mì gói trộn rau", "12 phút", 4.0, RecipeCategory.QUICK, "https://gocamthuc.acecookvietnam.vn/wp-content/uploads/2023/12/ACECOOK-day2-TOPVIEW-6-scaled.jpg");
        addRecipe("Cơm chiên trứng", "20 phút", 4.3, RecipeCategory.QUICK, "https://www.huongnghiepaau.com/wp-content/uploads/2016/06/com-chien-toi-trung.jpg");
        addRecipe("Sandwich rau củ", "8 phút", 4.2, RecipeCategory.QUICK, "https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-102.jpg");
        addRecipe("Cháo yến mạch", "15 phút", 4.1, RecipeCategory.QUICK, "https://cdn.tgdd.vn/Files/2018/11/25/1133505/yen-mach-la-gi-cach-nau-chao-yen-mach-bo-duong-ngon-ngat-ngay-10.jpg");

        addRecipe("Phở bò", "45 phút", 4.8, RecipeCategory.TRADITIONAL, "https://cafefcdn.com/2018/7/19/photo-2-1531984647242593960017.png");
        addRecipe("Bún riêu cua", "40 phút", 4.5, RecipeCategory.TRADITIONAL, "https://cdn.xanhsm.com/2025/01/7f24de71-bun-rieu-quy-nhon-1.jpg");
        addRecipe("Bánh chưng", "120 phút", 4.7, RecipeCategory.TRADITIONAL, "https://cdn.xanhsm.com/2025/01/62eda6b7-banh-chung-1.jpg");
        addRecipe("Nem rán", "60 phút", 4.6, RecipeCategory.TRADITIONAL, "https://thewoksoflife.com/wp-content/uploads/2020/08/cha-gio-vietnamese-fried-spring-rolls-17.jpg");
        addRecipe("Canh chua cá", "35 phút", 4.4, RecipeCategory.TRADITIONAL, "https://i-giadinh.vnecdn.net/2023/04/25/Thanh-pham-1-1-7239-1682395675.jpg");

        addRecipe("Chè đậu xanh", "45 phút", 4.2, RecipeCategory.DESSERT, "https://images.unsplash.com/photo-1604908177522-4023ac76fae1?crop=entropy");
        addRecipe("Bánh flan", "90 phút", 4.6, RecipeCategory.DESSERT, "https://images.unsplash.com/photo-1515003197210-e0cd71810b5f");
        addRecipe("Bánh chuối hấp", "60 phút", 4.4, RecipeCategory.DESSERT, "https://images.unsplash.com/photo-1514996937319-344454492b37?crop=entropy");
        addRecipe("Kem dừa", "25 phút", 4.5, RecipeCategory.DESSERT, "https://images.unsplash.com/photo-1469428946641-83c13ea94ceb");
        addRecipe("Chè bắp", "50 phút", 4.3, RecipeCategory.DESSERT, "https://images.unsplash.com/photo-1604908177522-4023ac76fae1?ixlib=rb-4.0.3");

        addRecipe("Trà đá", "5 phút", 4.0, RecipeCategory.DRINK, "https://images.unsplash.com/photo-1481391319762-47dff72954d1");
        addRecipe("Cà phê sữa đá", "7 phút", 4.7, RecipeCategory.DRINK, "https://images.unsplash.com/photo-1509042239860-f550ce710b93");
        addRecipe("Nước mía", "5 phút", 4.1, RecipeCategory.DRINK, "https://images.unsplash.com/photo-1527169402691-feff5539e52c");
        addRecipe("Sinh tố xoài", "10 phút", 4.5, RecipeCategory.DRINK, "https://images.unsplash.com/photo-1597959843363-6007a0c9c4b3");
        addRecipe("Sữa đậu nành", "15 phút", 4.2, RecipeCategory.DRINK, "https://images.unsplash.com/photo-1497534446932-c925b458314e");
    }

    private void addRecipe(String name,
                           String duration,
                           double rating,
                           RecipeCategory category,
                           String heroImageUrl,
                           String... stepImageUrls) {
        counter++;
        String id = generateId(name, counter);
        String description = String.format(Locale.getDefault(),
                "Hướng dẫn chi tiết giúp bạn chuẩn bị món %s thơm ngon nhanh chóng.", name);
        List<RecipeStep> steps = generateDefaultSteps(name, heroImageUrl, stepImageUrls);
        recipes.add(new Recipe(id, name, duration, rating, category, heroImageUrl, description, steps));
    }

    private List<RecipeStep> generateDefaultSteps(String name, String heroImageUrl, String... stepImageUrls) {
        List<RecipeStep> steps = new ArrayList<>();
        String[] urls = prepareStepUrls(heroImageUrl, stepImageUrls);
        steps.add(new RecipeStep(
                "Chuẩn bị nguyên liệu",
                String.format(Locale.getDefault(),
                        "Chuẩn bị đầy đủ nguyên liệu tươi sạch cho món %s. Rửa sạch, cắt thái theo yêu cầu của món ăn.",
                        name),
                urls[0]));
        steps.add(new RecipeStep(
                "Chế biến món ăn",
                String.format(Locale.getDefault(),
                        "Thực hiện các thao tác chính để chế biến món %s. Điều chỉnh lửa và nêm nếm vừa ăn.",
                        name),
                urls[1]));
        steps.add(new RecipeStep(
                "Hoàn thiện và thưởng thức",
                String.format(Locale.getDefault(),
                        "Trình bày món %s ra đĩa/bát đẹp mắt, thêm topping hoặc rau thơm tùy thích và thưởng thức khi còn nóng.",
                        name),
                urls[2]));
        return steps;
    }

    private String[] prepareStepUrls(String heroImageUrl, String... stepImageUrls) {
        String[] urls = new String[3];
        for (int i = 0; i < 3; i++) {
            if (stepImageUrls != null && i < stepImageUrls.length && stepImageUrls[i] != null && !stepImageUrls[i].trim().isEmpty()) {
                urls[i] = stepImageUrls[i].trim();
            } else {
                urls[i] = heroImageUrl;
            }
        }
        return urls;
    }

    private String generateId(String name, int index) {
        String base = name.toLowerCase(Locale.getDefault())
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return base + "_" + index;
    }
}
