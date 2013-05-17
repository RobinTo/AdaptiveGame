package com.me.mygdxgame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class GameProcessor
{
	int livesLeft, currentGold;

	HashMap<Float, Enemy> enemyWave = new HashMap<Float, Enemy>();
	List<Float> waveTime = new ArrayList<Float>();
	float currentMinionDelay = 2.0f; // Will be set at random
	int waveSize = 10;
	int waveParts = 3;
	int waveIncrements = 5;
	int wavePartDelay = 7; // Seconds
	int nextEnemyGeneratedCounter = 0;
	float lastMinionTime = 0;
	Random rand = new Random();

	List<Missile> missiles = new ArrayList<Missile>();
	List<Enemy> enemies = new ArrayList<Enemy>();
	List<Tower> towers = new ArrayList<Tower>();
	List<Enemy> diggerEnemies = new ArrayList<Enemy>();
	Tower selectedTower;

	boolean earthquakeEnabled = true;
	boolean movesTowers = true;
	boolean tempNudge = false;

	float nudgeTimer = 0;
	float nudgeRemainingTime = 0f;
	float nudgeRandomizerTimer = 1.0f;
	float nudgeRandomizerInterval = 0.5f;
	int nudges = 0; // Counter to check for on off nudges every 5 seconds.

	float superEnemyHealthMultiplier = 4.0f;
	float superEnemySpeedMultiplierBonus = 2.0f; // CHANGED: If currentMoveSpeedMultiplier was 2.0f, it will now be 4.0f (Multiplication).
	float superEnemySpeedSizeScale = 0.8f; // Size scale for super enemies with speed bonus.
	float superEnemyHealthSizeScale = 1.2f; // ^ for health super minions

	public void initialize(int startGold)
	{
		livesLeft = GameConstants.startLives;
		currentGold = startGold;

	}

	public void generateNextEnemy(float statMultiplier, ThinkTank thinkTank,
			Map map, TextureAtlas enemiesAtlas, TextureAtlas miscAtlas,
			float diggerChance, float superEnemyChance)
	{
		currentMinionDelay = rand.nextInt(30);
		currentMinionDelay /= 10.0f;
		float time = lastMinionTime + currentMinionDelay;
		lastMinionTime += currentMinionDelay; // Basically same as time
		Enemy e = null;
		double randValue = rand.nextDouble();

		if (randValue <= diggerChance)
		{
			e = createEnemy("digger", thinkTank, map, enemiesAtlas, miscAtlas);
			e.modifyOriginalHealth(statMultiplier);
			e.modifyOriginalMoveSpeed(statMultiplier);
		} else if (randValue <= (1.0 - diggerChance) / 3.0 + diggerChance)
		{
			e = createEnemy("basic", thinkTank, map, enemiesAtlas, miscAtlas);
			e.modifyOriginalHealth(statMultiplier);
			e.modifyOriginalMoveSpeed(statMultiplier);
		} else if (randValue <= 2.0 * (1.0 - diggerChance) / 3.0 + diggerChance)
		{
			e = createEnemy("fast", thinkTank, map, enemiesAtlas, miscAtlas);
			e.modifyOriginalMoveSpeed(statMultiplier);
		} else
		{
			e = createEnemy("tough", thinkTank, map, enemiesAtlas, miscAtlas);
			e.modifyOriginalHealth(statMultiplier);
		}

		if (!waveTime.contains(time))
		{
			float superMinionRand = (float) rand.nextDouble();
			if (superMinionRand < superEnemyChance)
			{
				superMinionRand = rand.nextFloat();
				if (superMinionRand < 0.8f)
				{
					e.modifyOriginalHealth(superEnemyHealthMultiplier);
					e.setScale(superEnemyHealthSizeScale);
//					e.setColor(e.getColor().r + 150, e.getColor().g, e.getColor().b, e.getColor().a);
					e.superToughEnemy = true;
				}
				superMinionRand = rand.nextFloat();
				if (superMinionRand < 0.5f)
				{
					e.modifyOriginalMoveSpeed(e
							.getStat("currentMoveSpeedMultiplier")
							* superEnemySpeedMultiplierBonus);
					e.setScale(superEnemySpeedSizeScale);
//					e.setColor(e.getColor().r, e.getColor().g, e.getColor().b + 150, e.getColor().a);
					e.superFastEnemy = true;
				}
				superMinionRand = rand.nextFloat();
				if (superMinionRand < 0.5f)
				{
					e.superInvisibleEnemy = true;
				}
				superMinionRand = rand.nextFloat();
				if (superMinionRand < 0.5f)
				{
					e.setColor(0.1f, 0.1f, 0.1f, 1.0f);
					e.superShieldedEnemy = true;
				}
			}
			waveTime.add(time);
			enemyWave.put(time, e);
		}
		else
		{
			generateNextEnemy(statMultiplier, thinkTank, map, enemiesAtlas, miscAtlas, diggerChance, superEnemyChance);
		}
	}

	// Kept function in case we want to display information somewhere else later. Currently not in use.
	public List<String> selectEnemy(Enemy e)
	{
		List<String> textForBox = new ArrayList<String>();

		textForBox.add(e.enemyStats.type);
		textForBox.add("Health: " + Math.round(e.getStat("currentHealth")));
		textForBox.add("Speed: "
				+ Math.round(e.getStat("currentMoveSpeedMultiplier")));
		textForBox.add("Yields: " + Math.round(e.getStat("currentGoldYield")));
		return textForBox;

	}

	public List<String> selectTower(Tower t,
			HashMap<String, TowerStats> towerInfo)
	{
		if (t != null)
		{
			List<String> textForBox = new ArrayList<String>();
			selectedTower = t;
			textForBox.add(t.towerStats.type);
			textForBox.add(t.towerStats.description);

			if (selectedTower.towerStats.upgradesTo.equals("null"))
			{
				textForBox.add("Upgrade: MAX");
			} else
			{
				int upgradeCost = selectedTower.towerStats.upgradeCost;
				textForBox.add("Upgrade: " + upgradeCost);
			}
			textForBox.add("Sell: " + selectedTower.towerStats.sellPrice);
			return textForBox;
		} else
		{
			selectedTower = null;
			return null;
		}
	}

	// Eventually take a towerInfo id, and create appropriate.
	public Tower createTower(String type, Vector2 tilePosition,
			TextureAtlas towersAtlas, TextureAtlas miscAtlas,
			HashMap<String, TowerStats> towerInfo)
	{
		Tower t = new Tower(towerInfo.get(type), towersAtlas
				.createSprite(towerInfo.get(type).towerTexture), miscAtlas
				.createSprite(towerInfo.get(type).missileTexture), towersAtlas
				.createSprite("walls"));
		t.setPosition(tilePosition.x * GameConstants.tileSize, tilePosition.y
				* GameConstants.tileSize);
		return t;
	}

	public void createWave(ThinkTank thinkTank, Map map,
			TextureAtlas enemiesAtlas, TextureAtlas miscAtlas)
	{
		Enemy e = createEnemy("fast", thinkTank, map, enemiesAtlas, miscAtlas);
		addEnemyToWave(0.1f, e);
		Collections.sort(waveTime);
	}

	public void updateGame(float totalTime, Camera gameCamera, Map map, AssetManager assetManager, Stage stage, HeadsUpDisplay hud, float nudgeChanceConstant)
	{
		if (earthquakeEnabled && !isGameWon() && !isGameLost())
		{
			doEarthquake(gameCamera, map, nudgeChanceConstant, assetManager);
		}
		
		if (enemies.size() > 0)
		{
			for (int i = 0; i < towers.size(); i++)
			{
				towers.get(i).calculateTarget(Gdx.graphics.getDeltaTime(), enemies, null);
				if (towers.get(i).canShoot)
				{
					Missile m = towers.get(i).shoot();
					if (m != null)
					{
						assetManager.playSound(towers.get(i).towerStats.shootSound);
						stage.addActor(m);
						missiles.add(m);
						m.setZIndex(towers.get(i).getZIndex() - 1);
					}
				}
			}
		}
		for (int i = 0; i < missiles.size(); i++)
		{
			missiles.get(i).timeToHitTarget -= Gdx.graphics.getDeltaTime();
			if (missiles.get(i).timeToHitTarget <= 0)
			{
				// If TargetStrategy.Single
				if (missiles.get(i).effect.missileTarget.targetingStrategy == TargetingStrategy.Single)
				{
					assetManager.playSound(missiles.get(i).impactSound);
					Enemy targEnemy = ((TargetSingle) (missiles.get(i).effect.missileTarget)).targetEnemy;
					if (targEnemy.superShieldedEnemy)
						continue;
					Iterator<String> it = missiles.get(i).effect.effects.keySet().iterator();
					while (it.hasNext())
					{
						String s = it.next();
						if (missiles.get(i).effect.effects.get(s).b)
						{
							targEnemy.setStat(s, missiles.get(i).effect.effects.get(s).f);
						}
						else
						{
							targEnemy.editStat(s, missiles.get(i).effect.effects.get(s).f);
						}
					}
				}
				// ----------------------
				// If TargetStrategy.Circle
				else if (missiles.get(i).effect.missileTarget.targetingStrategy == TargetingStrategy.Circle)
				{
					TargetCircle targetCircle = (TargetCircle) missiles.get(i).effect.missileTarget;
					List<Enemy> enemiesInCircle = HitDetector.getEnemiesInCircle(enemies, targetCircle.x1, targetCircle.y1, targetCircle.radius);
					Iterator<String> it = missiles.get(i).effect.effects.keySet().iterator();
					while (it.hasNext())
					{
						String s = it.next();
						for (int eT = 0; eT < enemiesInCircle.size(); eT++)
						{
							Enemy targetEnemy = enemiesInCircle.get(eT);
							if (targetEnemy.superShieldedEnemy)
								continue;
							
							if (missiles.get(i).effect.effects.get(s).b)
							{
								targetEnemy.setStat(s, missiles.get(i).effect.effects.get(s).f);
							} else
							{
								targetEnemy.editStat(s, missiles.get(i).effect.effects.get(s).f);
							}
						}
					}
					final ExtendedActor visualEffectActor = new ExtendedActor(missiles.get(i).sprite);
					TargetCircle tC = (TargetCircle) (missiles.get(i).effect.missileTarget);
					visualEffectActor.setPosition(tC.x1 - tC.radius, tC.y1 - tC.radius);
					visualEffectActor.setWidth(tC.radius * 2);
					visualEffectActor.setHeight(tC.radius * 2);
					visualEffectActor.addAction(sequence(Actions.fadeOut(0.5f), run(new Runnable()
					{
						public void run()
						{
							visualEffectActor.remove();
						}
					})));
					stage.addActor(visualEffectActor);
					assetManager.playSound(missiles.get(i).impactSound);

				}
				// ----------------------
				// If TargetStrategy.CircleOnSelf
				else if (missiles.get(i).effect.missileTarget.targetingStrategy == TargetingStrategy.CircleOnSelf)
				{
					TargetCircleOnSelf targetCircle = (TargetCircleOnSelf) missiles.get(i).effect.missileTarget;
					List<Enemy> enemiesInCircle = HitDetector.getEnemiesInCircle(enemies, (int) (missiles.get(i).getX() + missiles.get(i).getWidth() / 2), (int) (missiles.get(i).getY() + missiles.get(i).getHeight() / 2), targetCircle.radius);
					Iterator<String> it = missiles.get(i).effect.effects.keySet().iterator();
					while (it.hasNext())
					{
						String s = it.next();
						for (int eT = 0; eT < enemiesInCircle.size(); eT++)
						{
							Enemy targetEnemy = enemiesInCircle.get(eT);
							if (targetEnemy.superShieldedEnemy)
								continue;
							
							if (missiles.get(i).effect.effects.get(s).b)
							{
								targetEnemy.setStat(s, missiles.get(i).effect.effects.get(s).f);
							} else
							{
								targetEnemy.editStat(s, missiles.get(i).effect.effects.get(s).f);
							}
						}
					}
				}
				// ----------------------
				// If TargetStrategy.Line
				else if (missiles.get(i).effect.missileTarget.targetingStrategy == TargetingStrategy.Line)
				{
					TargetLine targetLine = (TargetLine) missiles.get(i).effect.missileTarget;
					List<Enemy> enemiesInLine = HitDetector.getEnemiesOnLine(enemies, new Vector2(targetLine.x1, targetLine.y1), new Vector2(targetLine.x2, targetLine.y2));
					Iterator<String> it = missiles.get(i).effect.effects.keySet().iterator();
					while (it.hasNext())
					{
						String s = it.next();
						for (int eT = 0; eT < enemiesInLine.size(); eT++)
						{
							Enemy targetEnemy = enemiesInLine.get(eT);
							if (targetEnemy.superShieldedEnemy)
								continue;
						
							if (missiles.get(i).effect.effects.get(s).b)
							{
								targetEnemy.setStat(s, missiles.get(i).effect.effects.get(s).f);
							}
							else
							{
								targetEnemy.editStat(s, missiles.get(i).effect.effects.get(s).f);
							}
						}
					}
				}
				// ----------------------

				missiles.get(i).remove();
				missiles.remove(i);
				i--;
			}
			// Complete Missile effects and damages.

		}

		stage.act(Gdx.graphics.getDeltaTime());
		
		if(diggerEnemies.size() > 0)
		{
			for(Enemy e : diggerEnemies)
			{
				int diggerX = (int)Math.floor(e.getX()/GameConstants.tileSize);
				int diggerY = (int)Math.floor(e.getY()/GameConstants.tileSize);
				diggerX = Math.max(0, diggerX);
				diggerX = Math.min(Map.mapWidth-1, diggerX);
				diggerY = Math.max(0, diggerY);
				diggerY = Math.min(Map.mapHeight-1, diggerY);
				if(!map.pathTiles.contains(map.getMap()[diggerX][diggerY]))
				{
					int newTile = map.pathTiles.get(rand.nextInt(map.pathTiles.size()));
					map.map[diggerX][diggerY] = newTile;
					map.mapActors[diggerX][diggerY].region = new TextureRegion(map.textures.get(newTile));
					e.offPath = true;
					e.lastChanged = new Vector2(diggerX, diggerY);
					map.generateDirections();
					if (waveTime.size() > 0)
					{
						for(float enemyTimes : waveTime)
						{
							if(!enemyWave.get(enemyTimes).willDigg)
								enemyWave.get(enemyTimes).generateDirections(map.directions);
						}
					}
					// Remove tower if in path
					List<Tower> removeTowerList = new ArrayList<Tower>();
					for (Tower t : towers)
					{
						if (t.getX() == diggerX * GameConstants.tileSize
								&& t.getY() == diggerY * GameConstants.tileSize)
						{
							removeTowerList.add(t);
						}
					}
					for (Tower tower : removeTowerList)
					{
						assetManager.playSound("towerDestroyed");
						towers.remove(tower);
						tower.remove();
					}
				}
			}
		}
		
		for (int counter = 0; counter < enemies.size(); counter++)
		{
			Enemy enemy = enemies.get(counter);
			if (enemy.getStat("currentHealth") <= 0)
			{
				enemies.remove(enemy);
				currentGold += enemy.getStat("currentGoldYield");
				hud.goldButton.setText("        " + currentGold);
				enemy.remove();
				counter--;
			} else if (enemy.getActions().size == 0)
			{
				if(enemy.willDigg)
				{
					map.findStartPoint();
					map.generateDirections();
				}
				enemies.remove(enemy);
				enemy.remove();
				livesLeft = livesLeft == 0 ? 0 : livesLeft - 1; 
				hud.livesButton.setText("" + livesLeft);
				counter--;
			}
		}
	}

	public boolean isGameWon()
	{
		if (enemies.size() <= 0 && enemyWave.size() <= 0)
			return true;
		else
			return false;
	}

	public boolean isGameLost()
	{
		if (livesLeft <= 0)
			return true;
		else
			return false;
	}

	public void resetGame(float nudgeChance, int startGold)
	{
		Random random = new Random();
		if (random.nextFloat() < nudgeChance)
			earthquakeEnabled = true;
		else
			earthquakeEnabled = false;

		towers.clear();
		enemies.clear();
		enemyWave.clear();
		waveTime.clear();
		currentGold = startGold;
		livesLeft = GameConstants.startLives;
		diggerEnemies.clear();
		lastMinionTime = 0;
	}

	private void addEnemyToWave(float time, Enemy enemy)
	{
		enemyWave.put(time, enemy);
		waveTime.add(time);
	}

	private Enemy createEnemy(String type, ThinkTank thinkTank, Map map,
			TextureAtlas enemiesAtlas, TextureAtlas miscAtlas)
	{
		List<Direction> directions = new ArrayList<Direction>();
		boolean digger = false;
		
		Vector2 startPoint = map.startPoint.cpy();
		if (type.equals("digger"))
		{
			if (rand.nextDouble() < 0.5)
			{
				directions = (List<Direction>) map.directions.clone();
				List<Direction> removedDirections = (List<Direction>) map.directions.clone();
				int randomNumber = rand.nextInt(directions.size());
				
				removedDirections =  removedDirections.subList(randomNumber, removedDirections.size());
				directions = directions.subList(0, randomNumber);
				for (Direction d : removedDirections)
				{
					if (d == Direction.Right)
						directions.add(Direction.Right);
				}
				directions.add(Direction.Right);
			}
			else
			{
				startPoint.y = rand.nextInt(map.mapHeight-1);
				for(int i = 0; i < map.mapWidth; i++)
					directions.add(Direction.Right);
			}
			digger = true;
		} else
			directions = map.directions;

		return new Enemy(thinkTank.enemyInfo.get(type), startPoint,
				directions, enemiesAtlas.createSprite(thinkTank.enemyInfo
						.get(type).enemyTexture), miscAtlas
						.createSprite("healthBarRed"), miscAtlas
						.createSprite("healthBarYellow"), miscAtlas
						.createSprite("super"), miscAtlas
						.createSprite("slowIcon"), miscAtlas
						.createSprite("DoTIcon"), digger);
	}

	private void doEarthquake(Camera gameCamera, Map map,
			float nudgeChanceConstant, AssetManager assetManager)
	{
		// Earthquake functionality, can be moved wherever, just to test.
		if (nudgeRemainingTime > 0)
		{
			nudgeRemainingTime -= Gdx.graphics.getDeltaTime();
			nudgeTimer += Gdx.graphics.getDeltaTime();
			if (nudgeTimer > 0.1)
			{
				if (tempNudge)
				{
					gameCamera.position.set(rand.nextInt(40 - 20)
							+ gameCamera.viewportWidth / 2, rand
							.nextInt(40 - 20)
							+ gameCamera.viewportHeight / 2, 0);
				} else
				{
					gameCamera.position.set(rand.nextInt(40 - 20)
							+ gameCamera.viewportWidth / 2, rand
							.nextInt(40 - 20)
							+ gameCamera.viewportHeight / 2, 0);
				}
				nudgeTimer = 0;
				if (movesTowers)
				{
					for (Tower t : towers)
					{
						double d = rand.nextDouble();
						if (d < 0.1)
						{
							if (d < 0.025
									&& map.canBuild((int) Math
											.round((t.getX() + GameConstants.tileSize)
													/ GameConstants.tileSize), (int) Math
											.round(t.getY()
													/ GameConstants.tileSize)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX()
											+ GameConstants.tileSize
											&& towers.get(c).getY() == t.getY())
										canMove = false;
								}
								if (canMove && !t.hasWall())
									t.setPosition(t.getX()
											+ GameConstants.tileSize, t.getY());
							} else if (d < 0.05
									&& map.canBuild((int) Math
											.round((t.getX() - GameConstants.tileSize)
													/ GameConstants.tileSize), (int) Math
											.round(t.getY()
													/ GameConstants.tileSize)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX()
											- GameConstants.tileSize
											&& towers.get(c).getY() == t.getY())
										canMove = false;
								}
								if (canMove && !t.hasWall())
								{
									t.setPosition(t.getX()
											- GameConstants.tileSize, t.getY());
								}
							} else if (d < 0.075
									&& map.canBuild((int) Math.round(t.getX()
											/ GameConstants.tileSize), (int) Math
											.round((t.getY() + GameConstants.tileSize)
													/ GameConstants.tileSize)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX()
											&& towers.get(c).getY() == t.getY()
													+ GameConstants.tileSize)
										canMove = false;
								}
								if (canMove && !t.hasWall())
									t.setPosition(t.getX(), t.getY()
											+ GameConstants.tileSize);
							} else if (map.canBuild((int) Math.round(t.getX()
									/ GameConstants.tileSize), (int) Math
									.round((t.getY() - GameConstants.tileSize)
											/ GameConstants.tileSize)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX()
											&& towers.get(c).getY() == t.getY()
													- GameConstants.tileSize)
										canMove = false;
								}
								if (canMove && !t.hasWall())
									t.setPosition(t.getX(), t.getY()
											- GameConstants.tileSize);

							}
						}
					}
				}
			}
			tempNudge = !tempNudge;

			if (nudgeRemainingTime <= 0)
			{
				gameCamera.position
						.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);
			}

			gameCamera.update();

		} else
		{
			nudges++;
			if(rand.nextDouble() < nudgeChanceConstant)
			{
				earthquakeEnabled = true;
			}
			else
			{
				earthquakeEnabled = false;
			}
			nudgeRandomizerTimer -= Gdx.graphics.getDeltaTime();
			if (nudgeRandomizerTimer <= 0)
			{
				if (rand.nextDouble() < nudgeChanceConstant)
				{
					nudgeRemainingTime = 1.5f;
					assetManager.playSound("earthquake");
				}
				nudgeRandomizerTimer = nudgeRandomizerInterval;
			}
		}
		// ---------------
	}
}
