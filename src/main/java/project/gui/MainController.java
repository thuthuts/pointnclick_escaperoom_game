package project.gui;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.animation.PathAnimation;
import project.audio.SoundPlayer;
import project.game.JSONTextManager;
import project.game.SceneSwitchItems;
import project.game.player.Player;

import javax.swing.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController extends SuperController implements Initializable {

    private static final Logger log = LogManager.getLogger(MainController.class);

    @FXML
    private VBox VBoxButtons;
    @FXML
    private HBox HBoxRestart;
    private Player player;
    @FXML
    private Button START, RESTART, HIGHSCORE, SETTINGS, EXIT;
    @FXML
    private ImageView arrow_start, arrow_restart, arrow_score, arrow_set, arrow_exit;
    @FXML
    private ImageView star1, star2, star3, star4;
    @FXML
    private AnchorPane container;
    private Image[] starKeyframes;
    private PathTransition currentAnimation;
    private SoundPlayer soundPlayer;
    private Image arrow;
    @FXML
    private ImageView backgroundChemicals, foregroundChemicals;
    @FXML
    private ImageView reflectionForeground, reflectionReagenglass, reflectionBackground;
    @FXML
    private ImageView reagenGlasses;
    @FXML
    private ImageView corona;
    private MediaPlayer mediaPlayer;


    public MainController(Player player, SoundPlayer soundPlayer, JSONTextManager textManager) {
        this.player = player;
        this.soundPlayer = soundPlayer;
        if (!textManager.mapExists()) textManager.parseJSON("ItemText");
    }


    /**
     * @param buttons        on start menu e.g. "start game", "highscore", "settings"
     * @param button_arrows: placeholder for hover animation
     */
    public void setHoverListener(Button[] buttons, ImageView[] button_arrows) {
        for (int i = 0; i < buttons.length; i++) {
            int finalI = i;
            buttons[i].setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    currentAnimation = PathAnimation.addPathTransition(button_arrows[finalI], -5, 0);
                    button_arrows[finalI].setImage(arrow);
                }
            });

            buttons[i].setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    currentAnimation.stop();
                    button_arrows[finalI].setImage(null);
                }
            });
        }
    }

    public void addSparkleAnimation(ImageView[] images, int delay, int period) {
        Arrays.stream(images).forEach(image -> {
            var star = new Object() {
                int count = 0;
            };

            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            e -> {
                                log.info("Image id: " + image.getId());
                                log.info("count: " + star.count);

                                image.setVisible(true);
                                if (star.count > starKeyframes.length - 1) {
                                    star.count = 0;
                                    image.setVisible(false);
                                } else {
                                    image.setImage(starKeyframes[star.count]);
                                    star.count++;
                                }
                            }
                    ),
                    new KeyFrame(Duration.millis(250))
            );
            timeline.setCycleCount(starKeyframes.length + 1);

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(timeline::play, delay, period, TimeUnit.SECONDS);
        });
    }


    /**
     * Buttons to switch to other scenes (Settings, Highscore, Exit) are set
     */
    public void addButtonClickListener() {
        SETTINGS.setOnMouseClicked(mouseEvent -> {
            loadNextSceneSettings(SceneSwitchItems.Scenes.MAIN.getFXMLURL(), container, player);
        });

        HIGHSCORE.setOnMouseClicked(mouseEvent -> {
            loadNextScene(SceneSwitchItems.Scenes.HIGHSCORE.getFXMLURL(), container, player);
        });
        EXIT.setOnMouseClicked(mouseEvent -> System.exit(0));
    }


    /**
     * sets animation images by adding click listeners on image views
     */
    public void setAnimationImages() {
        backgroundChemicals.setOnMouseClicked(handleBackgroundAnimation);
        reagenGlasses.setOnMouseClicked(handleReagenGlassesAnimation);

    }

    /**
     * @param foregroundChemicals ImageView of chemicals, which start bubbling when hovering over them
     */
    public void setForegroundHover(ImageView foregroundChemicals) {
        foregroundChemicals.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                foregroundChemicals.setImage(new Image(getClass().getResource("/img/startScreenDesign/foregroundChemicals.gif").toExternalForm()));
                //reflectionForeground.setImage(new Image(getClass().getResource("/img/startScreenDesign/ReflectionsForegroundTransition.png").toExternalForm()));
                mediaPlayer = soundPlayer.playSoundEffect("bubbling");
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
        });

        foregroundChemicals.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                foregroundChemicals.setImage(new Image(getClass().getResource("/img/startScreenDesign/foregroundChemicals.png").toExternalForm()));
                //reflectionForeground.setImage(new Image(getClass().getResource("/img/startScreenDesign/ReflectionsForeground.png").toExternalForm()));

                mediaPlayer.stop();

            }
        });

    }

    /**
     * rotation animation of corona images in the top right corner
     */
    private void setRotationAnimation(ImageView image) {
        RotateTransition rotation = new RotateTransition(Duration.seconds(3), corona);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setByAngle(360);

        image.setOnMouseEntered(e -> rotation.play());
        image.setOnMouseExited(e -> rotation.pause());
    }

    private final EventHandler<MouseEvent> handleBackgroundAnimation = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Image transition = new Image(getClass().getResource("/img/startScreenDesign/Second.gif").toExternalForm());
            Image afterTrans = new Image(getClass().getResource("/img/startScreenDesign/Last.png").toExternalForm());
            backgroundChemicals.setImage(transition);
            //reflectionBackground.setImage(new Image(getClass().getResource("/img/startScreenDesign/reflectionBgAfterTransition.png").toExternalForm()));
            soundPlayer.playSoundEffect("bubbling");

            Timer timer = new Timer(3470, event -> backgroundChemicals.setImage(afterTrans));
            timer.start();
            backgroundChemicals.setOnMouseClicked(null);
        }
    };


    private final EventHandler<MouseEvent> handleReagenGlassesAnimation = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Image blueTransition = new Image(getClass().getResource("/img/startScreenDesign/blueTransition.gif").toExternalForm());
            Image bothBlue = new Image(getClass().getResource("/img/startScreenDesign/bothBlue.png").toExternalForm());
            reagenGlasses.setImage(blueTransition);
            //reflectionReagenglass.setImage(new Image(getClass().getResource("/img/startScreenDesign/ReflectionReagenzglassAfterTrans.png").toExternalForm()));

            Timer timer = new Timer(600, event -> reagenGlasses.setImage(bothBlue));
            soundPlayer.playSoundEffect("bubblesReagenglass");
            timer.start();
            reagenGlasses.setOnMouseClicked(null);
        }
    };

    public void playSound() {
        if (soundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            soundPlayer.playBackgroundSound("Menu_sound");
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAnimationImages();
        setRotationAnimation(corona);
        setForegroundHover(foregroundChemicals);

        starKeyframes = new Image[]{new Image(getClass().getResource("/img/starFrame1.png").toExternalForm()), new Image(getClass().getResource("/img/starFrame2.png").toExternalForm()),
                new Image(getClass().getResource("/img/starFrame3.png").toExternalForm()), new Image(getClass().getResource("/img/starFrame4.png").toExternalForm())};


        setHoverListener(new Button[]{START, RESTART, HIGHSCORE, SETTINGS, EXIT}, new ImageView[]{arrow_start, arrow_restart, arrow_score, arrow_set, arrow_exit});

        /*addSparkleAnimation(new ImageView[]{star1},0,6);
        addSparkleAnimation(new ImageView[]{star2},2,6);
        addSparkleAnimation(new ImageView[]{star3},1,6);
        addSparkleAnimation(new ImageView[]{star4},2,6);*/

        arrow = new Image(getClass().getResource("/img/Menu_arrow_red.png").toExternalForm());
        // Prepare Menu, depending on if the player has played already or not.
        if (!(player.getName() == null)) {
            START.setText(resourceBundle.getString("loadGameButton"));

            START.setOnMouseClicked(mouseEvent -> {
                soundPlayer.stopBackgroundSound();
                loadNextScene(SceneSwitchItems.Scenes.LABORATORY.getFXMLURL(), container, player);
            });

            RESTART.setOnMouseClicked(mouseEvent -> {
                soundPlayer.stopBackgroundSound();
                loadPopUp("/fxml/ConfirmationView.fxml", container, player,600, 300);
            });

        } else {
            VBoxButtons.getChildren().remove(HBoxRestart);
            START.setOnMouseClicked(mouseEvent -> {
                //soundPlayer.fadeBackgroundSound("config2");
                soundPlayer.stopBackgroundSound();
                loadNextScene(SceneSwitchItems.Scenes.CONFIGURATIONS.getFXMLURL(), container, player);
            });
        }
        addButtonClickListener();

        Timer timer = new Timer(500, event -> playSound());
        timer.setRepeats(false);
        timer.start();

        Font.loadFont(getClass().getResourceAsStream("/font/Pokemon_Classic.ttf"), 20);
        //log.info(pixelFont.getName());
    }
}
