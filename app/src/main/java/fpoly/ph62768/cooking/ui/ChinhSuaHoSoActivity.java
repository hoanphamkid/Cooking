package fpoly.ph62768.cooking.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import fpoly.ph62768.cooking.GiaoDienTrangChuActivity;
import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.auth.UserAccount;
import fpoly.ph62768.cooking.auth.UserAccountManager;

public class ChinhSuaHoSoActivity extends AppCompatActivity {
    private ImageView avatarImage;
    private TextInputEditText nameInput, emailInput, phoneInput, bioInput, addressInput, websiteInput;
    private Button saveButton, cancelButton, changeAvatarButton;
    private MaterialToolbar toolbar;

    private UserAccountManager accountManager;
    private String currentUserEmail, currentUserName;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chinh_sua_ho_so);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
        Intent intent = getIntent();
        currentUserEmail = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_EMAIL);
        currentUserName = intent.getStringExtra(GiaoDienTrangChuActivity.EXTRA_USER_NAME);
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            currentUserEmail = accountManager.getCurrentUserEmail(this);
        }

        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Kh", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentUserName == null || currentUserName.isEmpty()) {
            currentUserName = accountManager.getCurrentUserName(this);
        }

        if (currentUserName == null || currentUserName.isEmpty()) {
            Toast.makeText(this, "Kh", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setupToolbar();
        setupActivityLaunchers();
        loadUserData();
        setupButtonListeners();
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupActivityLaunchers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), result -> {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                selectedImageUri = result.getData().getData();
                                loadAvatar(selectedImageUri.toString());
                            }
        });

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                    loadAvatar(selectedImageUri.toString());
                }
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        takePicturePickerDialog();
                    } else {
                        Toast.makeText(this, "Cần cấp quyền thay đổi ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
        });
    }

  private void loadUserData() {
      UserAccount account = accountManager.getAccount(currentUserEmail);
      if (account != null) {
          nameInput.setText(account.getName());
          emailInput.setText(currentUserEmail);
          phoneInput.setText(account.getPhone());
          bioInput.setText(account.getBio());
          addressInput.setText(account.getAddress());
          websiteInput.setText(account.getWebsite());

          String avatarUrl = account.getAvatarUrl();
          if (avatarUrl != null && !avatarUrl.isEmpty()) {
              loadAvatar(avatarUrl);
          } else {
              loadAvatar("https://images.unsplash.com/photo-1524504388940-b1c1722653e1");
          }
      }
  }

  private void loadAvatar(String url) {
      Glide.with(this)
              .load(url)
              .placeholder(R.drawable.ic_profile_placeholder)
              .circleCrop()
              .into(avatarImage);
  }

  private void setupButtonListeners() {
        changeAvatarButton.setOnClickListener(v -> requestPermission());
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setTitle("Hủy thay đổi").setMessage("Bạn có muốn hủy các thay đổi?")
                    .setPositiveButton("Có", (dialog, which) -> finish())
                            .setNegativeButton("Không", null)
                            .show();
        });
  }

  private void checkPermissionAndShowPicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        } else {
            showImagePickerDialog();
        }
  }

    private void showImagePickerDialog() {
        String[] options = {"Chọn ảnh từ thư viện", "Chụp ảnh"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh đại diện")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                }).show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }
    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickPhotoIntent);
    }

    private void saveProfile() {
        String name = nameInput.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Vui lòng nhập tên hiển thị");
            nameInput.requestFocus();
            return;
        }

        String phone = phoneInput.getText().toString();
        String bio = bioInput.getText().toString();
        String address = addressInput.getText().toString();
        String website = websiteInput.getText().toString();

        UserAccount account = accountManager.getAccount(currentUserEmail);
        if (account != null) {
            account.setName(name);
            account.setPhone(phone);
            account.setBio(bio);
            account.setAddress(address);
            account.setWebsite(website);

            if (selectedImageUri != null) {
                account.setAvatarUrl(selectedImageUri.toString());
            }
            boolean success = accountManager.updateAccount(account);
            if (success) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("profile_uploaded", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Kiểm tra xem có thay đổi nào chưa lưu không
        UserAccount currentAccount = accountManager.getAccount(currentUserEmail);
        if (currentAccount != null && hasChanges(currentAccount)) {
            new AlertDialog.Builder(this)
                    .setTitle("Chưa lưu thay đổi")
                    .setMessage("Bạn có thay đổi chưa được lưu. Bạn có muốn lưu trước khi thoát?")
                    .setPositiveButton("Lưu", (dialog, which) -> saveProfile())
                    .setNegativeButton("Không lưu", (dialog, which) -> super.onBackPressed())
                    .setNeutralButton("Hủy", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasChanges(UserAccount account) {
        String currentName = nameInput.getText().toString().trim();
        String currentPhone = phoneInput.getText().toString().trim();
        String currentBio = bioInput.getText().toString().trim();
        String currentAddress = addressInput.getText().toString().trim();
        String currentWebsite = websiteInput.getText().toString().trim();

        return !currentName.equals(account.getName() != null ? account.getName() : "") ||
                !currentPhone.equals(account.getPhone() != null ? account.getPhone() : "") ||
                !currentBio.equals(account.getBio() != null ? account.getBio() : "") ||
                !currentAddress.equals(account.getAddress() != null ? account.getAddress() : "") ||
                !currentWebsite.equals(account.getWebsite() != null ? account.getWebsite() : "") ||
                selectedImageUri != null;
    }



    private void InitUI() {
            toolbar = findViewById(R.id.edit_profile_toolbar);
            avatarImage = findViewById(R.id.edit_profile_avatar);
            nameInput = findViewById(R.id.edit_profile_name_input);
            emailInput = findViewById(R.id.edit_profile_email_input);
            phoneInput = findViewById(R.id.edit_profile_phone_input);
            bioInput = findViewById(R.id.edit_profile_bio_input);
            addressInput = findViewById(R.id.edit_profile_address_input);
            websiteInput = findViewById(R.id.edit_profile_website_input);
            saveButton = findViewById(R.id.edit_profile_save_btn);
            cancelButton = findViewById(R.id.edit_profile_cancel_btn);
            changeAvatarButton = findViewById(R.id.edit_profile_change_avatar_btn);

    }

}