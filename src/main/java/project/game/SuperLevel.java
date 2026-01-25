package project.game;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.gui.SuperController;
import project.audio.SoundPlayer;

import java.io.IOException;
import java.util.Arrays;

public class SuperLevel extends SuperController implements ILevel {

    private static final Logger log = LogManager.getLogger(SuperLevel.class);

    private AnchorPane container;
    private final int levelNumber;

    // TODO: 08.12.2020 parse textmanager at the start of game configurations, inject afterwards
    private Player player;
    private JSONTextManager textManager;
    private InventoryBuilder inventoryBuilder;
    private SoundPlayer soundPlayer;

    public SuperLevel(Player player, InventoryBuilder inventoryBuilder, JSONTextManager textManager, SoundPlayer soundPlayer, int levelNumber) {
        this.player = player;
        this.levelNumber = levelNumber;
        this.inventoryBuilder = inventoryBuilder;
        this.textManager = textManager;
        this.soundPlayer = soundPlayer;
    }

    private final EventHandler<MouseEvent> minigameClickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            SceneSwitchItems.Minigames minigame = SceneSwitchItems.Minigames.valueOf(((Node)mouseEvent.getSource()).getId());
            if(!player.getMinigameSolved()[minigame.ordinal()]) {
                loadPopUp(minigame.getFXMLURL(), container, player, 770, 560);
            }
        }
    };

    /**
     * This method loops through all scene switching items and sets a MouseClick-Event on each.
     * @param sceneSwitchItems all items that will change to another scene (EX.: door)
     */
    @Override
    public void setAllSceneSwitchItems(ImageView[] sceneSwitchItems) {
        Arrays.stream(sceneSwitchItems).parallel().forEach(imageView -> {
            SceneSwitchItems.Scenes item = SceneSwitchItems.Scenes.valueOf(imageView.getId());
                imageView.setOnMouseClicked(mouseEvent -> {
                    loadNextScene(item.getFXMLURL(), container, player);
                });
        });
    }
    /**
     * This method loops through all minigame items and sets a MouseClick-Event on each.
     *
     * @param minigameItems all items that will open a popup-minigame after clicking on it.
     */
    @Override
    public void setAllMinigameItems(ImageView[] minigameItems) {
        Arrays.stream(minigameItems).parallel().forEach(imageView -> {
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, minigameClickEvent);
        });
    }

    public void removeMinigameClickEvent(Node node){
        node.removeEventHandler(MouseEvent.MOUSE_CLICKED, minigameClickEvent);
    }

    /**
     * This method loops through all describable items and sets a MouseClick-Event on each.
     * An instance of JSONTextManager gets the matching description of the clicked item as a String and
     * it is passed to loadPopup() to open the Popup, which prints out the
     * itemDesc.
     *
     * @param roomItems      all items in the room that are being described (ex.: Bin)
     * @param inventorySlots all imageviews(InventoryItems) in the inventory(Gridpane) that are being described.
     */
    @Override
    public void setAllDescribableItems(ImageView[] roomItems, ObservableList<Node> inventorySlots) {
        Arrays.stream(roomItems).parallel().forEach(imageView -> {
            imageView.setOnMouseClicked(mouseEvent -> {
                Node node = (Node) mouseEvent.getSource();
                loadTextPopUp(textManager.getText(node.getId(), player.getLangSettings()), container, player);
            });
        });
        //inventorySlots = list0,list1....list7
        inventorySlots.stream().parallel().forEach(img -> {
            img.setOnMouseClicked(mouseEvent -> {
                Node node = (Node) mouseEvent.getSource();
                if (textManager.hasKey(node.getId())) {
                    loadTextPopUp(textManager.getText(((Node) mouseEvent.getSource()).getId(), player.getLangSettings()), container, player);
                }
            });
        });
    }

    /**
     * This method loops through all pickable items on the scene and sets a MouseClick-Event on each.
     * Whenever a Node of that array is clicked, it will be added to the inventory and may get removed from the ground(scene) depending on the boolean param.
     *
     * @param inventoryItems is every single item in the scene that can be picked up by the player.
     * @param removeTile     whether or not the item should be removed from the scene.
     */
    @Override
    public void setAllInventoryItems(ImageView[] inventoryItems, boolean removeTile) {
        // parallel throws exception for duplicate children
        Arrays.stream(inventoryItems).forEach(item -> {
             if (!player.getHasInventoryItemBooleans()[InventoryItems.valueOf(item.getId()).ordinal()][0] &&
                 !player.getHasInventoryItemBooleans()[InventoryItems.valueOf(item.getId()).ordinal()][1]) {

                item.setOnMouseClicked(mouseEvent -> {
                    soundPlayer.playSoundEffect("item_collected");
                    InventoryItems itemEnum = InventoryItems.valueOf((((Node) mouseEvent.getSource()).getId()));
                    log.debug("SELECTED: " + itemEnum.name());
                    // Item removes itself
                    inventoryBuilder.addItem(itemEnum, (ImageView) mouseEvent.getSource(), container, removeTile);
                    log.debug(Arrays.deepToString(player.getHasInventoryItemBooleans()));
                    if (!removeTile) item.setOnMouseClicked(null);
                });
            } else {
                if (removeTile)container.getChildren().remove(item);
            }
        });
    }

    @Override
    public void addSortItemsButton(Button button) {
        button.setOnMouseClicked(mouseEvent -> {
            soundPlayer.playSoundEffect("sort");
            inventoryBuilder.rebuildInventory();
        });
    }

    public void addFailCountListener(Label label){
        label.textProperty().bind(player.getFailCounterProperty().asString());
    }

    public void addTutorialButton(Button button){
        button.setOnMouseClicked(mouseEvent -> loadPopUp("/fxml/TutorialView.fxml", container, player, 723, 563));
    }

    public void addBackToMenuButton(Button button){
        button.setOnMouseClicked(mouseEvent -> {
            loadPopUp("/fxml/BackToMenuView.fxml",container,player,600,300);
        });
    }

    public void addInsideFridge(ImageView button){
        button.setOnMouseClicked(mouseEvent -> loadPopUp("/fxml/InsideFridge.fxml",container,player,770,560));
    }


    public void addSettingsButton(String actualURL, ImageView icon) {
        icon.setOnMouseClicked(mouseEvent -> loadNextSceneSettings(actualURL, container, player));
    }

    @Override
    public void addSaveGameStateButton(Button button, Label infoLabel) {
        button.setOnMouseClicked(mouseEvent -> {
            try {
                player.setEndDate();
                GameState.saveGame(player, "playerData");
                player.setCalcStartDate();
                infoLabel.setStyle("-fx-text-fill: #3a8335");
                setTextTimer(infoLabel, "saved!", 2);
                soundPlayer.playSoundEffect("game_saved");
                log.info("Your game has been saved...\n" + player);
            } catch (IOException e) {
                infoLabel.setStyle("-fx-text-fill: #cb5757");
                setTextTimer(infoLabel, "failed...", 2);
                e.printStackTrace();
            }
        });
    }

    public void setContainer(AnchorPane container) {
        this.container = container;
    }

    /**
     * This method will show a message (Error or tip) for the player
     * for a specified time.
     * @param text that is being shown.
     */
    private void setTextTimer(Label infoLabel, String text , int duration){
        infoLabel.setText(text);
        PauseTransition wait = new PauseTransition(Duration.seconds(duration));
        wait.setOnFinished((e) -> infoLabel.setText(null));
        wait.play();
    }

    public void reloadPlayerInventoryProgress(){
        if (!player.hasEndDateNull()){
            inventoryBuilder.rebuildInventory();
        }
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "Level: " + levelNumber;
    }
}
