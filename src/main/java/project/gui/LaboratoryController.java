package project.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.*;
import project.game.player.Player;
import project.network.HTTPBuilder;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LaboratoryController extends SuperLevel implements Initializable {

    private static final Logger log = LogManager.getLogger(LaboratoryController.class);
    private Player player;
    private SoundPlayer soundPlayer;

    private GridPane inventory;
    @FXML private AnchorPane container;
    @FXML private HBox bottom;
    @FXML private Button sort, saveGame, tutorial, backToMenu;
    @FXML private Label saveInfo, failCountLabel;
    @FXML ImageView settingsIcon, windowLight, FADE;
    // Item ImageViews to pick up.
    @FXML private ImageView KEYKARO, KEYCROSS, KEYPIK, RECIPE_WATER, RECIPE_YEAST, SUGAR;
    // Minigame ImageViews that will open the respective popup
    @FXML private ImageView JUNCTIONBOX, DOOR, PC, WINDOW, TABLE;
    // Items to describe
    @FXML private ImageView CANDLE1, CANDLE2, WOODWINDOW, TRASH1, TRASH2, REDLIGHT1, REDLIGHT2, SCREEN1, SCREEN2, KEYBOARD1, KEYBOARD2, NOTE2, NOTE3, NOTE4, NOTE5, NOTE6,
    PEN1, PEN2, HARDWARE1, HARDWARE2, MONITOR1, MONITOR2, MACHINES1, MACHINES2 ,BLOOD, FLOWER1, PAPER1, PAPER2, PAPER3, MONALISA, SUBSTANCES, PAPERSTACK1,
    PAPERSTACK2, WEIRDDRAWING, MINICHAIR1, ENTRYDOOR, MINITABLE1, MINITABLE2, VENTS1, VENTS2;


    public LaboratoryController(Player player, JSONTextManager textManager, GridpaneBuilder gridpaneBuilder, SoundPlayer soundPlayer, InventoryBuilder inventoryBuilder) {
        super(player , inventoryBuilder, textManager, soundPlayer, 0);
        this.inventory = gridpaneBuilder.getInventory();
        this.soundPlayer = soundPlayer;
        this.player = player;
    }

    public void setupGUIPlayerProgress() {
        reloadPlayerInventoryProgress();
        if (player.getMinigameSolved()[SceneSwitchItems.Minigames.JUNCTIONBOX.ordinal()]){
            Node node = container.getChildren().stream().filter(nodeScene -> nodeScene.getId().equals("LIGHT")).findFirst().get();
            container.getChildren().remove(node);
        }

        if (player.getMinigameSolved()[SceneSwitchItems.Minigames.DOOR.ordinal()]){
            //Node node = container.getChildren().stream().filter(nodeScene -> nodeScene.getId().equals("DOOR")).findFirst().get();
            removeMinigameClickEvent(DOOR);
            DOOR.setOnMouseClicked(mouseEvent -> loadNextScene(SceneSwitchItems.Scenes.LIVINGROOM.getFXMLURL(), container, player));
        }

        addFailCountListener(failCountLabel);
        addTutorialButton(tutorial);
        addBackToMenuButton(backToMenu);
    }

    public void startWindowLightAnimation(){
        FadeTransition ft = new FadeTransition(Duration.millis(10000), windowLight);
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
    }

    public void playSound(){
        if (soundPlayer.getStatus() != MediaPlayer.Status.PLAYING){
            soundPlayer.playBackgroundSound("laboratory2");
        }
    }

    public void postPlayerData() throws IOException {
        HTTPBuilder builder = new HTTPBuilder("https://escaperoom-game.herokuapp.com/players");
        player.setEndDate();
        GameState.saveGame(player, "playerData");
        builder.httpPOST(player);
        log.debug("Player has been posted");
    }

    public void loadEndingPopUp(){
        loadPopUp(SceneSwitchItems.Scenes.ENDING.getFXMLURL(), container, player, 770, 560);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Platform.runLater(this::loadEndingPopUp);

        Timer fadeTimer = new Timer(1000, event -> makeFadeOut(FADE, container));
        fadeTimer.setRepeats(false);
        fadeTimer.start();

        startWindowLightAnimation();
        Timer soundTimer = new Timer(500, event -> playSound());
        soundTimer.setRepeats(false);
        soundTimer.start();

        setContainer(container);
        setupGUIPlayerProgress();
        bottom.getChildren().add(inventory);
        setAllInventoryItems(new ImageView[]{KEYKARO, KEYCROSS, KEYPIK, RECIPE_WATER, SUGAR}, true);
        setAllInventoryItems(new ImageView[]{RECIPE_YEAST}, false);
        setAllDescribableItems(new ImageView[]{CANDLE1, CANDLE2, WOODWINDOW, TRASH1, TRASH2, REDLIGHT1, REDLIGHT2, SCREEN1, SCREEN2, KEYBOARD1, KEYBOARD2, NOTE2, NOTE3, NOTE4, NOTE5, NOTE6,
                PEN1, PEN2, HARDWARE1, HARDWARE2, MONITOR1, MONITOR2, MACHINES1, MACHINES2, BLOOD, FLOWER1, PAPER1, PAPER2, PAPER3, MONALISA, SUBSTANCES, PAPERSTACK1,
                PAPERSTACK2, WEIRDDRAWING, MINICHAIR1, ENTRYDOOR, MINITABLE1, MINITABLE2, VENTS1, VENTS2}, inventory.getChildren());

        setAllMinigameItems(new ImageView[]{JUNCTIONBOX, DOOR, PC, TABLE, WINDOW});
        addSortItemsButton(sort);
        addSaveGameStateButton(saveGame, saveInfo);
        addSettingsButton(SceneSwitchItems.Scenes.LABORATORY.getFXMLURL(), settingsIcon);
    }
}
