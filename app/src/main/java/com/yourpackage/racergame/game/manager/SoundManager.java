package com.yourpackage.racergame.game.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import com.yourpackage.racergame.R;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final String TAG = "SoundManager";
    private static SoundManager instance;

    private SoundPool soundPool;
    private final Map<String, Integer> soundMap = new HashMap<>();
    private final Map<Integer, Boolean> loadedMap = new HashMap<>(); // theo dõi đã load xong chưa
    private MediaPlayer bgMusic;
    private Context context;

    private boolean isSoundEnabled = true;
    private boolean isMusicEnabled = true;
    private boolean isSoundPoolReady = false;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        initSoundPool();
        loadSounds();
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(8)
                    .setAudioAttributes(attrs)
                    .build();
        } else {
            soundPool = new SoundPool(8, android.media.AudioManager.STREAM_MUSIC, 0);
        }

        // Quan trọng: lắng nghe khi load xong
        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            if (status == 0) {
                loadedMap.put(sampleId, true);
                Log.d(TAG, "Sound loaded thành công, id = " + sampleId);
            } else {
                Log.e(TAG, "Load sound thất bại, id = " + sampleId);
            }
        });
    }

    private void loadSounds() {
        try {
            int coinId = soundPool.load(context, R.raw.coin, 1);
            int gemId = soundPool.load(context, R.raw.gem, 1);
            int playId = soundPool.load(context, R.raw.play, 1);
            int rockId = soundPool.load(context, R.raw.rock, 1);

            soundMap.put("coin", coinId);
            soundMap.put("gem", gemId);
            soundMap.put("play", playId);
            soundMap.put("rock", rockId);

            Log.d(TAG, "Đã gọi load 4 file sound");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi loadSounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playSound(String name) {
        if (!isSoundEnabled) return;

        Integer id = soundMap.get(name);
        if (id == null) {
            Log.w(TAG, "Không tìm thấy sound: " + name);
            return;
        }

        // Chỉ phát khi đã load xong
        if (Boolean.TRUE.equals(loadedMap.get(id))) {
            soundPool.play(id, 1f, 1f, 1, 0, 1f);
        } else {
            Log.w(TAG, "Sound chưa load xong: " + name);
        }
    }

    public void startBackgroundMusic() {
        if (!isMusicEnabled) return;

        try {
            if (bgMusic == null) {
                bgMusic = MediaPlayer.create(context, R.raw.bgm);
                if (bgMusic != null) {
                    bgMusic.setLooping(true);
                    bgMusic.setVolume(0.5f, 0.5f);
                    Log.d(TAG, "Tạo MediaPlayer bgm thành công");
                } else {
                    Log.e(TAG, "MediaPlayer.create trả về null - kiểm tra file bgm.mp3");
                }
            }

            if (bgMusic != null && !bgMusic.isPlaying()) {
                bgMusic.start();
                Log.d(TAG, "Bắt đầu phát nhạc nền");
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi startBackgroundMusic: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (bgMusic != null) {
            try {
                if (bgMusic.isPlaying()) bgMusic.stop();
                bgMusic.release();
            } catch (Exception ignored) {}
            bgMusic = null;
        }
    }

    public void pauseBackgroundMusic() {
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }

    public void resumeBackgroundMusic() {
        if (isMusicEnabled) {
            if (bgMusic == null) {
                startBackgroundMusic();
            } else if (!bgMusic.isPlaying()) {
                bgMusic.start();
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        isSoundEnabled = enabled;
    }

    public void setMusicEnabled(boolean enabled) {
        isMusicEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }

    public void release() {
        stopBackgroundMusic();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}