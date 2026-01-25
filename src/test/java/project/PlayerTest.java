package project;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import project.game.player.Player;
import project.game.PlayerFactory;

public class PlayerTest {

    private Player player;

    @Before
    public void createPlayer(){
        player = PlayerFactory.getPlayer("testing");
    }

    @Test
    public void getNameTest() {
        player.setName("player1");
        Assert.assertEquals("player1", player.getName());
        player.setName("Hello world");
        Assert.assertEquals("Hello world", player.getName());
    }

    @Test
    public void getFailCounterTest() {
        Assert.assertEquals(0, player.getFailCounter());
        player.increaseFailCount();
        Assert.assertEquals(1, player.getFailCounter());
        for(int i = 0; i <= 10; i++) {
            player.increaseFailCount();
        }
        Assert.assertEquals(12, player.getFailCounter());
    }
}
