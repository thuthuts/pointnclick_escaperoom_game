package project.gui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.SceneSwitchItems;
import project.game.player.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class WinnerController extends SuperController implements Initializable {

    @FXML public ImageView forground_winner;
    @FXML private AnchorPane container;
    @FXML private Label closeLabel;
    private Player player;

    private static final Logger log = LogManager.getLogger(WinnerController.class);

    public WinnerController(Player player) {
        this.player = player;
    }

    public RotateTransition rotateAnimation(ImageView image) {
        RotateTransition rt = new RotateTransition(Duration.millis(5000), image);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        log.debug("animation start");
        return rt;
    }

    private void animationThread() {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    rotateAnimation(forground_winner).play();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.debug(e);
                }
                return null;
            }
        };

        sleeper.setOnSucceeded(event -> {
            rotateAnimation(forground_winner).stop();
            AnchorPane ownerPane = (AnchorPane) ((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();
            loadPopUp(SceneSwitchItems.Scenes.ENDING.getFXMLURL(), ownerPane, player, 600, 300);
            closePopUp(container);
        });
        new Thread(sleeper).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
        animationThread();
    }
}
