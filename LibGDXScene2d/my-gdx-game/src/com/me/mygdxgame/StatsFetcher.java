package com.me.mygdxgame;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

public class StatsFetcher {

	public static HashMap<String, TowerStats> loadTowerStats(FileHandle handle)
			throws NumberFormatException, ParseException {
		List<String> fileContent = GameConstants.readRawTextFile(handle);
		System.out.println("Loaded file");

		HashMap<String, TowerStats> towerInfo = new HashMap<String, TowerStats>();

		// Localize for machines using . and machines using , as separators.
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("0.#");

		format.setDecimalFormatSymbols(symbols);
		for (int x = 0; x * 32 < fileContent.size(); x++) {
			String[] readStats = new String[32];
			for (int i = 0; i < 32; i++) {
				String s = fileContent.get(i + (32 * x));
				String[] split = s.split(":");
				readStats[i] = split[1];
			}
			towerInfo.put(
					readStats[0],
					new TowerStats(readStats[0], readStats[1], readStats[2],
							readStats[3], readStats[4], format.parse(
									readStats[5]).floatValue(), Integer
									.parseInt(readStats[6]), Integer
									.parseInt(readStats[7]), Integer
									.parseInt(readStats[8]), Integer
									.parseInt(readStats[9]), Integer
									.parseInt(readStats[10]), Integer
									.parseInt(readStats[11]), Integer
									.parseInt(readStats[12]), Integer
									.parseInt(readStats[13]), Integer
									.parseInt(readStats[14]), Integer
									.parseInt(readStats[15]), Integer
									.parseInt(readStats[16]), Integer
									.parseInt(readStats[17]), Integer
									.parseInt(readStats[18]), Integer
									.parseInt(readStats[19]), Float
									.parseFloat(readStats[20]), Float
									.parseFloat(readStats[21]), Float
									.parseFloat(readStats[22]), Integer
									.parseInt(readStats[23]), Integer
									.parseInt(readStats[24]), Float
									.parseFloat(readStats[25]), Integer
									.parseInt(readStats[26]), Integer
									.parseInt(readStats[27]), Float
									.parseFloat(readStats[28]), Integer
									.parseInt(readStats[29]), Integer
									.parseInt(readStats[30]), Float
									.parseFloat(readStats[31])));
		}
		return towerInfo;
	}

	public static HashMap<String, EnemyStats> generateEnemyInfo(
			FileHandle handle) {
		HashMap<String, EnemyStats> enemyInfo = new HashMap<String, EnemyStats>();
		List<String> fileContent = GameConstants.readRawTextFile(handle);
		System.out.println("Loaded file");
		for (int x = 0; x * 5 < fileContent.size(); x++) {
			String[] readStats = new String[5];
			for (int i = 0; i < 5; i++) {
				String s = fileContent.get(i + (5 * x));
				String[] split = s.split(":");
				readStats[i] = split[1];
			}
			enemyInfo.put(
					readStats[0],
					new EnemyStats(readStats[0], readStats[1], Integer
							.parseInt(readStats[2]), Float
							.parseFloat(readStats[3]), Integer
							.parseInt(readStats[4])));
		}

		return enemyInfo;
	}
}
