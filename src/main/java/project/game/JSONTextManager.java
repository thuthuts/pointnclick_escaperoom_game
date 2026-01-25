package project.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Settings;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class creates a Map of all Item descriptions in a level in its corresponding language DE/EN.
 * It is used to create a text-popup to describe the item that has been clicked.
 * The map is being parsed once the user has set their language to play in.
 * You may want to retrieve the value text of an item key, or check if the map includes that key.
 */
public class JSONTextManager {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final Logger log = LogManager.getLogger(JSONTextManager.class);
    private Map<String, ArrayList<String>> itemMap;

    /** This method will parse a json file into a hashmap with an String of item names as key and
     *  a String of description as value
     * @param fileName of json file (Without .json extension)
     */
    public void parseJSON(String fileName) {
            try  {
                File itemFile = new File(getClass().getResource("/text/" + fileName + ".json").toURI());
                itemMap = objectMapper.readValue(itemFile, new TypeReference<>() {});
                log.debug("Item Description Hashmap has been created");

            } catch (IOException | URISyntaxException e) {
                log.fatal("File does not exist");
            }
    }

    /**
     *  This method will get the description of an item from the map with a given language.
     *  @param item id of the item.
     */
    public String getText(String item, Settings.language lang) {

        if (itemMap.containsKey(item)) {
                String itemText = itemMap.get(item).get(lang.ordinal());
                log.debug("\nText of key: " + item + ", Value: " + itemText);
                return itemText;

        } else {
                log.debug("Item does not exist");
        }
        return null;
    }

    public boolean hasKey(String item){
        return itemMap.containsKey(item);
    }

    public boolean mapExists(){
        return itemMap != null;
    }

    @Override
    public String toString() {
        return itemMap.toString();
    }
}


