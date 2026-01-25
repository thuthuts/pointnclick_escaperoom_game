package project.gui.minigame;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.gui.SuperController;
import project.audio.SoundPlayer;
import project.game.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class M_4ImageController extends SuperController implements IMinigame, Initializable {

    @FXML
    private AnchorPane container;
    @FXML
    Label closeLabel;
    @FXML
    GridPane buttons;
    @FXML
    GridPane input;
    @FXML
    Button deleteB, resetB;
    private Player player;
    private InventoryBuilder inventoryBuilder;
    private SoundPlayer soundPlayer;
    private List<String> letters;
    private int inputIndex = 0;
    private String resultLetters;

    private static final Logger log = LogManager.getLogger(M_4ImageController.class);


    public M_4ImageController(Player player, InventoryBuilder inventoryBuilder, SoundPlayer soundPlayer){
        this.player = player;
        this.inventoryBuilder = inventoryBuilder;
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void randomize() {
        String[] list = resultLetters.split("");
        letters = new ArrayList<>(Arrays.asList(list));
        int max = 12 - resultLetters.length();

        for (int i = 0; i < max; i++) {
            Random rnd = new Random();
            char randomLetter = (char) ('a' + rnd.nextInt(26));
            letters.add(Character.toString(randomLetter).toUpperCase());
        }
        Collections.shuffle(letters);
        log.debug("Generated random letters, combined them with resultLetters " +
                "and shuffled them: " + letters.toString());
    }


    /**
     * Sets generated letters and solution letters to each input button.
     * Additionally, adds EventHandlers to handle user input.
     */
    public void setButtons() {
        AtomicInteger index = new AtomicInteger();
        buttons.getChildren().forEach(node -> {
            Button button = (Button) node;
            button.setText(letters.get(index.get()));
            button.setOnMouseClicked(setInput);
            index.getAndIncrement();
        });
    }


    /**
     * Receives user input and sets corresponding input buttons to display it on the screen.
     */
    private final EventHandler<MouseEvent> setInput = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            soundPlayer.playSoundEffect("buttonPress");

            if (inputIndex < resultLetters.length()) {
                Button b = (Button) mouseEvent.getSource();
                Button button = (Button) input.getChildren().get(inputIndex);
                button.setText(b.getText());
                inputIndex++;

                if (inputIndex == resultLetters.length()) {
                    checkInput();
                }
            }
        }
    };


    /**
     * As soon as the user has filled all input buttons, the input is checked whether their answer
     * is right or wrong.
     */
    public void checkInput() {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i< resultLetters.length(); i++){
            Button inputField = (Button) input.getChildren().get(i);
            result.append(inputField.getText());
        }
        log.debug("Your answer was: " + result);

        if(result.toString().equals(resultLetters)){
            animateButtons(true);
            inventoryBuilder.addItem(InventoryItems.YEAST,null,container,false);
            removeClickListener();
            player.setMinigameSolved(SceneSwitchItems.Minigames.WHISKEY.ordinal(), true);
            log.debug("Congratulations! That's the right answer!");

        } else {
            animateButtons(false);
            player.increaseFailCount();
            log.debug("Oh no! Try again!");

        }
    }


    /**
     *This method starts an animation and shows, whether the user's input is correct or not. Based
     * on that, a different animation is selected and the input buttons have different background colors.
     *
     * @param isSolved decides which animation should start
     */
    private void animateButtons(boolean isSolved) {

        if (isSolved) {
            input.getChildren().forEach(node -> {
                Button userInput = (Button) node;
                animateButtonOnSolved(userInput);
            });
        } else {
            input.getChildren().forEach(node -> {
                Button b = (Button) node;
                animateButtonOnNotSolved(b);
            });
        }
    };

    /**
     * Animate the input Button
     * @param button is animated
     */
    private void animateButtonOnSolved(Button button) {
        ParallelTransition pTrans = new ParallelTransition();
        setCSS(button, true);
        ScaleTransition trans = new ScaleTransition(Duration.millis(1000), button);
        trans.setByX(0.13);
        trans.setByY(0.13);
        trans.setAutoReverse(true);
        trans.setCycleCount(5);
        pTrans.getChildren().add(trans);
        pTrans.play();
    }

    /**
     * Animate the input Button
     * @param button is animated
     */
    private void animateButtonOnNotSolved(Button button) {
        ParallelTransition pTrans = new ParallelTransition();
        setCSS(button, false);
        RotateTransition rotate = new RotateTransition(Duration.millis(60), button);
        rotate.setByAngle(15);
        rotate.setCycleCount(10);
        rotate.setAutoReverse(true);
        pTrans.getChildren().add(rotate);
        pTrans.play();
    }

    /**
     * Removes user input
     */
    @Override
    public void reset() {
        resetB.setOnMouseClicked(mouseEvent -> {
            soundPlayer.playSoundEffect("buttonPress");

            input.getChildren().forEach(node -> {
                Button inputB = (Button) node;
                inputB.setText("");
                removeCSS(inputB);
                inputIndex = 0;
            });
        });
    }


    public void delete() {
        deleteB.setOnMouseClicked(mouseEvent -> {
            soundPlayer.playSoundEffect("buttonPress");

            for (int i = resultLetters.length() - 1; i >= 0; i--) {
                Button userInput = (Button)input.getChildren().get(i);
                if (inputIndex == 1) {
                    removeCSS(userInput);
                }
                if (!userInput.getText().equals("")) {
                    userInput.setText("");
                    inputIndex--;
                    break;
                }
            }
        });
    };


    /**
     * @param button   input button which will be styled
     * @param isSolved factor, which decides how input button will be styled (background color will be
     *                 either red or green)
     */
    private void setCSS(Button button, boolean isSolved) {
        if (isSolved) {
            button.getStyleClass().remove("imageMinigameFalse");
            button.getStyleClass().add("imageMinigameTrue");
        } else {
            button.getStyleClass().remove("imageMinigameTrue");
            button.getStyleClass().add("imageMinigameFalse");
        }
    }

    private void removeCSS(Button button) {
        button.getStyleClass().remove("imageMinigameTrue");
        button.getStyleClass().remove("imageMinigameFalse");
    }

    /**
     * removes click listeners on buttons to avoid getting the reward mulitple times
     */
    private void removeClickListener(){
        buttons.getChildren().forEach(btn -> {
            btn.setOnMouseClicked(null);
        });
    }

    private void setInputGrid(){
        for(int i = 0; i < resultLetters.length(); i++){
            Button b = new Button();
            b.setMinWidth(69);
            b.setMinHeight(49);
            b.getStyleClass().add("border");
            input.add(b,i,0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
        this.resultLetters = resourceBundle.getString("resultLetters");
        randomize();
        setInputGrid();
        setButtons();
        reset();
        delete();
    }
}
