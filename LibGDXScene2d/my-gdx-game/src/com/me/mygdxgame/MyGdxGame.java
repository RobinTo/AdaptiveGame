package com.me.mygdxgame;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
	

	HashMap<String, TowerStats> towerInfo = new HashMap<String, TowerStats>();
    HashMap<String, EnemyStats> enemyInfo = new HashMap<String, EnemyStats>();
	
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
	
	boolean wasTouched = true;
	Vector2 touchedTile = new Vector2(0,0);
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		//Gdx.graphics.setDisplayMode((int)w, (int)h, true);
		spriteBatch = new SpriteBatch();
		
		font = new BitmapFont();
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		Texture text = new Texture(Gdx.files.internal("data/libgdx.png"));
		text.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Sprite sprite = new Sprite(text);
		sprite.setPosition(5, 5);


		mapTilesAtlas = new TextureAtlas(Gdx.files.internal("images/mapTiles.atlas"));
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("images/enemies.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("images/misc.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("images/towers.atlas"));
		
		map = new Map(mapTilesAtlas);
		try
		{
			map.loadMap(Gdx.files.getLocalStoragePath() + ".//bin//Maps/map.txt");
			System.out.println("Create done.");
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
			// Don't care for now
		}

		// Load TowerInfo
		try {
			this.loadTowerStats(Gdx.files.getLocalStoragePath()
					+ ".//bin//Stats/towerStats.txt");
			System.out.println("Create Stats done.");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage()+ "errorTowerInfo");
			// Don't care for now
		}

		// Load EnemyInfo
		try {
			this.generateEnemyInfo(Gdx.files.getLocalStoragePath()
					+ ".//bin//Stats/enemyStats.txt");
			System.out.println("Create Stats done.");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage() + "errorEnemyInfo");
			// Don't care for now
		}

		
		buildTower("arrow", new Vector2(1,1));
		createWave();
		
		// UI Creation
		Table uiTable = new Table();
		uiTable.setSize(GameConstants.screenWidth, 128);
		uiTable.setPosition(0, GameConstants.screenHeight-128);
		uiTable.setColor(Color.BLACK);
		TextButton.TextButtonStyle arrowTowerButtonStyle = new TextButton.TextButtonStyle();
		TextureRegion upStyle = new TextureRegion(towersAtlas.createSprite("arrowTower"));
		TextureRegion downStyle = new TextureRegion(towersAtlas.createSprite("arrowTower"));
		arrowTowerButtonStyle.font = font;
		arrowTowerButtonStyle.up = new TextureRegionDrawable(upStyle);
		arrowTowerButtonStyle.down = new TextureRegionDrawable(downStyle);
		TextButton arrowTowerButton = new TextButton("test", arrowTowerButtonStyle);
		arrowTowerButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				building = true;
				buildingTower = "arrow";
				buildingTowerSprite = towersAtlas.createSprite("arrowTower");
				return true;
			}
		});
		uiTable.add(arrowTowerButton);
		stage.addActor(uiTable);
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
        
        /* Remove commenting here for FPS
        timer += Gdx.graphics.getDeltaTime();
        uC++;
        if(timer >= 1)
        {
        	System.out.println(uC);
        	uC = 0;
        	timer = 0;
        }*/
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
        
        spriteBatch.begin();
        map.draw(spriteBatch);
        if(building)
        {
        	spriteBatch.draw(buildingTowerSprite, touchedTile.x*64, touchedTile.y*64);
        }
        spriteBatch.end();
        stage.draw();
        
	}

	@Override
	public void resize(int width, int height) {
        stage.setViewport(width, height, true);
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
				System.out.println("Spawned enemy: " + waveTime.get(0) + " at " + totalTime + " Size: " + stage.getActors().size);
				enemies.add(enemyWave.get(waveTime.get(0)));
				enemyWave.remove(waveTime.get(0));
				waveTime.remove(0);
			}
		}
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
		return new Enemy(enemyInfo.get(type), map.startPoint, map.directions, enemiesAtlas.createSprite(enemyInfo.get(type).enemyTexture));
	}
	
	public void buildTower(String type, Vector2 tilePosition)
	{
		Tower t = createTower(type, tilePosition);
		stage.addActor(t);
		towers.add(t);
	}
	
	// Eventually take a towerInfo id, and create appropriate.
	private Tower createTower(String type, Vector2 tilePosition)
	{
		Tower t = new Tower(towerInfo.get(type), towersAtlas.createSprite(towerInfo.get(type).towerTexture1), towersAtlas.createSprite(towerInfo.get(type).towerTexture2), towersAtlas.createSprite(towerInfo.get(type).towerTexture3), miscAtlas.createSprite(towerInfo.get(type).missileTexture));
		t.setPosition(tilePosition.x * GameConstants.tileSize, tilePosition.y
				* GameConstants.tileSize);
		return t;
	}
	
	private void handleInput()
	{
		touchedTile = new Vector2((float)Math.floor((Gdx.input.getX()/GameConstants.tileSize)), (float)Math.floor(((stage.getHeight()-Gdx.input.getY())/GameConstants.tileSize)));
		if(wasTouched && !Gdx.input.isTouched())
		{
			if(building)
			{
				if(touchedTile.x<=20 && touchedTile.y<=10)
					buildTower(buildingTower, touchedTile);
				building = false;
			}
		}
		if(Gdx.input.isTouched())
		{
			wasTouched = true;
		}
	}
	
	private void generateEnemyInfo(String path)
			throws IOException {
		// Eventually do in thinkTank with parameters
		// String type, int health, int speed, int goldYield, String enemyTexture, String redHealthBar, String yellowHealthBar

		Path readPath = Paths.get(path);
		Charset ENCODING = StandardCharsets.UTF_8;
		List<String> fileContent = Files.readAllLines(readPath, ENCODING);
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
	
	private void loadTowerStats(String path)
			throws IOException {
		Path readPath = Paths.get(path);
		Charset ENCODING = StandardCharsets.UTF_8;
		List<String> fileContent = Files.readAllLines(readPath, ENCODING);
		int yCounter = 0;
		System.out.println("Loaded file");
		for (int x = 0; x * 14 < fileContent.size(); x++) {
			String[] readStats = new String[14];
			for (int i = 0; i < 14; i++) {
				String s = fileContent.get(i + (14 * x));
				String[] split = s.split(":");
				readStats[i] = split[1];
			}
			towerInfo.put(
					readStats[0],
					new TowerStats(readStats[0], readStats[1], readStats[2], readStats[3], readStats[4],
							Float.parseFloat(readStats[5]), Integer
									.parseInt(readStats[6]), Integer
									.parseInt(readStats[7]), Integer
									.parseInt(readStats[8]), Integer
									.parseInt(readStats[9]), Integer
									.parseInt(readStats[10]), Integer
									.parseInt(readStats[11]), Integer
									.parseInt(readStats[12]), Integer
									.parseInt(readStats[13])));
		}
	}
}
