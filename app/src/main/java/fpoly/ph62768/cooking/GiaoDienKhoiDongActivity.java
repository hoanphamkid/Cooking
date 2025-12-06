package fpoly.ph62768.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class GiaoDienKhoiDongActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giao_dien_khoi_dong);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(GiaoDienKhoiDongActivity.this, GiaoDienChinhActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}

