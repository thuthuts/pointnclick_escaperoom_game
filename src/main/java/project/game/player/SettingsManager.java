package project.game.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 *
 */
public class SettingsManager implements Serializable {

    private static final Logger log = LogManager.getLogger(SettingsManager.class);
    private Settings.language lang;
    private Settings.theme theme;
    private double bgSoundVolume;
    private double effectSoundVolume;

    public SettingsManager() {
        log.info("Created default settings - Language : English , Theme : Dark");
        this.lang = Settings.language.ENGLISH;
        this.theme = Settings.theme.DARK;
        this.bgSoundVolume = 0.1;
        this.effectSoundVolume = 0.1;
    }

    public ResourceBundle getLangBundle() {
        return lang.getBundle();
    }

    public String getThemeURL() {
        return theme.getURL();
    }

    public void setLanguage(Settings.language lang) {
        this.lang = lang;
    }

    public void setTheme(Settings.theme theme){
        this.theme = theme;
    }

    public Settings.language getLanguage(){
        return lang;
    }

    public Settings.theme getTheme(){
        return theme;
    }

    public double getBgSoundVolume(){
        return bgSoundVolume;
    }

    public void setBgSoundVolume(double newVolume){
        this.bgSoundVolume = newVolume;
    }

    public double getEffectSoundVolume(){
        return effectSoundVolume;
    }

    public void setEffectSoundVolume(double newVolume){
        this.effectSoundVolume = newVolume;
    }
}

