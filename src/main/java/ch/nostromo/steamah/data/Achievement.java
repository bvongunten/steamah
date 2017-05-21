package ch.nostromo.steamah.data;

import javafx.beans.property.BooleanProperty;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Achievement entity
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class Achievement {

    private StringProperty apiName;
    private StringProperty name;
    private StringProperty description;
    private StringProperty unlockTime;
    private BooleanProperty achieved;

    public Achievement(String apiName, String name, String description, Boolean achieved, String unlockTime) {
        this.apiName = new SimpleStringProperty(apiName);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.unlockTime = new SimpleStringProperty(unlockTime);
        this.achieved = new SimpleBooleanProperty(achieved);
    }

    public String getName() {
        return name.get();
    }

    public String getApiName() {
        return apiName.get();
    }

    public String getDescription() {
        return description.get();
    }

    public Boolean isAchieved() {
        return achieved.get();
    }

    public String getUnlockTime() {
        return unlockTime.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty apiNameProperty() {
        return apiName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty unlockTimeProperty() {
        return unlockTime;
    }

    public BooleanProperty achievedProperty() {
        return achieved;
    }

}
