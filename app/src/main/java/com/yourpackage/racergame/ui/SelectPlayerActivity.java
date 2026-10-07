package com.yourpackage.racergame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.adapters.PlayerAdapter;
import com.yourpackage.racergame.database.DatabaseHelper;
import com.yourpackage.racergame.database.Player;
import com.yourpackage.racergame.game.manager.PlayerManager;
import java.util.ArrayList;
import java.util.List;

public class SelectPlayerActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PlayerManager playerManager;
    private RecyclerView rvPlayers;
    private PlayerAdapter adapter;
    private TextView btnBack;
    private Button btnCreatePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);

        dbHelper = new DatabaseHelper(this);
        playerManager = PlayerManager.getInstance(this);

        rvPlayers = findViewById(R.id.rvPlayers);
        btnBack = findViewById(R.id.btnBack);
        btnCreatePlayer = findViewById(R.id.btnCreatePlayer);

        rvPlayers.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        btnCreatePlayer.setOnClickListener(v -> showCreatePlayerDialog());

        loadPlayers();
    }

    private void loadPlayers() {
        List<Player> players = dbHelper.getAllPlayers();

        adapter = new PlayerAdapter(players, player -> {
            // Khi bấm nút CHỌN
            playerManager.setCurrentPlayer(player);
            Toast.makeText(this, "Đã chọn: " + player.username, Toast.LENGTH_SHORT).show();
            finish(); // quay về MainMenu
        });

        rvPlayers.setAdapter(adapter);
    }

    private void showCreatePlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tạo người chơi mới");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("Nhập tên người chơi...");
        input.setPadding(40, 30, 40, 30);
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.length() > 15) {
                Toast.makeText(this, "Tên tối đa 15 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelper.createNewPlayer(name);
            if (id != -1) {
                Toast.makeText(this, "Tạo thành công: " + name, Toast.LENGTH_SHORT).show();
                loadPlayers();
            } else {
                Toast.makeText(this, "Tạo thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}