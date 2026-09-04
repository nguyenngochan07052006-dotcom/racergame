package com.yourpackage.racergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.game.GameView;

public class GameActivity extends AppCompatActivity implements GameView.GameOverCallback {

    private GameView gameView;
    private ImageButton btnPause;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        gameView.setGameOverCallback(this);
        gameView.setZOrderOnTop(false);

        btnPause = findViewById(R.id.btnPause);
        btnPause.bringToFront();

        btnPause.setOnClickListener(v -> togglePause());
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameView.pauseGame();
            btnPause.setImageResource(android.R.drawable.ic_media_play);
        } else {
            gameView.resumeGame();
            btnPause.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    @Override
    public void onGameOver(int score, int distance, int coins, int gems) {
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
        // Khi bấm nút Back vật lý của điện thoại thì thoát
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null && !isPaused) {
            isPaused = true;
            gameView.pauseGame();
            if (btnPause != null) {
                btnPause.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    }
}