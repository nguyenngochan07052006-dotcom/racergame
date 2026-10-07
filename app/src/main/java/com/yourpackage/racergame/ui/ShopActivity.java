package com.yourpackage.racergame.ui;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.racergame.R;
import com.yourpackage.racergame.adapters.CarShopAdapter;
import com.yourpackage.racergame.database.CarShop;
import com.yourpackage.racergame.database.DatabaseHelper;
import com.yourpackage.racergame.database.Player;
import com.yourpackage.racergame.game.manager.PlayerManager;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PlayerManager playerManager;
    private TextView tvCoins, btnBack;
    private ListView lvCars;
    private CarShopAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        dbHelper = new DatabaseHelper(this);
        playerManager = PlayerManager.getInstance(this);

        tvCoins = findViewById(R.id.tvCoins);
        btnBack = findViewById(R.id.btnBack);
        lvCars = findViewById(R.id.lvCars);

        btnBack.setOnClickListener(v -> finish());

        loadData();

        lvCars.setOnItemClickListener((parent, view, position, id) -> {
            CarShop car = adapter.getItem(position);
            if (car != null) handleBuyCar(car);
        });
    }

    private void loadData() {
        Player player = playerManager.getCurrentPlayer();
        if (player != null) {
            tvCoins.setText("💰 " + player.totalCoins);
        }

        List<CarShop> cars = dbHelper.getAllCars();
        adapter = new CarShopAdapter(this, cars);
        lvCars.setAdapter(adapter);
    }

    private void handleBuyCar(CarShop car) {
        if (car.isUnlocked) {
            dbHelper.setSelectedCarId(car.id);
            playerManager.refreshPlayer();
            Toast.makeText(this, "✅ Đã chọn xe: " + car.carName, Toast.LENGTH_SHORT).show();
            loadData();
            return;
        }

        Player player = playerManager.getCurrentPlayer();
        if (player == null) return;

        if (player.totalCoins < car.price) {
            Toast.makeText(this, "❌ Không đủ tiền! Cần " + car.price + " coins", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.unlockCar(car.id, player.id, car.price);
        if (success) {
            dbHelper.setSelectedCarId(car.id);
            Toast.makeText(this, "🎉 Mua và chọn xe thành công!", Toast.LENGTH_SHORT).show();
            playerManager.refreshPlayer();
            loadData();
        } else {
            Toast.makeText(this, "❌ Mua xe thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}