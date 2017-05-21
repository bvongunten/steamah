package ch.nostromo.steamah.steam.json;

/**
 * Json binding for steam response
 *
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 */
public class JsonAchievement {
	private String apiname;
	private String achieved;
	private String name;
	private String description;
	private String unlocktime;
	
	public String getUnlocktime() {
        return unlocktime;
    }

    public void setUnlocktime(String unlocktime) {
        this.unlocktime = unlocktime;
    }

    public String getApiname() {
		return apiname;
	}
    
    public String getAchieved() {
        return achieved;
    }
    
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

}
