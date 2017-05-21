package ch.nostromo.steamah.steam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

import ch.nostromo.steamah.data.Game;
import ch.nostromo.steamah.steam.json.JsonGame;
import ch.nostromo.steamah.steam.json.JsonGames;

/**
 * Fetches the games from the Steam Api service.
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class GameLoader extends AbstractLoader<List<Game>> {

	public GameLoader(String key, String steamId, String language) {
	    super(key, steamId, language);
	}

	/**
	 * Returns a list of all games owned by a given steamId
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private JsonGames getGamesByOwner() throws MalformedURLException, IOException {
		String url = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" + getKey() + "&steamid="
				+ getSteamId() + "&format=json&include_appinfo=1&include_played_free_games=1";

		String json = loadJson(url);

		return new Gson().fromJson(json, JsonGames.class);
	}

	@Override
	protected List<Game> call() throws Exception {

		List<Game> result = new ArrayList<>();

		JsonGames jsonGames = getGamesByOwner();
		
		int count = 0;
		for (JsonGame jsonGame : jsonGames.getResponse().getGames()) {
			
            updateProgress(count, jsonGames.getResponse().getGameCount());
			
            String appId = convertToStringValue(jsonGame.getAppid());
            String name = convertToStringValue(jsonGame.getName());
            Integer playedForever = convertToIntegerValue(jsonGame.getPlaytimeForever());
            Integer playedTwoWeeks = convertToIntegerValue(jsonGame.getPlaytimeTwoWeeks());
            Boolean hasStats = convertToBooleanValue(jsonGame.getHasStats());
            
			result.add(new Game(appId, name, playedForever, playedTwoWeeks, hasStats));
			count++;

		}
		
        updateProgress(jsonGames.getResponse().getGameCount(), jsonGames.getResponse().getGameCount());


		// Sort by name
		Collections.sort(result, (g1, g2) -> g1.getName().compareTo(g2.getName()));
		
		return result;
	}
	


}
