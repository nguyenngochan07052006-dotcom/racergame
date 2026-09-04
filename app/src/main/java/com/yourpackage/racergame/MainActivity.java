package com.yourpackage.racergame;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.database.Player;
import com.yourpackage.racergame.ui.GameActivity;
import com.yourpackage.racergame.ui.LeaderboardActivity;
import com.yourpackage.racergame.ui.ShopActivity;
import com.yourpackage.racergame.utils.PlayerManager;

public class MainActivity extends AppCompatActivity {

    private PlayerManager playerManager;
    private TextView tvPlayerName, tvTotalCoins;
    private Button btnPlay, btnShop, btnLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerManager = PlayerManager.getInstance(this);

        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvTotalCoins = findViewById(R.id.tvTotalCoins);
        btnPlay = findViewById(R.id.btnPlay);
        btnShop = findViewById(R.id.btnShop);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);

        loadPlayerData();
        checkAndAskName(); // ← Kiểm tra tên ngay khi vào app

        btnPlay.setOnClickListener(v -> {
            // Kiểm tra lại tên trước khi chơi
            Player player = playerManager.getCurrentPlayer();
            if (player == null || player.username == null || player.username.trim().isEmpty() || player.username.equals("Player")) {
                showNameDialog(true); // bắt buộc nhập tên
            } else {
                startActivity(new Intent(this, GameActivity.class));
            }
        });

        btnShop.setOnClickListener(v -> startActivity(new Intent(this, ShopActivity.class)));
        btnLeaderboard.setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerManager.refreshPlayer();
        loadPlayerData();
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

        // Nếu vẫn là tên mặc định → bắt nhập tên mới
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

        // Nếu đã có tên thì hiện sẵn
        Player current = playerManager.getCurrentPlayer();
        if (current != null && current.username != null && !current.username.equals("Player")) {
            input.setText(current.username);
            input.setSelection(current.username.length());
        }

        builder.setView(input);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String name = input.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                if (isRequired) {
                    showNameDialog(true); // bắt nhập lại
                }
                return;
            }

            if (name.length() > 15) {
                Toast.makeText(this, "Tên tối đa 15 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu tên mới
            playerManager.updatePlayerName(name);
            loadPlayerData();
            Toast.makeText(this, "Xin chào, " + name + "!", Toast.LENGTH_SHORT).show();
        });

        if (!isRequired) {
            builder.setNegativeButton("Hủy", null);
        } else {
            // Không cho tắt dialog nếu bắt buộc
            builder.setCancelable(false);
        }

        builder.show();
    }
}