package project;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import project.gui.minigame.M_TidyUpController;
import project.game.InventoryBuilder;
import project.game.player.Player;
import project.game.PlayerFactory;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class BeanTest {
   private Player player;
   private M_TidyUpController m_tidyUpController;
   private InventoryBuilder inventoryBuilder;

   private ApplicationContext context = new ClassPathXmlApplicationContext("file:src/main/resources/spring/spring.xml");


@Before


    public void createBeans() {

        player = PlayerFactory.getPlayer("testing");
        m_tidyUpController = (M_TidyUpController) context.getBean("m_tidyup");
        inventoryBuilder = (InventoryBuilder) context.getBean("inventoryBuilder");

    }

    @Test
    public void givenBean__instanceOfExpectedClass__thenCorrect() {
        Assert.assertThat(player, isA(Player.class));
        Assert.assertThat(inventoryBuilder,isA(InventoryBuilder.class));
        Assert.assertThat(m_tidyUpController, isA(M_TidyUpController.class));

   }

}





