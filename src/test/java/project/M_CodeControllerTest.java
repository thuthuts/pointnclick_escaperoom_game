package project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import project.gui.minigame.M_CodeController;

public class M_CodeControllerTest {

    private static final Logger log = LogManager.getLogger(M_CodeController.class);
    private M_CodeController controller;

    @Before
    public void createBean(){
        ApplicationContext context = new ClassPathXmlApplicationContext("file:src/main/resources/spring/spring.xml");
        this.controller = (M_CodeController) context.getBean("m_code");
    }

    @Test
    public void randomizeTest(){
        for (int i = 0; i < 10; i++){
            controller.randomize();
            String code = controller.getSecretCode();
            Assert.assertTrue(hasNoRepeatableNumbers(code));
        }
    }

    @Test
    public void predefinedCodeTest(){
        Assert.assertTrue(hasNoRepeatableNumbers("1234"));
        Assert.assertTrue(hasNoRepeatableNumbers("1457"));
        Assert.assertTrue(hasNoRepeatableNumbers("6784"));
        Assert.assertTrue(hasNoRepeatableNumbers("2578"));
        Assert.assertTrue(hasNoRepeatableNumbers("9023"));
        Assert.assertTrue(hasNoRepeatableNumbers("8652"));
        Assert.assertTrue(hasNoRepeatableNumbers("1025"));

        Assert.assertFalse(hasNoRepeatableNumbers("1111"));
        Assert.assertFalse(hasNoRepeatableNumbers("6778"));
        Assert.assertFalse(hasNoRepeatableNumbers("0922"));
        Assert.assertFalse(hasNoRepeatableNumbers("8933"));
        Assert.assertFalse(hasNoRepeatableNumbers("2020"));
    }

    /**
     * This method checks if a number in the 4-digit code repeats itself in the secretCode.
     * For this test to be successful(true), the numbers in the code should never be repeated.
     * They are occuring only once.
     * @param code of the door security
     * @return {boolean} true - numbers don't repeat.
     */
    public boolean hasNoRepeatableNumbers(String code){
        log.debug(code);
        for (int i = 0; i < 3; i++){
            for (int a = i+1; a <= 3; a++){
                log.debug("Check charAt: " + i + "(" + code.charAt(i) + ")" + ", with charAt: " + a + "(" + code.charAt(a) + ")");
                if (code.charAt(i) == code.charAt(a)) return false;
            }
        }
        return true;
    }
}
