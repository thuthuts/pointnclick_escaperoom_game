package project.gui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.JSONTextManager;
import project.game.SceneSwitchItems;
import project.game.SuperLevel;
import project.game.player.Player;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LaboDoorController extends SuperLevel implements Initializable {

    private static final Logger log = LogManager.getLogger(LaboDoorController.class);
    private Player player;
    private SoundPlayer soundPlayer;
    @FXML private AnchorPane container;
    @FXML private Button tutorial;
    @FXML ImageView settingsIcon, SECURITY, FADE;

    public LaboDoorController(Player player, SoundPlayer soundPlayer) {
        super(player , null, null, soundPlayer, 0);
        this.soundPlayer = soundPlayer;
        this.player = player;
    }
    public void loadTutorialPopup(){
        loadPopUp(SceneSwitchItems.Scenes.TUTORIAL.getFXMLURL(), container, player, 770, 560);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setContainer(container);

        Timer soundTimer = new Timer(500, event -> soundPlayer.playSoundEffect("drive_away"));
        soundTimer.setRepeats(false);
        soundTimer.start();

        Timer fadeTimer = new Timer(2000, event -> {
            makeFadeOut(FADE,container);
            Platform.runLater(this::loadTutorialPopup);
        });
        fadeTimer.setRepeats(false);
        fadeTimer.start();

        addTutorialButton(tutorial);
        setAllMinigameItems(new ImageView[]{SECURITY});
        addSettingsButton(SceneSwitchItems.Scenes.LABODOORROOM.getFXMLURL(), settingsIcon);
    }
}
