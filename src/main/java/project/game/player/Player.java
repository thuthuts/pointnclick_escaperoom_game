package project.game.player;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.IntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * This class takes care of the Players Data.
 * It will save the name, failCount, startDate, endDate ...
 * The Player will be Serialized to convert the Java Object into a .ser file (persistent data).
 */
public class Player implements Serializable {

    private static final Logger log = LogManager.getLogger(Player.class);

    @JsonProperty
    private String sumPlayTime;
    @JsonIgnore
    private FailCountProperty property;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd, HH:mm:ss")
    private LocalDateTime  endDate, startDate;

    private LocalDateTime calcStartDate;
    private String name;
    private int failCounter;
    private SettingsManager playerSettings;
    private PlayerProgressManager playerInventory;


    public Player(){
    }

    public Player(SettingsManager playerSettings, PlayerProgressManager playerInventory, FailCountProperty property) {
        this.failCounter = 0;
        this.playerSettings = playerSettings;
        this.playerInventory = playerInventory;
        this.property = property;
        log.info("A new Player has been created..." + this);
    }

    /**
     *
     * Overrided equals method for HighscoreController to check if current player already has
     * an entry in highscore list
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Player){
            Player player = (Player) obj;
            return player.name.equals(this.name) && player.startDate.toString().equals(this.startDate.toString().substring(0, 19)) &&
                    player.endDate.toString().equals(this.endDate.toString().substring(0, 19)) && player.failCounter == this.failCounter &&
                    player.sumPlayTime.equals(this.sumPlayTime);
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *  This method will set calcStartDate to the current date
     *  calcStartDate is the time when the player starts to play the game at each save game
     */
    public void setCalcStartDate() {
        this.calcStartDate = LocalDateTime.now();
    }

    public void setEndDate() {
        this.endDate = LocalDateTime.now();
        log.info("set the EndDate of player " + name);
    }

    public String getName() {
        return name;
    }

    public void setHasInventoryItem(int itemIndex, boolean hasItem) {
        playerInventory.setHasItem(itemIndex, hasItem);
    }

    public void increaseItemInventoryNumber(int value) {
        playerInventory.increaseItemNumber(value);
    }

    public int getInventoryItemCount() {
        return playerInventory.getItemNumber();
    }

    public boolean[][] getHasInventoryItemBooleans() {
        return playerInventory.getHasItem();
    }

    public void setHasUsedInventoryItem(int index, boolean hasItem) {
        playerInventory.setHasUsedItem(index, hasItem);
    }


    public boolean hasEndDateNull(){
        return endDate == null;
    }

    public int getFailCounter() {
        return failCounter;
    }

    public IntegerProperty getFailCounterProperty(){
        return property.getFailCountProperty();
    }

    public void setStartDate(){
        this.startDate = LocalDateTime.now();
    }

    public String getStartDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
        return startDate.format(dtf);
    }

    public String getEndDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
        log.debug("Here" + endDate.format(dtf));
        return endDate.format(dtf);
    }

    public String getPlayerSettingsThemeUrl() {
        return playerSettings.getThemeURL();
    }

    public ResourceBundle getPlayerSettingLangBundle() {
        return playerSettings.getLangBundle();
    }

    public Settings.language getLangSettings(){
        return playerSettings.getLanguage();
    }

    public Settings.theme getThemeSettings(){
        return playerSettings.getTheme();
    }

    public void setLangSettings(Settings.language lang){
        playerSettings.setLanguage(lang);
    }

    public void setThemeSettings(Settings.theme theme){
        playerSettings.setTheme(theme);
    }

    public void setBGSoundVolume(double newVolume){
        playerSettings.setBgSoundVolume(newVolume);
    }

    public void setEffectSoundVolume(double newVolume){
        playerSettings.setEffectSoundVolume(newVolume);
    }

    public double getBGSoundVolume(){
        return playerSettings.getBgSoundVolume();
    }

    public double getEffectSoundVolume(){
        return playerSettings.getEffectSoundVolume();
    }

    public void setMinigameSolved(int minigameIndex, boolean solved) {
        this.playerInventory.setMinigameSolved(minigameIndex, solved);
    }

    public boolean[] getMinigameSolved() {
        return playerInventory.getSolvedMinigame();
    }


    public LocalDateTime getCalcStartDate() {
        return calcStartDate;
    }

    public String getSumPlayTime() {
        return sumPlayTime;
    }

    public String getSumPlayTimeAsTimeString(){
        return sumPlayTime.substring(2,sumPlayTime.length()-9).concat("s").replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }

    public void increaseFailCount() {
        this.failCounter++;
        this.property.increaseFailCountProperty();
    }

    /*-----set time values and calculate play time-----*/

    /**
     * sumPlayTime represents the duration of the play time
     * This method will calculate the duration of the play time.
     * Therefore the duration is build between calcStartDate and the current time.
     * If the player has already saved the game, the new session duration is being added on top of the current total.
     */
    public void setSumPlayTime() {
        Duration sumPlayTimeDur;
            try {
                if (sumPlayTime == null) {
                    sumPlayTimeDur = Duration.between(calcStartDate, LocalDateTime.now()).abs();
                } else {
                    sumPlayTimeDur = Duration.parse(sumPlayTime);
                    sumPlayTimeDur = sumPlayTimeDur.plus(Duration.between(calcStartDate, LocalDateTime.now()).abs());
                }
                sumPlayTime = sumPlayTimeDur.toString();
            } catch (Exception e) {
                log.fatal("Unable to calculate playtime.");
                e.printStackTrace();
            }
        }

    @Override
    public String toString() {
        return "\nPlayer name: " + name + "\nDate Start: " + startDate + "\nDate End: " + endDate + "\nFail Counter: " + failCounter + "\nSumPlayTime: " + sumPlayTime;
    }
}




