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
		for(int i = 0; i < fileContent.size(); i++)
		{
			boolean towerDone = false;
			
			String type="", towerTexture="", upgradesTo = "", missileTexture = "";
			
			MissileEffect missileEffects = null;
			float reloadTime = 1f;
			int sellPrice=0, upgradeCost = 0, buildCost = 0, range = 0;
			HashMap<String, FloatingBoolean> effectsForMissile = new HashMap<String, FloatingBoolean>();
			TargetingStrategy targetStrategy = TargetingStrategy.Single;
			while(!towerDone)
			{
				String[] split = fileContent.get(i).split(":");
				String testString0 = split[0].toLowerCase();
				if(testString0.equals("endtower"))
				{
					towerDone = true;
				}
				else
				{
					if(testString0.equals("towertexture"))
					{
						towerTexture = split[1];
					}
					else if(testString0.equals("missiletexture"))
					{
						missileTexture = split[1];
					}
					else if(testString0.equals("upgradesto"))
					{
						upgradesTo = split[1];
					}
					else if(testString0.equals("type"))
					{
						type = split[1];
					}
					else if(testString0.equals("sellprice"))
					{
						sellPrice = Integer.parseInt(split[1]);
					}
					else if(testString0.equals("buildcost"))
					{
						buildCost = Integer.parseInt(split[1]);
					}
					else if(testString0.equals("upgradecost"))
					{
						upgradeCost = Integer.parseInt(split[1]);
					}
					else if(testString0.equals("effect"))
					{
						FloatingBoolean fb = new FloatingBoolean(split[1].equals("set") ? true: false, Float.parseFloat(split[3]));
						effectsForMissile.put(split[2], fb);
					}
					else if(testString0.equals("effect"))
					{
						missileTexture = split[1];
					}
					else if(testString0.equals("targeting"))
					{
						if(split[1].toLowerCase().equals("circle"))
						{
							targetStrategy = TargetingStrategy.Circle;
						}
						else if(split[1].toLowerCase().equals("line"))
						{
							targetStrategy = TargetingStrategy.Line;
						}
					}
					else if(testString0.equals("reloadtime"))
					{
						reloadTime = Float.parseFloat(split[1]);
					}
					else if(testString0.equals("range"))
					{
						range = Integer.parseInt(split[1]);
					}
					i++;
				}
				
			}
			missileEffects = new MissileEffect(new MissileTarget(targetStrategy), effectsForMissile);
			System.out.println("Created tower: " + type + ":" + towerTexture + ":" + missileTexture + ":" + sellPrice + ":" + upgradeCost + ":" + buildCost + ":" + missileEffects.effects.size() + ":" + reloadTime + ":" + range);
			towerInfo.put(type, new TowerStats(type, upgradesTo, towerTexture, missileTexture, sellPrice, upgradeCost, buildCost, missileEffects, reloadTime, range));
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
