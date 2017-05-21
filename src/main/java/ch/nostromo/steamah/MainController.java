package ch.nostromo.steamah;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import ch.nostromo.steamah.data.Game;
import ch.nostromo.steamah.steam.AchievementsLoader;
import ch.nostromo.steamah.steam.GameLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * JavaFX Controller for the main window
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class MainController implements Initializable {


    public static final String APP_PREFS = "SteamAchievementHunter";
    public static final String APP_PREFS_KEY = "Key";
    public static final String APP_PREFS_STEAM_ID = "SteamId";
    public static final String APP_PREFS_LANGUAGE = "Language";

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField txtLanguage;

    @FXML
    private TextField txtKey;

    @FXML
    private TextField txtSteamId;

    @FXML
    private TextField txtFilter;

    @FXML
    private CheckBox cbFilterNonAchievement;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblInfo;

    @FXML
    private TableColumn<Game, String> colName;

    @FXML
    private TableColumn<Game, Number> colAchievementsTotal;

    @FXML
    private TableColumn<Game, Number> colAchievementsDone;

    @FXML
    private TableColumn<Game, Number> colAchievementsPercentage;

    @FXML
    private TableView<Game> tableView;

    private ObservableList<Game> tableViewList = FXCollections.observableArrayList();

    /**
     * Preferences
     */
    private Preferences preferences;

    /**
     * Load preferences, create cell factories and attach listeners
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Load preferences
        preferences = Preferences.userRoot().node(APP_PREFS);
        txtKey.setText(preferences.get(APP_PREFS_KEY, ""));
        txtSteamId.setText(preferences.get(APP_PREFS_STEAM_ID, ""));
        txtLanguage.setText(preferences.get(APP_PREFS_LANGUAGE, "english"));

        // Build tableview
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colAchievementsTotal.setCellValueFactory(cellData -> cellData.getValue().achievementsTotalProperty());
        colAchievementsDone.setCellValueFactory(cellData -> cellData.getValue().achievementsDoneProperty());
        colAchievementsPercentage.setCellValueFactory(cellData -> cellData.getValue().achievementsPercentageProperty());

        // Add table view mouse listener
        tableView.setRowFactory(tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showAchievementsForm(row.getItem());
                }
            });
            return row;
        });

        lblInfo.setText("No games loaded ...");
    }

    /**
     * Show the achievement form, if the game is not yet populated it will be fetched.
     * 
     * @param game
     */
    private void showAchievementsForm(Game game) {
        try {

            if (!game.isPopulated()) {
                List<Game> helperList = new ArrayList<>();
                helperList.add(game);
                AchievementsLoader task = new AchievementsLoader(txtKey.getText(), txtSteamId.getText(), txtLanguage.getText(), helperList);
                task.run();
            }

            if (game.getHasStats()) {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/achievements.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                scene.getStylesheets().add("dark.css");

                AchievementsController achievementsController = fxmlLoader.<AchievementsController>getController();

                achievementsController.setAchievements(game);
                Stage stage = new Stage();
                stage.setTitle(game.getName());
                stage.setScene(scene);
                stage.show();
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(SteamahApp.APP_TITLE);
                alert.setContentText("This game does not support achievements!");
                alert.showAndWait();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Loads the list of gmaes
     * 
     * @param event
     */
    @FXML
    public void actionLoadAllGames(ActionEvent event) {
        preferences.put(APP_PREFS_KEY, txtKey.getText());
        preferences.put(APP_PREFS_STEAM_ID, txtSteamId.getText());
        preferences.put(APP_PREFS_LANGUAGE, txtLanguage.getText());

        GameLoader task = new GameLoader(txtKey.getText(), txtSteamId.getText(), txtLanguage.getText());

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                updateTableView(task.getValue());
            }
        });

        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                updateTableView(new ArrayList<>());
                throw new RuntimeException(task.getException());
            }
        });

        Service<List<Game>> service = new Service<List<Game>>() {
            @Override
            protected Task<List<Game>> createTask() {
                return task;
            }
        };

        progressBar.progressProperty().bind(service.progressProperty());

        lblInfo.setText("Loading ...");

        service.start();
    }

    /**
     * Load game achievements in a service for currently shown games
     * 
     * @param gamesToLoad
     */
    @FXML
    void actionLoadAllAchievements(ActionEvent event) {

        AchievementsLoader task = new AchievementsLoader(txtKey.getText(), txtSteamId.getText(), txtLanguage.getText(), tableView.getItems());

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                updateTableView(null);
            }
        });

        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                throw new RuntimeException(task.getException());
            }
        });

        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return task;
            }
        };

        progressBar.progressProperty().bind(service.progressProperty());

        lblInfo.setText("Loading ...");

        service.start();

        updateTableView(null);
    }

    /**
     * Predicate check on text & check box filters
     * 
     * @param achievement
     * @return
     */
    private boolean predicateCheck(Game game) {
        boolean showOnTextFilter;
        if (txtFilter.getText().isEmpty()) {
            showOnTextFilter = true;
        } else {
            showOnTextFilter = game.getName().toLowerCase().contains(txtFilter.getText().toLowerCase());
        }

        boolean showOnCheckBoxFilter;
        if (!cbFilterNonAchievement.isSelected()) {
            showOnCheckBoxFilter = true;
        } else {
            showOnCheckBoxFilter = game.getHasStats();
        }

        return showOnTextFilter && showOnCheckBoxFilter;
    }

    /**
     * Shows the games and does apply filters. If loadedGames is null, the existing table will be (re-)shown.
     * 
     * @param loadedGames
     */
    private void updateTableView(List<Game> loadedGames) {

        if (loadedGames != null) {
            tableViewList = FXCollections.observableArrayList(loadedGames);
        }

        // apply filters
        FilteredList<Game> filteredData = new FilteredList<>(tableViewList, p -> true);

        // Filter on already set checkbox && text
        filteredData.setPredicate(p -> predicateCheck(p));

        // Listener for filter text change
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(game -> {
                return predicateCheck(game);
            });
        });

        // Filter for cb change
        cbFilterNonAchievement.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(game -> {
                return predicateCheck(game);
            });
        });

        // Update label on change
        filteredData.addListener((Change<? extends Game> c) -> {
            updateInfoLabel();
        });

        SortedList<Game> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
        tableView.refresh();

        updateInfoLabel();

    }

    /**
     * Show the info label ...
     */
    private void updateInfoLabel() {

        int achievementTotal = 0;
        int achievementDone = 0;

        for (Game game : tableView.getItems()) {
            if (game.isPopulated() && game.getHasStats()) {
                achievementTotal += game.achievementsTotalProperty().get();
                achievementDone += game.achievementsDoneProperty().get();
            }
        }

        int percentage = 100;
        if (achievementTotal > 0) {
            percentage = achievementDone * 100 / achievementTotal;
        }

        lblInfo.setText("Games loaded: " + tableView.getItems().size() + ", Achievements: " + achievementTotal + ", Unlocked: " + achievementDone + " (" + percentage + "%)");

    }

    /**
     * Show some info
     * 
     * @param event
     */
    @FXML
    void actionInfo(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("About");
        WebView webView = new WebView();
        webView.getEngine().loadContent(
                "<html><center><b>" + SteamahApp.APP_TITLE + "</b></p> by Bernhard von Gunten bvg@nostromo.ch</p></p>Steam API Key Site: https://steamcommunity.com/dev/apikey</center></html>");
        webView.setPrefSize(400, 150);
        alert.getDialogPane().setContent(webView);

        alert.showAndWait();
        
    }

    /**
     * Quit the application
     * 
     * @param event
     */
    @FXML
    public void actionQuit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

}
