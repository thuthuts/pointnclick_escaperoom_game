package project.gui.minigame;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.*;
import project.game.player.Player;
import project.gui.SuperController;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;


public class M_TableController extends SuperController implements IMinigame, Initializable {

    private JSONTextManager textManager;
    private final InventoryItems[] items = InventoryItems.values();
    @FXML
    private AnchorPane container;
    @FXML
    Label gram, thinkingBox, endCard, closeLabel;
    @FXML
    HBox inventoryContainer;
    @FXML
    Button plus, minus, confirm;
    @FXML
    GridPane inventoryTable;
    @FXML
    ImageView bowlBox, mixerBox, mixerButton, distillerButton;


    private Player player;
    private InventoryBuilder inventoryBuilder;
    private SoundPlayer soundPlayer;
    private ResourceBundle resourceBundle;


    private final String keyWater = InventoryItems.WATER.toString(); //WATER
    private final String keyHotWater = "HOTWATER"; //HOTWATER
    private final String keySugar = InventoryItems.SUGAR.toString();
    private final String keyNaCL = InventoryItems.NACL.toString();
    private final String keyYeast = InventoryItems.YEAST.toString();


    //weight part
    private int gramCount = 0;
    private String selectedElement = "";
    private byte increaseCount = 0;
    private byte decreaseCount = 0;

    //heat water
    private boolean distillerOn = false;
    private int heatingCount = 0;

    //Mix vaccine
    private boolean clickWater35 = false;
    private boolean clickYeast25 = false;
    private boolean clickNacl15 = false;
    private boolean clickSugar30 = false;

    //Map to save the right gram amount of every element
    private final HashMap<String, Integer> weightMap = new HashMap<>();

    //gifs
    private final String[] gifURL = {"/img/tableEndMinigame/NACL_low.gif", "/img/tableEndMinigame/NACL_tooMuch.gif", "/img/tableEndMinigame/NACL_right_.gif",
            "/img/tableEndMinigame/Water_low.gif", "/img/tableEndMinigame/Water_right_.gif", "/img/tableEndMinigame/Water_tooMuch.gif",
            "/img/tableEndMinigame/Yeast_low_.gif", "/img/tableEndMinigame/Yeast_right.gif", "/img/tableEndMinigame/Yeast_tooMuch.gif",
            "/img/tableEndMinigame/Sugar_low.gif", "/img/tableEndMinigame/Sugar_right_.gif", "/img/tableEndMinigame/Sugar_tooMuch.gif"};

    //ImageViews which are set by setGif() method
    private final ImageView fire = new ImageView(new Image(getClass().getResource("/img/tableEndMinigame/Fire.gif").toExternalForm()));
    private final ImageView mixerGif = new ImageView(new Image(getClass().getResource("/img/tableEndMinigame/Mixer.gif").toExternalForm()));
    //PNGs
    private final Image mixerEmpty = new Image(getClass().getResource("/img/tableEndMinigame/Mixer_empty.png").toExternalForm());
    private final Image mixerFull = new Image(getClass().getResource("/img/tableEndMinigame/Mixer_.png").toExternalForm());

    // controller internal Items
    private final Image hotWater35 = new Image(getClass().getResource("/img/tableEndMinigame/hotWater35.png").toExternalForm());
    private final Image nacl15 = new Image(getClass().getResource("/img/tableEndMinigame/nacl15.png").toExternalForm());
    private final Image sugar30 = new Image(getClass().getResource("/img/tableEndMinigame/sugar30.png").toExternalForm());
    private final Image yeast25 = new Image(getClass().getResource("/img/tableEndMinigame/yeast25.png").toExternalForm());


    private static final Logger log = LogManager.getLogger(M_TableController.class);


    public M_TableController(Player player, InventoryBuilder inventoryBuilder, JSONTextManager textManager, SoundPlayer soundPlayer) {
        this.player = player;
        this.soundPlayer = soundPlayer;
        this.textManager = textManager;
        this.inventoryBuilder = inventoryBuilder;
    }


    /**
     * Fill up the weightMap with right amounts. WightMap is Hashmap with String as key and Integer as value
     */
    private void fillWeightMap() {
        weightMap.put("HOTWATER", 35);
        weightMap.put("YEAST", 25);
        weightMap.put("NACL", 15);
        weightMap.put("SUGAR", 30);

        System.out.println(weightMap.toString());
    }

    /**
     * Thread to heat water (if the process is successful, water is replaced by hotWater Image)
     */
    private void heatingThread() {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.debug("oi");
                }
                return null;
            }
        };

        sleeper.setOnSucceeded(event -> {
            thinkingBox.setText(resourceBundle.getString("waterHot"));
            replaceInviItem("WATER");
            inventoryBuilder.addItem(InventoryItems.HOTWATER, null, null, false);
        });
        new Thread(sleeper).start();
    }

    /**
     * fill the inventoryTable GridPane with the items the player has currently collected
     */
    private void fillInvi() {
        boolean[][] playerItems = player.getHasInventoryItemBooleans();
        int counter = 0;
        for (int i = 0; i < playerItems.length; i++) {
            if (playerItems[i][0]) {
                InventoryItems item = InventoryItems.values()[i];
                String imageURL = getClass().getResource(item.getURL()).toExternalForm();
                ImageView view = ((ImageView) inventoryTable.getChildren().get(counter));
                view.setId(item.name());
                addClickListener(item.name(), view);
                setImageIntoImageView(imageURL, view);
                counter++;
            }
        }
        Platform.runLater(this::addInventoryListener);
    }

    private void addInventoryListener() {
        Object[] items = Arrays.stream(InventoryItems.values()).filter(item -> item.name().contains("RECIPE")).toArray();
        AnchorPane ownerPane = (AnchorPane) ((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();

        for (Node item : inventoryTable.getChildren()) {
            if (item instanceof ImageView && hasEnum(item.getId()) && Arrays.asList(items).contains(InventoryItems.valueOf(item.getId()))) {

                item.setOnMouseClicked(mouseEvent -> {
                    log.debug("clicked");
                    Node node = (Node) mouseEvent.getSource();
                    if (textManager.hasKey(node.getId())) {
                        loadTextPopUp(textManager.getText(((Node) mouseEvent.getSource()).getId(), player.getLangSettings()), ownerPane, player);
                    }
                });
            }
        }
    }

    public boolean hasEnum(String str) {
        for (InventoryItems me : items) {
            if (me.name().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    /**
     * Create from given URL an image and set it to an ImageView
     *
     * @param url  is the url to the png
     * @param view is the ImageView the image in which the image is set
     */
    private void setImageIntoImageView(String url, ImageView view) {
        Image image = new Image(url);
        view.setImage(image);
    }

    /**
     * Set clicklistener on the inventory items. The respective item is placed on the scale. In case of click on WATER the
     * heatingThread is started (removing WATER from inventory in LaboratoryController) and set heatingCount to 1 (water can
     * only be heated once). Set the selectedElement to the value of the currently clicked element and call clickListenerReset()
     *
     * @param key  the key of the clicked item
     * @param view the ImageView that is clicked
     */
    private void addClickListener(String key, ImageView view) {

        switch (key) {
            case "WATER":
                view.setOnMouseClicked(mouseEvent -> {
                    setClickableScale();
                    setCounterZero();
                    if (distillerOn) {
                        if (heatingCount < 1) {
                            thinkingBox.setText(resourceBundle.getString("waterHeating"));
                            heatingThread();
                            heatingCount = 1;
                            inventoryBuilder.removeItem(InventoryItems.valueOf(key));
                        }
                    } else {
                        thinkingBox.setText(resourceBundle.getString("waterCool"));
                    }
                    this.selectedElement = keyWater;
                    log.debug("You clicked on " + selectedElement);
                });
                break;

            case "YEAST":
                view.setOnMouseClicked(mouseEvent -> {
                    clickListenerReset(6);
                    this.selectedElement = keyYeast;
                    soundPlayer.playSoundEffect(selectedElement);
                    log.debug("You clicked on " + selectedElement);
                });
                break;

            case "NACL":
                view.setOnMouseClicked(mouseEvent -> {
                    clickListenerReset(0);
                    this.selectedElement = keyNaCL;
                    soundPlayer.playSoundEffect(selectedElement);
                    log.debug("You clicked on " + selectedElement);
                });
                break;

            case "SUGAR":
                view.setOnMouseClicked(mouseEvent -> {
                    soundPlayer.playSoundEffect("sugar");
                    clickListenerReset(9);
                    this.selectedElement = keySugar;
                    soundPlayer.playSoundEffect(selectedElement);
                    log.debug("You clicked on " + selectedElement);
                });
                break;

            case "HOTWATER":
                view.setOnMouseClicked(mouseEvent -> {
                    clickListenerReset(3);
                    this.selectedElement = keyHotWater;
                    soundPlayer.playSoundEffect(selectedElement);
                    log.debug("You clicked on " + selectedElement);
                });
                break;

            default:
                addClicklistenerForVaccine(key, view);
        }
    }

    /**
     * Method for avoiding duplicate code in clickListener switch method that
     * resets the scale and counter.
     *
     * @param index of gifURL Array.
     */
    public void clickListenerReset(int index) {
        setClickableScale();
        setCounterZero();
        putElemOnScale(new Image(getClass().getResource(gifURL[index]).toExternalForm()));
    }

    /**
     * Set a gif to the bowlBox ImageView. (A empty ImageView with a fix position)
     *
     * @param gif the image to be assigned to the ImageView
     */
    private void putElemOnScale(Image gif) {
        bowlBox.setImage(gif);
        log.debug("The bowl with " + selectedElement + " is now on the scale.");
    }


    /**
     * Checks the weight from a given key with weightMap. Changes the gif dynamically depending on the choosen amount.
     * If player set right amount the current item is changed into a new item (no object from InventoryItems enum!)
     *
     * @param key the item/element, which the weight should be checked
     */
    private void checkTheWeight(String key) {
        soundPlayer.playSoundEffect("code_input");
        thinkingBox.setText(resourceBundle.getString("niceScale"));
        int weight = weightMap.get(key);

        if (gramCount < weight) {
            thinkingBox.setText(resourceBundle.getString("falseAmount"));
            player.increaseFailCount();
            log.debug(player.getFailCounter());
        } else if (gramCount > weight) {
            replaceTooMuchMeasure();
            thinkingBox.setText(resourceBundle.getString("falseAmount"));
            player.increaseFailCount();
            log.debug(player.getFailCounter());
        } else {
            log.debug("check weigth..");
            replaceInviItem(key);
            if (!key.equals("HOTWATER")) {
                inventoryBuilder.removeItem(InventoryItems.valueOf(key));
                bowlBox.setImage(null);
                thinkingBox.setText(resourceBundle.getString("rightAmount"));
            }
        }
    }


    /**
     * Replace an inventoryTable item with the following item.
     *
     * @param key the item/element to replace with new item.
     */
    private void replaceInviItem(String key) {
        InventoryItems item = InventoryItems.valueOf(key);
        player.setHasInventoryItem(item.ordinal(), false);
        for (int i = 0; i < inventoryTable.getChildren().size(); i++) {

            if (((ImageView) inventoryTable.getChildren().get(i)).getImage().getUrl().endsWith(item.getURL())) {
                ((ImageView) inventoryTable.getChildren().get(i)).setImage(null);
                inventoryTable.getChildren().get(i).setId("list" + i);

                switch (key) {
                    case "WATER":
                        ((ImageView) inventoryTable.getChildren().get(i)).setImage(new Image(getClass().getResource(InventoryItems.HOTWATER.getURL()).toExternalForm()));
                        addClickListener("HOTWATER", ((ImageView) inventoryTable.getChildren().get(i)));
                        break;
                    case "HOTWATER":
                        ((ImageView) inventoryTable.getChildren().get(i)).setImage(hotWater35);
                        inventoryBuilder.removeItem(InventoryItems.HOTWATER);
                        inventoryBuilder.addItem(InventoryItems.HOTWATER35, null, null, false);
                        addClicklistenerForVaccine("HOTWATER35", ((ImageView) inventoryTable.getChildren().get(i)));
                        break;
                    case "SUGAR":
                        ((ImageView) inventoryTable.getChildren().get(i)).setImage(sugar30);
                        inventoryBuilder.addItem(InventoryItems.SUGAR30, null, null, false);
                        addClicklistenerForVaccine("SUGAR30", ((ImageView) inventoryTable.getChildren().get(i)));
                        break;
                    case "YEAST":
                        ((ImageView) inventoryTable.getChildren().get(i)).setImage(yeast25);
                        inventoryBuilder.addItem(InventoryItems.YEAST25, null, null, false);
                        addClicklistenerForVaccine("YEAST25", ((ImageView) inventoryTable.getChildren().get(i)));
                        break;
                    case "NACL":
                        ((ImageView) inventoryTable.getChildren().get(i)).setImage(nacl15);
                        inventoryBuilder.addItem(InventoryItems.NACL15, null, null, false);
                        addClicklistenerForVaccine("NACL15", ((ImageView) inventoryTable.getChildren().get(i)));
                        break;
                }
                break;
            }
        }
    }


    /**
     * Remove an inventoryTable item with given key
     *
     * @param keys the item/element to remove from inventoryTable
     */
    private void removeInviItem(String[] keys) {
        inventoryTable.getChildren().stream().filter(node -> {
            for (String key : keys) {
                if (node.getId().equals(key)) return true;
            }
            return false;
        }).forEach(node -> ((ImageView) node).setImage(null));
        inventoryBuilder.removeItem(InventoryItems.HOTWATER35);
        inventoryBuilder.removeItem(InventoryItems.YEAST25);
        inventoryBuilder.removeItem(InventoryItems.NACL15);
        inventoryBuilder.removeItem(InventoryItems.SUGAR30);
    }


    /**
     * This method ist the OnAction method from confirm button
     */
    public void confirm() {
        checkTheWeight(selectedElement);
    }

    @Override
    public void randomize() {
        //may change amount of ingredient?! randomize the amounts like LightPower the logical statements?!
    }

    /**
     * Set the increase and decrease counter zero
     */
    private void setCounterZero() {
        this.increaseCount = 0;
        this.decreaseCount = 0;
    }

    /**
     * Reset the gram label to zero
     */
    @Override
    public void reset() {
        gramCount = 0;
        gram.setText(toString(gramCount));
    }

    /**
     * Increase the amount on scale and replace the current gif.
     */
    private void increaseWeight() {
        this.increaseCount++;
        soundPlayer.playSoundEffect("code_input");
        int increasGram = this.gramCount;
        gram.setText("");
        increasGram += 5;
        gram.setText(toString(increasGram));
        this.gramCount = increasGram;
        replaceRightMeasure(this.increaseCount);
        log.debug("increase weight +5");
    }

    /**
     * Decrease the amount on scale and replace the current gif.
     */
    private void decreaseWeight() {
        this.decreaseCount++;
        soundPlayer.playSoundEffect("code_input");
        int decreasGram = this.gramCount;
        gram.setText("");
        if (decreasGram >= 5) {
            decreasGram -= 5;
            gram.setText(toString(decreasGram));
            this.gramCount = decreasGram;
            replaceRightMeasure(this.decreaseCount);
            log.debug("decrease weight -5");
        } else {
            gram.setText("0");
        }
    }

    /**
     * Replace dinamically the current gif by given count
     *
     * @param count is the current amount of gram
     */
    private void replaceRightMeasure(byte count) {
        if (count <= 1) {
            soundPlayer.playSoundEffect(selectedElement);
            if (selectedElement.equals(keyHotWater)) {
                putElemOnScale(new Image(getClass().getResource(gifURL[4]).toExternalForm()));
            } else if (selectedElement.equals(keyYeast)) {
                putElemOnScale(new Image(getClass().getResource(gifURL[7]).toExternalForm()));
            } else if (selectedElement.equals(keyNaCL)) {
                putElemOnScale(new Image(getClass().getResource(gifURL[2]).toExternalForm()));
            } else if (selectedElement.equals(keySugar)) {
                putElemOnScale(new Image(getClass().getResource(gifURL[10]).toExternalForm()));
            }
        }
    }

    /**
     * Replace dynamically the current gif with gif ...tooMuch.gif
     */
    private void replaceTooMuchMeasure() {
        soundPlayer.playSoundEffect(selectedElement);
        if (selectedElement.equals(keyHotWater)) {
            putElemOnScale(new Image(getClass().getResource(gifURL[5]).toExternalForm()));
        } else if (selectedElement.equals(keyYeast)) {
            putElemOnScale(new Image(getClass().getResource(gifURL[8]).toExternalForm()));
        } else if (selectedElement.equals(keyNaCL)) {
            putElemOnScale(new Image(getClass().getResource(gifURL[1]).toExternalForm()));
        } else if (selectedElement.equals(keySugar)) {
            putElemOnScale(new Image(getClass().getResource(gifURL[11]).toExternalForm()));
        }

    }

    /**
     * Set clicklistener on plus and minus button
     */
    private void setClickableScale() {
        plus.setOnMouseClicked(mouseEvent -> increaseWeight());
        minus.setOnMouseClicked(mouseEvent -> decreaseWeight());
    }

    /**
     * Add a given ImageView to container
     *
     * @param gif ist the ImageView
     * @param x,y is the position of the gif
     */
    private void setGif(ImageView gif, double x, double y) {
        gif.setLayoutX(x);
        gif.setLayoutY(y);
        container.getChildren().add(gif);
    }

    /**
     * Remove an ImageView from container
     *
     * @param content is the ImageView that should removed
     */
    private void removeImageView(ImageView content) {
        container.getChildren().remove(content);
        log.debug("removed element " + content.getImage().getUrl());
    }

    /**
     * Set the clicklistener on distillerButton (add fire gif, or remove fire gif)
     */
    private void setDistillerListener() {
        distillerButton.setOnMouseClicked(mouseEvent -> {
            soundPlayer.playSoundEffect("distiller");
            if (!distillerOn) {
                setGif(fire, 305, 150);
                distillerOn = true;
                log.debug("Distiller on now.");
            } else {
                distillerOn = false;
                removeImageView(fire);
                log.debug("Distiller off now.");
            }
        });
    }

    /**
     * Set clicklistener on new (TableController intern) inventory items with given key and ImageView.
     * Set the booleans true if the item in the view is clicked and call fillUpMixer() method.
     *
     * @param key  is key of item that is clicked
     * @param view is the ImageView of inventoryTable
     */
    private void addClicklistenerForVaccine(String key, ImageView view) {
        view.setId(key);

        switch (key) {

            case "HOTWATER35":
                view.setOnMouseClicked(mouseEvent -> {
                    soundPlayer.playSoundEffect("code_input");
                    this.clickWater35 = true;
                    setClicked(view);
                    log.debug("water true");
                    fillUpMixer();
                });
                break;

            case "YEAST25":
                view.setOnMouseClicked(mouseEvent -> {
                    soundPlayer.playSoundEffect("code_input");
                    this.clickYeast25 = true;
                    setClicked(view);
                    log.debug("yeast true");
                    fillUpMixer();
                });
                break;

            case "NACL15":
                view.setOnMouseClicked(mouseEvent -> {
                    soundPlayer.playSoundEffect("code_input");
                    this.clickNacl15 = true;
                    setClicked(view);
                    log.debug("nacl true");
                    fillUpMixer();
                });
                break;

            case "SUGAR30":
                view.setOnMouseClicked(mouseEvent -> {
                    soundPlayer.playSoundEffect("code_input");
                    this.clickSugar30 = true;
                    setClicked(view);
                    log.debug("sugar true");
                    fillUpMixer();
                });
                break;
        }
    }

    /**
     * Set the background color and opacity of given ImageView
     *
     * @param view is the ImageView of inventoryTable
     */
    private void setClicked(ImageView view) {
        view.setStyle("-fx-background-color: black;");
        view.setOpacity(0.5);
    }

    /**
     * check if all neccesary items are clicked and change the image of mixerBox to mixerFull. Call turnOnMixer.
     */
    private void fillUpMixer() {
        // once all items have been clicked, the mixer can be turned on
        if (clickYeast25 && clickWater35 && clickNacl15 && clickSugar30) {
            turnOnMixer();
            mixerBox.setImage(mixerFull);
            thinkingBox.setText(resourceBundle.getString("interesting"));
            log.debug("set mixer on button");
        } else {
            thinkingBox.setText(resourceBundle.getString("notEnough"));
        }

    }

    /**
     * Thread sleeps 3 seconds by calling
     */
    private void mixingThread() {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.debug(e);
                }
                return null;
            }
        };

        sleeper.setOnSucceeded(event -> stopMixing());
        new Thread(sleeper).start();

    }

    /**
     * Set the mixerGif and call mixingThread. Deletes the items that are necessary for mixing and call TurnOffMixer
     */
    private void mixVaccine() {
        setGif(mixerGif, 529, 288);
        soundPlayer.playSoundEffect("mixer");
        mixingThread();
        log.debug(inventoryTable.getChildren());
        removeInviItem(new String[]{"nacl15", "yeast25", "sugar30", "hotWater35"});
        turnOffMixer();
    }

    /**
     * This clicklistener set on mixerButton method mixVaccine()
     */
    private void turnOnMixer() {
        mixerButton.setOnMouseClicked(mouseEvent -> {
            mixVaccine();
            soundPlayer.playSoundEffect("code_input");
        });
    }

    /**
     * This clicklistener set on mixerButton null
     */
    private void turnOffMixer() {
        mixerButton.setOnMouseClicked(null);
    }


    /**
     * This method remove the mixerGif and set to mixerBox the mixerEmpty image. Call endMiniGame.
     */
    private void stopMixing() {
        removeImageView(mixerGif);
        mixerBox.setImage(mixerEmpty);
        endMiniGame();
    }

    /**
     * This method remove the (may) leftover elements and add the vaccine item to inventory in LaboratoryController
     * Call thread endCardThread
     */
    private void endMiniGame() {
        removeImageView(fire);
        bowlBox.setImage(null);
        inventoryBuilder.addItem(InventoryItems.VACCINE, null, container, false);
        player.setMinigameSolved(SceneSwitchItems.Minigames.TABLE.ordinal(), true);
        soundPlayer.playSoundEffect("finishedTable");
        loadAnimationPopup();
        closePopUp(container);
    }

    public void loadAnimationPopup() {
        AnchorPane ownerPane = (AnchorPane) ((Stage) container.getScene().getWindow()).getOwner().getScene().getRoot();
        loadPopUp("/fxml/WinnerView.fxml", ownerPane, player, 723, 563);
    }

    /**
     * This Method return a given int to as string
     *
     * @param i Is the int that should be converted.
     */
    private String toString(int i) {
        return String.valueOf(i);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        thinkingBox.setText(resourceBundle.getString("whatSouldIDo"));
        closeLabel.setOnMouseClicked(m -> closePopUp(container));
        fillWeightMap();
        setDistillerListener();
        fillInvi();
        mixerBox.setImage(mixerEmpty);
        //------------------------------------------------------------------------------------------------------------


    }
}
