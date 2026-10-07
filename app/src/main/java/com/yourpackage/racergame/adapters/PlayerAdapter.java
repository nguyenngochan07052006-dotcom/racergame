package com.yourpackage.racergame.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.database.Player;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    public interface OnPlayerSelectListener {
        void onSelect(Player player);
    }

    private final List<Player> list;
    private final OnPlayerSelectListener listener;

    public PlayerAdapter(List<Player> list, OnPlayerSelectListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player p = list.get(position);
        holder.tvName.setText(p.username);
        holder.tvCoins.setText("🪙 " + p.totalCoins);

        // Quan trọng: gán click cho nút
        holder.btnSelect.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelect(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCoins;
        Button btnSelect;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvPlayerName);
            tvCoins = v.findViewById(R.id.tvPlayerCoins);
            btnSelect = v.findViewById(R.id.btnSelect);
        }
    }
}