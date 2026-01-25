package project.gui.minigame;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.gui.SuperController;
import project.audio.SoundPlayer;
import project.game.IMinigame;
import project.game.player.Player;
import project.game.SceneSwitchItems;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class M_CodeController extends SuperController implements IMinigame, Initializable {

    @FXML private AnchorPane container;
    @FXML Label closeLabel;
    @FXML ImageView light;
    @FXML TextArea display;
    @FXML GridPane keypad;
    private String secretCode;
    private SoundPlayer soundPlayer;
    private Player player;

    private static final Logger log = LogManager.getLogger(SuperController.class);


    public M_CodeController(Player player, SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
        this.player = player;
    }

    /**
     * This EventHandler will check which numeric-button has been clicked
     * and forwards this information to handleNumberInput for checking whether
     * or not that input is false.
     */
    private final EventHandler<MouseEvent> numberInputEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            soundPlayer.playSoundEffect("code_input");
            String buttonText = ((Button) mouseEvent.getSource()).getText();
            handleNumberInput(buttonText);
        }
    };

    /**
     * This EventHandler will be added to Button "DEL", it will delete the
     * last input on the keypad.
     */
    private final EventHandler<MouseEvent> deleteEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            soundPlayer.playSoundEffect("code_input");
            deleteLastInput();
        }
    };

    /**
     * This EventHandler will be added to Button "AC" and deletes every input
     * made by the player thus far.
     */
    private final EventHandler<MouseEvent> resetEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            soundPlayer.playSoundEffect("code_input");
            reset();
        }
    };

    public String getSecretCode() {
        return secretCode;
    }

    public void setLightColor(String color){
        switch (color){
            case "green":   light.setImage(new Image(getClass().getResource("/img/light_green.png").toExternalForm())); break;
            case "red":     light.setImage(new Image(getClass().getResource("/img/light_red.png").toExternalForm())); break;
            default:        light.setImage(new Image(getClass().getResource("/img/light_grey.png").toExternalForm())); break;
        }
    }

    /**
     * If the button contains a number that has been generated in the secretCode, then the button
     * will recieve a picture that implies that it is included in the code.
     */
    public void setButtonPictures(){
        keypad.getChildren().forEach(node -> {
            String lastNodeIdChar = node.getId().substring(node.getId().length() - 1);
            if (secretCode.contains(lastNodeIdChar)) {
                ((Button)node).setGraphic(new ImageView(new Image(getClass().getResource("/img/keypad_used.png").toExternalForm())));
            } else {
                ((Button)node).setGraphic(new ImageView(new Image(getClass().getResource("/img/keypad.png").toExternalForm())));
            }
        });
    }


    /**
     * Add MouseClickListener to Number Buttons, DEL Button and AC Button with their
     * corresponding EventHandler.
     */
    public void addKeypadListener(){
        keypad.getChildren().forEach(node -> {
            if(node instanceof Button) {
                switch(node.getId()) {
                   default:  node.addEventHandler(MouseEvent.MOUSE_CLICKED, numberInputEvent);break; // NUMBER
                   case "DEL": node.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteEvent);break; // DEL
                   case "AC": node.addEventHandler(MouseEvent.MOUSE_CLICKED, resetEvent); break; // AC
                }
            }
        });
    }
    
    public void removeKeypadListener(){
        keypad.getChildren().forEach(node -> {
            if(node instanceof Button) {
                switch(node.getId()) {
                    default:  node.removeEventHandler(MouseEvent.MOUSE_CLICKED, numberInputEvent);break; // NUMBER
                    case "DEL": node.removeEventHandler(MouseEvent.MOUSE_CLICKED, deleteEvent);break; // DEL
                    case "AC": node.removeEventHandler(MouseEvent.MOUSE_CLICKED, resetEvent); break; // AC
                }
            }
        });
    }

    /**
     * This method handles the input given from the player.
     * It adds the input to the textarea as a display and checks whether or not the
     * numeric character input is equal to the secretCode that has been generated. true = green light, false = red light.
     * @param buttonText number that the clicked button represents.
     */
    public void handleNumberInput(String buttonText){
        try {
            if (display.getText().length() != 4) display.appendText(buttonText);

            int length = display.getText().length()-1;
            if ((display.getText().charAt(length)) == secretCode.charAt(length)) setLightColor("green");
            else setLightColor("red");

            log.debug("The secret code is: " + secretCode + ", Your input is: " + display.getText());

            if (display.getText().length() == 4 && display.getText().equals(secretCode)){
                player.setMinigameSolved(SceneSwitchItems.Minigames.SECURITY.ordinal(), true);
                log.debug("You've cracked the code! Wow!");
                display.setStyle("-fx-text-fill: green;");
                removeKeypadListener();
                soundPlayer.playSoundEffect("creaky_open_door");

                soundPlayer.stopBackgroundSound();
                AnchorPane ownerPane = (AnchorPane) ((Stage)container.getScene().getWindow()).getOwner().getScene().getRoot();
                loadNextScene(SceneSwitchItems.Scenes.LABORATORY.getFXMLURL(), ownerPane, player);
                closePopUp(container);

            } else if (display.getText().length() == 4) playerHasFailed();

        } catch (Exception e){
            log.debug("Too fast... Wait until the input display has restarted.");
        }

    }

    /**
     * This method deletes the last input that has been recieved from the player.
     * The light will be set to default grey.
     */
    public void deleteLastInput(){
        setLightColor("default");
        String displayText = display.getText();
        if (displayText.length() > 0){
            display.setText(displayText.substring(0, displayText.length()-1));
        }
    }

    Timer timer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setLightColor("default");
            log.debug("You've failed... Try again :)");
            display.setText("");
            display.setStyle("-fx-text-fill: #318add;");
        }
    });

    /**
     * This method is informing the player for their failure and turns the input red.
     * The light will be set to red aswell.
     */
    public void playerHasFailed(){
        display.setStyle("-fx-text-fill: red;");
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * This method randomizes the secretCode each time the minigame is being opened.
     * The secretCode will never include the same number more than twice.
     */
    @Override
    public void randomize() {
        String possibleNumbers = "1234567890";
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 4; i++){
            int randomNum = (int) (Math.random() * possibleNumbers.length()-1);
            char chosenNumber = possibleNumbers.charAt(randomNum);
            builder.append(chosenNumber);
            possibleNumbers = possibleNumbers.replaceAll( String.valueOf(chosenNumber), "");
            log.debug(possibleNumbers + ", " + chosenNumber + ", " + builder.toString());
        }
        secretCode = builder.toString();
        log.debug("The secret code is: " + secretCode);
    }

    @Override
    public void reset() {
        setLightColor("default");
        display.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
        if (secretCode == null) randomize();
        setButtonPictures();
        addKeypadListener();
    }
}
