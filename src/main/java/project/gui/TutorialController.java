package project.gui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TutorialController extends SuperController implements Initializable {

    @FXML
    private AnchorPane container;
    @FXML
    private Label closeLabel, pageCount;
    @FXML
    private Button leftButton, rightButton;

    @FXML
    private ImageView explanationView;
    @FXML
    private Label explanationText;

    private String explanationGifURL[] = {  "/img/tutorial/configs.gif","/img/tutorial/collectItem.gif","/img/tutorial/save.gif",
                                            "/img/tutorial/settings.png","/img/tutorial/itemDescription.png",
                                            "/img/tutorial/failCount.png", "/img/tutorial/highscore.png"};

    private int index = 0;
    private int wordPageLength;
    private ResourceBundle resourceBundle;


    private static final Logger log = LogManager.getLogger(TutorialController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.resourceBundle = resourceBundle;
        explanationView.setImage(new Image(getClass().getResource(explanationGifURL[index]).toExternalForm()));
        setWordPageLength();
        leftButton.setOpacity(0.2);
        log.debug("word length: " + wordPageLength);
        addSlideShowClickListeners();
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));

    }



    /**
     * this method calculates the length of the word "page" to adapt the text label
     * to the chosen language
     */
    private void setWordPageLength(){
        for(int i = 0; i < pageCount.getText().length(); i++){
            if(pageCount.getText().charAt(i) != ' '){
                wordPageLength++;
            }else{
                break;
            }
        }
    }


    private void addSlideShowClickListeners(){
        /**
         * This method will add click listeners for
         * @param leftButton and @param rightButton, so one can click through different tutorial instructions
         * @param index works like a counter and determines which page you are on
         * @param explanationGifUrl includes all paths to the required GIFs,
         *                          where the position number matches the number of pages on which the image is to be found
         *
         */

        rightButton.setOnAction(e -> {
            index++;

            if (index >= explanationGifURL.length-1){
                index = explanationGifURL.length-1 ;
                fadeTransition(rightButton, -0.8);}

            fadeTransition(leftButton,1-leftButton.getOpacity());
            explanationView.setImage(new Image(getClass().getResource(explanationGifURL[index]).toExternalForm()));
            explanationText.setText(resourceBundle.getString("explanation"+ (index+1)));
            pageCount.setText(pageCount.getText().substring(0, wordPageLength) + " " + (index+1));



            });


        leftButton.setOnAction(e -> {
            index--;
            if (index <= 0) {
                index = 0;
                fadeTransition(leftButton,-0.8);
            }

            explanationView.setImage(new Image(getClass().getResource(explanationGifURL[index]).toExternalForm()));
            fadeTransition(rightButton,1-rightButton.getOpacity());
            explanationText.setText(resourceBundle.getString("explanation"+ (index+1)));
            pageCount.setText(pageCount.getText().substring(0, wordPageLength) + " " + (index+1));
        });
    }

    private void fadeTransition(Node node, double opacity){
        //Instantiating FadeTransition class
        FadeTransition fade = new FadeTransition();
        //setting the duration for the Fade transition
        fade.setDuration(Duration.millis(1000));
        //setting the initial and the target opacity value for the transition
        fade.setFromValue(node.getOpacity());
        fade.setToValue(node.getOpacity() + opacity);
        //setting the given node as the node onto which the transition will be applied
        fade.setNode(node);
        //playing the transition
        fade.play();
    }
}
