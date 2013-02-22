package no.uia.adaptiveTD;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class AdaptiveTD implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private BitmapFont font;

	boolean saveReplay = false; 	// Not added functionality yet
	boolean saved = false;
	//ReplayHandler replayHandler;
	boolean useReplay = false;
	boolean savedParameters = false;
	
	HashMap<String, TowerStats> towerInfo = new HashMap<String, TowerStats>();
    HashMap<String, EnemyStats> enemyInfo = new HashMap<String, EnemyStats>();
    
    HashMap<Float, Enemy> enemyWave = new HashMap<Float, Enemy>();
    List<Float> waveTime = new ArrayList<Float>();
    
    Sprite targetingCircle;
    EventHandler1 eventHandler = new EventHandler1();
    
    List<Enemy> enemies = new ArrayList<Enemy>();
    Enemy targetEnemy;
    Enemy selectedEnemy;
    
    // WaveHandler waveHandler = new WaveHandler(); // Not implemented yet, loads and saves waves.
	
    HashMap<Float, String> enemyBaseWave = new HashMap<Float, String>(); // Used when waveHandler loads, to identify enemy types as saved in enemyStats by Strings
    
    List<TowerClass> towers = new ArrayList<TowerClass>();
    List<Missile> missiles = new ArrayList<Missile>();
    TowerClass selectedTower;
    
    boolean gameOver = false;
    boolean paused = false;
    int startGold = 50;
    int currentGold = startGold;
    
    int startingLives = 5;
    int currentLives = startingLives;
    
    
    
	boolean unlockFrames = false;
	boolean onlyUpdates = false;
	float w, h;
	CharSequence UPS = "0";
	CharSequence FPS = "0";
	
	Vector3 touchPos = new Vector3();
	
	Map map;
	
	TextureAtlas mapTilesAtlas;	// Map tiles
	TextureAtlas enemiesAtlas;	// Enemies
	TextureAtlas miscAtlas;		// Various small stuff like bullets, health bar, sell and upgrade buttons
	TextureAtlas towersAtlas;	// Towers

	float totalGameTime = 0;
	
	// Fps and Ups counters
	int updateC = 0;
	int updateT = 0;
	double timer = 1.0;
	int frameC = 0;
	int frameT = 0;
	double frameTimer = 0;
	
	@Override
	public void create() {
		System.out.println("Creating.");
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		if (unlockFrames)
			Gdx.graphics.setVSync(false);

		camera = new OrthographicCamera(1, h / w);
		camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();

		font = new BitmapFont();

		mapTilesAtlas = new TextureAtlas(Gdx.files.internal("images/mapTiles.atlas"));
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("images/mapTiles.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("images/mapTiles.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("images/mapTiles.atlas"));
		
		map = new Map(mapTilesAtlas);
		
		Vector3 touchPos;
		// Loading of map, currently an index out of bounds exception.
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
		
		createWave();
		
		// towerInfo = readTowerInfo();
		// enemyIno = readEnemyInfo();
		
		if(useReplay/* && file.exists(replayFilePath)*/)
		{
			// replayHandler.LoadReplay(replayFilePath);
		}
		else // In case file does not exist.
			useReplay = false;
	}

	@Override
	public void dispose() {
		batch.dispose();
		mapTilesAtlas.dispose();
		enemiesAtlas.dispose();
		miscAtlas.dispose();
		towersAtlas.dispose();
	}


	@Override
	public void render() {

		float gameTime = Gdx.graphics.getDeltaTime();
		frameTimer += gameTime;
		timer -= frameTimer;
		update((float) (gameTime - frameTimer));
		updateC++;
		if (!onlyUpdates)
			draw();
		frameC++;
		frameTimer = 0;

		if (timer <= 0.0) {
			updateT = updateC;
			frameT = frameC;
			updateC = 0;
			frameC = 0;
			timer = 1.0;
		}
		FPS = Integer.toString(frameT);
		UPS = Integer.toString(updateT);
	}

	private void update(float gameTime) {
		
        if (targetEnemy != null && targetEnemy.getCurrentHealth() <= 0)
            targetEnemy = null;
		
        if(!gameOver && !paused)
        {
        	if(useReplay)
        	{
        		// Set events from replay using replayHandler.
        	}
        	else
        	{
        		eventHandler.newRound();
        		handleInput();
        	}
        	
        	totalGameTime += gameTime;
        	
        	if(!enemyWave.isEmpty() && !waveTime.isEmpty())
        	{
        		if(totalGameTime > waveTime.get(0))
        		{
        			enemies.add(enemyWave.get(waveTime.get(0)));
        			enemyWave.remove(waveTime.get(0));
        			waveTime.remove(0);
        		}
        	}
        	if(saveReplay)
        	{
        		
        	}
        	
        }
	}

	private void draw() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		map.draw(batch); // Needs to fix loadMap before this can be run.
		font.draw(batch, FPS, 10, h - 10);
		font.draw(batch, UPS, 10, h - 20);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {

	}
	
	private void createWave()
	{
		// waveHandler.loadWave(waveFile);
		// Add waves to enemyWave and float times also to waveTimer
		Collections.sort(waveTime); // Thus waveTime.get(0) will be lowest, waveTime.get(1) next and so on.
	}
	
	private void handleInput()
	{
		if(Gdx.input.isTouched())
		{
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
		}
	}
}
