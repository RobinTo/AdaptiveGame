package com.me.mygdxgame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class MyGdxGame implements ApplicationListener
{

	boolean earthquakeEnabled = true;
	boolean movesTowers = true;
	boolean tempNudge = false;
	float nudgeTimer = 0;
	float nudgeRemainingTime = 0f;
	float nudgeRandomizerTimer = 1.0f;
	float nudgeRandomizerInterval = 0.5f;

	float currentMinionDelay = 2.0f; // Will be set at random
	int waveSize = 10;
	int waveParts = 3;
	int waveIncrements = 5;
	int wavePartDelay = 7; // Seconds

	Group mapGroup;
	boolean wasTab = false;

	boolean fullScreen = false; // Full screen yes or no.
	boolean printDebug = true; // Print debug, add or remove writes in end of
								// render.

	ReplayHandler replayHandler = new ReplayHandler();
	boolean saveReplay = false;
	boolean useReplay = false;
	String replayPath = "/AdaptiveTD/Replays/testReplay.txt"; // Must be
																// external,
																// relative to
																// user
																// directory.
	String replaySavePath = "/AdaptiveTD/Replays/testReplay.txt";

	boolean savedParameters = false;
	String parameterSavePath = "/AdaptiveTD/Parameters/parameters.txt";

	boolean paused = true;
	boolean resuming = true;

	private float pauseTime = GameConstants.startTime;

	private static final int VIRTUAL_WIDTH = 1280;
	private static final int VIRTUAL_HEIGHT = 768;
	private static final float ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;
	private Rectangle viewport;

	HashMap<Float, Enemy> enemyWave = new HashMap<Float, Enemy>();
	List<Float> waveTime = new ArrayList<Float>();

	List<Enemy> enemies = new ArrayList<Enemy>();
	List<Tower> towers = new ArrayList<Tower>();

	List<Missile> missiles = new ArrayList<Missile>();
	Enemy focusFireEnemy;

	Tower selectedTower;
	
	HashMap<String, Sound> sounds = new HashMap<String, Sound>();

	

	Stage stage;
	ExtendedActor actor;

	Map map;
	TextureAtlas mapTilesAtlas; // Map tiles
	TextureAtlas enemiesAtlas; // Enemies
	TextureAtlas miscAtlas; // Various small stuff like bullets, health bar,
							// sell and upgrade buttons
	TextureAtlas towersAtlas; // Towers

	SpriteBatch spriteBatch;

	BitmapFont font;

	Sprite buildingTowerSprite = null;

	boolean building = false, wasTouched = false, won = false, lost = false;

	float totalTime = 0;

	double timer = 0;
	int uC = 0, uT = 0;

	Vector2 touchedTile = new Vector2(0, 0);

	String buildingTower = "", towerName = "Tower";

	Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);


	Camera gameCamera;

	ExtendedActor temporaryTowerActor = null;

	int livesLeft, currentGold;
	EventHandler eventHandler = new EventHandler();

	FeedbackTextInput listener = new FeedbackTextInput();

	ListenerGenerator listenerGenerator;
	ButtonGenerator buttonGenerator;
	ThinkTank thinkTank;
	Questionaire questionaire;
	View view;
	
	@Override
	public void create()
	{
		gameCamera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		Gdx.graphics.setTitle("Adaptive Tower Defense v0.001");
		spriteBatch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("Fonts/KatyBerry.fnt"), Gdx.files.internal("Fonts/KatyBerry_0.tga"), false);

		stage = new Stage();
		stage.setCamera(gameCamera);
		Gdx.input.setInputProcessor(stage);

		mapTilesAtlas = new TextureAtlas(Gdx.files.internal("Images/mapTiles.atlas"));
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("Images/enemies.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("Images/misc.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("Images/towers.atlas"));

		map = new Map(mapTilesAtlas);
		FileHandle handle = Gdx.files.internal("Maps/map.txt");
		mapGroup = map.loadMap(handle);
		stage.addActor(mapGroup);

		thinkTank = new ThinkTank();
		FileHandle towerHandle = Gdx.files.internal("Stats/towerStats.txt");
		try
		{
			thinkTank.towerInfo = StatsFetcher.loadTowerStats(towerHandle);
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileHandle enemyHandle = Gdx.files.internal("Stats/enemyStats.txt");
		thinkTank.enemyInfo = StatsFetcher.generateEnemyInfo(enemyHandle);

		createWave();

		livesLeft = GameConstants.startLives;
		currentGold = GameConstants.startGold;

		listenerGenerator = new ListenerGenerator(this);
		buttonGenerator = new ButtonGenerator();
		
		// UI Creation
		System.out.println("Generating UI");
		view = new View(font);
		view.createUI(miscAtlas, towersAtlas, thinkTank.towerInfo, stage, buttonGenerator, listenerGenerator);
		// -----------

		if (useReplay)
		{
			FileHandle replayHandle = Gdx.files.external(replayPath);
			replayHandler.loadReplay(replayHandle);
		}

		Gdx.gl.glClearColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, Color.GRAY.a);

		loadSounds();
		thinkTank.defaultEnemyInfo = StatsFetcher.generateEnemyInfo(enemyHandle);
		try
		{
			thinkTank.defaultTowerInfo = StatsFetcher.loadTowerStats(towerHandle);
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileHandle variableHandle = Gdx.files.external("/AdaptiveTD/Parameters.txt");
		thinkTank.initializeVariables(variableHandle);

		newGame();
	}

	private void newGame()
	{
		for (Actor actor : mapGroup.getChildren())
		{
			((MapTile) actor).setColor(thinkTank.variables.r, thinkTank.variables.g, thinkTank.variables.b, thinkTank.variables.a);
		}
	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}

	@Override
	public void render()
	{
		// clear previous frame
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (won || lost)
		{
			if (questionaire != null && Questionaire.happy > 0 && Questionaire.difficult > 0)
			{
				thinkTank.calculateVariables(Questionaire.happy, Questionaire.difficult);
				resetGame();
			}
		}
		if (!paused)
		{
			updateGame();
			// Draws game
			stage.draw();
		} else if (resuming && !won && !lost)
		{
			totalTime += Gdx.graphics.getDeltaTime();

			if (!useReplay)
			{
				handleInput();
				eventHandler.update();
			} else
			{
				eventHandler.events = replayHandler.playReplay(totalTime);
			}
			if (saveReplay)
			{
				replayHandler.addEvents(totalTime, eventHandler);
			}
			thinkTank.calculateNewParameters(0, currentGold, livesLeft, towers, eventHandler.events);
			handleEvents();
			view.updateYellowBoxPosition();
			// Draws game
			stage.draw();

			pauseTime -= Gdx.graphics.getDeltaTime();
			spriteBatch.begin();
			font.setScale(10);
			if (!Integer.toString((int) Math.ceil(pauseTime)).equals("0"))
				font.draw(spriteBatch, Integer.toString((int) Math.ceil(pauseTime)), GameConstants.screenWidth / 2 - 32, GameConstants.screenHeight / 2);
			font.setScale(1);
			spriteBatch.end();
			if (pauseTime <= 0)
				paused = false;
		}

		// Fps counter
		timer += Gdx.graphics.getDeltaTime();
		uC++;
		if (timer >= 1)
		{
			// System.out.println("FPS: " + uC);
			uC = 0;
			timer = 0;
		}

		if (Gdx.input.isKeyPressed(Keys.TAB) && !wasTab)
		{
			view.updateConsoleState(true, thinkTank.oldVariables, thinkTank.variables, thinkTank.thinkTankInfo);
			wasTab = true;
		} else if (!Gdx.input.isKeyPressed(Keys.TAB))
			wasTab = false;
		if (Gdx.input.isKeyPressed(Keys.X))
		{
			waveTime.clear();
			enemyWave.clear();
			for (Enemy e : enemies)
				stage.getActors().removeValue(e, true);
			enemies.clear();
		}

		if (won)
		{
			spriteBatch.begin();
			font.setScale(10);
			font.draw(spriteBatch, "Game won", GameConstants.screenWidth / 2 - 300, GameConstants.screenHeight / 2);
			// questionaire.draw(spriteBatch);
			font.setScale(1);
			spriteBatch.end();
		} else if (lost)
		{
			spriteBatch.begin();
			font.setScale(10);
			font.draw(spriteBatch, "Game lost", GameConstants.screenWidth / 2 - 300, GameConstants.screenHeight / 2);
			// questionaire.draw(spriteBatch);
			font.setScale(1);
			spriteBatch.end();
		}
	}

	private void doEarthquake()
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
					gameCamera.position.set(rand.nextInt(40 - 20) + gameCamera.viewportWidth / 2, rand.nextInt(40 - 20) + gameCamera.viewportHeight / 2, 0);
				} else
				{
					gameCamera.position.set(rand.nextInt(40 - 20) + gameCamera.viewportWidth / 2, rand.nextInt(40 - 20) + gameCamera.viewportHeight / 2, 0);
				}
				nudgeTimer = 0;
				if (movesTowers)
				{
					for (Tower t : towers)
					{
						double d = rand.nextDouble();
						if (d < 0.1)
						{
							if (d < 0.025 && map.canBuild((int) Math.round((t.getX()+64) / 64), (int) Math.round(t.getY() / 64)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX()+64 && towers.get(c).getY() == t.getY())
										canMove = false;
								}
								if(canMove)
									t.setPosition(t.getX() + 64, t.getY());
							}
							else if (d < 0.05 && map.canBuild((int) Math.round((t.getX()-64) / 64), (int) Math.round(t.getY() / 64)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX()-64 && towers.get(c).getY() == t.getY())
										canMove = false;
								}
								if(canMove)
								{
									t.setPosition(t.getX() - 64, t.getY());
								}
							}
							else if (d < 0.075 && map.canBuild((int) Math.round(t.getX() / 64), (int) Math.round((t.getY()+64) / 64)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX() && towers.get(c).getY() == t.getY()+64)
										canMove = false;
								}
								if(canMove)
									t.setPosition(t.getX(), t.getY()+64);
							}
							else if (map.canBuild((int) Math.round(t.getX() / 64), (int) Math.round((t.getY()-64) / 64)))
							{
								boolean canMove = true;
								for (int c = 0; c < towers.size(); c++)
								{
									if (towers.get(c).getX() == t.getX() && towers.get(c).getY() == t.getY()-64)
										canMove = false;
								}
								if(canMove)
									t.setPosition(t.getX(), t.getY()-64);
							}
						}
					}
				}
			}
			tempNudge = !tempNudge;

			if (nudgeRemainingTime <= 0)
				gameCamera.position.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);

			gameCamera.update();

		} else
		{
			nudgeRandomizerTimer -= Gdx.graphics.getDeltaTime();
			if (nudgeRandomizerTimer <= 0)
			{
				if (rand.nextDouble() < 0.1)
					nudgeRemainingTime = 1.5f;
				nudgeRandomizerTimer = nudgeRandomizerInterval;
			}
		}
		// ---------------
	}

	@Override
	public void resize(int width, int height)
	{

		float aspectRatio = (float) width / (float) height;
		float scale = 1f;
		Vector2 crop = new Vector2(0f, 0f);

		if (aspectRatio > ASPECT_RATIO)
		{
			scale = (float) height / (float) VIRTUAL_HEIGHT;
			crop.x = (width - VIRTUAL_WIDTH * scale) / 2f;
		} else if (aspectRatio < ASPECT_RATIO)
		{
			scale = (float) width / (float) VIRTUAL_WIDTH;
			crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
		} else
		{
			scale = (float) width / (float) VIRTUAL_WIDTH;
		}

		float w = (float) VIRTUAL_WIDTH * scale;
		float h = (float) VIRTUAL_HEIGHT * scale;

		Gdx.graphics.setDisplayMode((int) w, (int) h, fullScreen);

		viewport = new Rectangle(crop.x, crop.y, w, h);

		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		gameCamera.position.set(gameCamera.viewportWidth / 2, gameCamera.viewportHeight / 2, 0);

		gameCamera.update();
		gameCamera.apply(Gdx.gl10);

	}


	private void updateGame()
	{
		totalTime += Gdx.graphics.getDeltaTime();

		doEarthquake();

		if (!useReplay)
		{
			handleInput();
			eventHandler.update();
		} else
		{
			eventHandler.events = replayHandler.playReplay(totalTime);
		}
		if (saveReplay)
		{
			replayHandler.addEvents(totalTime, eventHandler);
		}
		if (!won && !lost)
			thinkTank.calculateNewParameters(totalTime, currentGold, livesLeft, towers, eventHandler.events);
		handleEvents();
		view.updateYellowBoxPosition();
		checkWave(totalTime);
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
						sounds.get(towers.get(i).towerStats.shootSound).play();
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
					Enemy targEnemy = ((TargetSingle) (missiles.get(i).effect.missileTarget)).targetEnemy;
					Iterator<String> it = missiles.get(i).effect.effects.keySet().iterator();
					while (it.hasNext())
					{
						String s = it.next();
						if (missiles.get(i).effect.effects.get(s).b)
						{
							targEnemy.setStat(s, missiles.get(i).effect.effects.get(s).f);
						} else
						{
							targEnemy.editStat(s, missiles.get(i).effect.effects.get(s).f);
						}
					}
					sounds.get(missiles.get(i).impactSound).play();
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
							if (missiles.get(i).effect.effects.get(s).b)
							{
								enemiesInCircle.get(eT).setStat(s, missiles.get(i).effect.effects.get(s).f);
							} else
							{
								enemiesInCircle.get(eT).editStat(s, missiles.get(i).effect.effects.get(s).f);
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
					sounds.get(missiles.get(i).impactSound).play();

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
							if (missiles.get(i).effect.effects.get(s).b)
							{
								enemiesInCircle.get(eT).setStat(s, missiles.get(i).effect.effects.get(s).f);
							} else
							{
								enemiesInCircle.get(eT).editStat(s, missiles.get(i).effect.effects.get(s).f);
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
							if (missiles.get(i).effect.effects.get(s).b)
							{
								enemiesInLine.get(eT).setStat(s, missiles.get(i).effect.effects.get(s).f);
							} else
							{
								enemiesInLine.get(eT).editStat(s, missiles.get(i).effect.effects.get(s).f);
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

		for (int counter = 0; counter < enemies.size(); counter++)
		{
			Enemy enemy = enemies.get(counter);
			if (enemy.getStat("currentHealth") <= 0)
			{
				enemies.remove(enemy);
				currentGold += enemy.getStat("currentGoldYield");
				view.goldButton.setText("        " + currentGold);
				enemy.remove();
				counter--;
			} else if (enemy.getActions().size == 0)
			{
				enemies.remove(enemy);
				enemy.remove();
				livesLeft--;
				view.livesButton.setText("" + livesLeft);
				counter--;
			}
		}
		checkWinConditions();

	}
	
	public void resetGame()
	{
		if (saveReplay && !useReplay)
		{
			FileHandle saveHandle = Gdx.files.external(replaySavePath);
			replayHandler.saveReplay(saveHandle);
		}
		replayHandler.events.clear();
		replayHandler.savingEvents.clear();
		// useReplay = false;

		if (useReplay)
		{
			FileHandle replayHandle = Gdx.files.external(replayPath);
			replayHandler.loadReplay(replayHandle);
		}

		stage.getActors().clear();

		towers.clear();
		enemies.clear();
		enemyWave.clear();
		waveTime.clear();
		view.towerKeys.clear();
		totalTime = 0;

		map = new Map(mapTilesAtlas);
		FileHandle handle = Gdx.files.internal("Maps/map.txt");
		mapGroup = map.loadMap(handle);
		stage.addActor(mapGroup);
		createWave();
		view.createUI(miscAtlas, towersAtlas, thinkTank.towerInfo, stage, buttonGenerator, listenerGenerator);

		currentGold = GameConstants.startGold;
		view.goldButton.setText("        " + currentGold);
		livesLeft = GameConstants.startLives;
		view.livesButton.setText("" + livesLeft);

		won = false;
		lost = false;

		thinkTank.clear();

		pauseTime = GameConstants.startTime;
		paused = true;
		resuming = true;

		if (questionaire != null)
		{
			questionaire.reset();
			questionaire = null;
		}

		lastMinionTime = 0;
		float statMultiplier = 1.0f;
		for (int t = 0; t < waveParts; t++)
		{
			for (int i = 0; i < waveSize + (t * waveIncrements); i++)
			{
				generateNextEnemy(statMultiplier);
			}
			statMultiplier += 0.25f;
			lastMinionTime += wavePartDelay;
		}
		view.updateConsoleState(false, thinkTank.oldVariables, thinkTank.variables, thinkTank.thinkTankInfo);
		newGame();
	}

	private void checkWave(float totalTime)
	{
		if (waveTime.size() > 0)
		{
			if (waveTime.get(0) <= totalTime)
			{
				stage.addActor(enemyWave.get(waveTime.get(0)));
				enemies.add(enemyWave.get(waveTime.get(0)));
				enemyWave.remove(waveTime.get(0));
				waveTime.remove(0);
				// generateNextEnemy();
			}
		}
	}

	private void createWave()
	{
		addEnemyToWave(0.1f, createEnemy("fast"));
		Collections.sort(waveTime);
	}

	private void addEnemyToWave(float time, Enemy enemy)
	{
		enemyWave.put(time, enemy);
		waveTime.add(time);
	}

	private Enemy createEnemy(String type)
	{
		return new Enemy(thinkTank.enemyInfo.get(type), map.startPoint, map.directions, enemiesAtlas.createSprite(thinkTank.enemyInfo.get(type).enemyTexture), miscAtlas.createSprite("healthBarRed"), miscAtlas.createSprite("healthBarYellow"));
	}

	public void buildTower(String type, Vector2 tilePosition)
	{
		eventHandler.queueEvent(new Event("build", (int) tilePosition.x, (int) tilePosition.y, type));
	}

	// Eventually take a towerInfo id, and create appropriate.
	private Tower createTower(String type, Vector2 tilePosition)
	{
		Tower t = new Tower(thinkTank.towerInfo.get(type), towersAtlas.createSprite(thinkTank.towerInfo.get(type).towerTexture), miscAtlas.createSprite(thinkTank.towerInfo.get(type).missileTexture));
		t.setPosition(tilePosition.x * GameConstants.tileSize, tilePosition.y * GameConstants.tileSize);
		return t;
	}

	public void selectTower(Tower t)
	{
		if (t != null)
		{
			List<String> textForBox = new ArrayList<String>();
			selectedTower = t;
			textForBox.add(t.towerStats.type);
			// nameLabel.setText(t.towerStats.type);

			textForBox.add(t.towerStats.description);
			// buildCostLabel.setText(t.towerStats.description);

			if (selectedTower.towerStats.upgradesTo.equals("null"))
			{
				textForBox.add("Upgrade: MAX");
				// upgradeCostLabel.setText("Upgrade: MAX");
			} else
			{
				int upgradeCost = thinkTank.towerInfo.get(selectedTower.towerStats.upgradesTo).buildCost - selectedTower.towerStats.buildCost;
				textForBox.add("Upgrade: " + upgradeCost);
				// upgradeCostLabel.setText("Upgrade: " + upgradeCost);
			}
			textForBox.add("Sell: " + selectedTower.towerStats.sellPrice);
			// uiLabelSellPrice.setText("Sell: " +
			// selectedTower.towerStats.sellPrice);
			view.fadeInYellowBox(t, textForBox);
		} else
		{
			selectedTower = null;
			view.yellowBoxLabel.setText("");
			view.fadeOutYellowBox();
		}
	}

	private void selectEnemy(Enemy e)
	{
		List<String> textForBox = new ArrayList<String>();

		textForBox.add(e.enemyStats.type);
		textForBox.add("Health: " + e.getStat("currentHealth"));
		textForBox.add("Yields: " + e.getStat("currentGoldYield"));
		view.fadeInYellowBox(e, textForBox);
	}

	private void handleEvents()
	{
		for (int i = 0; i < eventHandler.events.size(); i++)
		{
			Event e = eventHandler.events.get(i);
			if (e.eventType.equals("build"))
			{
				if (map.canBuild((int) e.x, (int) e.y))
				{

					Tower t = createTower(e.tower, new Vector2(e.x, e.y));
					int buildCost = thinkTank.towerInfo.get(e.tower).buildCost;
					boolean canAfford = currentGold >= buildCost ? true : false;

					boolean canBuild = canAfford;
					if (canAfford)
					{
						for (int c = 0; c < towers.size(); c++)
						{
							if (towers.get(c).getX() == t.getX() && towers.get(c).getY() == t.getY())
								canBuild = false;
						}
					}
					if (canBuild)
					{

						currentGold -= buildCost;
						view.goldButton.setText("        " + currentGold);
						stage.addActor(t);
						towers.add(t);
						selectTower(t);
					} else
					{
						t = null;
					}
				}
			} else if (e.eventType.equals("sell"))
			{

			} else if (e.eventType.equals("upgrade"))
			{
				for (int u = 0; u < towers.size(); u++)
				{
					if (e.x == (int) (towers.get(u).getX() / GameConstants.tileSize) && e.y == (int) (towers.get(u).getY() / GameConstants.tileSize))
					{
						if (!towers.get(u).towerStats.upgradesTo.equals("null"))
						{
							TowerStats newTowerStats = thinkTank.towerInfo.get(towers.get(u).towerStats.upgradesTo);
							towers.get(u).upgrade(newTowerStats, towersAtlas.createSprite(newTowerStats.towerTexture), miscAtlas.createSprite(newTowerStats.missileTexture));
							selectTower(towers.get(u));
						}
					}
				}
			}
		}
	}

	private void handleInput()
	{
		Vector2 input = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		input = stage.screenToStageCoordinates(input);
		Actor a = stage.hit(input.x, input.y, false);
		if (a != null && a.getClass() == MapTile.class)
		{
			touchedTile = new Vector2((float) Math.floor(a.getX() / 64), (float) Math.floor(a.getY() / 64));
		}
		if (wasTouched && !Gdx.input.isTouched())
		{
			if (building && temporaryTowerActor != null)
			{
				if (touchedTile.x <= 20 && touchedTile.y <= 10 && a != null && a.getClass() == MapTile.class)
					buildTower(buildingTower, touchedTile);
				building = false;
				temporaryTowerActor.remove();
				temporaryTowerActor = null;
			}
			wasTouched = false;
		}
		if (Gdx.input.isTouched())
		{
			if (temporaryTowerActor != null && a != null)
			{
				temporaryTowerActor.setPosition(a.getX(), a.getY());
			}
			wasTouched = true;
			Actor hit = stage.hit(Gdx.input.getX(), GameConstants.screenHeight - Gdx.input.getY(), false);
			if (hit != null && hit.getClass() == Tower.class)
			{
				Tower t = (Tower) hit;
				selectTower(t);
			} else if (hit != null && hit.getClass() == Enemy.class)
			{
				Enemy e = (Enemy) hit;
				selectEnemy(e);
			} else if (Gdx.input.justTouched())
			{
				view.fadeOutYellowBox();
			}
		} else if (temporaryTowerActor != null)
		{
			temporaryTowerActor.remove();
			temporaryTowerActor = null;
		}
	}

	private void loadSounds()
	{
		System.out.println("Loading sounds");

		Iterator<String> it = thinkTank.towerInfo.keySet().iterator();
		while (it.hasNext())
		{
			String s = it.next();
			if (!thinkTank.towerInfo.get(s).impactSound.equals(""))
				sounds.put(thinkTank.towerInfo.get(s).impactSound, Gdx.audio.newSound(Gdx.files.internal("sounds/" + thinkTank.towerInfo.get(s).impactSound)));
			if (!thinkTank.towerInfo.get(s).shootSound.equals(""))
				sounds.put(thinkTank.towerInfo.get(s).shootSound, Gdx.audio.newSound(Gdx.files.internal("sounds/" + thinkTank.towerInfo.get(s).shootSound)));
		}
	}

	private void checkWinConditions()
	{
		if (livesLeft <= 0)
		{
			// Loser
			lost = true;
			if (!savedParameters)
			{
				thinkTank.writeVariablesToDisk(new FileHandle(parameterSavePath));
				savedParameters = true;
			}
			questionaire = new Questionaire(miscAtlas.createSprite("thumbUp"), miscAtlas.createSprite("thumbDown"), miscAtlas.createSprite("thumbSide"), stage, font, buttonGenerator);
		} else if (enemies.size() <= 0 && enemyWave.size() <= 0)
		{
			// Winner
			won = true;
			// resetGame();
			if (!savedParameters)
			{
				thinkTank.writeVariablesToDisk(new FileHandle(parameterSavePath));
				savedParameters = true;
			}
			questionaire = new Questionaire(miscAtlas.createSprite("thumbUp"), miscAtlas.createSprite("thumbDown"), miscAtlas.createSprite("thumbSide"), stage, font, buttonGenerator);
		}
	}

	int nextEnemyGeneratedCounter = 0;
	float lastMinionTime = 0;
	Random rand = new Random();

	private void generateNextEnemy(float statMultiplier)
	{
		currentMinionDelay = rand.nextInt(30);
		currentMinionDelay /= 10.0f;
		float time = lastMinionTime + currentMinionDelay;
		lastMinionTime += currentMinionDelay; // Basically same as time
		Enemy e = null;
		double randValue = rand.nextDouble();

		if (randValue <= 0.50)
		{
			e = createEnemy("basic");
			e.modifyOriginalHealth(statMultiplier);
			e.modifyOriginalMoveSpeed(statMultiplier);
		} else if (randValue <= 0.75)
		{
			e = createEnemy("fast");
			e.modifyOriginalMoveSpeed(statMultiplier);
		} else
		{
			e = createEnemy("tough");
			e.modifyOriginalHealth(statMultiplier);
		}

		if (!waveTime.contains(time))
		{
			waveTime.add(time);
			enemyWave.put(time, e);
		} else
		{
			generateNextEnemy(statMultiplier);
		}
	}
	
	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}
