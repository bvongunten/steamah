package ch.nostromo.steamah.steam.json;

import com.google.gson.annotations.SerializedName;

/**
 * Json binding for steam response
 *
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 */
public class JsonGamesResponse {
	
	@SerializedName("game_count")
    private String gameCount;

    @SerializedName("games")
    private JsonGame[] games;

	public int getGameCount() {
		return Integer.valueOf(gameCount).intValue();
	}

	public JsonGame[] getGames() {
		return games;
	}

    
}
