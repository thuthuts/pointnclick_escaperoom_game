package project.gui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.game.SceneSwitchItems;
import project.utils.STATICS;

public abstract class SuperController {

    private static final Logger log = LogManager.getLogger(SuperController.class);
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Method is being called if the next Scene is going to be 1280x720px big.
     * can be called for simply changing a scene.
     * @param fxmlUrl FXML file that is being loaded.
     * @param container is the main gui container element of the scene calling this method.
     * @param player is the player that is currently playing.
     */
    public void loadNextScene(String fxmlUrl, AnchorPane container, Player player){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlUrl), player.getPlayerSettingLangBundle());
            loader.setControllerFactory(Main.getContext()::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 720);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(player.getPlayerSettingsThemeUrl()).toExternalForm());

            // Gets stage information
            Stage window = (Stage) container.getScene().getWindow();
            window.setScene(scene);
            scene.setCursor(new ImageCursor(new Image(getClass().getResource("/img/cursor.png").toExternalForm())));

        } catch (Exception e){ log.fatal("Loading next Scene failed!\n" + e.getMessage() + "\n");e.printStackTrace(); }
    }

    public void loadNextSceneSettings(String lastFXML, AnchorPane container, Player player){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneSwitchItems.Scenes.SETTINGS.getFXMLURL()), player.getPlayerSettingLangBundle());
            loader.setControllerFactory(Main.getContext()::getBean);
            Parent root = loader.load();
            SettingsController controller = loader.getController();

            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(player.getPlayerSettingsThemeUrl()).toExternalForm());
            // Gets stage information
            Stage window = (Stage) container.getScene().getWindow();
            window.setScene(scene);
            controller.addBackButtonListener(lastFXML);
            scene.setCursor(new ImageCursor(new Image(getClass().getResource("/img/cursor.png").toExternalForm())));

        } catch (Exception e){ log.fatal("Loading next Scene failed!\n" + e.getMessage() + "\n");e.printStackTrace(); }
    }

    /**
     * This method loads a Pop-Up window in the current window.
     * @param fxmlUrl FXML file that is being loaded.
     * @param width of the pop-up.
     * @param height of the pop-up.
     */
    public void loadPopUp(String fxmlUrl, AnchorPane container, Player player, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlUrl), player.getPlayerSettingLangBundle());
            loader.setControllerFactory(Main.getContext()::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root, width, height);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            scene.setCursor(new ImageCursor(new Image(getClass().getResource("/img/cursor.png").toExternalForm())));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(container.getScene().getWindow());
            //blurBG(container);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(player.getPlayerSettingsThemeUrl()).toExternalForm());

            // This will make the window movable by dragging it elsewhere.
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
            // The Owner Window is being paused until the PopUp has been closed
            stage.showAndWait();
        } catch (Exception e){ log.fatal("Loading Popup failed!"); e.printStackTrace(); }
    }

    /**
     * This method loads a Pop-Up window which prints out the description of a describable item.
     * @param itemText Description of describable item, which can be found in Item.json
     */
    public void loadTextPopUp(String itemText, AnchorPane container, Player player){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemDescPopup.fxml"), player.getPlayerSettingLangBundle());
            loader.setControllerFactory(Main.getContext()::getBean);
            Parent root = loader.load();
            ItemDescPopupController controller = loader.getController();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(container.getScene().getWindow());

            Scene scene = new Scene(root, 1280, 720);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource(player.getPlayerSettingsThemeUrl()).toExternalForm());

            stage.setScene(scene);
            scene.setCursor(new ImageCursor(new Image(getClass().getResource("/img/cursor.png").toExternalForm())));
            controller.setText(itemText);
            stage.show();

            stage.setX(container.getScene().getWindow().getX()+37);
            stage.setY(container.getScene().getWindow().getY()+540);

        } catch(Exception e){
            log.fatal("Loading Popup failed!"); e.printStackTrace();
        }
    }

    /**
     * Blur the owner window when the PopUp has been opened.
     * @param pane of the owner.
     */
    public void blurBG(AnchorPane pane){
        BoxBlur blur = new BoxBlur(3,3,3);
        pane.setEffect(blur);
    }

    /**
     * Unblur the owner window when the PopUp has been closed.
     * @param pane of the owner.
     */
    public void unblurBG(AnchorPane pane){
        pane.setEffect(null);
    }

    public void closePopUp(AnchorPane pane) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    /**
     * Method JavaFX Animation for Scene Transition (FadeOUT).
     * @param node element that is being faded out
     */
    public void makeFadeOut(Node node, AnchorPane container) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(2000));
        transition.setNode(node);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(m -> container.getChildren().remove(node));
        transition.play();
    }
}

