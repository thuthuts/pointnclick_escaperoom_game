package project.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import project.game.player.Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * This class builds HTTP Requests to a Database Server with GET, POST and DELETE Methods.
 */
public class HTTPBuilder {

    private final String POSTS_API_URL;
    private final Logger log = LogManager.getLogger(HTTPBuilder.class);

    public HTTPBuilder(String POSTS_API_URL) {
        this.POSTS_API_URL = POSTS_API_URL;
    }

    /**
     * Turn a player into a JSONObject.
     * This avoids problems with failed LocalDateTime JSON Parsing.
     * @param player who ended the game
     * @return JSONObject
     */
    private JSONObject playerToJSON(Player player){
        JSONObject pl = new JSONObject("{\"name\":\"" + player.getName() + "\", \"failCounter\":\"" + player.getFailCounter() + "\", \"startDate\":\"" + player.getStartDate() +
                "\", \"endDate\":\"" + player.getEndDate() + "\", \"sumPlayTime\":\"" + player.getSumPlayTime() + "\"}");
        log.debug(pl.toString());
        return pl;
    }

    /**
     *
     * @param jsonList Response of Get-Request, which is a list of players
     * @return jsonList as ArrayList with Player Objects
     * @throws IOException
     * todo Duration?
     */
    public List<Player> JSONtoPlayer(HttpResponse<String> jsonList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.readValue(jsonList.body(), new TypeReference<ArrayList<Player>>() {
        });
    }

    /**
     * Method to encrypt data of authorization header.
     * @param username of login
     * @param password of login
     * @return auth data encrypted
     */
    private String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    /**
     * Send GET Method Request to Server to retrieve High Score of the database.
     * Duration has a maximum of 10 Seconds.
     * The Server is most probably unavailable if failed.
     */
    public HttpResponse<String> httpGET() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("Authorization", "Basic ZXNjYXBlOmphbnNs")
                    .timeout(Duration.ofSeconds(10000))
                    .header("accept", "application/json")
                    .uri(URI.create(POSTS_API_URL))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            log.fatal("httpGET Request has failed: \n" + e.getMessage() + "\n");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send POST Method Request to Server to save your score to the high score list.
     * Duration has a maximum of 10 Seconds.
     * The Server is most probably unavailable if failed.
     * @param player who has finished the game successfully.
     */
    public HttpResponse<String> httpPOST(Player player){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString((playerToJSON(player)).toString()))
                    .header("Authorization", "Basic ZXNjYXBlOmphbnNs")
                    .timeout(Duration.ofSeconds(10000))
                    .uri(URI.create(POSTS_API_URL))
                    .header("Content-Type", "application/json")
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            log.fatal("httpPOST Request has failed: \n" + e.getMessage() + "\n");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send DELETE Method Request to Server to delete a specific PRIMARY KEY id in the database.
     * Duration has a maximum of 10 Seconds.
     * The Server is most probably unavailable if failed.
     * @param id of player PK in the database.
     */
    public HttpResponse<String> httpDELETE(int id){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .header("Authorization", "Basic ZXNjYXBlOmphbnNs")
                    .timeout(Duration.ofSeconds(10000))
                    .uri(URI.create(POSTS_API_URL + "/" + id))
                    .header("Content-Type", "application/json")
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            log.fatal("httpDELETE Request has failed: \n" + e.getMessage() + "\n");
            e.printStackTrace();
        }
        return null;
    }

}
