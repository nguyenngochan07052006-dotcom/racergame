package com.yourpackage.racergame.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.adapter.HighScoreAdapter;
import com.yourpackage.racergame.database.DatabaseHelper;
import com.yourpackage.racergame.database.HighScore;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView lvHighScores;
    private TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        dbHelper = new DatabaseHelper(this);
        lvHighScores = findViewById(R.id.lvHighScores);
        tvNoData = findViewById(R.id.tvNoData);

        loadLeaderboard();
    }

    private void loadLeaderboard() {
        List<HighScore> highScores = dbHelper.getTopHighScores();

        if (highScores.isEmpty()) {
            lvHighScores.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            lvHighScores.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            HighScoreAdapter adapter = new HighScoreAdapter(this, highScores);
            lvHighScores.setAdapter(adapter);
        }
    }

    public void onBackPressed(View view) {
        finish();
    }
}