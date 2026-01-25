package project.game.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.InventoryItems;
import project.game.SceneSwitchItems;

import java.io.Serializable;

/**
 * This class keeps track of the players progress and whether or not they have
 * collected an item. This class is being created in each Player.java Instance.
 * The index of the boolean[] Array corresponds to the index of InventoryItems[]
 *              0     1      2
 * boolean[] = {true, false , ...} -> Player collected InventoryItems.values()[0]
 *                                 -> Player didn't collect InventoryItems.values()[1]
 */
public class PlayerProgressManager implements Serializable {

    private static final Logger log = LogManager.getLogger(PlayerProgressManager.class);
    private boolean[][] hasItem;
    private boolean[] solvedMinigame;
    private int itemNumber;

    public PlayerProgressManager() {
        // Array Scales itself with Item and Minigame Enum Count and fills everything up with false by default if Player is new
        hasItem = new boolean[InventoryItems.values().length][2];
        solvedMinigame = new boolean[SceneSwitchItems.Minigames.values().length];
        itemNumber = 0;
    }

    public boolean[][] getHasItem() {
        return hasItem;
    }

    public void setHasItem(int index, boolean hasItem) {
        this.hasItem[index][0] = hasItem;
    }

    public void setHasUsedItem(int index, boolean hasItem) {
        this.hasItem[index][1] = hasItem;
    }

    public void setMinigameSolved(int minigameIndex, boolean solved) {
        this.solvedMinigame[minigameIndex] = solved;
    }

    public boolean[] getSolvedMinigame() {
        return solvedMinigame;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public void increaseItemNumber(int increase){
        this.itemNumber = itemNumber + increase;
    }
}
