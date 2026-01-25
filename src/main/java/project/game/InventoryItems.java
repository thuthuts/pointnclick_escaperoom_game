package project.game;

/**
 * This Item Enum class keeps track of every single collectable
 * item in the game. It's responsible for building the inventory
 * with the right item pictures in resources/img (String URL)
 */
public enum InventoryItems {

    //the four keys have to be index 0-3
    KEYHEART("/img/keyHeart.png"),
    KEYCROSS("/img/keyCross.png"),
    KEYKARO("/img/keyKaro.png"),
    KEYPIK("/img/keyPik.png"),
    PAPER("/img/paper.png"),
    YEAST("/img/yeast.png"),
    NACL("/img/NaCl.png"),
    SUGAR("/img/sugar.png"),
    WATER("/img/water.png"),
    RECIPE_NACL("/img/note.png"),
    RECIPE_WATER("/img/note.png"),
    RECIPE_YEAST("/img/note.png"),
    RECIPE_SUGAR("/img/note.png"),
    HOTWATER("/img/tableEndMinigame/hotWater.png"),
    HOTWATER35("/img/tableEndMinigame/hotWater35.png"),
    NACL15("/img/tableEndMinigame/nacl15.png"),
    SUGAR30("/img/tableEndMinigame/sugar30.png"),
    YEAST25("/img/tableEndMinigame/yeast25.png"),
    VACCINE("/img/vaccine.png"),
    PW_NOTE("/img/note.png");

    private final String URL;

    InventoryItems(String url) {
        this.URL = url;
    }

    public String getURL() {
        return URL;
    }
}
