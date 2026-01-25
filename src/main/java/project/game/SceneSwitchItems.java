package project.game;

public class SceneSwitchItems {

    public enum Minigames{

        WHISKEY("/fxml/M_4ImageView.fxml"),
        PC("/fxml/M_PCView.fxml"),
        JUNCTIONBOX("/fxml/M_LightView.fxml"),
        BED("/fxml/M_TidyUpView.fxml"),
        DOOR("/fxml/M_DoorLockView.fxml"),
        SECURITY("/fxml/M_CodeView.fxml"),
        TABLE("/fxml/M_TableView.fxml"),
        WINDOW("/fxml/M_WindowView.fxml");

        private final String FXMLURL;

        Minigames(String fxmlUrl) {
            this.FXMLURL = fxmlUrl;
        }
        public String getFXMLURL() {
            return FXMLURL;
        }
    }

    public enum Scenes{
        COMPUTERROOM("/fxml/ComputerRoomView.fxml"),
        EMAILTAB("/fxml/EmailView.fxml"),
        LIVINGROOM("/fxml/LivingRoomView.fxml"),
        LABORATORY("/fxml/LaboratoryView.fxml"),
        LABODOORROOM("/fxml/LaboDoorView.fxml"),
        ENDING("/fxml/EndingView.fxml"),
        HIGHSCORE("/fxml/HighscoreView.fxml"),
        SETTINGS("/fxml/SettingsView.fxml"),
        MAIN("/fxml/MainView.fxml"),
        CONFIGURATIONS("/fxml/ConfigurationsView.fxml"),
        TUTORIAL("/fxml/TutorialView.fxml");

        private final String FXMLURL;

        Scenes(String fxmlUrl) {
            this.FXMLURL = fxmlUrl;
        }
        public String getFXMLURL() {
            return FXMLURL;
        }
    }
}
