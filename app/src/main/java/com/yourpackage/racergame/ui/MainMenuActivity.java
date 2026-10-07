package com.yourpackage.racergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.database.Player;
import com.yourpackage.racergame.game.manager.PlayerManager;
import com.yourpackage.racergame.game.manager.SoundManager;

public class MainMenuActivity extends AppCompatActivity {

    private PlayerManager playerManager;
    private SoundManager soundManager;
    private TextView tvPlayerName, tvTotalCoins;
    private Button btnPlay, btnShop, btnLeaderboard, btnSelectPlayer;
    private TextView btnSettings;   // ← Đúng kiểu TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        playerManager = PlayerManager.getInstance(this);
        soundManager = SoundManager.getInstance(this);

        // Bắt đầu nhạc nền
        soundManager.startBackgroundMusic();

        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvTotalCoins = findViewById(R.id.tvTotalCoins);
        btnPlay = findViewById(R.id.btnPlay);
        btnShop = findViewById(R.id.btnShop);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnSelectPlayer = findViewById(R.id.btnSelectPlayer);
        btnSettings = findViewById(R.id.btnSettings);

        loadPlayerData();
        checkAndAskName();

        btnPlay.setOnClickListener(v -> {
            Player player = playerManager.getCurrentPlayer();
            if (player == null || player.username == null || player.username.trim().isEmpty() || player.username.equalsIgnoreCase("Player")) {
                showNameDialog(true);
            } else {
                soundManager.playSound("play");
                startActivity(new Intent(this, GameActivity.class));
            }
        });

        btnShop.setOnClickListener(v -> startActivity(new Intent(this, ShopActivity.class)));

        btnLeaderboard.setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));

        btnSelectPlayer.setOnClickListener(v -> startActivity(new Intent(this, SelectPlayerActivity.class)));

        btnSettings.setOnClickListener(v -> showSettingsDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerManager.refreshPlayer();
        loadPlayerData();
        soundManager.resumeBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
    }

    private void loadPlayerData() {
        Player player = playerManager.getCurrentPlayer();
        if (player != null) {
            tvPlayerName.setText("👤 " + player.username);
            tvTotalCoins.setText("💰 " + player.totalCoins);
        }
    }

    private void checkAndAskName() {
        Player player = playerManager.getCurrentPlayer();
        if (player == null) return;
        if (player.username == null || player.username.trim().isEmpty() || player.username.equalsIgnoreCase("Player")) {
            showNameDialog(true);
        }
    }

    private void showNameDialog(boolean isRequired) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isRequired ? "Nhập tên người chơi" : "Đổi tên người chơi");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("Nhập tên của bạn...");
        input.setPadding(40, 30, 40, 30);

        Player current = playerManager.getCurrentPlayer();
        if (current != null && current.username != null && !current.username.equalsIgnoreCase("Player")) {
            input.setText(current.username);
            input.setSelection(current.username.length());
        }

        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                if (isRequired) showNameDialog(true);
                return;
            }
            if (name.length() > 15) {
                Toast.makeText(this, "Tên tối đa 15 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }
            playerManager.updatePlayerName(name);
            loadPlayerData();
            Toast.makeText(this, "Xin chào, " + name + "!", Toast.LENGTH_SHORT).show();
        });

        if (!isRequired) {
            builder.setNegativeButton("Hủy", null);
        } else {
            builder.setCancelable(false);
        }

        builder.show();
    }

    private void showSettingsDialog() {
        String soundStatus = soundManager.isSoundEnabled() ? "BẬT" : "TẮT";
        String musicStatus = soundManager.isMusicEnabled() ? "BẬT" : "TẮT";

        new AlertDialog.Builder(this)
                .setTitle("⚙️ CÀI ĐẶT ÂM THANH")
                .setItems(new String[]{
                        "Âm thanh hiệu ứng: " + soundStatus,
                        "Nhạc nền: " + musicStatus,
                        "Đóng"
                }, (dialog, which) -> {
                    if (which == 0) {
                        boolean newState = !soundManager.isSoundEnabled();
                        soundManager.setSoundEnabled(newState);
                        Toast.makeText(this, "Âm thanh hiệu ứng: " + (newState ? "BẬT" : "TẮT"), Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        boolean newState = !soundManager.isMusicEnabled();
                        soundManager.setMusicEnabled(newState);
                        Toast.makeText(this, "Nhạc nền: " + (newState ? "BẬT" : "TẮT"), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}