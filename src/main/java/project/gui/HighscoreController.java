package project.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.game.player.Player;
import project.game.SceneSwitchItems;
import project.thread.HighscoreThread;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

public class HighscoreController extends SuperController implements Initializable {

    private static final Logger log = LogManager.getLogger(HighscoreController.class);

    @FXML private TableView<Player> tableViewHighscore;
    @FXML private TableColumn<Player, String> columnTime;
    @FXML private TableColumn<Player, Integer> columnFailCount;
    @FXML private TableColumn<Player, String> columnName;
    @FXML private TableColumn <Player, Player> rank;
    @FXML private AnchorPane container;
    @FXML private Button backBtn;
    @FXML private Button refreshButton;
    private Player player;
    private int playerRank;
    private ObservableList<Player> data = FXCollections.observableArrayList();

    public HighscoreController(Player player){
        this.player = player;
    }


    /**
     * get data of API call which was initiated by HighscoreThread
     */
    private void fillDataList(HighscoreThread thread){
        data = thread.getData();

        //todo highlight player
        playerRank = 1;
        for(Player player : data){
            if(this.player.equals(player)){
                log.debug("\nPlayer has been found");
                break;
            }
            playerRank++;
        }
    }

    private void setRank(){
        rank.setCellValueFactory(p -> new ReadOnlyObjectWrapper(p.getValue()));
        rank.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Player, Player> call(TableColumn<Player, Player> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Player playerEntry, boolean empty) {
                        super.updateItem(playerEntry, empty);
                        if (this.getTableRow() != null && playerEntry != null) {
                            setText(this.getTableRow().getIndex() + 1 + "");

                            if (this.getTableRow().getIndex() + 1 == playerRank){
                                setStyle("-fx-text-fill: #b16200");
                            }

                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        rank.setSortable(false);
    }

    private void initializeTableColumns() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("sumPlayTime"));
        columnFailCount.setCellValueFactory(new PropertyValueFactory<>("failCounter"));
        setRank();
    }

    private void setTimeColumnDataFormatted() {
        columnTime.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Player, String> call(TableColumn<Player, String> playerStringTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String playerEntry, boolean empty) {
                        super.updateItem(playerEntry, empty);
                        if (this.getTableRow() != null && playerEntry != null) {
                            setText(formatDuration(Duration.parse(this.getItem())));

                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
    }

    private String formatDuration(Duration duration) {
        long s = duration.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

    private final EventHandler<MouseEvent> refresh = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            HighscoreThread thread = new HighscoreThread();
            thread.run();
            fillDataList(thread);
            tableViewHighscore.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            initializeTableColumns();
            tableViewHighscore.getItems().setAll(data);

        }
        };


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        backBtn.setOnMouseClicked(mouseEvent -> loadNextScene(SceneSwitchItems.Scenes.MAIN.getFXMLURL(), container, player));
        fillDataList(Main.getHighscoreThread());
        tableViewHighscore.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initializeTableColumns();
        tableViewHighscore.getItems().setAll(data);
        setTimeColumnDataFormatted();
        refreshButton.setOnMouseClicked(refresh);
    }
}
