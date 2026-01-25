package project.gui.minigame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.gui.SuperController;
import project.audio.SoundPlayer;
import project.game.IMinigame;
import project.game.player.Player;
import project.game.SceneSwitchItems;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

public class M_LightController extends SuperController implements IMinigame, Initializable {

    @FXML
    private ImageView imageViewLogicalLabel1;
    @FXML
    private ImageView imageViewLogicalLabel2;
    @FXML
    private ImageView imageViewLogicalLabel3;
    @FXML
    private ImageView imageViewLogicalLabel4;
    @FXML
    private ImageView imageViewButton1;
    @FXML
    private ImageView imageViewButton2;
    @FXML
    private ImageView imageViewButton3;
    @FXML
    private ImageView imageViewButton4;
    @FXML
    private ImageView imageViewPowerOff;
    @FXML
    private ImageView imageViewPowerOn;
    @FXML
    private AnchorPane container;
    @FXML
    private ImageView imageViewPowerSlider;
    @FXML
    private ImageView imageViewBackground;
    @FXML
    private Label closeLabel;

    private Player player;
    private SoundPlayer soundPlayer;
    private final String REDLIGHTIMAGE_FILENAME = "light_red.png";
    private final String GREENLIGHTIMAGE_FILENAME = "light_green.png";
    private final String GREYLIGHTIMAGE_FILENAME = "light_grey.png";
    private final String KIPPSCHALTER_L_IMAGE_FILENAME = "kippschalter_links.png";
    private final String KIPPSCHALTER_R_IMAGE_FILENAME = "kippschalter_rechts.png";

    private final boolean[] buttonStateArray = new boolean[4];
    private final boolean[] correctStateArray = new boolean[4];
    private ImageView[] logicalImages = new ImageView[4];
    private ImageView[] imageViewButtons = new ImageView[4];

    private final String[] logicalImageUrl = {"logik_11.png", "logik_21.png", "logik_31.png", "logik_40.png"};
    // 0 -> false
    // 1 -> true

    private static final Logger log = LogManager.getLogger(SuperController.class);


    public M_LightController(Player player, SoundPlayer soundPlayer) {
        this.player = player;
        this.soundPlayer = soundPlayer;
    }

    @Override
    public void randomize() {
        shuffleAndSetUpLogikImages();
        setRandomButtonState();
    }

    /**
     * sets the correctStateArray depending on logicalImageUrl
     */
    private void initializeCorrectStateArray() {
        for (int i = 0; i < correctStateArray.length; i++) {
            String url = logicalImageUrl[i];
            int index = url.indexOf(".") - 1;
            char logicalPrefix = url.charAt(index);
            switch (logicalPrefix) {
                case '0':
                    correctStateArray[i] = false;
                    break;
                case '1':
                    correctStateArray[i] = true;
                    break;
            }
        }
    }

    /**
     * Shuffles Urls in LogicalArray and sets images to this shuffeld array
     */
    private void shuffleAndSetUpLogikImages() {
        shuffleLogicalImageUrlArray();
        for (int i = 0; i < logicalImageUrl.length; i++) {
            ImageView imageView = logicalImages[i];
            String url = logicalImageUrl[i];
            setImageIntoImageView(url, imageView);
        }
    }

    /**
     * This method shuffles strings in the LogicalImageUrlArray
     */
    private void shuffleLogicalImageUrlArray() {
        Random rand = new Random();

        for (int i = 0; i < logicalImageUrl.length; i++) {
            int randomIndexToSwap = rand.nextInt(logicalImageUrl.length);
            String temp = logicalImageUrl[randomIndexToSwap];
            logicalImageUrl[randomIndexToSwap] = logicalImageUrl[i];
            logicalImageUrl[i] = temp;
        }
        initializeCorrectStateArray();
        log.debug("logicalImageUrlArray: " + Arrays.toString(logicalImageUrl));
        log.debug("correctStateArray: " + Arrays.toString(correctStateArray));
    }

    /**
     * This method plays click sound
     */
    private void playClickSound() {
        String CLICKSOUND_FILENAME = "light_power_switch";
        soundPlayer.playSoundEffect(CLICKSOUND_FILENAME);
    }

    @Override
    public void reset() {

    }

    /**
     * Set ImageViewButtons randomly on or off
     */
    private void setRandomButtonState() {
        for (int i = 0; i < buttonStateArray.length; i++) {
            int randomNum = (int) Math.round(Math.random());
            switch (randomNum) {
                case 0:
                    buttonStateArray[i] = false;
                    break;
                case 1:
                    buttonStateArray[i] = true;
                    break;
            }
        }
        if (Arrays.equals(buttonStateArray, correctStateArray)) {
            setRandomButtonState();
        } else {
            log.debug("randomButtonState: " + Arrays.toString(buttonStateArray));
            initializeImageToButtonState();
        }
    }

    /**
     * This method sets the image for the on off power slider
     * @param on determines if the on or off image should be set
     */
    private void setSliderImage(boolean on) {
        setImageIntoImageView(GREYLIGHTIMAGE_FILENAME, imageViewPowerOff);
        setImageIntoImageView(GREYLIGHTIMAGE_FILENAME, imageViewPowerOn);
        if (on) {
            log.debug("Slider on");
            setImageIntoImageView(GREENLIGHTIMAGE_FILENAME, imageViewPowerOn);
            setImageIntoImageView(KIPPSCHALTER_R_IMAGE_FILENAME, imageViewPowerSlider);
        } else {
            log.debug("Slider off");
            setImageIntoImageView(REDLIGHTIMAGE_FILENAME, imageViewPowerOff);
            setImageIntoImageView(KIPPSCHALTER_L_IMAGE_FILENAME, imageViewPowerSlider);
        }
    }

    /**
     * This method determines which image should be set to a button and sets the image view
     */
    private void initializeImageToButtonState() {
        for (int i = 0; i < buttonStateArray.length; i++) {
            boolean buttonOn = buttonStateArray[i];
            setButtonImage(buttonOn, i);
        }
    }

    /**
     * This method sets the image to the image view
     * @param on specifies if the on or off image should be set
     * @param buttonNumber indicates which imageView should the image be set to
     */
    private void setButtonImage(boolean on, int buttonNumber) {
        ImageView imageView = imageViewButtons[buttonNumber];
        if (on) {
            log.debug("Button " + buttonNumber + " on");
            setImageIntoImageView("schalter_1.png", imageView);
        } else {
            log.debug("Button " + buttonNumber + " off");
            setImageIntoImageView("schalter_0.png", imageView);
        }
    }

    /**
     * sets image from url to view
     *
     * @param url  is the source for the image
     * @param view is the ImageView the image is set in
     */
    private void setImageIntoImageView(String url, ImageView view) {
        String fullImageUrl = getClass().getResource("/img/" + url).toExternalForm();
        Image image = new Image(fullImageUrl);
        view.setImage(image);
    }

    /**
     * This method sets a clickListener to each button
     * and sets true in buttonStateArray when state was false before and vice versa
     */
    private void setUpButtonClickListener() {
        for (int i = 0; i < imageViewButtons.length; i++) {
            ImageView imageViewButton = imageViewButtons[i];
            int arrayIndex = i;
            imageViewButton.setOnMouseClicked(mouseEvent -> {
                playClickSound();
                boolean currentState = buttonStateArray[arrayIndex];
                setButtonImage(!currentState, arrayIndex);
                buttonStateArray[arrayIndex] = !currentState;
            });
        }
    }

    /**
     * Compares buttonStateArray and correctStateArray
     * @return true when elements of buttonStateArray are the same as correctStateArray
     */
    private boolean validate() {
        return Arrays.equals(buttonStateArray, correctStateArray);
    }

    /**
     * Sets clickListener to powerSlider
     * ClickListener calls validate
     */
    private void setUpPowerClickListener() {
        imageViewPowerSlider.setOnMouseClicked(mouseEvent -> {
            boolean powerOn = validate();
            setSliderImage(powerOn);
            if (powerOn) {
                playClickSound();
                log.debug("Congratulations the light is now on");

                soundPlayer.playSoundEffect("accept_mission");

                AnchorPane ownerPane = (AnchorPane) ((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();
                Node node = ownerPane.getChildren().stream().filter(nodeScene -> nodeScene.getId().equals("LIGHT")).findFirst().get();
                makeFadeOut(node, ownerPane);

                // TODO: 12.01.21 Remove clickListener from all buttons

                player.setMinigameSolved(SceneSwitchItems.Minigames.JUNCTIONBOX.ordinal(), true);
            } else {
                log.debug("Sorry power still doesn't work");
                player.increaseFailCount();
            }
        });
    }

    /**
     * This method sets up the imageView Arrays logicalImages and imageViewButtons
     */
    private void fillUIArrays() {
        logicalImages = new ImageView[]{imageViewLogicalLabel1, imageViewLogicalLabel2, imageViewLogicalLabel3, imageViewLogicalLabel4};
        imageViewButtons = new ImageView[]{imageViewButton1, imageViewButton2, imageViewButton3, imageViewButton4};
    }

    /**
     * This method calls all methods that are needed for a correct game state
     */
    private void setUpGame() {
        fillUIArrays();
        setImageIntoImageView("schaltschrank.png", imageViewBackground);
        setSliderImage(false);
        randomize();
        setUpButtonClickListener();
        setUpPowerClickListener();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpGame();
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
    }
}
