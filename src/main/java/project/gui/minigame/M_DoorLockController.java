package project.gui.minigame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.IMinigame;
import project.game.InventoryBuilder;
import project.game.InventoryItems;
import project.game.SceneSwitchItems;
import project.game.player.Player;
import project.gui.SuperController;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class M_DoorLockController extends SuperController implements IMinigame, Initializable {

    @FXML
    private AnchorPane container;
    @FXML
    GridPane inventory;
    @FXML
    Label closeLabel;
    @FXML
    ImageView imageViewBackground;
    private Player player;

    private static final Logger log = LogManager.getLogger(M_DoorLockController.class);
    private final String[] backgroundImgURLs = {"minigame_doorlock_cross.png", "minigame_doorlock_heart.png", "minigame_doorlock_karo.png", "minigame_doorlock_pik.png"};
    private String correctKeyString;
    private String backgroundURL;
    private SoundPlayer soundPlayer;
    private InventoryBuilder inventoryBuilder;


    public M_DoorLockController(Player player, InventoryBuilder inventoryBuilder, SoundPlayer soundPlayer) {
        this.player = player;
        this.inventoryBuilder = inventoryBuilder;
        this.soundPlayer = soundPlayer;
    }

    /**
     * Fill GridPane with the collected Keys from the Laboratory and add clickListener to those keys
     */
    private void setupInventory() {
        boolean[][] playerItems = player.getHasInventoryItemBooleans();
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            if (playerItems[i][0]) {
                InventoryItems item = InventoryItems.values()[i];
                String imageURL = getClass().getResource(item.getURL()).toExternalForm();
                String key = item.toString();
                ImageView view = ((ImageView) inventory.getChildren().get(counter));
                setImageIntoImageView(imageURL, view);
                addClickListener(view, key);
                counter++;
            }
        }
        log.debug("Key Inventory set up");
    }

    /**
     * sets image from url to view
     *
     * @param url  is the source for the image
     * @param view is the ImageView the image is set in
     */
    private void setImageIntoImageView(String url, ImageView view) {
        Image image = new Image(url);
        view.setImage(image);
    }

    /**
     * setClicklistener to ImageView with different ClickHandlers
     * depending on the InventorItem
     *
     * @param view the ImageView the ClickListiner is set to
     * @param key  the String of the Item to check if its the correct Key
     */
    private void addClickListener(ImageView view, String key) {
        view.setOnMouseClicked(mouseEvent -> {
            log.debug("Key: " + key + " clicked");

                if (key.equals(correctKeyString)) {
                    playSound(true);
                    log.debug("Correct Key, the door opens");

                    player.setMinigameSolved(SceneSwitchItems.Minigames.DOOR.ordinal(), true);

                    Object[] items = Arrays.stream(InventoryItems.values()).filter(m -> m.name().contains("KEY")).toArray();
                    for (Object item: items) {
                        if (player.getHasInventoryItemBooleans()[((InventoryItems)item).ordinal()][0]){
                            inventoryBuilder.removeItem((InventoryItems)item);
                        }
                        player.setHasUsedInventoryItem(((InventoryItems)item).ordinal(), true);
                    }
                    closePopUp(container);
                    AnchorPane ownerPane = (AnchorPane) ((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();
                    loadNextScene(SceneSwitchItems.Scenes.LIVINGROOM.getFXMLURL(), ownerPane, player);

                } else {
                    playSound(false);
                    player.increaseFailCount();
                    log.debug("Not the right key");
                }
        });
    }

    /**
     * plays use_key sound and if doorShouldOpen is true plays creaky_open_door
     * @param doorShouldOpen determines if the door opening sound should be played or not
     */
    private void playSound(boolean doorShouldOpen) {
        if (doorShouldOpen) {
            soundPlayer.playSoundEffect("creaky_open_door");
        } else {
            soundPlayer.playSoundEffect("use_key");
        }
    }

    /**
     * creates a Sound Thread and runs it.
     * @param soundName is the name of the sound file for the media that is being played.
     */
    private void playSound(String soundName) {
        soundPlayer.playSoundEffect(soundName);
    }

    public void updateBackground() {
        String fullImageUrl = getClass().getResource("/img/" + backgroundURL).toExternalForm();
        setImageIntoImageView(fullImageUrl, imageViewBackground);
        log.debug("Background Image set: " + backgroundURL);
    }

    @Override
    public void randomize() {
        int randomIndex = (int) Math.floor(Math.random() * backgroundImgURLs.length);
        backgroundURL = backgroundImgURLs[randomIndex];
        correctKeyString = "KEY" + backgroundURL.substring(18, backgroundURL.indexOf(".")).toUpperCase();
        log.debug("Correct Key: " + correctKeyString);
    }

    @Override
    public void reset() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (correctKeyString == null) randomize();
        updateBackground();
        setupInventory();
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
    }
}
