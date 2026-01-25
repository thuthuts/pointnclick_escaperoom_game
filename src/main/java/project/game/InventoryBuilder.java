package project.game;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.gui.GridpaneBuilder;

import java.util.Arrays;

/**
 * This class builds an inventory with a gridpane.
 * You may add one item at a time, add multiple at once or remove a specific one after it has been used.
 */
public class InventoryBuilder {

    private static final Logger log = LogManager.getLogger(InventoryBuilder.class);

    private GridPane inventory;
    private Player player;

    public InventoryBuilder(GridpaneBuilder gridpaneBuilder, Player player) {
        this.inventory = gridpaneBuilder.getInventory();
        this.player = player;
    }

    /**
     * Inventory building with one specific item.
     * This method will add one item to the inventory.
     * @param item Found item.
     * @param tile Item that is potentially being removed from the scene altogether (depending on boolean).
     * @param container is the pane of the current scene.
     * @param removeTile whether or not the tile should be removed from the scene.
     */
    public void addItem(InventoryItems item, ImageView tile, AnchorPane container, boolean removeTile) {
        player.setHasInventoryItem(item.ordinal(), true);
        if (removeTile) container.getChildren().remove(tile);

        ImageView it = (ImageView) inventory.getChildren().get(player.getInventoryItemCount());
        it.setImage(new Image(getClass().getResource(item.getURL()).toExternalForm()));
        it.setId(item.name());
        log.debug("Id of imageview is now: " + it.getId());
        player.increaseItemInventoryNumber(1);

        log.debug("Player has a total of " + player.getInventoryItemCount() + " item(s) right now!");
    }


    /**
     * Inventory building with multiple items.
     * This method will add multiple items to the inventory at once.
     * @param items Found items.
     */
    public void addItems(InventoryItems[] items) {
        Arrays.stream(items).parallel().forEach(foundItem -> {
            player.setHasInventoryItem(foundItem.ordinal(), true);
            ImageView it = (ImageView)inventory.getChildren().get(player.getInventoryItemCount());
            it.setImage(new Image(getClass().getResource(foundItem.getURL()).toExternalForm()));
            it.setId(foundItem.name());
            player.increaseItemInventoryNumber(1);
        });

        log.debug("Player has a total of " + player.getInventoryItemCount() + " item(s) right now!");
    }

    /**
     * Removing an item from the inventory after it has been used up.
     * @param item that has been used up and gets deleted from the inventory.
     */
    public void removeItem(InventoryItems item) {
        player.setHasInventoryItem(item.ordinal(), false);
        player.setHasUsedInventoryItem(item.ordinal(), true);

        for (int i = 0; i < inventory.getChildren().size(); i++) {
            Node node = inventory.getChildren().get(i);
            if (node.getId().equals(item.name())) {
                ((ImageView)node).setImage(null);
                inventory.getChildren().get(i).setId("list" + i);
                break;
            }
        }
        // Reduce total Itemnumber by one
        player.increaseItemInventoryNumber(-1);
        log.debug("Player has a total of " + player.getInventoryItemCount() + " item(s) right now!");
        removeEmptyInventorySpace();
    }

    /**
     * Rebuilds the inventory chronologically and refills the empty space that removeItem method has created.
     * EX.: RECORD | KEY | NOTE     -> Key is being removed with removeItem() and results in:
     *      RECORD | NULL | NOTE    -> Null gets removed and note is being taken one space forward with rebuildInventory():
     *      RECORD | NOTE
     */
    private void removeEmptyInventorySpace(){
        try {
            int count = 0;
            for (int i = 0; i < inventory.getChildren().size(); i++){
                ImageView imageView = (ImageView) inventory.getChildren().get(i);
                String id = inventory.getChildren().get(i).getId();

                if (imageView.getImage() != null){

                    Image img = imageView.getImage();
                    imageView.setImage(null);
                    imageView.setId("list" + i);

                    ((ImageView) inventory.getChildren().get(count)).setImage(img);
                    inventory.getChildren().get(count).setId(id);
                    count++;
                }
            }
            log.debug("Inventory: " + inventory.getChildren());

        } catch (Exception e){
            log.fatal("Inventory couldn't be rebuilded");
        }
    }

    /**
     * This method will sort the inventory of a player, given the ascending order of InventoryItems.java constants.
     * It may also be used to restore all items in the gridpane if the player has reloaded the game.
     */
    public void rebuildInventory(){
        boolean[][] playerItem = player.getHasInventoryItemBooleans();
        int count = 0;
        for (int i = 0; i < playerItem.length; i++) {
            if (playerItem[i][0]){
                ((ImageView)inventory.getChildren().get(count)).setImage(new Image(getClass().getResource(InventoryItems.values()[i].getURL()).toExternalForm()));
                inventory.getChildren().get(count).setId(InventoryItems.values()[i].name());
                count++;
            }
        }
    }
}
