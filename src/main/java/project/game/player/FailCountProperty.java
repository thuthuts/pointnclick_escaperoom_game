package project.game.player;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;

/**
 * This class takes care of the players current failCount (Number of times they've failed in a minigame).
 * And the purpose of the failCount is ultimately for the highscore, since it increases the playtime as a punishment.
 * In order to make the current failCount visible to the player, JavaFX needs an IntegerProperty to bind it to
 * the respective Label afterwards. The failCount Label updates itself whenever the failCount increases.
 *
 * Since IntegerProperty in itself is not {@link Serializable}, it was needed to create a seperate class to define
 * write and readExternal methods to save it into an .ser file afterwards. It is being converted into an int value for saving,
 * and the integer is being written back into the IntegerProperty Object by loading a player file.
 */
public class FailCountProperty implements Externalizable {

    private final IntegerProperty property = new SimpleIntegerProperty();

    public FailCountProperty() {
        this.property.set(0);
    }

    public IntegerProperty getFailCountProperty() {
        return property;
    }

    public int getFailCountInt() {
        return property.get();
    }

    public void increaseFailCountProperty() {
        this.property.set(property.get()+1);
    }

    public void setFailCount(int value) {
        this.property.set(value);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getFailCountInt());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setFailCount((int) in.readObject());
    }
}
