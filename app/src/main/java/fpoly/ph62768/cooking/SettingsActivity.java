package fpoly.ph62768.cooking;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import fpoly.ph62768.cooking.data.RecipePreferenceStore;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "app_settings";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_TIPS = "tips_enabled";

    private SharedPreferences preferences;
    private RecipePreferenceStore preferenceStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        preferenceStore = new RecipePreferenceStore(this);

        ImageButton backButton = findViewById(R.id.settings_back_button);
        SwitchMaterial notificationsSwitch = findViewById(R.id.settings_notifications_switch);
        SwitchMaterial tipsSwitch = findViewById(R.id.settings_tips_switch);
        MaterialButton clearHistoryButton = findViewById(R.id.settings_clear_history_button);
        TextView versionText = findViewById(R.id.settings_version_text);

        backButton.setOnClickListener(v -> onBackPressed());

        boolean notificationsEnabled = preferences.getBoolean(KEY_NOTIFICATIONS, true);
        boolean tipsEnabled = preferences.getBoolean(KEY_TIPS, true);

        notificationsSwitch.setChecked(notificationsEnabled);
        tipsSwitch.setChecked(tipsEnabled);

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply());

        tipsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                preferences.edit().putBoolean(KEY_TIPS, isChecked).apply());

        clearHistoryButton.setOnClickListener(v -> {
            preferenceStore.clearHistory();
            Toast.makeText(this, R.string.settings_history_cleared, Toast.LENGTH_SHORT).show();
        });

        versionText.setText(getString(R.string.settings_version_label, resolveVersionName()));
    }

    private String resolveVersionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName != null ? info.versionName : "1.0";
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }
}

