package project.gui.minigame;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.gui.SuperController;
import project.audio.SoundPlayer;
import project.game.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * This class controls the tidyUp-mini-game
 * You may click on the displayed items under the bed to "tidy them up"
 * If all items are tidied up you can find a note which is added to your inventory
 * and includes the computer password you were searching for
 */

public class M_TidyUpController extends SuperController implements IMinigame, Initializable {

    private static final Logger log = LogManager.getLogger(M_TidyUpController.class);

    @FXML
    private AnchorPane container;
    @FXML
    Label closeLabel;
    @FXML
    private ImageView item1, item2, item3, item4, item5, item0, computerPassword;
    private Player player;
    private InventoryBuilder inventoryBuilder;
    //counts the messy items which are tidied up, so the password could be collected
    private int count = 0;

    //images for the items which need to be tidied up
    private Image creeper;
    private Image matchaLatte;
    private Image papers;
    private Image box;
    private Image nutella;
    private Image computerPasswordImg;
    private ArrayList<Image> messyItemsList;
    //contains the imageViews which need to be set randomly with images
    private ImageView[] randomImageViews;
    private SoundPlayer soundPlayer;


    //constructor needs the player and inventoryBuilder to add the password note in your inventory
    public M_TidyUpController(Player player, InventoryBuilder inventoryBuilder, SoundPlayer soundPlayer) {

        this.player = player;
        this.inventoryBuilder = inventoryBuilder;
        this.soundPlayer = soundPlayer;
    }

    //initialises the images which are randomly displayed in the mini game
    public void setImages(){
        creeper = new Image(getClass().getResource("/img/creeper.png").toExternalForm());
        matchaLatte = new Image(getClass().getResource("/img/matchaLatte.png").toExternalForm());
        papers = new Image(getClass().getResource("/img/papers.png").toExternalForm());
        box = new Image(getClass().getResource("/img/KARTON1.png").toExternalForm());
        nutella = new Image(getClass().getResource("/img/nutella.png").toExternalForm());
        computerPasswordImg = new Image(getClass().getResource("/img/computerPassword.png").toExternalForm());
    }


    /**
     * This method will randomize the layout of the mini game
     * messyItems Array of Images which need to be tidied up
     * randomImageViews Array of imageViews which need to be set randomly with images from messyItems
     * messyItemList necessary to randomly assign an image to each imageView and delete it afterwords
     *                      from the List, so no image of an item can be assigned twice
     * randomMaxNumber equals the size of messyItemList, so the chosen image of the List is always in the
     *                        position range of the generated random number
     */
    @Override
    public void randomize() {

        Image[] messyItems = {creeper, papers, nutella, box, matchaLatte, papers};
        messyItemsList = new ArrayList<>(Arrays.asList(messyItems));

        for (ImageView messyItem : randomImageViews) {
            int randomMaxNumber;
            if (messyItemsList.size() == 1) {
                messyItem.setImage(messyItemsList.get(0));
            } else if (messyItemsList.size() == 2) {
                messyItem.setImage(messyItemsList.get(1));
                messyItemsList.remove(1);
            } else {
                randomMaxNumber = messyItemsList.size() - 1;
                Random random = new Random();
                int imageToSet = random.nextInt(randomMaxNumber);
                messyItem.setImage(messyItemsList.get(imageToSet));
                messyItemsList.remove(imageToSet);
                log.debug(messyItemsList);
            }

        }
    }


    @Override
    public void reset() {


    }


    /**
     * This method will display the opacity change of an image view and make it more visible
     * @param node the image view which opacity should change
     */
    public void fadeTransition(Node node) {

        //Instantiating FadeTransition class
        FadeTransition fade = new FadeTransition();
        //setting the duration for the Fade transition
        fade.setDuration(Duration.millis(2000));
        //setting the initial and the target opacity value for the transition
        fade.setFromValue(node.getOpacity());
        fade.setToValue(node.getOpacity() + 0.166);
        //setting the given node as the node onto which the transition will be applied
        fade.setNode(node);
        //playing the transition
        fade.play();
    }


    /**
     * This method will set ClickListeners to each imageView, so they can be tidied up and positioned at
     * the side, after once clicked
     * xPosition X-coordinates to assign each imageView a new position after it was clicked
     * yPosition Y-coordinates to assign each imageView a new position after it was clicked
     * computerPassword represents the "goal" of this mini game
     *                         can only be clicked when all other messy items have been removed
     *                         fade transition makes the element mor visible every time one item is tidied up
     *                         add the Inventory.NOTE to the inventory
     */
    public void setMouseCLickListener() {

        int[] xPosition = {14, 14, 14, 613, 613, 613};
        int[] yPosition = {205, 305, 405, 205, 305, 405};
        computerPassword.setOpacity(0);

        Arrays.stream(randomImageViews).forEach(imageView -> {
            imageView.setOnMouseClicked(mouseEvent -> {
                Node node = (Node) mouseEvent.getSource();
                String id = node.getId().substring(4, 5);
                soundPlayer.playSoundEffect("tidyUp");
                node.setLayoutX(xPosition[Integer.parseInt(id)]);
                node.setLayoutY(yPosition[Integer.parseInt(id)]);
                fadeTransition(computerPassword);
                count += 1;
            });
        });
    }

    /**
     * This method will add the password note to the inventory if all messy items are tidied up
     */
    public void checkPasswordClickable() {

        if (count == 6) {
            inventoryBuilder.addItem(InventoryItems.PW_NOTE, computerPassword, container, true);
            player.setMinigameSolved(SceneSwitchItems.Minigames.BED.ordinal(), true);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setImages();
        computerPassword.setImage(computerPasswordImg);
        randomImageViews = new ImageView[]{item1, item2, item3, item4, item5, item0};
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
        randomize();
        setMouseCLickListener();
        log.debug("initialized M_TidyUpController");


    }
}





