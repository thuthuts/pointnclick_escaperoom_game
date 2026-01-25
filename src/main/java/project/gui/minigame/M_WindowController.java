package project.gui.minigame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.gui.SuperController;
import project.audio.SoundPlayer;
import project.game.InventoryBuilder;
import project.game.InventoryItems;
import project.game.player.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class M_WindowController extends SuperController implements Initializable {

    @FXML private AnchorPane container;
    @FXML private Label closeLabel;
    @FXML private ImageView KEYHEART;
    private SoundPlayer soundPlayer;
    private Player player;
    private InventoryBuilder inventoryBuilder;

    private static final Logger log = LogManager.getLogger(SuperController.class);

    public M_WindowController(Player player, InventoryBuilder inventoryBuilder, SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
        this.inventoryBuilder = inventoryBuilder;
        this.player = player;
    }

    public void collectKey(){
        inventoryBuilder.addItem(InventoryItems.KEYHEART, KEYHEART, container, true);
        soundPlayer.playSoundEffect("item_collected");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        if (player.getHasInventoryItemBooleans()[InventoryItems.KEYHEART.ordinal()][0] || player.getHasInventoryItemBooleans()[InventoryItems.KEYHEART.ordinal()][1]) container.getChildren().remove(KEYHEART);
        closeLabel.setOnMouseClicked(mouseEvent -> closePopUp(container));
    }
}
