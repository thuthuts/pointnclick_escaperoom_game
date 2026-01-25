package project.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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

public class LivingRoomController extends SuperLevel implements Initializable {

    private static final Logger log = LogManager.getLogger(LivingRoomController.class);
    private Player player;
    private SoundPlayer soundPlayer;

    private GridPane inventory;
    @FXML private AnchorPane container;
    @FXML private HBox bottom;
    @FXML private Button sort, saveGame, tutorial, backToMenu;
    @FXML private Label saveInfo, failCountLabel;
    @FXML ImageView settingsIcon, windowLight, FADE;
    // Item ImageViews to pick up.
    @FXML private ImageView RECIPE_NACL, NACL, RECIPE_SUGAR;
    // Minigame ImageViews that will open the respective popup
    @FXML private ImageView BED, fridge;
    // ImageViews that changes whole scene
    @FXML private ImageView LABORATORY;
    // Items to describe
    @FXML private ImageView STARRYNIGHT, PAPERSTACK4, TRASH3, NOTE6, NOTE7, NOTE8, FLOWERS, LOCKGREEN, BLUESUBSTANCE, BEER, BEER2, BEER3, BEER4, BEER5, BEER6, CYANCREWMATE, BOOKSHELF1, BOOKSHELF2, SOS, BOTTLE, DRAWING, PIZZA,
    VENTS, BLOOD2, BARRELS1, BARRELS2, CHESTS, TREASURE, KNIFES, SUBSTANCES2, SCISSOR, PAPERSTACKS, SOFA, FIREPLACE, MINITABLE_LEFT, MINITABLE_RIGHT, FIRE;


    public LivingRoomController(Player player, JSONTextManager textManager, GridpaneBuilder gridpaneBuilder, SoundPlayer soundPlayer, InventoryBuilder inventoryBuilder) {
        super(player , inventoryBuilder, textManager, soundPlayer, 0);
        this.inventory = gridpaneBuilder.getInventory();
        this.soundPlayer = soundPlayer;
        this.player = player;
    }

    public void setupGUIPlayerProgress() {
        addFailCountListener(failCountLabel);
        addTutorialButton(tutorial);
        addSortItemsButton(sort);
        addSaveGameStateButton(saveGame, saveInfo);
        addSettingsButton(SceneSwitchItems.Scenes.LIVINGROOM.getFXMLURL(), settingsIcon);
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setContainer(container);
        setupGUIPlayerProgress();
        Timer fadeTimer = new Timer(1000, event -> makeFadeOut(FADE, container));
        fadeTimer.setRepeats(false);
        fadeTimer.start();

        startWindowLightAnimation();

        bottom.getChildren().add(inventory);
        setAllSceneSwitchItems(new ImageView[]{LABORATORY});
        setAllInventoryItems(new ImageView[]{NACL, RECIPE_NACL}, true);
        setAllInventoryItems(new ImageView[]{RECIPE_SUGAR}, false);
        setAllDescribableItems(new ImageView[]{STARRYNIGHT, PAPERSTACK4, TRASH3, NOTE6, NOTE7, NOTE8, FLOWERS, LOCKGREEN, BLUESUBSTANCE, BEER, BEER2, BEER3, BEER4, BEER5, BEER6, CYANCREWMATE, BOOKSHELF1, BOOKSHELF2, SOS, BOTTLE, DRAWING, PIZZA,
                VENTS, BLOOD2, BARRELS1, BARRELS2, CHESTS, TREASURE, KNIFES, SUBSTANCES2, SCISSOR, PAPERSTACKS, SOFA, FIREPLACE, MINITABLE_LEFT, MINITABLE_RIGHT, FIRE}, inventory.getChildren());
        addInsideFridge(fridge);
        setAllMinigameItems(new ImageView[]{BED});

    }
}
