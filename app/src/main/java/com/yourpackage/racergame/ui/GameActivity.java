package com.yourpackage.racergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.game.GameView;
import com.yourpackage.racergame.game.manager.SoundManager;

public class GameActivity extends AppCompatActivity implements GameView.GameOverCallback {

    private GameView gameView;
    private TextView btnSettings;
    private boolean isPaused = false;
    private AlertDialog pauseDialog;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        soundManager = SoundManager.getInstance(this);
        soundManager.playSound("play");
        soundManager.startBackgroundMusic();

        gameView = findViewById(R.id.gameView);
        gameView.setGameOverCallback(this);
        gameView.setSoundManager(soundManager);
        gameView.setZOrderOnTop(false);

        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.bringToFront();
        btnSettings.setOnClickListener(v -> showPauseMenu());
    }

    private void showPauseMenu() {
        if (isPaused) return;

        isPaused = true;
        gameView.pauseGame();
        soundManager.pauseBackgroundMusic();

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pause, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        builder.setView(dialogView);
        builder.setCancelable(false);

        pauseDialog = builder.create();
        if (pauseDialog.getWindow() != null) {
            pauseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        pauseDialog.show();

        Button btnResume = dialogView.findViewById(R.id.btnResume);
        Button btnRestart = dialogView.findViewById(R.id.btnRestart);
        Button btnSound = dialogView.findViewById(R.id.btnSound);
        Button btnExit = dialogView.findViewById(R.id.btnExit);

        // Cập nhật text nút âm thanh
        updateSoundButtonText(btnSound);

        btnResume.setOnClickListener(v -> {
            pauseDialog.dismiss();
            isPaused = false;
            gameView.resumeGame();
            soundManager.resumeBackgroundMusic();
        });

        btnRestart.setOnClickListener(v -> {
            pauseDialog.dismiss();
            soundManager.stopBackgroundMusic();
            recreate();
        });

        btnSound.setOnClickListener(v -> {
            // Đảo trạng thái
            boolean newSound = !soundManager.isSoundEnabled();
            boolean newMusic = !soundManager.isMusicEnabled();

            soundManager.setSoundEnabled(newSound);
            soundManager.setMusicEnabled(newMusic);

            updateSoundButtonText(btnSound);

            if (newMusic) {
                soundManager.startBackgroundMusic();
            }
        });

        btnExit.setOnClickListener(v -> {
            pauseDialog.dismiss();
            soundManager.stopBackgroundMusic();
            finish();
        });
    }

    private void updateSoundButtonText(Button btn) {
        if (soundManager.isSoundEnabled() && soundManager.isMusicEnabled()) {
            btn.setText("🔊  ÂM THANH: BẬT");
        } else {
            btn.setText("🔇  ÂM THANH: TẮT");
        }
    }

    @Override
    public void onGameOver(int score, int distance, int coins, int gems) {
        soundManager.stopBackgroundMusic();
        soundManager.playSound("rock"); // tiếng va chạm

        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("distance", distance);
        intent.putExtra("coins", coins);
        intent.putExtra("gems", gems);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        showPauseMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null && !isPaused) {
            showPauseMenu();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.stopBackgroundMusic();
        }
    }
}
