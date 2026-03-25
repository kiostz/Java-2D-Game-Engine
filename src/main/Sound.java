package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {
    Clip clip;
    URL[] soundURL = new URL[30];
    FloatControl gainControl;

    public Sound() {
        soundURL[0] = getClass().getResource("/sounds/GameThemeMusic.wav");
        soundURL[1] = getClass().getResource("/sounds/coin.wav");
        soundURL[2] = getClass().getResource("/sounds/key.wav");
        soundURL[3] = getClass().getResource("/sounds/door.wav");
        soundURL[4] = getClass().getResource("/sounds/chest.wav");
        soundURL[5] = getClass().getResource("/sounds/InGameBGM.wav");
    }
    public void setClip(int index) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL[index]);
            clip = AudioSystem.getClip(); // Get a clip resource
            clip.open(audioInputStream); // Open the clip with the audio stream

            // Look up for volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void play() {
        clip.start();
    }
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop() {
        clip.stop();
    }
    /**
     * Sets the volume of the audio clip.
     * @param volume The volume level to set. Accepted values: 0.0001f (minimum) to 1.0 (maximum).
     */
    public void setVolume(float volume) {
        if (gainControl != null) {
            // Logarithmic conversion: dB = 20 * log10(volume)
            float dB = (float) (Math.log10(volume) * 20.0);

            // Clamp to hardware limits to prevent errors
            if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
            if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();
            System.out.println(" Sound volume: " + dB);
            gainControl.setValue(dB); // Apply volume
        }
    }
}
