package fpoly.ph62768.cooking.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fpoly.ph62768.cooking.model.BaiChoDuyet;
import fpoly.ph62768.cooking.model.Recipe;
import fpoly.ph62768.cooking.model.RecipeCategory;
import fpoly.ph62768.cooking.model.RecipeFeedback;
import fpoly.ph62768.cooking.model.RecipeStep;

public class RecipeRepository {

    private static RecipeRepository instance;
    private final List<Recipe> recipes = new ArrayList<>();
    private final Map<String, List<RecipeFeedback>> feedbackByRecipe = new HashMap<>();
    private int counter = 0;

    private RecipeRepository() {
        seedData();
    }

    public static RecipeRepository getInstance() {
        if (instance == null) instance = new RecipeRepository();
        return instance;
    }

    public List<Recipe> getRecipes() {
        return Collections.unmodifiableList(recipes);
    }

    public Recipe getRecipeById(String id) {
        if (id == null) return null;
        for (Recipe r : recipes) {
            if (id.equals(r.getId())) return r;
        }
        return null;
    }

    public List<RecipeFeedback> getFeedbackForRecipe(String recipeId) {
        if (recipeId == null) {
            return Collections.emptyList();
        }
        List<RecipeFeedback> feedback = feedbackByRecipe.get(recipeId);
        if (feedback == null) {
            feedback = new ArrayList<>(createSampleFeedback(recipeId));
            feedbackByRecipe.put(recipeId, feedback);
        }
        return Collections.unmodifiableList(feedback);
    }

    public void addFeedbackForRecipe(String recipeId, RecipeFeedback feedback) {
        if (recipeId == null || feedback == null) {
            return;
        }
        List<RecipeFeedback> list = feedbackByRecipe.get(recipeId);
        if (list == null) {
            list = new ArrayList<>();
            feedbackByRecipe.put(recipeId, list);
        }
        list.add(0, feedback);
    }

    public Recipe addUserRecipe(BaiChoDuyet pendingRecipe) {
        if (pendingRecipe == null) {
            return null;
        }
        Recipe existing = getRecipeById(pendingRecipe.getId());
        if (existing != null) {
            return existing;
        }

        double rating = Math.min(5f, Math.max(1f, pendingRecipe.getDiemDanhGia()));
        List<RecipeStep> steps = buildStepsFromPending(pendingRecipe);
        Recipe recipe = new Recipe(
                pendingRecipe.getId(),
                pendingRecipe.getTenMon(),
                pendingRecipe.getThoiGianNau(),
                rating,
                RecipeCategory.ALL,
                pendingRecipe.getAnhMon(),
                pendingRecipe.getMoTa(),
                steps
        );
        recipes.add(0, recipe);
        feedbackByRecipe.putIfAbsent(recipe.getId(), new ArrayList<>());
        return recipe;
    }

    private List<RecipeStep> buildStepsFromPending(BaiChoDuyet pendingRecipe) {
        List<RecipeStep> steps = new ArrayList<>();
        if (pendingRecipe == null) {
            return steps;
        }
        String rawSteps = pendingRecipe.getCongThucChiTiet();
        if (rawSteps != null) {
            String[] parts = rawSteps.split("\\r?\\n|\\u2022|\\|");
            int index = 1;
            for (String part : parts) {
                String trimmed = part == null ? "" : part.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String title = String.format(Locale.getDefault(), "Bước %d", index++);
                steps.add(new RecipeStep(title, trimmed, pendingRecipe.getAnhMon()));
            }
        }
        if (steps.isEmpty()) {
            steps.add(new RecipeStep("Bước 1", pendingRecipe.getCongThucChiTiet(), pendingRecipe.getAnhMon()));
        }
        return steps;
    }

    private void seedData() {
        recipes.clear();
        feedbackByRecipe.clear();
        counter = 0;
        addRecipe(
                "Trứng luộc",
                "10 phút",
                2.2,
                RecipeCategory.LOW_CAL,
                "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2024/6/28/trung-luoc-17195964533531755427733.jpg",
                "Trứng luộc đơn giản, giữ trọn dinh dưỡng và dễ chuẩn bị cho mọi bữa ăn.",
                new String[]{
                        "Chuẩn bị trứng",
                        "Luộc trứng",
                        "Thưởng thức trứng luộc"
                },
                new String[]{
                        "Chọn trứng tươi, rửa sạch vỏ trước khi chế biến.",
                        "Đun nồi nước sôi, cho trứng vào luộc 6-8 phút tùy độ chín mong muốn.",
                        "Vớt trứng ra, ngâm nước lạnh, bóc vỏ và thưởng thức cùng muối tiêu hoặc nước mắm gừng."
                },
                "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2024/6/28/trung-luoc-17195964533531755427733.jpg",
                "https://cdn.tgdd.vn/Files/2019/03/01/1151123/4-bi-quyet-luoc-trung-ngon-1_800x450.jpg",
                "https://cdn.tgdd.vn/2021/10/CookRecipe/GalleryStep/trinh-bay-1200x676.jpg"
        );
        addRecipe(
                "Cá hấp gừng",
                "25 phút",
                3.6,
                RecipeCategory.LOW_CAL,
                "https://haisanloccantho.com/wp-content/uploads/2024/10/nguyen-lieu-chuan-bi-cho-mon-ca-tram-hap-gung.jpg",
                "Cá hấp gừng thơm dịu, giữ vị ngọt tự nhiên của cá, thích hợp cho bữa cơm gia đình.",
                new String[]{
                        "Sơ chế nguyên liệu",
                        "Hấp cá",
                        "Trang trí và dọn bàn"
                },
                new String[]{
                        "Rửa sạch cá, khứa nhẹ thân cá. Gừng, hành lá, ớt thái sợi mỏng.",
                        "Xếp gừng hành dưới đáy, đặt cá lên, hấp khoảng 15 phút đến khi cá chín.",
                        "Rưới nước mắm gừng, rắc hành ngò và thưởng thức nóng."
                },
                "https://haisanloccantho.com/wp-content/uploads/2024/10/nguyen-lieu-chuan-bi-cho-mon-ca-tram-hap-gung.jpg",
                "https://cdn.tgdd.vn/Files/2021/02/01/1326340/ca-dieu-hong-hap-gung-11.jpg",
                "https://cdn.tgdd.vn/Files/2021/02/01/1326340/ca-dieu-hong-hap-gung-12.jpg"
        );
        addRecipe(
                "Canh bí đỏ",
                "30 phút",
                1.4,
                RecipeCategory.LOW_CAL,
                "https://cdn.tgdd.vn/2021/05/CookProduct/1-1200x676-41.jpg",
                "Canh bí đỏ ngọt dịu, bổ dưỡng, rất hợp dùng nóng cho bữa tối nhẹ.",
                new String[]{
                        "Chuẩn bị bí đỏ",
                        "Nấu canh",
                        "Nêm nếm hoàn thiện"
                },
                new String[]{
                        "Gọt vỏ bí đỏ, bỏ ruột, cắt khối vừa ăn, rửa sạch và để ráo.",
                        "Phi thơm hành với một ít dầu, cho bí vào đảo sơ rồi thêm nước đun sôi.",
                        "Nêm muối, hạt nêm, thêm hành ngò và tắt bếp."
                },
                "https://cdn.tgdd.vn/2021/05/CookProduct/1-1200x676-41.jpg",
                "https://img-global.cpcdn.com/steps/09fd8458571f60ff/160x128cq80/canh-bi-d%E1%BB%8F-c%E1%BA%A3i-b%E1%BA%B9-dun-n%E1%BA%A5u-tom-recipe-step-3-photo.webp",
                "https://img-global.cpcdn.com/steps/65834b0b79e2e703/160x128cq80/canh-bi-d%E1%BB%8F-c%E1%BA%A3i-b%E1%BA%B9-dun-n%E1%BA%A5u-tom-recipe-step-4-photo.webp"
        );
        addRecipe(
                "Salad rau củ",
                "15 phút",
                0.1,
                RecipeCategory.LOW_CAL,
                "https://cdn.zsoft.solutions/poseidon-web/app/media/Kham-pha-am-thuc/04.2024/120424-3-mon-salad-buffet-poseidon-04.jpg",
                "Salad rau củ thanh mát với nhiều loại rau và nước sốt béo nhẹ.",
                new String[]{
                        "Chuẩn bị rau củ",
                        "Trộn nước sốt",
                        "Phối trộn salad"
                },
                new String[]{
                        "Rửa sạch xà lách, cà chua bi, dưa leo và các loại rau yêu thích, để ráo.",
                        "Pha nước sốt với dầu oliu, giấm balsamic, chút muối tiêu và mật ong.",
                        "Trộn rau với nước sốt, thêm hạt và phô mai nếu thích."
                },
                "https://cdn.zsoft.solutions/poseidon-web/app/media/Kham-pha-am-thuc/04.2024/120424-3-mon-salad-buffet-poseidon-04.jpg",
                "https://cdn.tgdd.vn/Files/2019/07/03/1177377/3-cach-lam-salad-rau-cu-giam-can-hiu-qua-cho-nhung-nguoi-ban-ron-7.jpg",
                "https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-102.jpg"
        );
        addRecipe(
                "Đậu hũ hấp hành",
                "20 phút",
                4.3,
                RecipeCategory.LOW_CAL,
                "https://storage.googleapis.com/onelife-public/blog.onelife.vn/2021/11/cach-lam-djau-hu-hap-hanh-huong-mon-chay-523692620962.jpg",
                "Đậu hũ hấp hành thanh đạm, thơm mùi hành gừng, phù hợp bữa chay.",
                new String[]{
                        "Chuẩn bị đậu và hành",
                        "Hấp đậu hũ",
                        "Rưới nước sốt"
                },
                new String[]{
                        "Cắt đậu hũ thành lát, hành lá cắt nhỏ, gừng băm nhuyễn.",
                        "Xếp đậu vào đĩa, rắc hành gừng rồi đem hấp khoảng 10 phút.",
                        "Pha nước tương với dầu mè, đường, rưới lên đậu và thưởng thức."
                },
                "https://storage.googleapis.com/onelife-public/blog.onelife.vn/2021/11/cach-lam-djau-hu-hap-hanh-huong-mon-chay-523692620962.jpg",
                "https://cdn.tgdd.vn/Files/2020/11/20/1306295/cach-lam-dau-hu-hap-hanh-voi-nuoc-tuong-mam-ngon-nhat-202011201058462119.jpg",
                "https://cdn.tgdd.vn/2020/11/CookRecipe/GalleryStep/9-1200x676.jpg"
        );

        addRecipe(
                "Ức gà nướng rau củ",
                "35 phút",
                4.7,
                RecipeCategory.HEALTHY,
                "https://cdn.tgdd.vn/2020/08/CookProduct/Untitled-4-1200x676-5.jpg",
                "Ức gà nướng cùng rau củ đầy màu sắc, ít dầu mỡ và giàu protein.",
                new String[]{
                        "Ướp ức gà",
                        "Nướng gà và rau",
                        "Trình bày món ăn"
                },
                new String[]{
                        "Rửa sạch ức gà, ướp với dầu oliu, tỏi băm, tiêu và muối trong 15 phút.",
                        "Xếp gà và rau củ cắt miếng vào khay, nướng 20 phút ở 200°C.",
                        "Rưới thêm nước sốt ưa thích, trang trí lá thơm và dùng nóng."
                },
                "https://cdn.tgdd.vn/2020/08/CookProduct/Untitled-4-1200x676-5.jpg",
                "https://cdn.tgdd.vn/Files/2020/05/19/1256932/cach-lam-uc-ga-nuong-rau-cu-tai-nha-duoc-lieunghu-vi-ngon-chuan-restaurant-202005191650403616.jpg",
                "https://cdn.tgdd.vn/Files/2020/05/19/1256932/cach-lam-uc-ga-nuong-rau-cu-tai-nha-duoc-lieunghu-vi-ngon-chuan-restaurant-202005191650405475.jpg"
        );
        addRecipe(
                "Bánh chuối yến mạch",
                "40 phút",
                4.4,
                RecipeCategory.HEALTHY,
                "https://gimmedelicious.com/wp-content/uploads/2021/12/Oatmeal-Banana-Bread-9.jpg",
                "Bánh chuối yến mạch mềm ẩm, thơm mùi chuối chín và yến mạch.",
                new String[]{
                        "Chuẩn bị bột bánh",
                        "Đổ khuôn và nướng",
                        "Làm nguội và thưởng thức"
                },
                new String[]{
                        "Nghiền chuối chín, trộn cùng yến mạch, trứng, sữa và mật ong.",
                        "Đổ bột vào khuôn, thêm hạt hoặc trái cây khô nếu thích, nướng 30 phút ở 180°C.",
                        "Lấy bánh ra, để nguội bớt rồi cắt lát dùng với trà hoặc sữa."
                },
                "https://gimmedelicious.com/wp-content/uploads/2021/12/Oatmeal-Banana-Bread-9.jpg",
                "https://cdn.tgdd.vn/2020/09/CookRecipe/5-1200x676-9.jpg",
                "https://cdn.tgdd.vn/Files/2019/08/05/1185231/banh-chuoi-yen-mach-don-gian-dam-bao-thanh-cong-cho-nguoi-moi-bat-dau-201908051554172014.jpg"
        );
        addRecipe(
                "Súp rau củ",
                "30 phút",
                4.2,
                RecipeCategory.HEALTHY,
                "https://cdn.tgdd.vn/2021/09/CookProduct/thum-1200x676-2.jpg",
                "Súp rau củ ngọt tự nhiên, đậm đà và giàu chất xơ cho cả gia đình.",
                new String[]{
                        "Sơ chế rau củ",
                        "Nấu súp",
                        "Hoàn thiện súp"
                },
                new String[]{
                        "Cắt nhỏ cà rốt, khoai tây, bắp và các loại rau củ khác.",
                        "Phi hành tỏi, cho rau vào đảo sơ rồi thêm nước hoặc nước dùng, nấu chín mềm.",
                        "Nêm gia vị, thêm hành ngò hoặc kem tươi tùy thích, dùng nóng."
                },
                "https://cdn.tgdd.vn/2021/09/CookProduct/thum-1200x676-2.jpg",
                "https://cdn.tgdd.vn/Files/2020/02/20/1236143/bo-tui-3-cach-lam-sup-rau-cu-dam-da-dinh-duong-cho-bua-sang-1.jpg",
                "https://cdn.tgdd.vn/Files/2020/02/20/1236143/bo-tui-3-cach-lam-sup-rau-cu-dam-da-dinh-duong-cho-bua-sang-2.jpg"
        );
        addRecipe(
                "Miến xào nấm",
                "25 phút",
                4.3,
                RecipeCategory.HEALTHY,
                "https://img-global.cpcdn.com/recipes/f5813392126d249c/400x400cq80/photo.jpg",
                "Miến xào nấm chay, dai thơm và ít calo, hợp thực đơn eat clean.",
                new String[]{
                        "Chuẩn bị nguyên liệu",
                        "Xào miến với nấm",
                        "Nêm nếm và trình bày"
                },
                new String[]{
                        "Ngâm miến mềm, cắt nấm và rau củ vừa ăn, chuẩn bị nước xốt.",
                        "Phi hành, cho nấm và rau vào xào, thêm miến và xốt, đảo đều.",
                        "Nêm lại gia vị, thêm hành lá, mè rang rồi dọn ra đĩa."
                },
                "https://img-global.cpcdn.com/recipes/f5813392126d249c/400x400cq80/photo.jpg",
                "https://cdn.tgdd.vn/Files/2018/08/22/1114397/cach-lam-mien-xao-nam-chay-ngon-thom-1.jpg",
                "https://cdn.tgdd.vn/Files/2018/08/22/1114397/cach-lam-mien-xao-nam-chay-ngon-thom-6.jpg"
        );
        addRecipe(
                "Salad cá ngừ",
                "18 phút",
                4.6,
                RecipeCategory.HEALTHY,
                "https://cdn.tgdd.vn/2020/07/CookRecipe/Avatar/salad-ca-ngu-ngam-dau-voi-trung-luoc-thumbnail.jpg",
                "Salad cá ngừ nhiều đạm, kèm rau xanh và sốt chua ngọt dễ ăn.",
                new String[]{
                        "Chuẩn bị nguyên liệu",
                        "Pha nước sốt",
                        "Trộn salad cá ngừ"
                },
                new String[]{
                        "Rửa sạch rau, cắt nhỏ; cá ngừ hộp để ráo; trứng luộc cắt múi.",
                        "Pha sốt với mayonnaise, chanh, mật ong và tiêu.",
                        "Cho rau, cá ngừ, trứng vào tô, rưới sốt và trộn đều."
                },
                "https://cdn.tgdd.vn/2020/07/CookRecipe/Avatar/salad-ca-ngu-ngam-dau-voi-trung-luoc-thumbnail.jpg",
                "https://cdn.tgdd.vn/Files/2019/07/25/1176777/thu-vi-voi-3-cach-lam-salad-ca-ngu-cuc-ngon-khong-ngan-9.jpg",
                "https://cdn.tgdd.vn/2020/07/CookRecipe/GalleryStep/step-4-1200x676-14.jpg"
        );

        addRecipe(
                "Bánh mì trứng ốp la",
                "10 phút",
                4.1,
                RecipeCategory.QUICK,
                "https://img-global.cpcdn.com/recipes/01914f4be6cc4786/1200x630cq80/photo.jpg",
                "Bánh mì kẹp trứng ốp la nhanh gọn, thích hợp cho bữa sáng no lâu.",
                new String[]{
                        "Chuẩn bị nguyên liệu",
                        "Ốp la",
                        "Kẹp và thưởng thức"
                },
                new String[]{
                        "Chuẩn bị bánh mì, trứng, rau xà lách và gia vị yêu thích.",
                        "Chiên trứng ốp la với bơ hoặc dầu, nêm muối tiêu nhẹ.",
                        "Kẹp trứng cùng rau và sốt vào bánh mì, dùng nóng."
                },
                "https://img-global.cpcdn.com/recipes/01914f4be6cc4786/1200x630cq80/photo.jpg",
                "https://cdn.tgdd.vn/Files/2021/01/26/1321046/cach-lam-banh-mi-op-la-don-gian-tai-nha-202101260921563126.jpg",
                "https://cdn.tgdd.vn/Files/2021/01/26/1321046/cach-lam-banh-mi-op-la-don-gian-tai-nha-202101260921576334.jpg"
        );
        addRecipe(
                "Mì gói trộn rau",
                "12 phút",
                4.0,
                RecipeCategory.QUICK,
                "https://gocamthuc.acecookvietnam.vn/wp-content/uploads/2023/12/ACECOOK-day2-TOPVIEW-6-scaled.jpg",
                "Mì gói trộn kết hợp rau tươi, giảm ngấy và tăng chất xơ.",
                new String[]{
                        "Luộc mì và rau",
                        "Pha nước sốt trộn",
                        "Trộn mì với rau"
                },
                new String[]{
                        "Luộc mì gói vừa chín, trụng nhanh rau xanh rồi để ráo.",
                        "Pha nước sốt từ gói mì, thêm dầu mè, giấm và ớt băm.",
                        "Trộn mì, rau với nước sốt, rắc mè rang và thưởng thức."
                },
                "https://gocamthuc.acecookvietnam.vn/wp-content/uploads/2023/12/ACECOOK-day2-TOPVIEW-6-scaled.jpg",
                "https://cdn.tgdd.vn/Files/2020/07/02/1267848/3-cach-lam-mi-tron-da-dang-huong-vi-ma-lam-cuc-ky-nhanh-202007021623091837.jpg",
                "https://cdn.tgdd.vn/Files/2020/07/02/1267848/3-cach-lam-mi-tron-da-dang-huong-vi-ma-lam-cuc-ky-nhanh-202007021623101587.jpg"
        );
        addRecipe(
                "Cơm chiên trứng",
                "20 phút",
                4.3,
                RecipeCategory.QUICK,
                "https://www.huongnghiepaau.com/wp-content/uploads/2016/06/com-chien-toi-trung.jpg",
                "Cơm chiên trứng thơm bơ tỏi, nguyên liệu quen thuộc nhưng hấp dẫn.",
                new String[]{
                        "Chuẩn bị cơm và trứng",
                        "Chiên cơm",
                        "Thưởng thức"
                },
                new String[]{
                        "Chuẩn bị cơm nguội tơi, đánh tan trứng cùng gia vị.",
                        "Phi tỏi với bơ, cho cơm và trứng vào đảo nhanh tay đến khi dậy mùi.",
                        "Thêm hành lá, tiêu xay, dọn ra đĩa và ăn nóng."
                },
                "https://www.huongnghiepaau.com/wp-content/uploads/2016/06/com-chien-toi-trung.jpg",
                "https://cdn.tgdd.vn/Files/2017/09/04/1014918/cach-lam-com-chien-trung-truyen-thong-ngon-mieng-1.jpg",
                "https://cdn.tgdd.vn/Files/2017/09/04/1014918/cach-lam-com-chien-trung-truyen-thong-ngon-mieng-5.jpg"
        );
        addRecipe(
                "Sandwich rau củ",
                "8 phút",
                4.2,
                RecipeCategory.QUICK,
                "https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-102.jpg",
                "Sandwich rau củ tươi mát, ít calo, giàu vitamin.",
                new String[]{
                        "Chuẩn bị rau và bánh",
                        "Phết sốt",
                        "Hoàn thiện sandwich"
                },
                new String[]{
                        "Rửa sạch rau xà lách, cà chua, dưa leo; để ráo.",
                        "Phết mayonnaise hoặc sốt sữa chua lên lát bánh mì.",
                        "Xếp các lớp rau củ, thêm lát phô mai nếu thích rồi kẹp lại."
                },
                "https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-102.jpg",
                "https://cdn.tgdd.vn/2020/07/CookRecipe/GalleryStep/step-2-1200x676-21.jpg",
                "https://cdn.tgdd.vn/2020/12/CookRecipe/GalleryStep/thanh-pham-102.jpg"
        );
        addRecipe(
                "Cháo yến mạch",
                "15 phút",
                4.1,
                RecipeCategory.QUICK,
                "https://cdn.tgdd.vn/Files/2018/11/25/1133505/yen-mach-la-gi-cach-nau-chao-yen-mach-bo-duong-ngon-ngat-ngay-10.jpg",
                "Cháo yến mạch ấm bụng, dễ tiêu hóa và giàu dưỡng chất.",
                new String[]{
                        "Chuẩn bị yến mạch",
                        "Nấu cháo",
                        "Nêm và dùng"
                },
                new String[]{
                        "Chuẩn bị yến mạch cán mỏng, sữa hoặc nước, trái cây topping.",
                        "Đun yến mạch với sữa/nước đến khi sánh mịn.",
                        "Nêm mật ong hoặc trái cây, thưởng thức khi còn ấm."
                },
                "https://cdn.tgdd.vn/Files/2018/11/25/1133505/yen-mach-la-gi-cach-nau-chao-yen-mach-bo-duong-ngon-ngat-ngay-10.jpg",
                "https://cdn.tgdd.vn/2020/07/CookRecipe/GalleryStep/buoc-2-1200x676-12.jpg",
                "https://cdn.tgdd.vn/2020/07/CookRecipe/GalleryStep/thanh-pham-1200x676-32.jpg"
        );

        addRecipe(
                "Phở bò",
                "45 phút",
                4.8,
                RecipeCategory.TRADITIONAL,
                "https://cafefcdn.com/2018/7/19/photo-2-1531984647242593960017.png",
                "Phở bò truyền thống với nước dùng trong, thơm mùi quế hồi.",
                new String[]{
                        "Hầm nước dùng",
                        "Chuẩn bị topping",
                        "Trụng bánh phở và thưởng thức"
                },
                new String[]{
                        "Hầm xương bò với quế, hồi, gừng nướng trong vài giờ cho ngọt nước.",
                        "Chuẩn bị thịt bò, hành tây, rau thơm, chanh ớt ăn kèm.",
                        "Trụng bánh phở, xếp thịt và rau, chan nước dùng nóng."
                },
                "https://cafefcdn.com/2018/7/19/photo-2-1531984647242593960017.png",
                "https://cdn.tgdd.vn/Files/2018/08/17/1112079/cach-nau-pho-bo-tai-nha-ngon-chuan-vi-ha-noi-1.jpg",
                "https://cdn.tgdd.vn/Files/2018/08/17/1112079/cach-nau-pho-bo-tai-nha-ngon-chuan-vi-ha-noi-7.jpg"
        );
        addRecipe(
                "Bún riêu cua",
                "40 phút",
                4.5,
                RecipeCategory.TRADITIONAL,
                "https://cdn.xanhsm.com/2025/01/7f24de71-bun-rieu-quy-nhon-1.jpg",
                "Bún riêu cua chua thanh, đậm đà hương cua đồng.",
                new String[]{
                        "Làm riêu cua",
                        "Nấu nước dùng",
                        "Trình bày tô bún"
                },
                new String[]{
                        "Xay cua đồng, lọc lấy nước và đun nhẹ cho riêu đóng tảng.",
                        "Nấu nước dùng với cà chua, me chua, huyết và đậu hũ.",
                        "Trụng bún, xếp riêu, chan nước dùng, thêm rau sống và mắm tôm."
                },
                "https://cdn.xanhsm.com/2025/01/7f24de71-bun-rieu-quy-nhon-1.jpg",
                "https://cdn.tgdd.vn/Files/2017/07/07/1001457/cach-nau-bun-rieu-cua-dam-da-dung-chuan-huong-vi-cua-dong-2.jpg",
                "https://cdn.tgdd.vn/Files/2017/07/07/1001457/cach-nau-bun-rieu-cua-dam-da-dung-chuan-huong-vi-cua-dong-7.jpg"
        );
        addRecipe(
                "Bánh chưng",
                "120 phút",
                4.7,
                RecipeCategory.TRADITIONAL,
                "https://cdn.xanhsm.com/2025/01/62eda6b7-banh-chung-1.jpg",
                "Bánh chưng truyền thống cho ngày Tết, nhân đậu thịt đậm đà.",
                new String[]{
                        "Chuẩn bị nhân và gạo",
                        "Gói bánh",
                        "Luộc bánh"
                },
                new String[]{
                        "Vo gạo nếp, ngâm đậu xanh và ướp thịt ba chỉ.",
                        "Xếp lá dong, đổ gạo, nhân đậu thịt và gói chặt bằng lạt.",
                        "Luộc bánh 8-10 giờ, vớt ra ép để ráo nước và nguội."
                },
                "https://cdn.xanhsm.com/2025/01/62eda6b7-banh-chung-1.jpg",
                "https://cdn.tgdd.vn/2020/01/CookRecipe/GalleryStep/thanh-pham-1200x676-11.jpg",
                "https://cdn.tgdd.vn/2021/01/CookRecipe/GalleryStep/step-14-1200x676-5.jpg"
        );
        addRecipe(
                "Nem rán",
                "60 phút",
                4.6,
                RecipeCategory.TRADITIONAL,
                "https://thewoksoflife.com/wp-content/uploads/2020/08/cha-gio-vietnamese-fried-spring-rolls-17.jpg",
                "Nem rán giòn rụm với nhân thịt và rau củ hài hòa.",
                new String[]{
                        "Trộn nhân nem",
                        "Gói nem",
                        "Chiên nem"
                },
                new String[]{
                        "Trộn thịt bằm với mộc nhĩ, miến, cà rốt và gia vị.",
                        "Trải bánh tráng, cho nhân vào và cuộn chặt tay.",
                        "Chiên ngập dầu đến khi vàng đều, vớt ra giấy thấm dầu và ăn cùng nước mắm chua ngọt."
                },
                "https://thewoksoflife.com/wp-content/uploads/2020/08/cha-gio-vietnamese-fried-spring-rolls-17.jpg",
                "https://cdn.tgdd.vn/Files/2020/09/21/1291953/cach-lam-nem-ran-cuc-gion-thom-ngon-don-gian-tai-nha-202009211355265394.jpg",
                "https://cdn.tgdd.vn/Files/2020/09/21/1291953/cach-lam-nem-ran-cuc-gion-thom-ngon-don-gian-tai-nha-202009211402021941.jpg"
        );
        addRecipe(
                "Canh chua cá",
                "35 phút",
                4.4,
                RecipeCategory.TRADITIONAL,
                "https://i-giadinh.vnecdn.net/2023/04/25/Thanh-pham-1-1-7239-1682395675.jpg",
                "Canh chua cá miền Tây chua ngọt, thơm mùi rau thơm và trái thơm.",
                new String[]{
                        "Sơ chế cá",
                        "Nấu canh chua",
                        "Nêm nếm cuối"
                },
                new String[]{
                        "Làm sạch cá, cắt khúc và ướp nhẹ với muối tiêu.",
                        "Đun nước cùng dứa, cà chua, bạc hà, cho cá vào nấu chín.",
                        "Nêm nước mắm, đường, me chua; thêm rau thơm và ớt khi tắt bếp."
                },
                "https://i-giadinh.vnecdn.net/2023/04/25/Thanh-pham-1-1-7239-1682395675.jpg",
                "https://cdn.tgdd.vn/Files/2020/05/05/1253305/cach-nau-canh-chua-ca-qua-thom-ngon-202005051807418275.jpg",
                "https://cdn.tgdd.vn/Files/2020/05/05/1253305/cach-nau-canh-chua-ca-qua-thom-ngon-202005051807424094.jpg"
        );

        addRecipe(
                "Chè đậu xanh",
                "45 phút",
                4.2,
                RecipeCategory.DESSERT,
                "https://images.unsplash.com/photo-1604908177522-4023ac76fae1?crop=entropy",
                "Chè đậu xanh mát lạnh, độ ngọt vừa phải, ăn kèm đá hoặc nước cốt dừa.",
                new String[]{
                        "Ngâm và nấu đậu",
                        "Thêm đường",
                        "Hoàn thiện chè"
                },
                new String[]{
                        "Đãi sạch đậu xanh, ngâm nước 2 giờ rồi nấu mềm.",
                        "Cho đường phèn vào nồi đậu, khuấy nhẹ cho tan đều.",
                        "Thêm vani hoặc nước cốt dừa, dùng nóng hoặc thêm đá lạnh."
                },
                "https://images.unsplash.com/photo-1604908177522-4023ac76fae1?crop=entropy",
                "https://cdn.tgdd.vn/Files/2017/05/31/992097/cach-nau-che-dau-xanh-bot-ban-ngon-tai-nha-201707031521138388.jpg",
                "https://cdn.tgdd.vn/Files/2017/05/31/992097/cach-nau-che-dau-xanh-bot-ban-ngon-tai-nha-201707031521148611.jpg"
        );
        addRecipe(
                "Bánh flan",
                "90 phút",
                4.6,
                RecipeCategory.DESSERT,
                "https://images.unsplash.com/photo-1515003197210-e0cd71810b5f",
                "Bánh flan béo mịn với lớp caramel thơm ngọt.",
                new String[]{
                        "Làm caramel",
                        "Đánh hỗn hợp trứng sữa",
                        "Hấp bánh flan"
                },
                new String[]{
                        "Đun đường đến khi ngả vàng rồi đổ vào khuôn.",
                        "Đánh trứng với sữa và vani, lọc qua rây cho mịn.",
                        "Đổ hỗn hợp vào khuôn caramel, hấp cách thủy 35 phút và làm lạnh."
                },
                "https://images.unsplash.com/photo-1515003197210-e0cd71810b5f",
                "https://cdn.tgdd.vn/Files/2018/09/18/1114591/cach-lam-banh-flan-sua-tuoi-khong-bi-tanh-201809181437202001.jpg",
                "https://cdn.tgdd.vn/Files/2018/09/18/1114591/cach-lam-banh-flan-sua-tuoi-khong-bi-tanh-201809181437238906.jpg"
        );
        addRecipe(
                "Bánh chuối hấp",
                "60 phút",
                4.4,
                RecipeCategory.DESSERT,
                "https://images.unsplash.com/photo-1514996937319-344454492b37?crop=entropy",
                "Bánh chuối hấp mềm dai, thơm bùi, ăn kèm nước cốt dừa béo ngậy.",
                new String[]{
                        "Chuẩn bị bột chuối",
                        "Hấp bánh",
                        "Làm nước cốt và thưởng thức"
                },
                new String[]{
                        "Trộn chuối chín với bột năng, đường và dừa nạo.",
                        "Đổ hỗn hợp vào khuôn, hấp khoảng 30 phút đến khi bánh chín.",
                        "Nấu nước cốt dừa với bột năng, ăn kèm bánh chuối khi còn ấm."
                },
                "https://images.unsplash.com/photo-1514996937319-344454492b37?crop=entropy",
                "https://cdn.tgdd.vn/Files/2018/02/09/1062474/cach-lam-banh-chuoi-hap-don-gian-thom-ngon-cho-gia-dinh-4.jpg",
                "https://cdn.tgdd.vn/2019/10/CookRecipe/Avatar/banh-chuoi-hap-thu-dau-nut-1200x676.jpg"
        );
        addRecipe(
                "Kem dừa",
                "25 phút",
                4.5,
                RecipeCategory.DESSERT,
                "https://images.unsplash.com/photo-1469428946641-83c13ea94ceb",
                "Kem dừa béo ngậy, thơm mùi vani và dừa nạo, giải nhiệt ngày nóng.",
                new String[]{
                        "Chuẩn bị hỗn hợp kem",
                        "Làm lạnh",
                        "Thưởng thức"
                },
                new String[]{
                        "Đun sữa dừa với đường và kem tươi đến khi hòa tan.",
                        "Đổ hỗn hợp vào khuôn, làm lạnh 6 giờ hoặc dùng máy làm kem.",
                        "Múc kem ra ly, thêm dừa nạo và lạc rang nếu thích."
                },
                "https://images.unsplash.com/photo-1469428946641-83c13ea94ceb",
                "https://cdn.tgdd.vn/Files/2018/06/14/1194070/cach-lam-kem-dua-thom-ngon-intenso-la-mieng-202106140938020343.jpg",
                "https://cdn.tgdd.vn/Files/2018/06/14/1194070/cach-lam-kem-dua-thom-ngon-intenso-la-mieng-202106140938026611.jpg"
        );
        addRecipe(
                "Chè bắp",
                "50 phút",
                4.3,
                RecipeCategory.DESSERT,
                "https://images.unsplash.com/photo-1604908177522-4023ac76fae1?ixlib=rb-4.0.3",
                "Chè bắp ngọt thơm hương bắp non, ăn nóng hay lạnh đều ngon.",
                new String[]{
                        "Sơ chế bắp",
                        "Nấu chè",
                        "Thêm topping"
                },
                new String[]{
                        "Bào bắp non, giữ lại nước ngọt, chuẩn bị bột năng và nước cốt dừa.",
                        "Nấu bắp với nước đến khi mềm, cho đường và bột năng hòa tan vào khuấy đều.",
                        "Múc chè ra chén, rưới nước cốt dừa và rắc mè rang."
                },
                "https://images.unsplash.com/photo-1604908177522-4023ac76fae1?ixlib=rb-4.0.3",
                "https://cdn.tgdd.vn/Files/2018/03/07/1072559/cach-nau-che-bap-nuoc-cot-dua-don-gian-thom-ngon-cho-gia-dinh-4.jpg",
                "https://cdn.tgdd.vn/Files/2018/03/07/1072559/cach-nau-che-bap-nuoc-cot-dua-don-gian-thom-ngon-cho-gia-dinh-6.jpg"
        );

        addRecipe(
                "Trà đá",
                "5 phút",
                4.0,
                RecipeCategory.DRINK,
                "https://images.unsplash.com/photo-1481391319762-47dff72954d1",
                "Trà đá mát lạnh, giải khát nhanh chóng trong ngày nắng.",
                new String[]{
                        "Pha trà",
                        "Làm lạnh",
                        "Thưởng thức"
                },
                new String[]{
                        "Hãm trà đen với nước sôi khoảng 5 phút.",
                        "Thêm đường hoặc mật ong nếu thích và để nguội, thêm đá.",
                        "Rót ra ly, thêm lát chanh hoặc lá bạc hà và thưởng thức."
                },
                "https://images.unsplash.com/photo-1481391319762-47dff72954d1",
                "https://cdn.tgdd.vn/Files/2017/08/01/1005179/cach-lam-tra-da-chua-ngot-dung-vi-3.jpg",
                "https://cdn.tgdd.vn/2020/07/CookRecipe/GalleryStep/thanh-pham-1200x676-86.jpg"
        );
        addRecipe(
                "Cà phê sữa đá",
                "7 phút",
                4.7,
                RecipeCategory.DRINK,
                "https://images.unsplash.com/photo-1509042239860-f550ce710b93",
                "Cà phê sữa đá đậm vị cà phê Việt Nam, ngọt béo vừa phải.",
                new String[]{
                        "Pha cà phê",
                        "Thêm sữa đặc",
                        "Khuấy và thêm đá"
                },
                new String[]{
                        "Pha cà phê phin với nước sôi, đợi nhỏ giọt hết.",
                        "Cho sữa đặc vào ly, đổ cà phê nóng lên và khuấy đều.",
                        "Thêm đá viên, nhấc ly lên cao rót lại để cà phê hòa quyện và thưởng thức."
                },
                "https://images.unsplash.com/photo-1509042239860-f550ce710b93",
                "https://cdn.tgdd.vn/Files/2018/01/12/1054154/cach-pha-ca-phe-phin-thom-ngon-chuan-vi-pha-vietnamese-coffee-6.jpg",
                "https://cdn.tgdd.vn/2021/08/CookRecipe/GalleryStep/thanh-pham-1200x676-197.jpg"
        );
        addRecipe(
                "Nước mía",
                "5 phút",
                4.1,
                RecipeCategory.DRINK,
                "https://images.unsplash.com/photo-1527169402691-feff5539e52c",
                "Nước mía ngọt thanh, giàu khoáng chất, giải khát tức thì.",
                new String[]{
                        "Chuẩn bị mía",
                        "Ép mía",
                        "Thưởng thức"
                },
                new String[]{
                        "Rửa sạch cây mía, chẻ nhỏ để ép.",
                        "Ép mía với máy ép chuyên dụng, có thể thêm tắc hoặc quất.",
                        "Rót ra ly có đá, thêm lát tắc và lá bạc hà nếu thích."
                },
                "https://images.unsplash.com/photo-1527169402691-feff5539e52c",
                "https://cdn.tgdd.vn/2020/08/CookRecipe/GalleryStep/ep-nuoc-mia-1200x676.jpg",
                "https://cdn.tgdd.vn/Files/2021/05/19/1350733/cach-pha-nuoc-mia-ngon-ngot-thanh-khong-bi-dong-202105191201567492.jpg"
        );
        addRecipe(
                "Sinh tố xoài",
                "10 phút",
                4.5,
                RecipeCategory.DRINK,
                "https://images.unsplash.com/photo-1577801591907-673bfd31e313",
                "Sinh tố xoài mát lạnh, bổ dưỡng cho những ngày hè.",
                new String[]{
                        "Chuẩn bị nguyên liệu",
                        "Xay và nêm",
                        "Trang trí và thưởng thức",
                        "Nấu và thưởng thức",
                        "Nấu và thưởng thức"
                },
                new String[]{
                        "Rửa sạch xoài, gọt vỏ, cắt miếng. Chuẩn bị sữa chua, đá viên, đường và lá bạc hà.",
                        "Cho xoài, sữa, đá vào máy xay. Xay nhuyễn, nêm đường vừa khẩu vị.",
                        "Rót sinh tố ra ly, thêm lát xoài và lá bạc hà để thơm ngon hơn.",
                        "A"
                },
                "https://tse3.mm.bing.net/th/id/OIP.Xr91TFrV1zXUvPa009Uc1QHaEh?pid=Api&P=0&h=180",
                "https://tse4.mm.bing.net/th/id/OIP.HMMWJzDgF3G45ZlmrN25bAHaEo?pid=Api&P=0&h=180",
                "https://images.unsplash.com/photo-1521302080334-4bebac2769c2"
        );

        addRecipe(
                "Sữa đậu nành",
                "15 phút",
                4.2,
                RecipeCategory.DRINK,
                "https://images.unsplash.com/photo-1497534446932-c925b458314e",
                "Sữa đậu nành thơm ngon, bổ dưỡng cho cả gia đình.",
                new String[]{
                        "Ngâm và rửa đậu",
                        "Xay và lọc",
                        "Nấu và thưởng thức"
                },
                new String[]{
                        "Ngâm đậu nành với nước sạch từ 6-8 giờ. Sau đó rửa lại nhiều lần cho sạch vỏ và mùi hăng.",
                        "Cho đậu đã ngâm vào máy xay cùng nước, xay nhuyễn rồi lọc qua túi vải để lấy phần sữa mịn.",
                        "Cho sữa vào nồi, nấu với lửa vừa, khuấy liên tục. Nêm đường tùy thích, rót ra ly và dùng nóng hay lạnh đều ngon."
                },
                "https://images.unsplash.com/photo-1504753793650-d4a2b783c15e",
                "https://images.unsplash.com/photo-1523475472560-d2df97ec485c",
                "https://images.unsplash.com/photo-1481391319762-47dff72954d1"
        );
        Collections.shuffle(recipes, new Random());
    }

    private void addRecipe(String name,
                           String duration,
                           double rating,
                           RecipeCategory category,
                           String heroImageUrl) {
        addRecipe(name, duration, rating, category, heroImageUrl, new String[0]);
    }

    private void addRecipe(String name,
                           String duration,
                           double rating,
                           RecipeCategory category,
                           String heroImageUrl,
                           String... stepImageUrls) {
        addRecipe(name, duration, rating, category, heroImageUrl, null, null, null, stepImageUrls);
    }

    private void addRecipe(String name,
                           String duration,
                           double rating,
                           RecipeCategory category,
                           String heroImageUrl,
                           String description,
                           String[] stepTitles,
                           String[] stepDescriptions,
                           String... stepImageUrls) {
        counter++;
        String id = generateId(name, counter);
        String finalDescription = (description != null && !description.trim().isEmpty())
                ? description.trim()
                : String.format(Locale.getDefault(),
                "Hướng dẫn chi tiết giúp bạn chuẩn bị món %s thơm ngon nhanh chóng.", name);
        List<RecipeStep> steps = buildSteps(name, heroImageUrl, stepTitles, stepDescriptions, stepImageUrls);
        recipes.add(new Recipe(id, name, duration, rating, category, heroImageUrl, finalDescription, steps));
        feedbackByRecipe.putIfAbsent(id, new ArrayList<>(createSampleFeedbackFromName(name)));
    }

    private List<RecipeStep> buildSteps(String name,
                                        String heroImageUrl,
                                        String[] stepTitles,
                                        String[] stepDescriptions,
                                        String... stepImageUrls) {
        List<RecipeStep> steps = new ArrayList<>();
        int stepCount = 3;
        if (stepTitles != null) {
            stepCount = Math.max(stepCount, stepTitles.length);
        }
        if (stepDescriptions != null) {
            stepCount = Math.max(stepCount, stepDescriptions.length);
        }
        if (stepImageUrls != null) {
            stepCount = Math.max(stepCount, stepImageUrls.length);
        }

        for (int i = 0; i < stepCount; i++) {
            String title = getStepTitle(stepTitles, i);
            String description = getStepDescription(stepDescriptions, i, name);
            String imageUrl = getStepImage(heroImageUrl, stepImageUrls, i);
            steps.add(new RecipeStep(title, description, imageUrl));
        }
        return steps;
    }

    private List<RecipeFeedback> createSampleFeedback(String recipeId) {
        Recipe recipe = getRecipeById(recipeId);
        String recipeName = recipe != null ? recipe.getName() : recipeId;
        return createSampleFeedbackFromName(recipeName);
    }

    private List<RecipeFeedback> createSampleFeedbackFromName(String recipeName) {
        List<RecipeFeedback> defaults = new ArrayList<>();
        defaults.add(new RecipeFeedback(
                "Minh Anh",
                4.5f,
                String.format(Locale.getDefault(),
                        "Món %s rất dễ làm và hương vị tuyệt vời. Cảm ơn vì công thức chi tiết!",
                        recipeName),
                "2 ngày trước"));
        defaults.add(new RecipeFeedback(
                "Hoàng Nam",
                5.0f,
                "Gia đình mình rất thích món này, mình sẽ làm lại vào cuối tuần tới.",
                "1 tuần trước"));
        return defaults;
    }

    private String getStepTitle(String[] customTitles, int index) {
        if (customTitles != null && index < customTitles.length && customTitles[index] != null && !customTitles[index].trim().isEmpty()) {
            return customTitles[index].trim();
        }
        switch (index) {
            case 0:
                return "Chuẩn bị nguyên liệu";
            case 1:
                return "Chế biến món ăn";
            case 2:
                return "Hoàn thiện và thưởng thức";
            default:
                return String.format(Locale.getDefault(), "Bước %d", index + 1);
        }
    }

    private String getStepDescription(String[] customDescriptions, int index, String recipeName) {
        if (customDescriptions != null && index < customDescriptions.length && customDescriptions[index] != null && !customDescriptions[index].trim().isEmpty()) {
            return customDescriptions[index].trim();
        }

        switch (index) {
            case 0:
                return String.format(Locale.getDefault(),
                        "Chuẩn bị đầy đủ nguyên liệu tươi sạch cho món %s. Rửa sạch, cắt thái theo yêu cầu của món ăn.",
                        recipeName);
            case 1:
                return String.format(Locale.getDefault(),
                        "Thực hiện các thao tác chính để chế biến món %s. Điều chỉnh lửa và nêm nếm vừa ăn.",
                        recipeName);
            default:
                return String.format(Locale.getDefault(),
                        "Trình bày món %s ra đĩa/bát đẹp mắt, thêm topping hoặc rau thơm tùy thích và thưởng thức khi còn nóng.",
                        recipeName);
        }
    }

    private String getStepImage(String heroImageUrl, String[] stepImageUrls, int index) {
        if (stepImageUrls != null && index < stepImageUrls.length && stepImageUrls[index] != null && !stepImageUrls[index].trim().isEmpty()) {
            return stepImageUrls[index].trim();
        }
        return heroImageUrl;
    }

    private String generateId(String name, int index) {
        String base = name.toLowerCase(Locale.getDefault())
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return base + "_" + index;
    }
}
