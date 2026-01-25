package project.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import project.game.player.Player;
import project.game.SceneSwitchItems;
import project.thread.HighscoreThread;
import project.utils.STATICS;

/**
 * This class will start the application with JavaFX.
 */
public class Main extends Application {

    private static final Logger log = LogManager.getLogger(Main.class);
    final int WIDTH = 1280;
    final int HEIGHT = 720;
    private Player player;
    private static ApplicationContext context;
    private static HighscoreThread highscoreThread;

    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // start HighscoreThread to get data from server and avoid long waiting time
        highscoreThread = new HighscoreThread();
        Thread thread = new Thread(highscoreThread);
        thread.start();

        try {
            initPlayer();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneSwitchItems.Scenes.MAIN.getFXMLURL()) , player.getPlayerSettingLangBundle());
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            stage.setTitle(STATICS.appName);
            stage.setResizable(false);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            scene.getStylesheets().add(getClass().getResource(player.getPlayerSettingsThemeUrl()).toExternalForm());
            stage.setScene(scene);

            scene.setCursor(new ImageCursor(new Image(getClass().getResource("/img/cursor.png").toExternalForm())));
            stage.show();
            log.info("The application is running...");

        } catch (Exception e) {
            log.fatal("The application is unable to start: \n" + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void initPlayer(){
        context = new ClassPathXmlApplicationContext("file:src/main/resources/spring/spring.xml");
        player = (Player) context.getBean("player");
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        Main.context = context;
    }

    public static HighscoreThread getHighscoreThread(){
        return highscoreThread;
    }
}