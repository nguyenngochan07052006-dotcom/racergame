package com.yourpackage.racergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.MainActivity;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.database.DatabaseHelper;
import com.yourpackage.racergame.database.Player;
import com.yourpackage.racergame.utils.PlayerManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameOverActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private PlayerManager playerManager;
    private TextView tvScore, tvDistance, tvCoins, tvGems, tvTotalCoins;
    private Button btnRetry, btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        dbHelper = new DatabaseHelper(this);
        playerManager = PlayerManager.getInstance(this);

        tvScore = findViewById(R.id.tvScore);
        tvDistance = findViewById(R.id.tvDistance);
        tvCoins = findViewById(R.id.tvCoins);
        tvGems = findViewById(R.id.tvGems);
        tvTotalCoins = findViewById(R.id.tvTotalCoins);
        btnRetry = findViewById(R.id.btnRetry);
        btnMenu = findViewById(R.id.btnMenu);

        int score = getIntent().getIntExtra("score", 0);
        int distance = getIntent().getIntExtra("distance", 0);
        int coins = getIntent().getIntExtra("coins", 0);
        int gems = getIntent().getIntExtra("gems", 0);

        tvScore.setText("🏁 Điểm số: " + score);
        tvDistance.setText("📏 Quãng đường: " + distance + "m");
        tvCoins.setText("💰 Coin: " + coins);
        tvGems.setText("💎 Ngọc: " + gems);

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(500);
        }

        saveGameData(score, coins, gems);

        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        });

        btnMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void saveGameData(int score, int coins, int gems) {
        Player player = dbHelper.getPlayer();
        if (player == null) return;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        dbHelper.insertHighScore(player.username, score, currentDate);

        int totalCoins = coins + (gems * 50);
        playerManager.updateCoins(totalCoins);

        Player updatedPlayer = dbHelper.getPlayer();
        if (updatedPlayer != null) {
            tvTotalCoins.setText("💰 Tổng coins: " + updatedPlayer.totalCoins);
        }
    }
}