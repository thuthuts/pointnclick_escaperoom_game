package project.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.JSONTextManager;
import project.game.player.Player;
import project.game.SceneSwitchItems;
import project.game.SuperLevel;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ComputerRoomController extends SuperLevel implements Initializable {

    private static final Logger log = LogManager.getLogger(ComputerRoomController.class);
    private Player player;
    private SoundPlayer soundPlayer;
    @FXML private AnchorPane container;
    @FXML private Button tutorial;
    @FXML ImageView settingsIcon, MONITOR, REDLIGHT, GREYLIGHT, FADE, door;
    private final int EMAIL_NOTIF_DUR = 0;


    public ComputerRoomController(Player player, JSONTextManager textManager, SoundPlayer soundPlayer) {
        super(player , null, textManager, soundPlayer, 0);
        this.soundPlayer = soundPlayer;
        this.player = player;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setContainer(container);

        Timer fadeTimer = new Timer(2000, event -> makeFadeOut(FADE, container));
        fadeTimer.setRepeats(false);
        fadeTimer.start();

        Timer audioTimer = new Timer(500, event -> {
            if (soundPlayer.getStatus() != MediaPlayer.Status.PLAYING){
                soundPlayer.playBackgroundSound("computerRoom");
            }
        });
        audioTimer.setRepeats(false);
        audioTimer.start();

        Timer email_notif_timer = new Timer(EMAIL_NOTIF_DUR, event -> {
            // Message recieved
            soundPlayer.playSoundEffect("message_recieved");
            GREYLIGHT.setVisible(false);
            MONITOR.setOnMouseClicked(messageEvent -> {
                loadPopUp(SceneSwitchItems.Scenes.EMAILTAB.getFXMLURL(), container, player, 770, 560);
            });
        });
        email_notif_timer.setRepeats(false);
        email_notif_timer.start();

        addTutorialButton(tutorial);
        addSettingsButton(SceneSwitchItems.Scenes.COMPUTERROOM.getFXMLURL(), settingsIcon);
    }
}
