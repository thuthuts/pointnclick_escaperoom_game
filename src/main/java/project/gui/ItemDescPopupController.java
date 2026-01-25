package project.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.animation.PathAnimation;
import project.audio.SoundPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class ItemDescPopupController implements Initializable {

    private SoundPlayer soundPlayer;
    @FXML private TextArea text;
    @FXML private AnchorPane pane;
    @FXML private VBox textContainer;
    @FXML private ImageView character, dialog_arrow;
    private String itemText;
    private int i = 0;
    private char[] textArray;

    private static final Logger log = LogManager.getLogger(ItemDescPopupController.class);

    public ItemDescPopupController(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    //Timer which prints out itemText letter by letter and let it appear in the Textarea
    Timer timer = new Timer(50, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            text.appendText(Character.toString(textArray[i]));
            i++;

                if(i == textArray.length) {
                    log.debug("timer stopped organically");
                    textContainer.removeEventHandler(MouseEvent.MOUSE_CLICKED, stopTimer);
                    startArrowAnimation();
                    textContainer.setOnMouseClicked(closeScene);
                    timer.stop();
                }
            if (i < textArray.length-1 && textArray[i] != ' ') soundPlayer.playSoundEffect("text_dialouge2");
        }
    });

    private final EventHandler<MouseEvent> stopTimer = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            log.debug("timer was forced to stop");
            timer.stop();
            text.setText(itemText);
            startArrowAnimation();
            textContainer.setOnMouseClicked(closeScene);
        }
    };

    private final EventHandler<MouseEvent> closeScene = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.close();
        }
    };

    /**
     *  This method receives the description of the clicked Item and sets
     *  itemText.
     *  @param itemText : Description of item which can be found in ItemText.json
     */
    public void setText(String itemText) {
        this.itemText = itemText;
        this.textArray = itemText.toCharArray();
    }

    public void startArrowAnimation(){
        dialog_arrow.setVisible(true);
        PathAnimation.addPathTransition(dialog_arrow, 0, -5);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        character.setImage(new Image(getClass().getResource("/character.png").toExternalForm()));
        timer.start();
        textContainer.setOnMouseClicked(stopTimer);
    }
}
