package fpoly.ph62768.cooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import fpoly.ph62768.cooking.data.remote.DistributorApiService;
import fpoly.ph62768.cooking.data.remote.DistributorRequest;
import fpoly.ph62768.cooking.model.Distributor;
import fpoly.ph62768.cooking.network.ApiClient;
import fpoly.ph62768.cooking.ui.DistributorAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DistributorCrudActivity extends AppCompatActivity implements DistributorAdapter.Listener {

    private DistributorApiService apiService;
    private DistributorAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyView;
    private EditText nameInput;
    private Button addButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_crud);

        apiService = ApiClient.getInstance().create(DistributorApiService.class);

        nameInput = findViewById(R.id.distributor_input_name);
        addButton = findViewById(R.id.distributor_add_button);
        swipeRefreshLayout = findViewById(R.id.distributor_swipe_refresh);
        progressBar = findViewById(R.id.distributor_progress);
        emptyView = findViewById(R.id.distributor_empty);
        RecyclerView recyclerView = findViewById(R.id.distributor_recycler);

        adapter = new DistributorAdapter();
        adapter.setListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadDistributors);
        addButton.setOnClickListener(v -> submitDistributor());
        findViewById(R.id.distributor_back).setOnClickListener(v -> finish());

        loadDistributors();
    }

    private void submitDistributor() {
        String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.distributor_error_name_required));
            return;
        }
        setLoading(true);
        apiService.createDistributor(new DistributorRequest(name))
                .enqueue(new Callback<Distributor>() {
                    @Override
                    public void onResponse(@NonNull Call<Distributor> call,
                                           @NonNull Response<Distributor> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            nameInput.setText("");
                            showToast(getString(R.string.distributor_toast_created));
                            loadDistributors();
                        } else {
                            showToast(getString(R.string.distributor_error_api));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Distributor> call, @NonNull Throwable t) {
                        setLoading(false);
                        showToast(t.getMessage());
                    }
                });
    }

    private void loadDistributors() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getDistributors().enqueue(new Callback<List<Distributor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Distributor>> call,
                                   @NonNull Response<List<Distributor>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Distributor> data = response.body();
                    adapter.submitList(data);
                    emptyView.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    showToast(getString(R.string.distributor_error_api));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Distributor>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showToast(t.getMessage());
            }
        });
    }

    private void updateDistributor(@NonNull Distributor distributor, @NonNull String newName) {
        if (TextUtils.isEmpty(distributor.getId())) {
            showToast(getString(R.string.distributor_error_missing_id));
            return;
        }
        setLoading(true);
        apiService.updateDistributor(distributor.getId(), new DistributorRequest(newName.trim()))
                .enqueue(new Callback<Distributor>() {
                    @Override
                    public void onResponse(@NonNull Call<Distributor> call,
                                           @NonNull Response<Distributor> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            showToast(getString(R.string.distributor_toast_updated));
                            loadDistributors();
                        } else {
                            showToast(getString(R.string.distributor_error_api));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Distributor> call, @NonNull Throwable t) {
                        setLoading(false);
                        showToast(t.getMessage());
                    }
                });
    }

    private void deleteDistributor(@NonNull Distributor distributor) {
        if (TextUtils.isEmpty(distributor.getId())) {
            showToast(getString(R.string.distributor_error_missing_id));
            return;
        }
        setLoading(true);
        apiService.deleteDistributor(distributor.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            showToast(getString(R.string.distributor_toast_deleted));
                            loadDistributors();
                        } else {
                            showToast(getString(R.string.distributor_error_api));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        setLoading(false);
                        showToast(t.getMessage());
                    }
                });
    }

    private void showEditDialog(@NonNull Distributor distributor) {
        View contentView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_distributor_edit, null, false);
        EditText editText = contentView.findViewById(R.id.dialog_distributor_input);
        editText.setText(distributor.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.distributor_dialog_edit_title)
                .setView(contentView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String newName = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(newName)) {
                        showToast(getString(R.string.distributor_error_name_required));
                    } else {
                        updateDistributor(distributor, newName);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showDeleteDialog(@NonNull Distributor distributor) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.distributor_dialog_delete_title)
                .setMessage(getString(R.string.distributor_dialog_delete_message, distributor.getName()))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> deleteDistributor(distributor))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        addButton.setEnabled(!loading);
        nameInput.setEnabled(!loading);
        if (!loading) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showToast(@Nullable String message) {
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.distributor_error_api);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEdit(@NonNull Distributor distributor) {
        showEditDialog(distributor);
    }

    @Override
    public void onDelete(@NonNull Distributor distributor) {
        showDeleteDialog(distributor);
    }
}

