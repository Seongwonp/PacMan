package PM;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * 게임 사운드를 관리하는 클래스
 * 모든 사운드 재생 로직을 한곳에 중앙화
 */
public class SoundManager {
    // Try loading sounds from classpath first; fall back to PacMan/ directory or working directory
    private static final String SOUND_DIR = "PacMan/";
    
    public static void playEnemySound() {
        playSound("Pac_Man_Ghost_Sound.wav");
    }
    
    public static void playDeathSound() {
        playSound("pacman_end_effect.wav");
    }
    
    public static void playMoveSound() {
        playSound("Pac_Man_Waka_Waka.wav");
    }
    
    public static void playStartSound() {
        playSound("Pacman_BGM.wav");
    }
    
    private static void playSound(String filename) {
        try {
            // 1) Try classpath resource
            java.net.URL url = SoundManager.class.getResource("/" + filename);
            AudioInputStream audioIn = null;
            if (url != null) {
                // Load directly from a URL returned by getResource
                audioIn = AudioSystem.getAudioInputStream(url);
            } else {
                // 2) Try loading as resource stream from classpath (useful when packaged)
                InputStream is = SoundManager.class.getResourceAsStream("/" + filename);
                if (is != null) {
                    audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                } else {
                    // 3) Fall back to PacMan/ directory (when running from repo root)
                    File soundFile = new File(SOUND_DIR + filename);
                    if (soundFile.exists()) {
                        audioIn = AudioSystem.getAudioInputStream(soundFile);
                    } else {
                        // 4) Finally try working directory
                        soundFile = new File(filename);
                        if (soundFile.exists()) {
                            audioIn = AudioSystem.getAudioInputStream(soundFile);
                        } else {
                            throw new java.io.FileNotFoundException(filename + " not found on classpath, PacMan/ or working directory");
                        }
                    }
                }
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("사운드 재생 오류: " + filename);
            e.printStackTrace();
        }
    }
}
