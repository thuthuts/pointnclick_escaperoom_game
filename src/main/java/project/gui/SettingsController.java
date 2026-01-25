package project.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.player.Player;
import project.game.player.Settings;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends SuperController implements Initializable {

    private static final Logger log = LogManager.getLogger(SettingsController.class);

    @FXML private AnchorPane container;
    @FXML private CheckBox checkLight, checkDark, checkEN, checkDE;
    @FXML private Button back;
    @FXML private Slider bgSoundSlider, effectSoundSlider;
    private SoundPlayer soundPlayer;
    private Player player;


    public SettingsController(Player player, SoundPlayer soundPlayer) {
        this.player = player;
        this.soundPlayer = soundPlayer;
    }

    public void addBackButtonListener(String lastFXML){
        back.setOnMouseClicked(click -> {
            loadNextScene(lastFXML, container, player);
        });
    }
    
    public Scene getCurrentScene(){
       return container.getScene();
    }

    
    public void checkboxCurrentSettings(){
        if(player.getLangSettings() == Settings.language.ENGLISH){
            checkEN.setSelected(true);
        }else{
            checkDE.setSelected(true);
        }

        if(player.getThemeSettings() == Settings.theme.DARK){
            checkDark.setSelected(true);
        }else{
            checkLight.setSelected(true);
        }
    }

    public void addChangeLanguageListeners(){
        checkEN.setOnMouseClicked(clickEvent -> {
            player.setLangSettings(Settings.language.ENGLISH);
            if(checkDE.isSelected()){
                checkDE.setSelected(false);
            }});

        checkDE.setOnMouseClicked(clickEvent -> {
            player.setLangSettings(Settings.language.GERMAN);
            if(checkEN.isSelected()){
                checkEN.setSelected(false);
            }});
    }

    public void updateSliderValues(){
        bgSoundSlider.setValue(player.getBGSoundVolume()*100);
        effectSoundSlider.setValue(player.getEffectSoundVolume()*100);
    }

    public void addVolumePropertyListener(){
        bgSoundSlider.valueProperty().addListener(observable -> {
            player.setBGSoundVolume(bgSoundSlider.getValue()/100);
            soundPlayer.updateVolumeSettings();
        });

        effectSoundSlider.valueProperty().addListener(observable -> {
            player.setEffectSoundVolume(effectSoundSlider.getValue()/100);
        });
    }

    public void addChangeThemeListeners(){
        checkDark.setOnMouseClicked(clickEvent -> {
            getCurrentScene().getStylesheets().clear();
            getCurrentScene().getStylesheets().add(getClass().getResource("/css/DarkTheme.css").toExternalForm());
            player.setThemeSettings(Settings.theme.DARK);
            log.debug("Set DarkTheme.css");
            if(checkLight.isSelected()){
                checkLight.setSelected(false);
            }
        });

        checkLight.setOnMouseClicked(clickEvent -> {
            getCurrentScene().getStylesheets().clear();
            getCurrentScene().getStylesheets().add(getClass().getResource("/css/LightTheme.css").toExternalForm());
            player.setThemeSettings(Settings.theme.LIGHT);
            log.debug("Set LightTheme.css");
            if(checkDark.isSelected()){
                checkDark.setSelected(false);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addVolumePropertyListener();
        updateSliderValues();
        checkboxCurrentSettings();
        addChangeLanguageListeners();
        addChangeThemeListeners();
    }
}
