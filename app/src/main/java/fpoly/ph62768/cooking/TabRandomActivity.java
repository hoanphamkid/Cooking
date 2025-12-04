package fpoly.ph62768.cooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TabRandomActivity extends AppCompatActivity {
    // Khai báo Views
    private CardView cardFoodResult;
    private ImageView imgFood;
    private TextView tvFoodName, tvCookingTime, tvDifficulty, tvDescription;
    private Button btnRandom, btnViewDetail;

    // Danh sách món ăn mẫu (Mock Data)
    private List<FoodModel> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_random);

        // 1. Ánh xạ View
        initViews();

        // 2. Chuẩn bị dữ liệu
        setupMockData();

        // 3. Xử lý sự kiện click
        setupListeners();
    }

    private void initViews() {
        cardFoodResult = findViewById(R.id.card_food_result);
        imgFood = findViewById(R.id.img_food);
        tvFoodName = findViewById(R.id.tv_food_name);
        tvCookingTime = findViewById(R.id.tv_cooking_time);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        tvDescription = findViewById(R.id.tv_description);
        btnRandom = findViewById(R.id.btn_random);
        btnViewDetail = findViewById(R.id.btn_view_detail);
    }

    private void setupMockData() {
        foodList = new ArrayList<>();
        // Thêm dữ liệu giả lập cho Cooking App
        foodList.add(new FoodModel("Phở Bò Tái Nạm", "45 phút", "Trung bình", "Món nước truyền thống với nước dùng đậm đà, thịt bò mềm và bánh phở dai."));
        foodList.add(new FoodModel("Sườn Xào Chua Ngọt", "30 phút", "Dễ", "Sườn non chiên vàng sốt cà chua, giấm, đường tạo vị chua ngọt hài hòa."));
        foodList.add(new FoodModel("Cá Kho Tộ", "60 phút", "Khó", "Cá lóc hoặc cá basa kho trong nồi đất với nước màu dừa, tiêu và ớt."));
        foodList.add(new FoodModel("Canh Chua Cá Lóc", "40 phút", "Trung bình", "Vị chua thanh mát từ me, kết hợp với cá lóc đồng và rau thơm."));
        foodList.add(new FoodModel("Thịt Kho Tàu", "90 phút", "Dễ", "Thịt ba chỉ kho mềm với trứng vịt, nước dừa tươi, màu sắc hấp dẫn."));
        foodList.add(new FoodModel("Gà Chiên Nước Mắm", "35 phút", "Dễ", "Cánh gà chiên giòn rụm áo lớp sốt nước mắm tỏi ớt đậm đà."));
        foodList.add(new FoodModel("Rau Muống Xào Tỏi", "10 phút", "Rất dễ", "Món rau dân dã, xanh mướt, giòn sần sật và thơm lừng mùi tỏi."));
    }

    private void setupListeners() {
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRandomAnimation();
            }
        });

        btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên món hiện tại
                String currentFood = tvFoodName.getText().toString();
                Toast.makeText(TabRandomActivity.this, "Đang mở công thức: " + currentFood, Toast.LENGTH_SHORT).show();
                // TODO: Intent intent = new Intent(Tab_RandomActivity.this, DetailActivity.class);
                // startActivity(intent);
            }
        });
    }

    // Hiệu ứng Fade out -> Đổi dữ liệu -> Fade in
    private void performRandomAnimation() {
        btnRandom.setEnabled(false); // Khóa nút để tránh click liên tục

        cardFoodResult.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // Khi ẩn xong thì đổi dữ liệu
                        showRandomFood();
                        // Hiện lại
                        cardFoodResult.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        btnRandom.setEnabled(true); // Mở lại nút
                                    }
                                });
                    }
                });
    }

    private void showRandomFood() {
        if (foodList == null || foodList.isEmpty()) return;

        // Random logic
        Random random = new Random();
        int index = random.nextInt(foodList.size());
        FoodModel randomFood = foodList.get(index);

        // Update UI
        tvFoodName.setText(randomFood.getName());
        tvCookingTime.setText(randomFood.getTime());
        tvDifficulty.setText("Độ khó: " + randomFood.getDifficulty());
        tvDescription.setText(randomFood.getDescription());

        // Hiện nút xem chi tiết nếu chưa hiện
        if (btnViewDetail.getVisibility() == View.GONE) {
            btnViewDetail.setVisibility(View.VISIBLE);
        }

        // Đổi màu ảnh đại diện ngẫu nhiên (Giả lập ảnh khác nhau)
        // Trong thực tế bạn sẽ dùng Glide/Picasso để load URL ảnh
        int[] colors = {0xFFFFCCBC, 0xFFFFE0B2, 0xFFC8E6C9, 0xFFB3E5FC, 0xFFE1BEE7};
        imgFood.setBackgroundColor(colors[index % colors.length]);
    }

    // Class nội bộ để chứa dữ liệu món ăn
    public static class FoodModel {
        private String name;
        private String time;
        private String difficulty;
        private String description;

        public FoodModel(String name, String time, String difficulty, String description) {
            this.name = name;
            this.time = time;
            this.difficulty = difficulty;
            this.description = description;
        }

        public String getName() { return name; }
        public String getTime() { return time; }
        public String getDifficulty() { return difficulty; }
        public String getDescription() { return description; }
    }
}