package ch.nostromo.steamah.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import ch.nostromo.steamah.data.Achievement;
import ch.nostromo.steamah.data.Game;

public class Export {

	public static void saveAchievements(Game game, Path path) throws IOException {
		
		// Sort by description
		List<Achievement> toBeExported = game.getAchievements();
		Collections.sort(toBeExported, (a, b) -> a.getDescription().compareTo(b.getDescription()));
		
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
			for (Achievement achievement : toBeExported) {
				writer.write(achievement.getName() + "\t" + achievement.getDescription() + "\t" + achievement.isAchieved() + "\t"+achievement.getUnlockTime() + System.lineSeparator());
			}
			
		}
	}
	
}
