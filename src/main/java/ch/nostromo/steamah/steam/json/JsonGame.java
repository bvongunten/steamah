package ch.nostromo.steamah.steam.json;

import com.google.gson.annotations.SerializedName;

/**
 * Json binding for steam response
 *
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 */
public class JsonGame {

	private String appid;

	private String name;

	@SerializedName("playtime_forever")
	private String playtimeForever;

	@SerializedName("playtime_2weeks")
	private String playtimeTwoWeeks;

	@SerializedName("has_community_visible_stats")
	private String hasStats;

    public String getAppid() {
        return appid;
    }

    public String getName() {
        return name;
    }

    public String getPlaytimeForever() {
        return playtimeForever;
    }

    public String getPlaytimeTwoWeeks() {
        return playtimeTwoWeeks;
    }

    public String getHasStats() {
        return hasStats;
    }

}
