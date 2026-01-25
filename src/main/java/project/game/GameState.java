package project.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;

import java.io.*;

/**
 * This class is responsible for saving and loading data of a Player.
 * The Player is Serializable, so the java object can be stored/restored in/from a *.ser file.
 */
public class GameState {

    private static final Logger log = LogManager.getLogger(GameState.class);

    /**
     * This method will save a players game data to a given fileName in the resources directory.
     * @param player that is being saved.
     * @param fileName the player data will be written into. (Without .ser extension)
     */
    public static void saveGame(Player player, String fileName) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            player.setSumPlayTime();
            fos = new FileOutputStream("src/main/resources/" + fileName + ".ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(player);

        } catch (IOException e){
            throw new IOException();

        } finally {
            assert oos != null;
            oos.flush();
            oos.close();
            fos.close();

        }
    }

    /**
     * This method deletes the fileName in src/main/resources/ directory
     * @param fileName is the file that gets deleted
     */
    public static boolean deleteGame(String fileName) {
        File fileResource = new File("src/main/resources/" + fileName + ".ser");
        File fileTarget = new File("target/classes/" + fileName + ".ser");
        if(fileResource.delete() && fileTarget.delete()) {
            log.debug("File " + fileName + " deleted");
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will load the game of a player with the given file name.
     * @param fileName of the data the player has been written into. (Without .ser extension)
     * @return saved player object.
     * @throws IOException File doesn't exist.
     */
    public static Player loadGame(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            File playerFile = new File(GameState.class.getResource("/" + fileName + ".ser").getFile());
            fis = new FileInputStream(playerFile);
            ois = new ObjectInputStream(fis);
            Player player = (Player) ois.readObject();
            player.setCalcStartDate();
            log.info("The existing player state has been reloaded..." + player);
            return player;

        } catch (IOException e){
            throw new IOException();

        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException();

        } finally {
            assert ois != null;
            ois.close();
            fis.close();

        }

    }
}
