package com.me.mygdxgame;

import java.text.ParseException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class MyGdxGame implements ApplicationListener
{
	Group mapGroup;
	boolean wasTab = false, wasSpace = false, wasHotKey = false;

	boolean fullScreen = false; // Full screen yes or no.
	boolean printDebug = true; // Print debug, add or remove writes in end of render.

	ReplayHandler replayHandler = new ReplayHandler();
	boolean saveReplay = false;
	boolean useReplay = false;
	String replayPath = "/AdaptiveTD/Replays/testReplay.txt"; // Must be external, relative to user directory.
	String replaySavePath = "/AdaptiveTD/Replays/testReplay.txt";

	boolean savedParametersAndRelations = false;
	String logSavePath = "/AdaptiveTD/Log/Logfile.txt";
	String parameterSavePath = "/AdaptiveTD/Parameters/parameters.txt";
	String relationsSavePath = "/AdaptiveTD/Parameters/relations.txt";

	boolean paused = true;
	boolean resuming = true;

	private float startPauseTime = GameConstants.startTime;

	private static final int VIRTUAL_WIDTH = 1280;
	private static final int VIRTUAL_HEIGHT = 768;
	private static final float ASPECT_RATIO = (float) VIRTUAL_WIDTH
			/ (float) VIRTUAL_HEIGHT;
	private Rectangle viewport;

	Stage stage;

	Map map;

	Sprite qBG;
	Sprite heartNoSprite;

	SpriteBatch spriteBatch;

	boolean building = false, wasTouched = false, won = false, lost = false;

	float totalTime = 0;

	double timer = 0;
	int uC = 0, uT = 0;

	Vector2 touchedTile = new Vector2(0, 0);

	String buildingTower = "", towerName = "Tower";
	Sprite buildingTowerSprite = null;

	Camera gameCamera;

	ExtendedActor temporaryTowerActor = null;

	EventHandler eventHandler;
	ListenerGenerator listenerGenerator;
	ButtonGenerator buttonGenerator;
	ThinkTank thinkTank;
	Questionaire questionaire;
	boolean questionaireIsDisplayed = false;
	HeadsUpDisplay hud;
	GameProcessor gameProcessor;
	AssetManager assetManager;
	StatsFetcher statsFetcher;
	Logger logger;

	int lastKeyPressed = -10;

	@Override
	public void create()
	{
		eventHandler = new EventHandler();
		logger = new Logger();
		gameCamera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		Gdx.graphics.setTitle("Adaptive Tower Defense v0.001");
		spriteBatch = new SpriteBatch();

		assetManager = new AssetManager();
		assetManager.initialize();

		stage = new Stage();
		stage.setCamera(gameCamera);
		Gdx.input.setInputProcessor(stage);

		map = new Map(assetManager.mapTilesAtlas);
		FileHandle handle = Gdx.files.internal("Maps/map.txt");
		mapGroup = map.loadMap(handle);
		stage.addActor(mapGroup);

		thinkTank = new ThinkTank();

		gameProcessor = new GameProcessor();
		gameProcessor.initialize(thinkTank.thinkTankInfo.startGold, logger);
		
		statsFetcher = new StatsFetcher();
		FileHandle towerHandle = Gdx.files.internal("Stats/towerStats.txt");
		try
		{
			thinkTank.towerInfo = statsFetcher.loadTowerStats(towerHandle);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		FileHandle enemyHandle = Gdx.files.internal("Stats/enemyStats.txt");
		thinkTank.enemyInfo = statsFetcher.generateEnemyInfo(enemyHandle);

		listenerGenerator = new ListenerGenerator(eventHandler, gameProcessor, thinkTank);
		buttonGenerator = new ButtonGenerator();

		// UI Creation
		System.out.println("Generating UI");
		hud = new HeadsUpDisplay(assetManager.font);
		hud.createUI(assetManager.miscAtlas, assetManager.towersAtlas,
				thinkTank.towerInfo, stage, buttonGenerator, listenerGenerator,
				thinkTank.thinkTankInfo.startGold);
		// -----------

		if (useReplay)
		{
			FileHandle replayHandle = Gdx.files.external(replayPath);
			replayHandler.loadReplay(replayHandle);
		}

		Gdx.gl.glClearColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b,
				Color.GRAY.a);

		System.out.println("Loading sounds");
		assetManager.loadSounds(thinkTank.towerInfo);
		System.out.println("Loading music");
		assetManager.loadMusic();
		thinkTank.defaultEnemyInfo = statsFetcher
				.generateEnemyInfo(enemyHandle);
		try
		{
			thinkTank.defaultTowerInfo = statsFetcher
					.loadTowerStats(towerHandle);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		qBG = assetManager.largeAtlas.createSprite("qBG");

		FileHandle parameterHandle = Gdx.files.external(parameterSavePath);
		FileHandle relationsHandle = Gdx.files.external(relationsSavePath);
		thinkTank.initializeUpgradeCost();
		thinkTank.initializeParameters(parameterHandle);
		thinkTank.initializeRelations(relationsHandle);


		resetGame();
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
			if (questionaire != null && questionaire.happy > 0
					&& questionaire.difficult > 0)
			{
				thinkTank.calculateVariables(questionaire.happy,
						questionaire.difficult, gameProcessor.livesLeft);
				logger.lastHappy = questionaire.happy;
				logger.lastDifficult = questionaire.difficult;
				resetGame();
			}
		}
		if (!paused)
		{
			totalTime += Gdx.graphics.getDeltaTime();
			if (!useReplay)
			{
				handleInput();
				eventHandler.update();
			}
			else
			{
				eventHandler.events = replayHandler.playReplay(totalTime);
			}
			if (saveReplay)
			{
				replayHandler.addEvents(totalTime, eventHandler);
			}
			if (!won && !lost)
			{
				thinkTank.calculateVariety(totalTime, gameProcessor.towers);
			}
			handleEvents();
			hud.updateYellowBoxPosition();
			checkWave(totalTime);

			gameProcessor.updateGame(totalTime, gameCamera, map, assetManager,
					stage, hud, thinkTank.thinkTankInfo.nudgeChance, thinkTank.thinkTankInfo.nudgeChanceInGame);

			if (gameProcessor.isGameLost())
			{
				lost = true;
			}
			else if (gameProcessor.isGameWon())
			{
				won = true;
			}
			if (gameProcessor.isGameLost() || gameProcessor.isGameWon())
			{				
				if (!savedParametersAndRelations)
				{
					thinkTank.thinkTankInfo.successiveGameCounter++;
					logger.writeLogToDisk(Gdx.files.external(logSavePath), thinkTank.parameters, won, thinkTank.thinkTankInfo.successiveGameCounter,
							gameProcessor.livesLeft, gameProcessor.currentGold, thinkTank.thinkTankInfo.startGold, thinkTank.thinkTankInfo.currentMetric, thinkTank.thinkTankInfo.lastMetric, thinkTank.thinkTankInfo.challengerMetric, thinkTank.thinkTankInfo.maxJumpDistance, thinkTank.thinkTankInfo.playerLevel, thinkTank.thinkTankInfo.gameLengthMultiplier);
					thinkTank.writeParametersToDisk(Gdx.files
							.external(parameterSavePath));
					thinkTank.writeRelationsToDisk(Gdx.files
							.external(relationsSavePath));
					savedParametersAndRelations = true;
				}
				if (!questionaireIsDisplayed)
				{
					hud.setAllButtonsTouchable(Touchable.disabled);
					paused = true;
					questionaire = new Questionaire(qBG, assetManager.miscAtlas.createSprite("heartFeedbackNo"),
							assetManager.miscAtlas.createSprite("heartFeedback"), assetManager.miscAtlas.createSprite("thumbUp"),
							assetManager.miscAtlas.createSprite("thumbDown"), assetManager.miscAtlas.createSprite("thumbSide"), stage, assetManager.font,
							buttonGenerator);
					questionaireIsDisplayed = true;
				}
			}

			// Draws game
			stage.draw();
		}
		else if (resuming && !won && !lost)
		{
			totalTime += Gdx.graphics.getDeltaTime();

			if (!useReplay)
			{
				handleInput();
				eventHandler.update();
			}
			else
			{
				eventHandler.events = replayHandler.playReplay(totalTime);
			}
			if (saveReplay)
			{
				replayHandler.addEvents(totalTime, eventHandler);
			}
			thinkTank.calculateVariety(0, gameProcessor.towers);
			handleEvents();
			hud.updateYellowBoxPosition();
			// Draws game
			stage.draw();

			startPauseTime -= Gdx.graphics.getDeltaTime();
			spriteBatch.begin();
			assetManager.font.setScale(10);
			if (!Integer.toString((int) Math.ceil(startPauseTime)).equals("0"))
				assetManager.font.draw(spriteBatch,
						Integer.toString((int) Math.ceil(startPauseTime)),
						GameConstants.screenWidth / 2 - 32,
						GameConstants.screenHeight / 2);
			assetManager.font.setScale(1);
			spriteBatch.end();
			if (startPauseTime <= 0)
			{
				paused = false;
				resuming = false;
			}
		}
		else if (paused)
		{
			if (!useReplay)
			{
				handleInput();
				eventHandler.update();
			}
//			ReplayFunksjon
//			else
//			{
//				eventHandler.events = replayHandler.playReplay(totalTime);
//			}
//			if (saveReplay)
//			{
//				replayHandler.addEvents(totalTime, eventHandler);
//			}
			handleEvents();
			hud.updateYellowBoxPosition();

			stage.draw();
			spriteBatch.begin();
			assetManager.font.setScale(2);
			assetManager.font.draw(spriteBatch, "Press SPACE to continue",
					GameConstants.screenWidth * 2/3,
					GameConstants.screenHeight / 10);
			assetManager.font.setScale(1);
			spriteBatch.end();
		}

		if (questionaire != null)
		{
			spriteBatch.begin();
			questionaire.draw(spriteBatch);
			spriteBatch.end();
		}
		// Fps counter
		timer += Gdx.graphics.getDeltaTime();
		uC++;
		if (timer >= 1)
		{
			//System.out.println("FPS: " + uC);
			uC = 0;
			timer = 0;
		}

		handleHotKeys();

		assetManager.checkMusic();
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
		}
		else if (aspectRatio < ASPECT_RATIO)
		{
			scale = (float) width / (float) VIRTUAL_WIDTH;
			crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
		}
		else
		{
			scale = (float) width / (float) VIRTUAL_WIDTH;
		}

		float w = (float) VIRTUAL_WIDTH * scale;
		float h = (float) VIRTUAL_HEIGHT * scale;

		Gdx.graphics.setDisplayMode((int) w, (int) h, fullScreen);

		viewport = new Rectangle(crop.x, crop.y, w, h);

		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
				(int) viewport.width, (int) viewport.height);

		gameCamera.position.set(gameCamera.viewportWidth / 2,
				gameCamera.viewportHeight / 2, 0);

		gameCamera.update();
		gameCamera.apply(Gdx.gl10);
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

		gameProcessor.resetGame(thinkTank.thinkTankInfo.nudgeChance,
				thinkTank.thinkTankInfo.startGold);
		hud.towerKeys.clear();
		totalTime = 0;

		mapGroup = map.regenerateMap();
		stage.addActor(mapGroup);
		hud.createUI(assetManager.miscAtlas, assetManager.towersAtlas,
				thinkTank.towerInfo, stage, buttonGenerator, listenerGenerator,
				thinkTank.thinkTankInfo.startGold);

		hud.goldButton.setText("        " + gameProcessor.currentGold);
		hud.livesButton.setText("" + gameProcessor.livesLeft);

		won = false;
		lost = false;
		logger.resetCounters();

		thinkTank.clear();

		startPauseTime = GameConstants.startTime;
		paused = true;
		resuming = true;

		if (questionaire != null)
		{
			questionaire.reset();
			questionaire = null;
			questionaireIsDisplayed = false;
		}
		hud.setAllButtonsTouchable(Touchable.enabled);
		building = false;
		
		float statMultiplier = 1.0f;
		for (int t = 0; t < gameProcessor.waveParts; t++)
		{
			for (int i = 0; i < gameProcessor.waveSize
					+ (t * gameProcessor.waveIncrements); i++)
			{
				gameProcessor.generateNextEnemy(statMultiplier, thinkTank, map,
						assetManager.enemiesAtlas, assetManager.miscAtlas,
						thinkTank.thinkTankInfo.diggerChance, thinkTank.thinkTankInfo.superEnemyChance);
			}
			statMultiplier += 0.25f;
			gameProcessor.lastMinionTime += gameProcessor.wavePartDelay;
		}
		hud.updateConsoleState(false, thinkTank.parameters,
				thinkTank.thinkTankInfo);
		savedParametersAndRelations = false;

		float colorValue;
		if (thinkTank.parameters.get("GlobalMonsterHP").value <= 1.0f)
			colorValue = 1.0f;
		else if (thinkTank.parameters.get("GlobalMonsterHP").value < 2.7f)
		{
			colorValue = thinkTank.parameters.get("GlobalMonsterHP").value / 3.0f;
			colorValue = 1.0f - colorValue;
		}
		else
		{
			colorValue = 0.1f;
		}
		for (Actor actor : mapGroup.getChildren())
		{
			((MapTile) actor)
					.setColor(colorValue, colorValue, colorValue, 1.0f);
		}

		this.setSliderLevels();
		assetManager.playSong(thinkTank.thinkTankInfo.speedLevel);
	}

	private void checkWave(float totalTime)
	{
		if (gameProcessor.waveTime.size() > 0)
		{
			if (gameProcessor.waveTime.get(0) <= totalTime)
			{
				Enemy addEnemy = gameProcessor.enemyWave
						.get(gameProcessor.waveTime.get(0));
				if (addEnemy.willDigg)
				{
					gameProcessor.diggerEnemies.add(addEnemy);
					assetManager.playSound("diggerEnemy");
				}
				if (addEnemy.superFastEnemy || addEnemy.superInvisibleEnemy || addEnemy.superShieldedEnemy || addEnemy.superToughEnemy)
					assetManager.playSound("superEnemy");
				stage.addActor(addEnemy);
				gameProcessor.enemies.add(gameProcessor.enemyWave
						.get(gameProcessor.waveTime.get(0)));
				gameProcessor.enemyWave.remove(gameProcessor.waveTime.get(0));
				gameProcessor.waveTime.remove(0);
			}
		}
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

					Tower tower = gameProcessor.createTower(e.tower,
							new Vector2(e.x, e.y), assetManager.towersAtlas,
							assetManager.miscAtlas, thinkTank.towerInfo);
					int buildCost = thinkTank.towerInfo.get(e.tower).buildCost;
					boolean canAfford = gameProcessor.currentGold >= buildCost ? true
							: false;

					boolean canBuild = canAfford;
					if (canAfford)
					{
						for (int c = 0; c < gameProcessor.towers.size(); c++)
						{
							if (gameProcessor.towers.get(c).getX() == tower
									.getX()
									&& gameProcessor.towers.get(c).getY() == tower
											.getY())
								canBuild = false;
						}
						
						if (canBuild)
						{
							assetManager.playSound("buildTower");
							gameProcessor.currentGold -= buildCost;
							hud.goldButton.setText("        "
									+ gameProcessor.currentGold);
							stage.addActor(tower);
							gameProcessor.towers.add(tower);
							hud.fadeInYellowBox(tower, gameProcessor.selectTower(
									tower, thinkTank.towerInfo));
							hud.updateCostLabels(tower);
							gameProcessor.logger.towersBuilt++;
							if (tower.towerStats.type.startsWith("Arrow"))
								gameProcessor.logger.arrowTowers++;
							else if (tower.towerStats.type.startsWith("Frost"))
								gameProcessor.logger.frostTowers++;
							else if (tower.towerStats.type.startsWith("Cannon"))
								gameProcessor.logger.cannonTowers++;
							else if (tower.towerStats.type.startsWith("Flame"))
								gameProcessor.logger.flameTowers++;
							else if (tower.towerStats.type.startsWith("Burning"))
								gameProcessor.logger.burningTowers++;
							else
								gameProcessor.logger.laserTowers++;
						}
						else
						{
							tower = null;
							assetManager.playSound("maxedOut");
						}
						
					}
					else
					{
						assetManager.playSound("notEnoughMoney");
					}
				}
				else
					assetManager.playSound("maxedOut");
			}
			else if (e.eventType.equals("sell"))
			{
				for (int u = 0; u < gameProcessor.towers.size(); u++)
				{
					Tower tower = gameProcessor.towers.get(u);
					if (e.x == (int) (tower.getX() / GameConstants.tileSize) && e.y == (int) (tower.getY() / GameConstants.tileSize))
					{
						gameProcessor.currentGold += tower.towerStats.sellPrice;
						hud.goldButton.setText("        " + gameProcessor.currentGold);
						tower.remove();
						gameProcessor.towers.remove(tower);
						gameProcessor.selectTower(null, thinkTank.towerInfo);
						hud.yellowBoxLabel.setText("");
						hud.fadeOutYellowBox();
						assetManager.playSound("sellTower");
						gameProcessor.logger.towersSold++;
					}
				}
			}
			else if (e.eventType.equals("upgrade"))
			{
				for (int u = 0; u < gameProcessor.towers.size(); u++)
				{
					Tower tower = gameProcessor.towers.get(u);
					if (e.x == (int) (tower.getX() / GameConstants.tileSize)
							&& e.y == (int) (tower.getY() / GameConstants.tileSize))
					{
						if (tower.towerStats.upgradesTo.equals("null"))
						{
							assetManager.playSound("maxedOut");
							hud.fadeInYellowBox(tower, gameProcessor
									.selectTower(tower, thinkTank.towerInfo));
							hud.updateCostLabels(tower);
						}
						else
						{
							int upgradeCost = tower.towerStats.upgradeCost;
							boolean canAfford = gameProcessor.currentGold >= upgradeCost ? true
									: false;
							if (canAfford)
							{
								assetManager.playSound("upgradeTower");
								TowerStats newTowerStats = thinkTank.towerInfo
										.get(tower.towerStats.upgradesTo);
								tower.upgrade(
										newTowerStats,
										assetManager.towersAtlas
												.createSprite(newTowerStats.towerTexture),
										assetManager.miscAtlas
												.createSprite(newTowerStats.missileTexture));
								gameProcessor.currentGold -= upgradeCost;
								hud.goldButton.setText("        "
										+ gameProcessor.currentGold);
								hud.fadeInYellowBox(tower,
										gameProcessor.selectTower(tower,
												thinkTank.towerInfo));
								hud.updateCostLabels(tower);
								gameProcessor.logger.upgradesBought++;
							}
							else
							{
								assetManager.playSound("notEnoughMoney");
								hud.fadeInYellowBox(tower,
										gameProcessor.selectTower(tower,
												thinkTank.towerInfo));
								hud.updateCostLabels(tower);
							}
						}
					}
				}
			}
			else if (e.eventType.equals("wall"))
			{
				for (int u = 0; u < gameProcessor.towers.size(); u++)
				{
					Tower tower = gameProcessor.towers.get(u);
					if (e.x == (int) (tower.getX() / GameConstants.tileSize)
							&& e.y == (int) (tower.getY() / GameConstants.tileSize))
					{
						int wallCost = tower.towerStats.buildCost * 2;
						boolean canAfford = gameProcessor.currentGold >= wallCost ? true
								: false;
						if (!tower.wall)
						{
							if (canAfford)
							{
								gameProcessor.currentGold -= wallCost;
								hud.goldButton.setText("        "
										+ gameProcessor.currentGold);
								tower.wall = true;
								assetManager.playSound("upgradeTower");
								hud.fadeInYellowBox(tower,
										gameProcessor.selectTower(tower,
												thinkTank.towerInfo));
								hud.updateCostLabels(tower);
							}
							else
							{
								assetManager.playSound("notEnoughMoney");
								hud.fadeInYellowBox(tower,
										gameProcessor.selectTower(tower,
												thinkTank.towerInfo));
								hud.updateCostLabels(tower);
							}
						}
						else
						{
							assetManager.playSound("maxedOut");
							hud.fadeInYellowBox(tower, gameProcessor
									.selectTower(tower, thinkTank.towerInfo));
							hud.updateCostLabels(tower);
						}
					}
				}
			}
			else if (e.eventType.equals("selectTower"))
			{
				building = true;
				buildingTower = thinkTank.towerInfo.get(e.tower).type;
				buildingTowerSprite = assetManager.towersAtlas.createSprite(thinkTank.towerInfo.get(e.tower).towerTexture);
				towerName = e.tower;
				temporaryTowerActor = new MapTile(assetManager.towersAtlas.createSprite(thinkTank.towerInfo.get(towerName).towerTexture), -64, 0);
				stage.addActor(temporaryTowerActor);
				hud.yellowBoxLabel.setText(towerName);
			}
			else if (e.eventType.equals("clean"))
			{
				thinkTank.clean(parameterSavePath, relationsSavePath);
				resetGame();
			}
		}
	}

	private void handleInput()
	{
		if (won || lost)
			return;
		Vector2 input = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		input = stage.screenToStageCoordinates(input);
		Actor a = stage.hit(input.x, input.y, true);
		if (a != null && a.getClass() == MapTile.class)
		{
			touchedTile = new Vector2((float) Math.floor(a.getX()
					/ GameConstants.tileSize), (float) Math.floor(a.getY()
					/ GameConstants.tileSize));
		}
		if (wasTouched && !Gdx.input.isTouched())
		{
			if (building && temporaryTowerActor != null)
			{
				if (touchedTile.x <= Map.mapWidth
						&& touchedTile.y <= Map.mapHeight && a != null
						&& a.getClass() == MapTile.class)
					eventHandler.queueEvent(new Event("build",
							(int) touchedTile.x, (int) touchedTile.y,
							buildingTower));
				building = false;
				temporaryTowerActor.remove();
				temporaryTowerActor = null;
			}
			wasTouched = false;
		}
		if (building)
		{
			if (temporaryTowerActor != null && a != null)
			{
				temporaryTowerActor.setPosition(a.getX(), a.getY());
			}
		}
		if (Gdx.input.isTouched())
		{
			wasTouched = true;
			Actor hit = stage.hit(Gdx.input.getX(), GameConstants.screenHeight
					- Gdx.input.getY(), false);
			if (hit != null && hit.getClass() == Tower.class)
			{
				Tower tower = (Tower) hit;
				hud.fadeInYellowBox(tower,
						gameProcessor.selectTower(tower, thinkTank.towerInfo));
				hud.updateCostLabels(tower);

			}
			else if (Gdx.input.justTouched())
			{
				hud.fadeOutYellowBox();
				hud.updateCostLabels("");
				gameProcessor.selectedTower = null;
			}
		}
		else if (temporaryTowerActor != null && !building)
		{
			temporaryTowerActor.remove();
			temporaryTowerActor = null;
		}
	}

	private void setSliderLevels()
	{
		float healthValue = thinkTank.parameters.get("GlobalMonsterHP").value;
		if (healthValue < 0.2f)
			hud.healthSlider.setValue(1);
		else if (healthValue < 0.35f)
			hud.healthSlider.setValue(2);
		else if (healthValue < 0.55f)
			hud.healthSlider.setValue(3);
		else if (healthValue < 0.8f)
			hud.healthSlider.setValue(4);
		else if (healthValue < 1.1f)
			hud.healthSlider.setValue(5);
		else if (healthValue < 1.4f)
			hud.healthSlider.setValue(6);
		else if (healthValue < 1.7f)
			hud.healthSlider.setValue(7);
		else if (healthValue < 2.1)
			hud.healthSlider.setValue(8);
		else if (healthValue < 2.5f)
			hud.healthSlider.setValue(9);
		else
			hud.healthSlider.setValue(10);

		hud.speedSlider.setValue(thinkTank.thinkTankInfo.speedLevel);

		float damageValue = thinkTank.parameters.get("TEDamage").value;
		if (damageValue < 0.10f)
			hud.damageSlider.setValue(1);
		else if (damageValue < 0.35f)
			hud.damageSlider.setValue(2);
		else if (damageValue < 0.60f)
			hud.damageSlider.setValue(3);
		else if (damageValue < 0.85f)
			hud.damageSlider.setValue(4);
		else if (damageValue < 1.10f)
			hud.damageSlider.setValue(5);
		else if (damageValue < 1.35f)
			hud.damageSlider.setValue(6);
		else if (damageValue < 1.60f)
			hud.damageSlider.setValue(7);
		else if (damageValue < 1.85f)
			hud.damageSlider.setValue(8);
		else if (damageValue < 2.10f)
			hud.damageSlider.setValue(9);
		else
			hud.damageSlider.setValue(10);
	}

	private void handleHotKeys()
	{
		if (won || lost)
			return;
		if (Gdx.input.isKeyPressed(Keys.TAB) && !wasTab)
		{
			hud.updateConsoleState(true, thinkTank.parameters,
					thinkTank.thinkTankInfo);
			wasTab = true;
		}
		else if (!Gdx.input.isKeyPressed(Keys.TAB))
			wasTab = false;
		if (!won && !lost) // Keys in here cannot be pressed if game is over
		{
			if (Gdx.input.isKeyPressed(Keys.X))
			{
				gameProcessor.waveTime.clear();
				gameProcessor.enemyWave.clear();
				for (Enemy e : gameProcessor.enemies)
					stage.getActors().removeValue(e, true);
				gameProcessor.enemies.clear();

			}
			if (Gdx.input.isKeyPressed(Keys.NUM_1) && !wasHotKey)
			{
				if (temporaryTowerActor != null)
				{
					temporaryTowerActor.remove();
					temporaryTowerActor = null;
				}
				eventHandler.queueEvent(new Event("selectTower", 0,0,hud.towerKeys.get(0)));
				wasHotKey = true;
			}
			else if (Gdx.input.isKeyPressed(Keys.NUM_2) &&!wasHotKey)
			{
				if (temporaryTowerActor != null)
				{
					temporaryTowerActor.remove();
					temporaryTowerActor = null;
				}
				eventHandler.queueEvent(new Event("selectTower", 0,0,hud.towerKeys.get(1)));
				wasHotKey = true;
			}
			else if (Gdx.input.isKeyPressed(Keys.NUM_3) &&!wasHotKey)
			{
				if (temporaryTowerActor != null)
				{
					temporaryTowerActor.remove();
					temporaryTowerActor = null;
				}
				eventHandler.queueEvent(new Event("selectTower", 0,0,hud.towerKeys.get(2)));
				wasHotKey = true;
			}
			else if (Gdx.input.isKeyPressed(Keys.NUM_4) &&!wasHotKey)
			{
				if (temporaryTowerActor != null)
				{
					temporaryTowerActor.remove();
					temporaryTowerActor = null;
				}
				eventHandler.queueEvent(new Event("selectTower", 0,0,hud.towerKeys.get(3)));
				wasHotKey = true;
			}
			else if (Gdx.input.isKeyPressed(Keys.NUM_5) &&!wasHotKey)
			{
				if (temporaryTowerActor != null)
				{
					temporaryTowerActor.remove();
					temporaryTowerActor = null;
				}
				eventHandler.queueEvent(new Event("selectTower", 0,0,hud.towerKeys.get(4)));
				wasHotKey = true;
			}
			else if (Gdx.input.isKeyPressed(Keys.NUM_6) &&!wasHotKey)
			{
				if (temporaryTowerActor != null)
				{
					temporaryTowerActor.remove();
					temporaryTowerActor = null;
				}
				eventHandler.queueEvent(new Event("selectTower", 0,0,hud.towerKeys.get(5)));
				wasHotKey = true;
			}
			else if (!Gdx.input.isKeyPressed(Keys.ANY_KEY))
			{
				wasHotKey = false;
			}
			if (!resuming) // Keys in here cannot be pressed during countdown
			{
				if (Gdx.input.isKeyPressed(Keys.SPACE) && !wasSpace)
				{
					paused = !paused;
					wasSpace = true;
				}
				else if (!Gdx.input.isKeyPressed(Keys.SPACE))
					wasSpace = false;
			}
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
