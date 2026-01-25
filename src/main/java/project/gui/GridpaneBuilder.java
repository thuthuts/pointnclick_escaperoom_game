package project.gui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GridpaneBuilder {

    private static final Logger log = LogManager.getLogger(GridpaneBuilder.class);
    private GridPane inventory;

    private final int ITEMSLOTS = 13;
    private final int WIDTH = 1040;
    private final int HEIGHT = 80;

    public GridpaneBuilder() {
        createGridPane();
    }

    public void createGridPane(){
        inventory = new GridPane();
        for (int i = 0; i < ITEMSLOTS; i++){
            ImageView item = new ImageView();
            inventory.add(item, i , 0);
        }
        inventory.setPrefWidth(WIDTH);
        inventory.setPrefHeight(HEIGHT);
        inventory.setMinWidth(WIDTH);
        inventory.setMinHeight(HEIGHT);
        inventory.setHgap(20);
        inventory.setLayoutX(22);
        inventory.setLayoutY(620);
        inventory.getStyleClass().add("inventoryBackground");
        log.debug("Inventory created");
    }

    public GridPane getInventory() {
        return inventory;
    }
}
