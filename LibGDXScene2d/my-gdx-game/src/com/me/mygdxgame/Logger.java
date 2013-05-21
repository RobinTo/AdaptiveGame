package com.me.mygdxgame;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.badlogic.gdx.files.FileHandle;

public class Logger
{
	int earthquakeCount, enemyCount, killCount, basicsCount, basicsKilled, fastCount, fastKilled, toughCount, toughKilled, diggerCount, diggersKilled,
			superCount, supersKilled, superFastCount, superFastKilled, superToughCount, superToughKilled, superShieldCount, superShieldKilled, superInvisCount,
			superInvisKilled, shotsFired, towersBuilt, upgradesBought, towersSold, towersDestroyed, earthquakeProofs, lastHappy, lastDifficult, arrowTowers,
			frostTowers, cannonTowers, laserTowers, burningTowers, flameTowers;

	public Logger()
	{
		this.resetCounters();
	}

	public void resetCounters()
	{
		earthquakeCount = 0;
		basicsCount = 0;
		basicsKilled = 0;
		toughCount = 0;
		toughKilled = 0;
		fastCount = 0;
		fastKilled = 0;
		diggerCount = 0;
		diggersKilled = 0;
		superCount = 0;
		supersKilled = 0;
		superFastCount = 0;
		superFastKilled = 0;
		superToughCount = 0;
		superToughKilled = 0;
		superInvisCount = 0;
		superInvisKilled = 0;
		superShieldCount = 0;
		superShieldKilled = 0;
		shotsFired = 0;
		towersBuilt = 0;
		towersSold = 0;
		upgradesBought = 0;
		towersDestroyed = 0;
		enemyCount = 0;
		killCount = 0;
		earthquakeProofs = 0;
		lastHappy = 0;
		lastDifficult = 0;
		arrowTowers = 0;
		frostTowers = 0;
		cannonTowers = 0;
		laserTowers = 0;
		burningTowers = 0;
		flameTowers = 0;
	}

	public void writeLogToDisk(FileHandle fileHandle, HashMap<String, Parameter> parameters, boolean won, int successiveGameCounter, int livesLeft,
			int goldLeft, int startGold, double currentMetric, double lastMetric, double challengerMetric, float maxJumpDistance, double playerLevel,
			double gameLengthMultiplier)
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale("en", "EN"));
		String formattedDate = df.format(new Date());

		// Start write for one game
		if (won)
		{
			fileHandle.writeString("Game number " + successiveGameCounter + " - at " + formattedDate + " - Game WON\r\n", true);
		}
		else
		{
			fileHandle.writeString("Game number " + successiveGameCounter + " - at " + formattedDate + " - Game LOST\r\n", true);
		}

		// Write some info
		fileHandle.writeString("Lives left        : " + livesLeft + "/" + GameConstants.startLives + "\r\n", true);
		fileHandle.writeString("Gold left         : " + goldLeft + "/" + startGold + "\r\n", true);
		fileHandle.writeString("Earthquake count  : " + earthquakeCount + "\r\n", true);
		fileHandle.writeString("Shots fired       : " + shotsFired + "\r\n", true);
		
		// Tower info
		fileHandle.writeString("---Tower information---" + "\r\n", true);
		fileHandle.writeString("Towers built      : " + towersBuilt + "\r\n", true);
		fileHandle.writeString("Upgrades bought   : " + upgradesBought + "\r\n", true);
		fileHandle.writeString("Towers sold       : " + towersSold + "\r\n", true);
		fileHandle.writeString("Towers destroyed  : " + towersDestroyed + "\r\n", true);
		fileHandle.writeString("Earthquake proofs : " + earthquakeProofs + "\r\n", true);
		fileHandle.writeString("Arrow towers      : " + arrowTowers + "\r\n", true);
		fileHandle.writeString("Frost towers      : " + frostTowers + "\r\n", true);
		fileHandle.writeString("Cannon towers     : " + cannonTowers + "\r\n", true);
		fileHandle.writeString("Flame towers      : " + flameTowers + "\r\n", true);
		fileHandle.writeString("Laser towers      : " + laserTowers + "\r\n", true);
		fileHandle.writeString("Burning towers    : " + burningTowers + "\r\n", true);

		// Write metric and jump info
		fileHandle.writeString("---Metric and jump information---" + "\r\n", true);
		fileHandle.writeString("Hearts feedback        : " + lastHappy + "\r\n", true);
		fileHandle.writeString("Difficulty feedback    : " + lastDifficult + "\r\n", true);
		fileHandle.writeString("Current metric         : " + currentMetric + "\r\n", true);
		fileHandle.writeString("Last metric            : " + lastMetric + "\r\n", true);
		fileHandle.writeString("Challenger metric      : " + challengerMetric + "\r\n", true);
		fileHandle.writeString("Max jump distance      : " + maxJumpDistance + "\r\n", true);
		fileHandle.writeString("Player Level           : " + playerLevel + "\r\n", true);
		fileHandle.writeString("Game length multiplier : " + gameLengthMultiplier + "\r\n", true);

		// Write enemies
		fileHandle.writeString("---Enemy information---" + "\r\n", true);
		fileHandle.writeString("Total enemies killed : " + killCount + "/" + enemyCount + "\r\n", true);
		fileHandle.writeString("Basic enemies killed : " + basicsKilled + "/" + basicsCount + "\r\n", true);
		fileHandle.writeString("Fast enemies killed  : " + fastKilled + "/" + fastCount + "\r\n", true);
		fileHandle.writeString("Tough enemies killed : " + toughKilled + "/" + toughCount + "\r\n", true);
		fileHandle.writeString("Diggers killed       : " + diggersKilled + "/" + diggerCount + "\r\n", true);
		fileHandle.writeString("Total supers killed  : " + supersKilled + "/" + superCount + "\r\n", true);
		fileHandle.writeString("Super fast   killed  : " + superFastKilled + "/" + superFastCount + "\r\n", true);
		fileHandle.writeString("Super tough  killed  : " + superToughKilled + "/" + superToughCount + "\r\n", true);
		fileHandle.writeString("Super shield killed  : " + superShieldKilled + "/" + superShieldCount + "\r\n", true);
		fileHandle.writeString("Super invis  killed  : " + superInvisKilled + "/" + superInvisCount + "\r\n", true);

		// Write parameters
		fileHandle.writeString("---Parameters---" + "\r\n", true);
		Iterator<String> parameterIterator = parameters.keySet().iterator();
		while (parameterIterator.hasNext())
		{
			String key = parameterIterator.next();
			Parameter parameter = parameters.get(key);

			// Add padding to make document look good
			// First padding
			int paddingCount = 24 - parameter.name.length();
			String paddingString = "";
			for (int counter = 0; counter < paddingCount; counter++)
			{
				paddingString = paddingString.concat(" ");
			}
			// Second padding
			paddingCount = 12 - ("" + parameter.value).length();
			String paddingString2 = "";
			for (int counter = 0; counter < paddingCount; counter++)
			{
				paddingString2 = paddingString2.concat(" ");
			}
			// Third padding
			paddingCount = 6 - ("" + parameter.minValue).length();
			String paddingString3 = "";
			for (int counter = 0; counter < paddingCount; counter++)
			{
				paddingString3 = paddingString3.concat(" ");
			}

			fileHandle.writeString(parameter.name + ":" + paddingString + parameter.value + paddingString2 + "Min: " + parameter.minValue + paddingString3
					+ "Max: " + parameter.maxValue + "\r\n", true);
		}

		// End write for one game
		fileHandle.writeString("\r\n", true);
	}
}
