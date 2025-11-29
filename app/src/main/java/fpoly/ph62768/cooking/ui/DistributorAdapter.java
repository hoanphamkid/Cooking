package fpoly.ph62768.cooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.ph62768.cooking.R;
import fpoly.ph62768.cooking.model.Distributor;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(@NonNull Distributor distributor);

        void onDelete(@NonNull Distributor distributor);
    }

    private final List<Distributor> items = new ArrayList<>();
    private Listener listener;

    public void submitList(List<Distributor> distributors) {
        items.clear();
        if (distributors != null) {
            items.addAll(distributors);
        }
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_distributor, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Distributor distributor = items.get(position);
        holder.bind(distributor);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView idView;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.distributor_name);
            idView = itemView.findViewById(R.id.distributor_id);
            editButton = itemView.findViewById(R.id.distributor_edit);
            deleteButton = itemView.findViewById(R.id.distributor_delete);
        }

        void bind(Distributor distributor) {
            nameView.setText(distributor.getName());
            idView.setText(distributor.getId() != null ? distributor.getId() : "");

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(distributor);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(distributor);
                }
            });
        }
    }
}

