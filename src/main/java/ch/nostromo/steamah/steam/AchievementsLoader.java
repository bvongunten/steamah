package ch.nostromo.steamah.steam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

import ch.nostromo.steamah.data.Achievement;
import ch.nostromo.steamah.data.Game;
import ch.nostromo.steamah.steam.json.JsonAchievement;
import ch.nostromo.steamah.steam.json.JsonAchievements;

/**
 * Fetches the achievements from the Steam Api service.
 * 
 * @author Bernhard von Gunten <bvg@nostromo.ch>
 *
 */
public class AchievementsLoader extends AbstractLoader<Void> {

    List<Game> gamesToLoad;

    public AchievementsLoader(String key, String steamId, String language, List<Game> gamesToLoad) {
        super(key, steamId, language);
        this.gamesToLoad = gamesToLoad;
    }

    /**
     * Returns the achievements of a given game & steamId.
     * 
     * @param appId
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    private JsonAchievements getAchievementsByGame(String appId) throws MalformedURLException, IOException {
        String url = "http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=" + appId + "&key=" + getKey() + "&steamid=" + getSteamId() + "&l=" + getLanguage();

        String json = loadJson(url);

        if (json == null) {
            return null;
        }

        return new Gson().fromJson(json, JsonAchievements.class);
    }

    @Override
    protected Void call() throws Exception {

        int count = 0;
        for (Game game : gamesToLoad) {
            updateProgress(count, gamesToLoad.size());

            JsonAchievements jsonAchievements = getAchievementsByGame(game.getAppId());

            List<Achievement> gameAchievements = new ArrayList<>();

            if (jsonAchievements != null && jsonAchievements.getPlayerstats() != null && jsonAchievements.getPlayerstats().getAchievements() != null) {
                for (JsonAchievement jsonAchievement : jsonAchievements.getPlayerstats().getAchievements()) {

                    String apiName = convertToStringValue(jsonAchievement.getApiname());
                    String name = convertToStringValue(jsonAchievement.getName());
                    String description = convertToStringValue(jsonAchievement.getDescription());
                    Boolean achieved = convertToBooleanValue(jsonAchievement.getAchieved());
                    
                   
                    String unlockTime = convertToDateStringValue(jsonAchievement.getUnlocktime());

                    gameAchievements.add(new Achievement(apiName, name, description, achieved, unlockTime));
                }
            }

            // Sort by name
            Collections.sort(gameAchievements, (o1, o2) -> o1.getName().compareTo(o2.getName()));

            game.setAchievements(gameAchievements);
            count++;
        }

        
        updateProgress(gamesToLoad.size(), gamesToLoad.size());

        return null;

    }

}
