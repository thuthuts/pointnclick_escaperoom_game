package project;

import org.junit.Before;
import org.junit.Test;
import project.game.player.Player;
import project.game.PlayerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class GameStateTest {

    private Player player;

    @Before
    public void createPlayer(){
        player = PlayerFactory.getPlayer("testing");
    }

    @Test
    public void saveGameTest(){
        /*player.setName("Tester");
        player.increaseFailCount();
        player.increaseFailCount();
        player.setCalcStartDate();*/
    }

    @Test
    public void loadGameTest() throws IOException, ClassNotFoundException, URISyntaxException {
        //Player loadedPlayer = game.loadGame("test");

        //Assert.assertEquals("Tester", loadedPlayer.getName());
        //Assert.assertEquals(2, loadedPlayer.getFailCounter());
    }
}
