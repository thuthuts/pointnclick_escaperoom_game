package project.game;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 */
public interface IMinigame {

    // method that adds variety to the minigame and randomizes input of the user
    public void randomize();

    // method that resets input of the user
    public void reset();
}
