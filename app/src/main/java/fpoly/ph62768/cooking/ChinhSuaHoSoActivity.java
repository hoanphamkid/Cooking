package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class ChinhSuaHoSoActivity extends AppCompatActivity {

    // Khai báo các biến View tương ứng với XML
    private MaterialToolbar toolbar;
    private ImageView imgAvatar;
    private Button btnChangeAvatar, btnCancel, btnSave;

    private TextInputEditText edtName, edtEmail, edtPhone, edtBio, edtAddress, edtWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh_sua_ho_so);

        // 1. Ánh xạ View từ XML sang Java
        initViews();

        // 2. Đổ dữ liệu giả định (để test giao diện)
        loadMockData();

        // 3. Thiết lập các sự kiện click (Listeners)
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.edit_profile_toolbar);
        imgAvatar = findViewById(R.id.edit_profile_avatar);
        btnChangeAvatar = findViewById(R.id.edit_profile_change_avatar_btn);

        // Inputs
        edtName = findViewById(R.id.edit_profile_name_input);
        edtEmail = findViewById(R.id.edit_profile_email_input);
        edtPhone = findViewById(R.id.edit_profile_phone_input);
        edtBio = findViewById(R.id.edit_profile_bio_input);
        edtAddress = findViewById(R.id.edit_profile_address_input);
        edtWebsite = findViewById(R.id.edit_profile_website_input);

        // Action Buttons
        btnCancel = findViewById(R.id.edit_profile_cancel_btn);
        btnSave = findViewById(R.id.edit_profile_save_btn);
    }

    private void loadMockData() {
        // Giả lập dữ liệu người dùng hiện tại
        edtName.setText("Nguyễn Văn A");
        edtEmail.setText("nguyenvana@gmail.com"); // Ô này đã set enabled=false trong XML
        edtPhone.setText("0912345678");
        edtBio.setText("Lập trình viên Mobile đam mê công nghệ.");
        edtAddress.setText("Số 1, Đại Cồ Việt, Hà Nội");
        edtWebsite.setText("https://github.com/nguyenvana");
    }

    private void setupListeners() {
        // Xử lý nút Back trên Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc Activity hiện tại để quay lại màn hình trước
                finish();
            }
        });

        // Xử lý nút Thay đổi ảnh đại diện
        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Mở thư viện ảnh ở đây.
                // Tạm thời hiển thị Toast để demo không lỗi.
                Toast.makeText(ChinhSuaHoSoActivity.this, "Chức năng chọn ảnh đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Hủy
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình chỉnh sửa
            }
        });

        // Xử lý nút Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        // Lấy dữ liệu từ các ô nhập liệu
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Validate cơ bản (Kiểm tra dữ liệu nhập vào)
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Tên hiển thị không được để trống");
            edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Số điện thoại không được để trống");
            edtPhone.requestFocus();
            return;
        }

        // TODO: Gọi API hoặc lưu vào Database ở đây

        // Hiển thị thông báo thành công
        Toast.makeText(this, "Đã lưu thay đổi hồ sơ!", Toast.LENGTH_SHORT).show();

        // Đóng màn hình sau khi lưu thành công
        finish();
    }
}