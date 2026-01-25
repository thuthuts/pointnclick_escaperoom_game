package project.thread;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.network.HTTPBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * Performs request to our online server and handles the response in order to display it in
 * the HighscoreController later.
 */
public class HighscoreThread implements Runnable{

    private static final Logger log = LogManager.getLogger(HighscoreThread.class);
    private HttpResponse<String> players;
    private HTTPBuilder builder;
    ObservableList<Player> data = FXCollections.observableArrayList();

    /**
     * starts HTTP Request to get data from heroku server
     */
    public void apiCall(){
        try {
            builder = new HTTPBuilder("https://escaperoom-game.herokuapp.com/players");
            players = builder.httpGET();
            data.addAll(builder.JSONtoPlayer(players));
            log.debug(players.body() + " data received");

        } catch(IOException e){
            log.error("API call failed, The Server is not available right now.");
        }
    }

    @Override
    public void run() {
            apiCall();
    }

    public ObservableList<Player> getData(){
        return data;
    }
}

