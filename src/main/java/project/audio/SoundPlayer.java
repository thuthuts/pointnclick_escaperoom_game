package project.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JavaFX MediaPlayer initialization
 * This class will play any BackgroundSound-(Looped)
 * and any SoundEffect-(Not Looped) in the players
 * preferred volume.
 */
public class SoundPlayer {

    private static final Logger log = LogManager.getLogger(SoundPlayer.class);

    private Player player;
    private MediaPlayer bgSound;

    public SoundPlayer(Player player) {
        this.player = player;
    }

    /**
     * This method will play a background sound from a given filename (without .mp3 extension)- loops indefinitely
     * @param filename of resource file.
     */
    public void playBackgroundSound(String filename) {
        Media me = new Media(getClass().getResource("/sound/" + filename + ".mp3").toString());
        bgSound = new MediaPlayer(me);
        bgSound.setCycleCount(MediaPlayer.INDEFINITE);
        bgSound.setVolume(player.getBGSoundVolume());
        bgSound.setAutoPlay(true);
        bgSound.play();
    }

    /**
     * This method will play a background sound from a given filename (without .mp3 extension) - doesn't loop
     * @param filename of resource file.
     * @return initialized MediaPlayer to stop a soundEffect from playing if needed.
     */
    public MediaPlayer playSoundEffect(String filename) {
        Media me = new Media(getClass().getResource("/sound/" + filename + ".mp3").toString());
        MediaPlayer mp = new MediaPlayer(me);
        mp.setVolume(player.getEffectSoundVolume());
        mp.setAutoPlay(true);
        mp.play();
        return mp;
    }

    public void stopBackgroundSound(){
        bgSound.stop();
    }

    public void updateVolumeSettings(){
        bgSound.setVolume(player.getBGSoundVolume());
    }

    public MediaPlayer getBgSoundMediaPlayer() {
        return bgSound;
    }

    public MediaPlayer.Status getStatus(){
        try {
            return bgSound.getStatus();
        } catch (Exception e){
            return MediaPlayer.Status.UNKNOWN;
        }
    }

    public void fadeBackgroundSound(String nextFilename){
        var ref = new Object() {
            double curVolume = player.getBGSoundVolume();
        };

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {

            log.debug(ref.curVolume);
            if (ref.curVolume < 0){
                log.debug("here");
                playBackgroundSound(nextFilename);
                exec.shutdown();
            }
            ref.curVolume = ref.curVolume-0.01;
            bgSound.setVolume(ref.curVolume);

        }, 0, 40, TimeUnit.MILLISECONDS);
        log.debug("Schedule running");
    }
}
