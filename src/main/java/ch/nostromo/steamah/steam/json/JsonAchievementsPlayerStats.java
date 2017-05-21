package ch.nostromo.steamah.steam.json;

import com.google.gson.annotations.SerializedName;

/**
 * Json binding for steam response
 *
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 */
public class JsonAchievementsPlayerStats {

	private String steamID;
	private String gameName;

	@SerializedName("achievements")
	private JsonAchievement[] achievements;

	public String getSteamID() {
		return steamID;
	}

	public String getGameName() {
		return gameName;
	}

	public JsonAchievement[] getAchievements() {
		return achievements;
	}

}
