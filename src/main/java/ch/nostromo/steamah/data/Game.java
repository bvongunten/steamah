package ch.nostromo.steamah.data;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Game entity
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class Game {

    private StringProperty appId;

    private StringProperty name;

    private IntegerProperty playtimeForever;

    private IntegerProperty playtimeTwoWeeks;

    private BooleanProperty hasStats;

    private IntegerProperty achievementsTotal;

    private IntegerProperty achievementsDone;

    private IntegerProperty achievementsPercentage;
    
    private boolean populated = false;
    
    private List<Achievement> achievements;
    

    public Game(String appId, String name, Integer playtimeForever, Integer playtimeTwoWeeks, Boolean hasStats) {
        this.appId = new SimpleStringProperty(appId);
        this.name = new SimpleStringProperty(name);
        this.playtimeForever = new SimpleIntegerProperty(playtimeForever);
        this.playtimeTwoWeeks = new SimpleIntegerProperty(playtimeTwoWeeks);
        this.hasStats = new SimpleBooleanProperty(hasStats);
    }

    public String getAppId() {
        return appId.get();
    }

    public StringProperty appIdProperty() {
        return appId;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Integer getPlayTimeForever() {
        return playtimeForever.get();
    }

    public IntegerProperty playtimeForeverProperty() {
        return playtimeForever;
    }

    public Integer getPlayTimeTwoWeeks() {
        return playtimeTwoWeeks.get();
    }

    public IntegerProperty playtimeTwoWeeksProperty() {
        return playtimeTwoWeeks;
    }

    public Boolean getHasStats() {
        return hasStats.get();
    }

    public BooleanProperty hasStatsProperty() {
        return hasStats;
    }

    public IntegerProperty achievementsTotalProperty() {
        return achievementsTotal;
    }

    public IntegerProperty achievementsDoneProperty() {
        return achievementsDone;
    }

    public IntegerProperty achievementsPercentageProperty() {
        return achievementsPercentage;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.populated = true;
        this.achievements = achievements;
        
        if (achievements.size() == 0) {
            this.hasStats.set(false);
            return;
        }
        
        this.achievementsTotal = new SimpleIntegerProperty(achievements.size());
        
        int countDone = 0;
        for (Achievement achievement : achievements) {
            if (achievement.isAchieved()) {
                countDone ++;
            }
        }
        this.achievementsDone = new SimpleIntegerProperty(countDone);
        
        int percentage = 100;
        if (achievements.size() > 0) {
           percentage = countDone * 100 / achievements.size();
        } 
        this.achievementsPercentage = new SimpleIntegerProperty(percentage);
    }
    
    public List<Achievement> getAchievements() {
        return achievements;
    }
    
    public boolean isPopulated() {
        return populated;
    }
    
}
