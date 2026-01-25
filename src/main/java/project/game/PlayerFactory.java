package project.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.FailCountProperty;
import project.game.player.Player;
import project.game.player.PlayerProgressManager;
import project.game.player.SettingsManager;

import java.io.IOException;

/**
 * This class is the factory for a player bean.
 * It either returns a new player (If no data file is available) or
 * returns a player with their progress of the game.
 */
public class PlayerFactory {

    private static final Logger log = LogManager.getLogger(PlayerFactory.class);

    /**
     * This method will create a Player through GameState.java.
     * If the player has saved the game once, their file will be reloaded respectively.
     * @param fileName to the saved game data. DEFAULT: "playerData.ser"
     * @return either an already existing player or a new player.
     */
    public static Player getPlayer(String fileName){
        try {
            return GameState.loadGame(fileName);
        } catch (IOException | NullPointerException e){

            // No player data has been found, so a new player is being returned
            Player player = new Player(new SettingsManager(), new PlayerProgressManager(), new FailCountProperty());
            player.setCalcStartDate();
            return player;

        } catch (ClassNotFoundException e){
            log.fatal("Class Player doesn't exist");
        }
        log.fatal("Player bean couldn't be created from factory...");
        return null;
    }
}
