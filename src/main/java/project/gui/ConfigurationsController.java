package project.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.audio.SoundPlayer;
import project.game.player.Player;
import project.game.SceneSwitchItems;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ConfigurationsController extends SuperController implements Initializable {

    private static final Logger log = LogManager.getLogger(ConfigurationsController.class);

    private final SoundPlayer soundPlayer;
    private Player player;
    @FXML private TextField name;
    @FXML private ImageView accessory, hair, skin, eyes, shirt;
    @FXML private ToggleButton BOY, GIRL;
    private ToggleGroup gender;
    @FXML private Button rAccessoryButton, lAccessoryButton, rHairButton, lHairButton;
    @FXML private GridPane skinColors, accessoryColors, hairColors, eyeColors, shirtColors;
    @FXML private Label accessoryLabel, hairLabel, nameError;
    @FXML private AnchorPane container;
    //counter to know which accessory image needs to be changed into an awt Image to draw the new character.png
    private int accessoryCounter = 0;
    private int hairCounter = 0;
    //array of the counters, so they can be passed individually into setClickListeners(), and there doesn't
    //has to be a method for each style of the character
    private int[] imageSliderPosition = {hairCounter, accessoryCounter};
    //Arrays with the urls of the images, so the chosen Image can be created as awt Image
    private final String[] urlsEyes = {"/img/character/eye_girl.png", "/img/character/eye_boy.png"};
    private final String[] urlsAccessories = {"/img/character/none.png", "/img/character/blush.png", "/img/character/glasses.png", "/img/character/poop.png", "/img/character/gloriole.png"};
    private final String[] urlsHairs = {"/img/character/none.png", "/img/character/smoothHair.png", "/img/character/shortHair.png", "/img/character/bunHair.png", "/img/character/liaHair.png", "/img/character/straight.png", "/img/character/wave.png", "/img/character/manHair.png", "/img/character/shortWavyHair.png", "/img/character/sidecutHair.png", "/img/character/punk.png",};
    private String[] categoryTitle;

    private HashMap<String, Color> colors;


    public ConfigurationsController(Player player, SoundPlayer soundPlayer){
        this.player = player;
        this.soundPlayer = soundPlayer;
    }

    public void fillColorHashMap(){
        // (colorName, Color Object)
        colors = new HashMap<>();
        colors.put("LIGHTSKIN", new Color(199, 120, 96));
        colors.put("RED", new Color(164,13,13));
        colors.put("GREEN", new Color(54, 106, 54));
        colors.put("BLUE", new Color(47, 47, 146));
        colors.put("PINK", new Color(199, 100, 132));
        colors.put("BROWNSKIN", new Color(97, 43, 10));
        colors.put("BROWN", new Color(101, 39, 36));
        colors.put("BLOND", new Color(218,165,32));
        colors.put("ORANGE", new Color(209,134, 0));
        colors.put("VIOLET", new Color(238,130,238));
        colors.put("BLACK", new Color(0, 0, 0));
    }

    //this method will draw the new character.png and set the name of the player as well as the finalStartDate
    public void submitConfigurations() {
        if (hasAllowedName()) {
            player.setName(name.getText());
            player.setStartDate();
            drawCharacter();
            soundPlayer.stopBackgroundSound();
            loadNextScene(SceneSwitchItems.Scenes.COMPUTERROOM.getFXMLURL(), container, player);
            log.debug("Name of the player is: " + player.getName());
            log.debug("FinalStartDate set to: " + player.getStartDate());
        } else {
            nameError.setText("Name must include\nbetween 1-15 letters");
        }
    }

    public void randomize(){
        changeRandomizedImage(skin, null, "/img/character/skin.png", skinColors, null, -1, false);
        changeRandomizedImage(shirt, null, "/img/character/shirt.png", shirtColors, null, -1, false);
        changeRandomizedImage(eyes, urlsEyes, null, eyeColors, null, -1, true);
        changeRandomizedImage(hair, urlsHairs, null, hairColors, hairLabel, 0, false);
        changeRandomizedImage(accessory, urlsAccessories, null, accessoryColors, accessoryLabel, 1, false);
    }

    /**
     *
     * @param image
     * @param urlImages
     * @param baseImgURL
     * @param colorGrid
     * @param category
     * @param categoryIndex
     * @param isGender
     */
    private void changeRandomizedImage(ImageView image, String[] urlImages, String baseImgURL, GridPane colorGrid, Label category, int categoryIndex, boolean isGender){
        try {
            int colorIndex = (int) (Math.random() * colorGrid.getChildren().size());

            if (urlImages != null){
                int styleIndex = (int) (Math.random() * urlImages.length);
                image.setImage(filterFxImage(urlImages[styleIndex], colors.get(colorGrid.getChildren().get(colorIndex).getId()), 200, 250));
                if (category != null) category.setText(categoryTitle[categoryIndex].substring(0,categoryTitle[categoryIndex].length()-1) + (styleIndex+1));
                if (isGender) gender.getToggles().get(styleIndex).setSelected(true);

            } else {
                image.setImage(filterFxImage(baseImgURL, colors.get(colorGrid.getChildren().get(colorIndex).getId()), 200, 250));
            }
            ((ToggleButton)colorGrid.getChildren().get(colorIndex)).setSelected(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void backToMenu(){
        soundPlayer.stopBackgroundSound();
        loadNextScene(SceneSwitchItems.Scenes.MAIN.getFXMLURL(), container, player);
    }

    public boolean hasAllowedName(){
        return !name.getText().equals("") && name.getText().length() <= 15;
    }

    /**
     * This method will take current red, green and blue values of a java.awt.Image and
     * adds the r g b shift values from the parameters as a filter on top of it.
     * @param image that gets filtered.
     * @param redIncrement red shift.
     * @param greenIncrement green shift.
     * @param blueIncrement blue shift.
     * @return image that has been filtered.
     */
    public java.awt.Image getFilteredImage(java.awt.Image image, int redIncrement, int greenIncrement, int blueIncrement) {
        ImageFilter filter = new RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int alpha = (rgb & 0xff000000);
                int red = (rgb & 0xff0000) >> 16;
                int green = (rgb & 0x00ff00) >> 8;
                int blue = (rgb & 0x0000ff);

                // Avoids maximum of 255 (RGB Color values can never go over 255)
                // 0xff is 255 in Hex format, minimum is 0 (Range 0-255)
                // It will never filter black! (0,0,0)
                if (red != 0 && green != 0 && blue != 0){
                    red = Math.max(0, Math.min(0xff, red + redIncrement));
                    green = Math.max(0, Math.min(0xff, green + greenIncrement));
                    blue = Math.max(0, Math.min(0xff, blue + blueIncrement));
                }

                return alpha | (red << 16) | (green << 8) | blue;
            }
        };
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
    }


    /**
     * This method can save a Java.awt.Image into a file.
     * @param image that is being turned into a file.
     * @param type format of the file.
     * @param dst destination of the file.
     */
    public void saveImageToFile(java.awt.Image image, String type, String dst){
        BufferedImage bi = new BufferedImage(200, 250, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        try {
            g.drawImage(image, 0, 0, null);
            ImageIO.write(bi, type, new File(dst));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will convert a given java.awt.Image to javafx.scene.Image.
     * @param imgAWT that is being converted.
     * @return javafx Image.
     */
    private Image convertAWTToFxImage(java.awt.Image imgAWT) {
        return SwingFXUtils.toFXImage((BufferedImage) imgAWT, null);
    }

    /**
     * This method will convert a given javafx.scene.Image to java.awt.Image.
     * @param imgFX that is being converted.
     * @return javafx Image.
     */
    private java.awt.Image convertFXToAWTImage(Image imgFX) {
        return SwingFXUtils.fromFXImage(imgFX, null);
    }

    //create awt Images of the chosen styles
    public java.awt.Image getChosenImageAsAwtImage(String baseImage) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(baseImage));
    }

    /**
     * This method will save the current styled character to a file.
     */
    public void drawCharacter() {
        //BufferedImage to draw the new character.png
        BufferedImage newImg = new BufferedImage(200, 250, BufferedImage.TYPE_INT_ARGB);
        //create graphics of the image so different graphics can be drawn onto it
        Graphics2D g2d = newImg.createGraphics();
        //Draw the images

        g2d.drawImage(convertFXToAWTImage(skin.getImage()), 0, 0,null);
        g2d.drawImage(convertFXToAWTImage(shirt.getImage()), 0, 0,null);
        g2d.drawImage(convertFXToAWTImage(hair.getImage()), 0, 0, null);
        g2d.drawImage(convertFXToAWTImage(eyes.getImage()), 0, 0,null);
        g2d.drawImage(convertFXToAWTImage(accessory.getImage()), 0, 0, null);
        g2d.dispose();

        //create the new character.png
        saveImageToFile(newImg, "png", "src/main/resources/character.png");
        saveImageToFile(newImg, "png", "target/classes/character.png");
        log.debug("New character image has been created.");
    }

    /**
     * This method will filter a given JavaFX Image with the given color values.
     * @param baseImageURL of resource that is being filtered.
     * @param color of the rgb values.
     * @return JavaFX Image.
     * @throws IOException if File doesn't exists
     */
    public Image filterFxImage(String baseImageURL, Color color, int width, int height) throws IOException {
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImg.createGraphics();

        g2d.drawImage(getFilteredImage(getChosenImageAsAwtImage(baseImageURL), color.getRed(), color.getGreen(), color.getBlue()), 0, 0,null);
        g2d.dispose();

        return convertAWTToFxImage(newImg);
    }

    /**
     * This method will add clickListeners to the buttons > and < beside the style Images, so they can be clicked through.
     * @param index of sliderPosition from a certain category.
     * @param rButton sliding through a category style to the right direction.
     * @param lButton sliding through a category style to the left direction.
     * @param slidingImages styles of a certain category (hair 1, hair 2, ...)
     * @param choosingImageView base ImageView of a categoryLayer that is being manipulated.
     * @param categoryLabel Label that represents number of a style (hair 1, hair 2, hair 3...) for the user.
     */
    public void addSlideShowClickListeners(int index, Button rButton, Button lButton, String[] slidingImages, ImageView choosingImageView, GridPane colorGrid ,Label categoryLabel){

    rButton.setOnAction(e -> {
        imageSliderPosition[index]++;
        if (imageSliderPosition[index] == slidingImages.length) {
            imageSliderPosition[index] = 0;
        }

        try {
            ToggleGroup colorGroup = ((ToggleButton)colorGrid.getChildren().get(0)).getToggleGroup();
            choosingImageView.setImage(filterFxImage(slidingImages[imageSliderPosition[index]], colors.get(((Node)colorGroup.getSelectedToggle()).getId()), 200, 250));
            categoryLabel.setText(categoryTitle[index].substring(0,categoryTitle[index].length()-1) + (imageSliderPosition[index]+1));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    });

    lButton.setOnAction(e -> {
        imageSliderPosition[index]--;
        if (imageSliderPosition[index] < 0) {
            imageSliderPosition[index] = slidingImages.length - 1;
        }

        try {
            ToggleGroup colorGroup = ((ToggleButton)colorGrid.getChildren().get(0)).getToggleGroup();
            choosingImageView.setImage(filterFxImage(slidingImages[imageSliderPosition[index]], colors.get(((Node)colorGroup.getSelectedToggle()).getId()), 200, 250));
            categoryLabel.setText(categoryTitle[index].substring(0,categoryTitle[index].length()-1) + (imageSliderPosition[index]+1));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    });

    addColorListeners(colorGrid, choosingImageView, slidingImages, categoryLabel);
    }

    /**
     * This method will add clickListeners to the colors of each category and manipulates that category layer with
     * an RGBFilter.
     * @param colorGrid colors of the category.
     * @param choosingImageView ImageView that is being changed once the color changes.
     * @param slidingImages All possible Images of a category (short hair, long hair...).
     * @param categoryLabel Label that represents number of a style (hair 1, hair 2, hair 3...) for the user.
     */
    private void addColorListeners(GridPane colorGrid, ImageView choosingImageView, String[] slidingImages, Label categoryLabel){
        ToggleGroup colorGroup = new ToggleGroup();
        colorGrid.getChildren().forEach(colorBtn -> {
            if (colorBtn instanceof ToggleButton){
                colorBtn.setOnMouseClicked(mouseEvent -> {
                    String colorId = ((Node)mouseEvent.getSource()).getId();
                    Color rgb = colors.get(colorId);

                    // Get current style Index of a category via it's label integer
                    int length = categoryLabel.getText().length();
                    int spaceIndex = categoryLabel.getText().indexOf(" ");
                    int slidingIndex = (Integer.parseInt(categoryLabel.getText().substring(spaceIndex+1, length)))-1;

                    try {
                        choosingImageView.setImage(filterFxImage(slidingImages[slidingIndex] , rgb, 200, 250));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                colorGroup.getToggles().add((ToggleButton)colorBtn);
            }
        });
        colorGroup.getToggles().get(0).setSelected(true);
        setGridpaneColors(colorGrid);
    }

    /**
     * This method will add clickListeners to the colors of each category and manipulates that category layer with
     * an RGBFilter.
     * @param colorGrid colors of the category.
     * @param choosingImageView ImageView that is being changed once the color changes.
     */
    private void addColorListeners(GridPane colorGrid, String baseImgURL, ImageView choosingImageView){
        ToggleGroup colorGroup = new ToggleGroup();
        colorGrid.getChildren().forEach(colorBtn -> {
            if (colorBtn instanceof ToggleButton){
                colorBtn.setOnMouseClicked(mouseEvent -> {
                    String colorId = ((Node)mouseEvent.getSource()).getId();
                    Color rgb = colors.get(colorId);

                    try {
                        choosingImageView.setImage(filterFxImage(baseImgURL, rgb, 200, 250));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                colorGroup.getToggles().add((ToggleButton)colorBtn);
            }
        });
        colorGroup.getToggles().get(0).setSelected(true);
        setGridpaneColors(colorGrid);
    }

    public void addEyeColorListener(){
        ToggleGroup colorGroup = new ToggleGroup();
        eyeColors.getChildren().forEach(colorBtn -> {
            if (colorBtn instanceof ToggleButton){
                colorBtn.setOnMouseClicked(mouseEvent -> {
                    String colorId = ((Node)mouseEvent.getSource()).getId();
                    Color rgb = colors.get(colorId);

                    try {
                        eyes.setImage(filterFxImage(getEyeBaseImgURL(), rgb, 200, 250));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                colorGroup.getToggles().add((ToggleButton)colorBtn);
            }
        });
        colorGroup.getToggles().get(0).setSelected(true);
        setGridpaneColors(eyeColors);
    }

    public void prepareInitialColors(){
        try {
            skin.setImage(filterFxImage("/img/character/skin.png", colors.get("LIGHTSKIN"), 200, 250));
            eyes.setImage(filterFxImage(urlsEyes[0], colors.get("RED"), 200, 250));
            hair.setImage(filterFxImage(urlsHairs[0], colors.get("RED"), 200, 250));
            accessory.setImage(filterFxImage(urlsAccessories[0], colors.get("RED"), 200, 250));
            shirt.setImage(filterFxImage("/img/character/shirt.png", colors.get("RED"), 200, 250));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setGridpaneColors(GridPane colorGrid){
        colorGrid.getChildren().forEach(node -> {
            if (node instanceof ToggleButton){
                try {
                    Color color = colors.get(node.getId());
                    ((ToggleButton)node).setGraphic(new ImageView(filterFxImage("/img/colorPalette.png", color, 25, 25)));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public String getEyeBaseImgURL(){
        if (((Node)gender.getSelectedToggle()).getId().equals("GIRL")) return urlsEyes[0];
        return urlsEyes[1];
    }

    public void setResourceTitles(ResourceBundle resourceBundle){
        categoryTitle = new String[]{resourceBundle.getString("category4"), resourceBundle.getString("category5")};
    }

    public void playSound(){
        if (soundPlayer.getStatus() != MediaPlayer.Status.PLAYING){
            soundPlayer.playBackgroundSound("config2");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setResourceTitles(resourceBundle);
        fillColorHashMap();
        prepareInitialColors();

        gender = new ToggleGroup();
        gender.getToggles().addAll(GIRL,BOY);
        GIRL.setSelected(true);

        GIRL.setOnMouseClicked(mouseEvent -> {
            try {
                eyes.setImage(filterFxImage(urlsEyes[0], colors.get(((Node)((ToggleButton)eyeColors.getChildren().get(0)).getToggleGroup().getSelectedToggle()).getId()), 200, 250));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        BOY.setOnMouseClicked(mouseEvent -> {
            try {
                eyes.setImage(filterFxImage(urlsEyes[1], colors.get(((Node)((ToggleButton)eyeColors.getChildren().get(0)).getToggleGroup().getSelectedToggle()).getId()), 200, 250));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addColorListeners(skinColors, "/img/character/skin.png", skin);
        addColorListeners(shirtColors, "/img/character/shirt.png", shirt);
        addEyeColorListener();

        addSlideShowClickListeners(0, rHairButton, lHairButton, urlsHairs, hair, hairColors, hairLabel);
        addSlideShowClickListeners(1, rAccessoryButton, lAccessoryButton,  urlsAccessories, accessory, accessoryColors, accessoryLabel);

        Timer timer = new Timer(500, event -> playSound());
        timer.setRepeats(false);
        timer.start();
    }
}
