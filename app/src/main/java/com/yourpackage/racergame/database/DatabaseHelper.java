package com.yourpackage.racergame.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RacerGame.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Player (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, totalCoins INTEGER DEFAULT 0, selectedCarId INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE HighScore (id INTEGER PRIMARY KEY AUTOINCREMENT, playerName TEXT, score INTEGER, playDate TEXT)");
        db.execSQL("CREATE TABLE CarShop (id INTEGER PRIMARY KEY AUTOINCREMENT, carName TEXT, price INTEGER, isUnlocked INTEGER DEFAULT 0)");

        String[] names = {"Xe Thể Thao Đỏ", "Xe Cảnh Sát", "Xe Đua F1", "Xe Tăng", "Xe Off-road"};
        int[] prices = {1000, 2000, 5000, 8000, 3000};
        for (int i = 0; i < names.length; i++) {
            ContentValues v = new ContentValues();
            v.put("carName", names[i]);
            v.put("price", prices[i]);
            v.put("isUnlocked", i == 0 ? 1 : 0);
            db.insert("CarShop", null, v);
        }

        ContentValues p = new ContentValues();
        p.put("username", "Player");
        p.put("totalCoins", 500);
        p.put("selectedCarId", 1);
        db.insert("Player", null, p);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Player");
        db.execSQL("DROP TABLE IF EXISTS HighScore");
        db.execSQL("DROP TABLE IF EXISTS CarShop");
        onCreate(db);
    }

    public Player getPlayer() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("Player", null, null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String name = c.getString(c.getColumnIndexOrThrow("username"));
            int coins = c.getInt(c.getColumnIndexOrThrow("totalCoins"));
            int carId = 1;
            int idx = c.getColumnIndex("selectedCarId");
            if (idx != -1) carId = c.getInt(idx);
            c.close();
            return new Player(id, name, coins, carId);
        }
        return null;
    }

    public void updatePlayerCoins(int playerId, int coins) {
        ContentValues v = new ContentValues();
        v.put("totalCoins", coins);
        getWritableDatabase().update("Player", v, "id=?", new String[]{String.valueOf(playerId)});
    }

    public void updatePlayerUsername(int playerId, String name) {
        ContentValues v = new ContentValues();
        v.put("username", name);
        getWritableDatabase().update("Player", v, "id=?", new String[]{String.valueOf(playerId)});
    }

    public void setSelectedCarId(int carId) {
        ContentValues v = new ContentValues();
        v.put("selectedCarId", carId);
        getWritableDatabase().update("Player", v, null, null);
    }

    public int getSelectedCarId() {
        Player p = getPlayer();
        return p != null ? p.selectedCarId : 1;
    }

    public void insertHighScore(String name, int score, String date) {
        ContentValues v = new ContentValues();
        v.put("playerName", name);
        v.put("score", score);
        v.put("playDate", date);
        getWritableDatabase().insert("HighScore", null, v);
    }

    public List<HighScore> getTopHighScores() {
        List<HighScore> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("HighScore", null, null, null, null, null, "score DESC", "10");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new HighScore(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("playerName")),
                        c.getInt(c.getColumnIndexOrThrow("score")),
                        c.getString(c.getColumnIndexOrThrow("playDate"))
                ));
            }
            c.close();
        }
        return list;
    }

    public List<CarShop> getAllCars() {
        List<CarShop> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("CarShop", null, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new CarShop(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("carName")),
                        c.getInt(c.getColumnIndexOrThrow("price")),
                        c.getInt(c.getColumnIndexOrThrow("isUnlocked")) == 1
                ));
            }
            c.close();
        }
        return list;
    }

    public boolean unlockCar(int carId, int playerId, int price) {
        Player p = getPlayer();
        if (p == null || p.totalCoins < price) return false;
        updatePlayerCoins(playerId, p.totalCoins - price);
        ContentValues v = new ContentValues();
        v.put("isUnlocked", 1);
        return getWritableDatabase().update("CarShop", v, "id=?", new String[]{String.valueOf(carId)}) > 0;
    }
    public List<Player> getAllPlayers() {
        List<Player> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("Player", null, null, null, null, null, "id ASC");
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String name = c.getString(c.getColumnIndexOrThrow("username"));
                int coins = c.getInt(c.getColumnIndexOrThrow("totalCoins"));
                int carId = 1;
                int idx = c.getColumnIndex("selectedCarId");
                if (idx != -1) carId = c.getInt(idx);
                list.add(new Player(id, name, coins, carId));
            }
            c.close();
        }
        return list;
    }

    public long createNewPlayer(String username) {
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("totalCoins", 500);
        v.put("selectedCarId", 1);
        return getWritableDatabase().insert("Player", null, v);
    }

    public void deletePlayer(int playerId) {
        getWritableDatabase().delete("Player", "id=?", new String[]{String.valueOf(playerId)});
    }
}