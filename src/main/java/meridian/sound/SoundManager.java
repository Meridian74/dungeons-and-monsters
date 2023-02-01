/**
 * @author Meridian
 * @since  2023.
 */
package meridian.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

   Clip clip;

   Map<String, URL> soundUrls;

   public SoundManager() {

      this.soundUrls = new HashMap<>();
      soundUrls.put("start", getClass().getResource("/sounds/start-game--medieval-show-fanfare-announcement.wav"));
   }

   public URL getSoundUrl(String keyname) {
      if (soundUrls.containsKey(keyname)) {
         return soundUrls.get(keyname);
      }
      else {
         throw new IllegalStateException("Sound file not found with this keyname: " + keyname);
      }

   }

   public void setPlayOfSoundFileByKeyname(String keyname) {
      try(AudioInputStream ais = AudioSystem.getAudioInputStream(getSoundUrl(keyname))) {
         this.clip = AudioSystem.getClip();
         this.clip.open(ais);

      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException exp) {
         throw new IllegalStateException("Cannot open audiofile: " + exp);
      }
   }

   public void play() {
      this.clip.start();
   }

   public void loop() {
      clip.loop(Clip.LOOP_CONTINUOUSLY);
   }

   public void stop() {
      this.clip.stop();
   }

}