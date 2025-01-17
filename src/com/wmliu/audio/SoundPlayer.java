package com.wmliu.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * 声音播放工具类
 */
public class SoundPlayer {
    private static final String SOUND_PATH = "resources/sounds/";
    
    public void playSound(String soundFile) {
        try {
            File file = new File(SOUND_PATH + soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
        } catch (Exception e) {
            System.err.println("播放声音失败: " + e.getMessage());
        }
    }
} 