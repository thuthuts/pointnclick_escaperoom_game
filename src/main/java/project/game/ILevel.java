package project.game;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * This Interface describes the methods of a Main Level-Scene. It is not displayed as a popup, but as a whole 1280x720px scene.
 */
public interface ILevel {

    // Changes whole Level Scene 1280x720px to another Level Scene 1280x720px (Laboratory to Bedroom)
    public void setAllSceneSwitchItems(ImageView[] sceneSwitchItems);
    // Adds clicklistener to imageviews that will open a minigame
    public void setAllMinigameItems(ImageView[] minigameItems);
    // Items that are standing in the room & are being described when clicked + inventory slots that are being described if the slot contains an item
    public void setAllDescribableItems(ImageView[] roomItems, ObservableList<Node> inventorySlots);
    // Items you can pick up from the ground and collect in your inventory
    public void setAllInventoryItems(ImageView[] inventoryItems, boolean removeTile);
    // Button for sorting Inventory
    public void addSortItemsButton(Button button);
    // Button for saving current player
    public void addSaveGameStateButton(Button button, Label infoLabel);

}
