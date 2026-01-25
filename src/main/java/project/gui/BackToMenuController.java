package project.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import project.audio.SoundPlayer;
import project.game.SceneSwitchItems;
import project.game.SuperLevel;
import project.game.player.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class BackToMenuController extends SuperController implements Initializable {

    private SoundPlayer soundPlayer;
    @FXML
    private AnchorPane container;

    @FXML
    private Label close, toMenu;

    public BackToMenuController(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    private void setButtons(){
        close.setOnMouseClicked(mouseEvent -> {
            closePopUp(container);
        });


        toMenu.setOnMouseClicked(mouseEvent -> {
            ApplicationContext context = new ClassPathXmlApplicationContext("file:src/main/resources/spring/spring.xml");
            Player player = (Player) context.getBean("player");
            Main.setContext(context);

            soundPlayer.stopBackgroundSound();
            AnchorPane ownerPane = (AnchorPane)((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();
            closePopUp(container);
            //load old progress
            loadNextScene(SceneSwitchItems.Scenes.MAIN.getFXMLURL(),ownerPane,player);
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setButtons();
    }
}
