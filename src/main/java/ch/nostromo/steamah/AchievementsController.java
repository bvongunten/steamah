package ch.nostromo.steamah;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ch.nostromo.steamah.data.Achievement;
import ch.nostromo.steamah.data.Game;
import ch.nostromo.steamah.export.Export;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * JavaFX Controller for the achievements list window
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class AchievementsController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private TableView<Achievement> tableView;

    @FXML
    private Label lblTitle;

    @FXML
    private TextField txtFilter;

    @FXML
    private CheckBox cbFilterAchieved;

    @FXML
    private CheckBox cbFilterMissing;

    @FXML
    private TableColumn<Achievement, String> colName;

    @FXML
    private TableColumn<Achievement, String> colDescription;

    @FXML
    private TableColumn<Achievement, String> colUnlockTime;

    @FXML
    private TableColumn<Achievement, Boolean> colAchieved;

    private ObservableList<Achievement> tableViewList;
    
    private Game currentGame;

    /**
     * Create cell value factories
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colUnlockTime.setCellValueFactory(cellData -> cellData.getValue().unlockTimeProperty());
        colAchieved.setCellValueFactory(cellData -> cellData.getValue().achievedProperty());
        colAchieved.setCellFactory(column -> new CheckBoxTableCell<Achievement, Boolean>());
    }

    /**
     * Predicate check on text & check box filters
     * 
     * @param achievement
     * @return
     */
    private boolean predicateCheck(Achievement achievement) {
        boolean showOnTextFilter;
        if (txtFilter.getText().isEmpty()) {
            showOnTextFilter = true;
        } else {
            showOnTextFilter = achievement.getName().toLowerCase().contains(txtFilter.getText().toLowerCase())
                    || achievement.getDescription().toLowerCase().contains(txtFilter.getText().toLowerCase());
        }

        boolean showOnCheckBoxFilter = false;
        if (cbFilterAchieved.isSelected() && achievement.isAchieved()) {
            showOnCheckBoxFilter = true;
        }

        if (cbFilterMissing.isSelected() && !achievement.isAchieved()) {
            showOnCheckBoxFilter = true;
        }

        return showOnTextFilter && showOnCheckBoxFilter;

    }

    /**
     * Populates the table view and sets listeners on text & cb filters
     * 
     * @param game
     */
    public void setAchievements(Game game) {

    	this.currentGame = game;
    	
        lblTitle.setText(game.getName());

        tableViewList = FXCollections.observableArrayList(game.getAchievements());

        // apply filters
        FilteredList<Achievement> filteredData = new FilteredList<>(tableViewList, p -> true);

        filteredData.setPredicate(achievement -> predicateCheck(achievement));

        // Listener for filter text change
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(achievemeent -> {
                return predicateCheck(achievemeent);
            });
        });

        // Filter for cb change
        cbFilterAchieved.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(achievemeent -> {
                return predicateCheck(achievemeent);
            });
        });
        cbFilterMissing.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(achievemeent -> {
                return predicateCheck(achievemeent);
            });
        });

        SortedList<Achievement> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
        tableView.refresh();

    }

    /**
     * Close frame
     * 
     * @param event
     */
    @FXML
    void actionClose(ActionEvent event) {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Export list
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    void actionExport(ActionEvent event) throws IOException {
        
    	 FileChooser fileChooser = new FileChooser();
    	 
         //Set extension filter
         FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
         fileChooser.getExtensionFilters().add(extFilter);
        
         //Show save file dialog
         File file = fileChooser.showSaveDialog((Stage) borderPane.getScene().getWindow());
        
         if(file != null){
        	 Export.saveAchievements(currentGame, file.toPath());
         }
    }

}
