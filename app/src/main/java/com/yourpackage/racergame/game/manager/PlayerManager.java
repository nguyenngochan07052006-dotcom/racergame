package com.yourpackage.racergame.game.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.yourpackage.racergame.database.DatabaseHelper;
import com.yourpackage.racergame.database.Player;

public class PlayerManager {
    private static PlayerManager instance;
    private DatabaseHelper dbHelper;
    private Player currentPlayer;
    private SharedPreferences prefs;

    private PlayerManager(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.prefs = context.getSharedPreferences("RacerGamePrefs", Context.MODE_PRIVATE);
        loadCurrentPlayer();
    }

    public static synchronized PlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadCurrentPlayer() {
        currentPlayer = dbHelper.getPlayer();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void updatePlayerName(String newName) {
        if (currentPlayer != null) {
            dbHelper.updatePlayerUsername(currentPlayer.id, newName);
            currentPlayer.username = newName;
            prefs.edit().putString("username", newName).apply();
        }
    }

    public void updateCoins(int coins) {
        if (currentPlayer != null) {
            int newTotal = currentPlayer.totalCoins + coins;
            dbHelper.updatePlayerCoins(currentPlayer.id, newTotal);
            currentPlayer.totalCoins = newTotal;
        }
    }

    public void refreshPlayer() {
        currentPlayer = dbHelper.getPlayer();
    }
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        prefs.edit().putInt("current_player_id", player.id).apply();
    }
}