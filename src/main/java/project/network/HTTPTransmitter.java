package project.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class HTTPTransmitter {
    private static final Logger log = LogManager.getLogger(HTTPTransmitter.class);


    public static void main(String[] args) throws IOException {
        /*Player player = new Player(new SettingsManager(), new InventoryManager());
        player.setName("UwU");
        player.increaseFailCount();
        player.setCalcStartDate();
        player.setEndDate();
        player.setSumPlayTime();

        HTTPBuilder builder = new HTTPBuilder("https://escaperoom-game.herokuapp.com/players");
        System.out.println(builder.httpPOST(player).body());*/
        //builder.httpPOST(player);
        /*HttpResponse<String> players = builder.httpGET();
        log.debug(players.body());
        System.out.println(builder.JSONtoPlayer(players));*/
    }
}
