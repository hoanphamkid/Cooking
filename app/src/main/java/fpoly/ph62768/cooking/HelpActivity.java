package fpoly.ph62768.cooking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    private static final String SUPPORT_EMAIL = "support@candycancook.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ImageButton backButton = findViewById(R.id.help_back_button);
        TextView emailButton = findViewById(R.id.help_contact_email);
        TextView faqButton = findViewById(R.id.help_faq_button);

        backButton.setOnClickListener(v -> onBackPressed());

        emailButton.setOnClickListener(v -> composeEmail());
        faqButton.setOnClickListener(v ->
                Toast.makeText(this, R.string.help_faq_placeholder, Toast.LENGTH_SHORT).show()
        );
    }

    private void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + SUPPORT_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_email_subject));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.help_no_email_app, Toast.LENGTH_SHORT).show();
        }
    }
}

