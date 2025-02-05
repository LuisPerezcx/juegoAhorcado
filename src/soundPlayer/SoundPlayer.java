package soundPlayer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {

    // Método para reproducir un sonido
    public static void playSound(String fileName) {
        try {
            URL soundURL = SoundPlayer.class.getResource("/sonidos/" + fileName);
            if (soundURL == null) {
                System.out.println("No se encontró el archivo: " + fileName);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}