package com.me.mygdxgame;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.badlogic.gdx.files.FileHandle;

public class Logger
{
	int earthquakeCount, enemyCount, killCount, basicsCount, basicsKilled, fastCount, fastKilled, toughCount, toughKilled, diggerCount, diggersKilled, superCount, supersKilled, superFastCount, superFastKilled, superToughCount,
	superToughKilled, superShieldCount, superShieldKilled, superInvisCount, superInvisKilled, shotsFired, towersBuilt, towersUpgraded, towersSold;
	
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
		towersUpgraded = 0;
		enemyCount = 0;
		killCount = 0;
	}
	
	public void writeLogToDisk(FileHandle fileHandle, HashMap<String, Parameter> parameters, boolean won, int successiveGameCounter, int livesLeft, int goldLeft)
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
		fileHandle.writeString("Lives left       : " + livesLeft + "/" + GameConstants.startLives + "\r\n", true);
		fileHandle.writeString("Gold left        : " + goldLeft + "\r\n", true);
		fileHandle.writeString("Earthquake count : " + earthquakeCount + "\r\n", true);
		fileHandle.writeString("Shots fired      : " + shotsFired + "\r\n", true);
		fileHandle.writeString("Towers built     : " + towersBuilt + "\r\n", true);
		fileHandle.writeString("Towers upgraded  : " + towersUpgraded + "\r\n", true);
		fileHandle.writeString("Towers sold      : " + towersSold + "\r\n", true);

		// Write enemies short version
		fileHandle.writeString("---Enemies - short version---" + "\r\n", true);
		fileHandle.writeString("Total enemies killed : " + killCount + "/" + enemyCount + "\r\n", true);
		fileHandle.writeString("Diggers killed       : " + diggersKilled + "/" + diggerCount + "\r\n", true);
		fileHandle.writeString("Supers killed        : " + supersKilled + "/" + superCount + "\r\n", true);

		// Write enemies long version
		fileHandle.writeString("---Enemies - long version---" + "\r\n", true);
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
