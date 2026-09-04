package com.yourpackage.racergame.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private SoundPool soundPool;
    private Map<String, Integer> soundMap;
    private Context context;

    public SoundManager(Context context) {
        this.context = context;
        soundMap = new HashMap<>();
        initSoundPool();
    }

    private void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(10, android.media.AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void playSound(String soundName) {
        Integer soundId = soundMap.get(soundName);
        if (soundId != null) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}