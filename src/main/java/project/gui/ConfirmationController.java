package project.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import project.game.GameState;
import project.game.SceneSwitchItems;
import project.game.player.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmationController extends SuperController implements Initializable {


    private static final Logger log = LogManager.getLogger(ConfirmationController.class);

    private Player player;
    @FXML
    private AnchorPane container;
    @FXML
    private Button confirmBtn, cancelBtn;
    @FXML
    private Label text;
    private ResourceBundle bundle;
    private String nextFXML;

    public ConfirmationController(Player player) {
        this.player = player;
    }

    public void setText(String key) {
        text.setText(bundle.getString(key));
    }

    public void setNextFXML(String nextFXML) {
        this.nextFXML = nextFXML;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setNextFXML(SceneSwitchItems.Scenes.CONFIGURATIONS.getFXMLURL());
        this.bundle = resourceBundle;
        confirmBtn.setOnMouseClicked(m -> {
            if (GameState.deleteGame("playerData")) {
                AnchorPane ownerPane = (AnchorPane) ((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();
                closePopUp(container);
                ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("file:src/main/resources/spring/spring.xml");
                Main.setContext(context);
                player = (Player) context.getBean("player");
                loadNextScene(nextFXML, ownerPane, player);
            } else {
                log.error("game could not be deleted");
            }
        });


        cancelBtn.setOnMouseClicked(mouseEvent -> {
            closePopUp(container);
        });
    }
}
