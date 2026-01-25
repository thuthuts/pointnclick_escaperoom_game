package project.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.player.Player;
import project.game.SceneSwitchItems;

import java.net.URL;
import java.util.ResourceBundle;

public class EmailController extends SuperController implements Initializable {

    @FXML private AnchorPane container;
    @FXML private Label closeLabel, MissionText, GreetingText;
    @FXML private Button acceptBtn;
    private SoundPlayer soundPlayer;
    private Player player;

    private static final Logger log = LogManager.getLogger(SuperController.class);

    public EmailController(Player player, SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
        this.player = player;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
        closeLabel.setStyle("-fx-text-fill: black");
        GreetingText.setText(resourceBundle.getString("greeting") + " " + player.getName());
        GreetingText.setStyle("-fx-text-fill: #999999");
        MissionText.setStyle("-fx-text-fill: #999999");

        acceptBtn.setOnMouseClicked(mouseEvent -> {
            soundPlayer.playSoundEffect("accept_mission");
            AnchorPane ownerPane = (AnchorPane) ((Stage)container.getScene().getWindow()).getOwner().getScene().getRoot();
            Node DOORNode = ownerPane.getChildren().stream().filter(nodeScene -> nodeScene.getId().equals("door")).findFirst().get();
            Node MONITORNode = ownerPane.getChildren().stream().filter(nodeScene -> nodeScene.getId().equals("MONITOR")).findFirst().get();

            MONITORNode.setOnMouseClicked(null);
            DOORNode.setOnMouseClicked(mouseEvent1 -> {
                loadNextScene(SceneSwitchItems.Scenes.LABODOORROOM.getFXMLURL(), ownerPane , player);
            });

            closePopUp(container);
        });
    }
}
