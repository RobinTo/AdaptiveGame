package com.me.mygdxgame;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyGdxGame implements ApplicationListener {
	
	HashMap<Float, Enemy> enemyWave = new HashMap<Float, Enemy>();
    List<Float> waveTime = new ArrayList<Float>();
	
	List<Enemy> enemies = new ArrayList<Enemy>();
	List<Tower> towers = new ArrayList<Tower>();
	
	Enemy focusFireEnemy;
	
	Tower selectedTower;

	HashMap<String, TowerStats> towerInfo = new HashMap<String, TowerStats>();
    HashMap<String, EnemyStats> enemyInfo = new HashMap<String, EnemyStats>();

	List<String> towerKeys = new ArrayList<String>();
	
	Stage stage;
	ExtendedActor actor;
	
	Map map;	
	TextureAtlas mapTilesAtlas;	// Map tiles
	TextureAtlas enemiesAtlas;	// Enemies
	TextureAtlas miscAtlas;		// Various small stuff like bullets, health bar, sell and upgrade buttons
	TextureAtlas towersAtlas;	// Towers
	
	SpriteBatch spriteBatch;
	
	BitmapFont font;
	
	String buildingTower = "";
	Sprite buildingTowerSprite = null;
	
	boolean building = false;
	
	float totalTime = 0;
	
	double timer = 0;
	int uC=0;
	int uT=0;
	
	boolean wasTouched = false;
	Vector2 touchedTile = new Vector2(0,0);
	
	String towerName = "Tower";
	String towerDamage = "10";
	String towerCost = "30";
	

	Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
	Label uiLabel;
	Label uiLabel2;
	Label uiLabel3;
	
	Camera gameCamera;

	MapTile temporaryTowerActor = null;
	
	boolean printDebug = true;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		//Gdx.graphics.setDisplayMode((int)w, (int)h, true);
		Gdx.graphics.setTitle("Adaptive Tower Defense v0.001");
		spriteBatch = new SpriteBatch();
		
		font = new BitmapFont();
		
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);

		mapTilesAtlas = new TextureAtlas(Gdx.files.internal("Images/mapTiles.atlas"));
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("Images/enemies.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("Images/misc.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("Images/towers.atlas"));
		
		map = new Map(mapTilesAtlas);
		FileHandle handle = Gdx.files.internal("Maps/map.txt");
		Group g = map.loadMap(handle);
		stage.addActor(g);


		FileHandle towerHandle = Gdx.files.internal("Stats/towerStats.txt");
		try {
			loadTowerStats(towerHandle);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileHandle enemyHandle = Gdx.files.internal("Stats/enemyStats.txt");
		generateEnemyInfo(enemyHandle);

		
		buildTower("arrow", new Vector2(1,1));
		createWave();
		
		// UI Creation
		towerKeys.addAll(towerInfo.keySet());
		for (int i = 0; i < towerKeys.size(); i++)
		{
			TextButton.TextButtonStyle arrowTowerButtonStyle = new TextButton.TextButtonStyle();
			System.out.println("Trying: " + towerInfo.get(towerKeys.get(i)).towerTexture1);
			TextureRegion upStyle = new TextureRegion(towersAtlas.createSprite(towerInfo.get(towerKeys.get(i)).towerTexture1));
			TextureRegion downStyle = new TextureRegion(towersAtlas.createSprite(towerInfo.get(towerKeys.get(i)).towerTexture1));
			arrowTowerButtonStyle.font = font;
			arrowTowerButtonStyle.up = new TextureRegionDrawable(upStyle);
			arrowTowerButtonStyle.down = new TextureRegionDrawable(downStyle);
			TextButton arrowTowerButton = new TextButton("", arrowTowerButtonStyle);
			final String currentKey = towerKeys.get(i);
			arrowTowerButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					building = true;
					buildingTower = towerInfo.get(currentKey).type;
					buildingTowerSprite = towersAtlas.createSprite(towerInfo.get(currentKey).towerTexture1);
					towerName = currentKey;
					temporaryTowerActor = new MapTile(towersAtlas.createSprite(towerInfo.get(towerName).towerTexture1), 64, 64);
					stage.addActor(temporaryTowerActor);
					uiLabel.setText(towerName);
					uiLabel2.setText("Damage: " + Integer.toString(towerInfo.get(currentKey).damage1));
					uiLabel3.setText("Cost: " + Integer.toString(towerInfo.get(currentKey).getBuildCost()));
					return true;
				}
			});
			arrowTowerButton.setPosition(10 + 10*i + 64*i, GameConstants.screenHeight - 100);
			stage.addActor(arrowTowerButton);
		}
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
		uiLabel = new Label(towerName, labelStyle);
		uiLabel.setPosition(800, GameConstants.screenHeight-40);
		stage.addActor(uiLabel);
		uiLabel2 = new Label(towerName, labelStyle);
		uiLabel2.setPosition(800, GameConstants.screenHeight-60);
		stage.addActor(uiLabel2);
		uiLabel3 = new Label(towerName, labelStyle);
		uiLabel3.setPosition(800, GameConstants.screenHeight-80);
		stage.addActor(uiLabel3);
		// -----------
		
	}

	@Override
	public void dispose() {
        stage.dispose();
	}

	@Override
	public void render() {	
        Gdx.gl.glClearColor(0, 0,255, 1);	
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        totalTime += Gdx.graphics.getDeltaTime();
        
        handleInput();
        checkWave(totalTime);
        if(enemies.size() > 0)
        {
        	for(int i = 0; i < towers.size(); i++)
        	{
        		towers.get(i).calculateTarget(Gdx.graphics.getDeltaTime(), enemies, null);
        	}
        }
		
        stage.act(Gdx.graphics.getDeltaTime());
        
        for (int counter = 0; counter < enemies.size(); counter++)
        {
        	Enemy enemy = enemies.get(counter);
        	if (enemy.currentHealth <= 0)
        	{
        		enemies.remove(enemy);
        		enemy.remove();
        		counter --;
        	}
        }

        stage.draw();
        spriteBatch.begin();
        /*if(building && touchedTile.x >= 0 && touchedTile.x < 20 && touchedTile.y >= 0 && touchedTile.y < 10)
        {
        	Vector2 newStage = stage.stageToScreenCoordinates(new Vector2(touchedTile.x*64, touchedTile.y*64));
        	spriteBatch.draw(buildingTowerSprite, newStage.x, newStage.y, 64, 64);
        }*/
        spriteBatch.end();
     
        timer += Gdx.graphics.getDeltaTime();
        uC++;
        if(timer >= 1)
        {
        	if(printDebug)
            {
            	System.out.println(getCameraX());
            	System.out.println(getCameraY());
            }
        	System.out.println(uC);
        	uC = 0;
        	timer = 0;
        }
       
	}

	@Override
	public void resize(int width, int height) {
        stage.setViewport(width, height, true);
		gameCamera = new OrthographicCamera(stage.getWidth(), stage.getHeight());
        stage.setCamera(gameCamera);
        gameCamera.translate(gameCamera.viewportWidth/2, gameCamera.viewportHeight/2, 0);
        gameCamera.update();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	private void checkWave(float totalTime)
	{
		if(waveTime.size() > 0)
		{
			if (waveTime.get(0) <= totalTime) {
				stage.addActor(enemyWave.get(waveTime.get(0)));
				enemies.add(enemyWave.get(waveTime.get(0)));
				enemyWave.remove(waveTime.get(0));
				waveTime.remove(0);
			}
		}
	}
	
	private int getCameraX()
	{
		return (int)(gameCamera.position.x - gameCamera.viewportWidth/2);
	}
	private int getCameraY()
	{
		return (int)(gameCamera.position.y - gameCamera.viewportHeight/2);
	}
	
	private void createWave()
	{
		addEnemyToWave(0.5f, createEnemy("basic"));
		addEnemyToWave(10.5f, createEnemy("fast"));
		addEnemyToWave(5.5f, createEnemy("tough"));
		Collections.sort(waveTime);
	}
	
	private void addEnemyToWave(float time, Enemy enemy)
	{
		enemyWave.put(time, enemy);
		waveTime.add(time);
	}
	
	// Eventually take an enemyInfo id, and create appropriate.
	private Enemy createEnemy(String type)
	{
		return new Enemy(enemyInfo.get(type), map.startPoint, map.directions, enemiesAtlas.createSprite(enemyInfo.get(type).enemyTexture), miscAtlas.createSprite("healthBarRed"), miscAtlas.createSprite("healthBarYellow"));
	}
	
	public void buildTower(String type, Vector2 tilePosition)
	{
		if(map.canBuild((int)tilePosition.x, (int)tilePosition.y))
		{
			Tower t = createTower(type, tilePosition);
			stage.addActor(t);
			towers.add(t);
			selectedTower = t;
		}
	}
	
	// Eventually take a towerInfo id, and create appropriate.
	private Tower createTower(String type, Vector2 tilePosition)
	{
		Tower t = new Tower(towerInfo.get(type), towersAtlas.createSprite(towerInfo.get(type).towerTexture1), towersAtlas.createSprite(towerInfo.get(type).towerTexture2), towersAtlas.createSprite(towerInfo.get(type).towerTexture3), miscAtlas.createSprite(towerInfo.get(type).missileTexture));
		t.setPosition(tilePosition.x * GameConstants.tileSize, tilePosition.y
				* GameConstants.tileSize);
		return t;
	}
	
	Vector2 justTouchedPos = new Vector2(0,0);
	Vector2 touchedMapPos = new Vector2(0,0);
	
	private void handleInput()
	{
		Vector2 input = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		input = stage.screenToStageCoordinates(input);
		Actor a = stage.hit(input.x, input.y, false);
		if(a != null && a.getClass() == MapTile.class)
		{
			touchedTile = new Vector2((float)Math.floor(a.getX()/64), (float)Math.floor(a.getY()/64)); 
		}
		if(wasTouched && !Gdx.input.isTouched())
		{
			if(building)
			{
				
				if(touchedTile.x<=20 && touchedTile.y<=10)
					buildTower(buildingTower, touchedTile);
				building = false;
				temporaryTowerActor.remove();
				temporaryTowerActor = null;
			}
			else
			{
				// Suggestion for drag functionality if we are to use viewport for android.
				int deltaX = (int)(justTouchedPos.x-Gdx.input.getX());
				int deltaY = (int)-(justTouchedPos.y-Gdx.input.getY());
				if(gameCamera.position.x-gameCamera.viewportWidth/2 + deltaX < 0)
					deltaX = -(int)(gameCamera.position.x - (gameCamera.viewportWidth/2));
				/* Klokken er 4 og denne delen blir bare rot, tar det senere :]
				else if(getCameraX()+gameCamera.viewportWidth + deltaX > GameConstants.screenWidth)
					deltaX = (int)(gameCamera.position.x - (GameConstants.screenWidth - (gameCamera.viewportWidth/2)));*/
				if(gameCamera.position.y-gameCamera.viewportHeight/2 + deltaY < 0)
					deltaY = -(int)(gameCamera.position.y - (gameCamera.viewportHeight/2));
				/* 
				else if(getCameraY()+gameCamera.viewportHeight + deltaY > GameConstants.screenHeight)
					deltaY = (int)(gameCamera.position.y - (GameConstants.screenHeight - (gameCamera.viewportHeight/2)));*/
				
				
				
				gameCamera.translate(deltaX, deltaY, 0);
				gameCamera.update();
			}
			wasTouched = false;
		}
		if(Gdx.input.isTouched())
		{
			if(Gdx.input.justTouched())
				justTouchedPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
			if(temporaryTowerActor != null && a != null)
				temporaryTowerActor.setPosition(a.getX(), a.getY());
			wasTouched = true;
			Actor hit = stage.hit(Gdx.input.getX(), GameConstants.screenHeight-Gdx.input.getY(), false);
			if(hit != null && hit.getClass() == Tower.class)
			{
				Tower t = (Tower)hit;
				selectedTower = t;
				uiLabel.setText(t.towerStats.type);
				uiLabel2.setText("Damage: " + Integer.toString(t.towerStats.damage1));
				uiLabel3.setText("Upgrade Cost: " + Integer.toString(t.towerStats.upgradeCost1));
			}
			else if(hit != null && hit.getClass() == Enemy.class)
			{
				Enemy e = (Enemy)hit;
				
				uiLabel.setText(e.enemyStats.type);
				uiLabel2.setText("Health: " + e.currentHealth);
				uiLabel3.setText("Yields: " + e.enemyStats.goldYield);
			}
		}
		else if(temporaryTowerActor != null)
		{
			temporaryTowerActor.remove();
			temporaryTowerActor = null;
		}
	}
	
	private void generateEnemyInfo(FileHandle handle) {
		// Eventually do in thinkTank with parameters
		// String type, int health, int speed, int goldYield, String enemyTexture, String redHealthBar, String yellowHealthBar

		List<String> fileContent = GameConstants.readRawTextFile(handle);
		int yCounter = 0;
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
	}
	
	private void loadTowerStats(FileHandle handle) throws NumberFormatException, ParseException {
		List<String> fileContent = GameConstants.readRawTextFile(handle);
		int yCounter = 0;
		System.out.println("Loaded file");
		
		// Localize for machines using . and machines using , as separators.
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("0.#");
		
		format.setDecimalFormatSymbols(symbols);
		for (int x = 0; x * 29 < fileContent.size(); x++) {
			String[] readStats = new String[29];
			for (int i = 0; i < 29; i++) {
				String s = fileContent.get(i + (29 * x));
				String[] split = s.split(":");
				readStats[i] = split[1];
			}
			towerInfo.put(
					readStats[0],
					new TowerStats(readStats[0], readStats[1], readStats[2], readStats[3], readStats[4],
							format.parse(readStats[5]).floatValue(), Integer
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
									.parseInt(readStats[16]), Float
									.parseFloat(readStats[17]), Float
									.parseFloat(readStats[18]), Float
									.parseFloat(readStats[19]), Integer
									.parseInt(readStats[20]), Integer
									.parseInt(readStats[21]),  Float
									.parseFloat(readStats[22]), Integer
									.parseInt(readStats[23]), Integer
									.parseInt(readStats[24]),  Float
									.parseFloat(readStats[25]), Integer
									.parseInt(readStats[26]), Integer
									.parseInt(readStats[27]), Float
									.parseFloat(readStats[28])));
		}
	}
}
