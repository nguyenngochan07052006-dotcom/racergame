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

    public static final String TABLE_PLAYER = "Player";
    public static final String COLUMN_PLAYER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_TOTAL_COINS = "totalCoins";

    public static final String TABLE_HIGHSCORE = "HighScore";
    public static final String COLUMN_SCORE_ID = "id";
    public static final String COLUMN_PLAYER_NAME = "playerName";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_PLAY_DATE = "playDate";

    public static final String TABLE_CARSHOP = "CarShop";
    public static final String COLUMN_CAR_ID = "id";
    public static final String COLUMN_CAR_NAME = "carName";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IS_UNLOCKED = "isUnlocked";

    private static final String CREATE_TABLE_PLAYER =
            "CREATE TABLE " + TABLE_PLAYER + " (" +
                    COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_TOTAL_COINS + " INTEGER DEFAULT 0, " +
                    "selectedCarId INTEGER DEFAULT 1" +   // ← thêm dòng này
                    ")";

    private static final String CREATE_TABLE_HIGHSCORE =
            "CREATE TABLE " + TABLE_HIGHSCORE + " (" +
                    COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLAYER_NAME + " TEXT, " +
                    COLUMN_SCORE + " INTEGER, " +
                    COLUMN_PLAY_DATE + " TEXT" +
                    ")";

    private static final String CREATE_TABLE_CARSHOP =
            "CREATE TABLE " + TABLE_CARSHOP + " (" +
                    COLUMN_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CAR_NAME + " TEXT, " +
                    COLUMN_PRICE + " INTEGER, " +
                    COLUMN_IS_UNLOCKED + " INTEGER DEFAULT 0" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYER);
        db.execSQL(CREATE_TABLE_HIGHSCORE);
        db.execSQL(CREATE_TABLE_CARSHOP);
        insertSampleCars(db);
        insertDefaultPlayer(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa hết bảng cũ để tạo lại cho chắc
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARSHOP);
        onCreate(db);
    }

    private void insertSampleCars(SQLiteDatabase db) {
        String[] carNames = {"Xe Thể Thao Đỏ", "Xe Cảnh Sát", "Xe Đua F1", "Xe Tăng", "Xe Off-road"};
        int[] prices = {1000, 2000, 5000, 8000, 3000};

        for (int i = 0; i < carNames.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CAR_NAME, carNames[i]);
            values.put(COLUMN_PRICE, prices[i]);
            values.put(COLUMN_IS_UNLOCKED, (i == 0) ? 1 : 0);
            db.insert(TABLE_CARSHOP, null, values);
        }
    }

    private void insertDefaultPlayer(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "Player");
        values.put(COLUMN_TOTAL_COINS, 500);
        values.put("selectedCarId", 1); // xe mặc định
        db.insert(TABLE_PLAYER, null, values);
    }

    public Player getPlayer() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLAYER, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_ID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            int totalCoins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_COINS));

            // Đọc thêm selectedCarId
            int selectedCarId = 1; // mặc định
            int colIndex = cursor.getColumnIndex("selectedCarId");
            if (colIndex != -1) {
                selectedCarId = cursor.getInt(colIndex);
            }

            Player player = new Player(id, username, totalCoins, selectedCarId);
            cursor.close();
            return player;
        }
        return null;
    }

    public void updatePlayerCoins(int playerId, int newCoins) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOTAL_COINS, newCoins);
        db.update(TABLE_PLAYER, values, COLUMN_PLAYER_ID + " = ?", new String[]{String.valueOf(playerId)});
    }

    public void updatePlayerUsername(int playerId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        db.update(TABLE_PLAYER, values, COLUMN_PLAYER_ID + " = ?", new String[]{String.valueOf(playerId)});
    }

    public void insertHighScore(String playerName, int score, String playDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, playerName);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_PLAY_DATE, playDate);
        db.insert(TABLE_HIGHSCORE, null, values);
    }

    public List<HighScore> getTopHighScores() {
        List<HighScore> highScores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HIGHSCORE, null, null, null, null, null,
                COLUMN_SCORE + " DESC", "10");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                HighScore hs = new HighScore(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAY_DATE))
                );
                highScores.add(hs);
            }
            cursor.close();
        }
        return highScores;
    }

    public List<CarShop> getAllCars() {
        List<CarShop> cars = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CARSHOP, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                CarShop car = new CarShop(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAR_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_UNLOCKED)) == 1
                );
                cars.add(car);
            }
            cursor.close();
        }
        return cars;
    }

    public boolean unlockCar(int carId, int playerId, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        Player player = getPlayer();
        if (player == null || player.totalCoins < price) {
            return false;
        }

        updatePlayerCoins(playerId, player.totalCoins - price);

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_UNLOCKED, 1);
        int rows = db.update(TABLE_CARSHOP, values, COLUMN_CAR_ID + " = ?", new String[]{String.valueOf(carId)});

        return rows > 0;
    }
    public int getSelectedCarId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLAYER, new String[]{"selectedCarId"}, null, null, null, null, null);
        int id = 1;
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        }
        return id;
    }

    public void setSelectedCarId(int carId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("selectedCarId", carId);
        db.update(TABLE_PLAYER, values, null, null);
    }
}