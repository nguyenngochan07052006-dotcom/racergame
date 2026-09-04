package com.yourpackage.racergame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.database.HighScore;
import java.util.List;

public class HighScoreAdapter extends ArrayAdapter<HighScore> {
    private final Context context;
    private final List<HighScore> highScores;

    public HighScoreAdapter(Context context, List<HighScore> highScores) {
        super(context, 0, highScores);
        this.context = context;
        this.highScores = highScores;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_highscore, parent, false);
        }

        HighScore hs = highScores.get(position);

        TextView tvRank = convertView.findViewById(R.id.tvRank);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvScore = convertView.findViewById(R.id.tvScore);
        TextView tvDate = convertView.findViewById(R.id.tvDate);

        String rank;
        if (position == 0) rank = "🥇";
        else if (position == 1) rank = "🥈";
        else if (position == 2) rank = "🥉";
        else rank = "#" + (position + 1);

        tvRank.setText(rank);
        tvName.setText(hs.playerName);
        tvScore.setText("🏁 " + hs.score + " điểm");
        tvDate.setText(hs.playDate);

        return convertView;
    }
}