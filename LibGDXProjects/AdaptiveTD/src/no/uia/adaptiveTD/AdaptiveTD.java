package no.uia.adaptiveTD;

import java.io.Console;
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
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
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
    
    List<Tower1> towers = new ArrayList<Tower1>();
    List<Missile> missiles = new ArrayList<Missile>();
    Tower1 selectedTower;
    
    boolean gameOver = false;
    boolean won = false;
    boolean paused = false;
    int startGold = 50;
    int currentGold = startGold;
    
    int startingLives = 5;
    int currentLives = startingLives;
    
    Sprite currentlyBuildingTower;
    Sprite rangeHighlight;
    
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
		enemiesAtlas = new TextureAtlas(Gdx.files.internal("images/enemies.atlas"));
		miscAtlas = new TextureAtlas(Gdx.files.internal("images/misc.atlas"));
		towersAtlas = new TextureAtlas(Gdx.files.internal("images/towers.atlas"));
		
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
		
		// Load TowerInfo
		try {
			this.loadTowerStats(Gdx.files.getLocalStoragePath()
					+ ".//bin//Stats/towerStats.txt");
			System.out.println("Create Stats done.");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			// Don't care for now
		}

		// Load EnemyInfo
		try {
			this.generateEnemyInfo(Gdx.files.getLocalStoragePath()
					+ ".//bin//Stats/enemyStats.txt");
			System.out.println("Create Stats done.");
		} catch (IOException ioe) {
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
		
		update((float) (gameTime));
		updateC++;
		if (!onlyUpdates)
			draw();
		frameC++;
		timer-= gameTime;
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
        		if(totalGameTime >= waveTime.get(0))
        		{
        			enemies.add(enemyWave.get(waveTime.get(0)));
        			enemyWave.remove(waveTime.get(0));
        			waveTime.remove(0);
        		}
        	}
        	if(saveReplay)
        	{
        		//replayHandler.update(totalGameTime, gameTime, eventHandler.getEvents());
        	}
        	
        	handleEvents();
        	
        	for(int i = 0; i < enemies.size(); i++)
        	{
        		enemies.get(i).Update(gameTime);
        		if(enemies.get(i).getCurrentHealth() <= 0)
        		{
        			currentGold += enemies.get(i).getEnemyInfo().getGoldYield();
        			enemies.remove(i);
        			i--;
        		}
        		else if(enemies.get(i).getPosition().x >= GameConstants.screenWidth)
        		{
        			currentLives--;
        			enemies.remove(i);
        			i--;
        		}
        	}
        	for(int i = 0; i < towers.size(); i++)
        	{
        		if(enemies.size() > 0)
        		{
        			towers.get(i).Update(gameTime, enemies, targetEnemy, missiles);
        			/* if(gui.building)
        			 * {
        			 * 	t.color = Color.WHITE;
        			 * }
        			 */
        		}
        	}
        	
        	for(int i = 0; i < missiles.size(); i++)
        	{
        		missiles.get(i).Update(gameTime, enemies);
        		if(missiles.get(i).remove)
        		{
        			missiles.remove(i);
        			i--;
        		}
        	}
        	
        	// gui.Update(gameTime, input, currentLives, currentGold, selectedTower, eventHandler);
        	
        	if(enemies.size() <= 0 && enemyWave.isEmpty())
        	{
        		gameOver = true;
        		won = true;
        	}

            if (gameOver)
            {
                if (saveReplay && !saved && !useReplay)
                {
                    //replayHandler.SaveReplay();
                    saved = true;
                }
                // input.Update(); // Take some input
                if (Gdx.input.isKeyPressed(Keys.ENTER))
                    restartGame();
                
                // Save parameters do fancy thinktank calculations.
            }
        }
	}

	private void draw() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		map.draw(batch);
		
		for(int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).Draw(batch);
		}
		for(int i = 0; i < missiles.size(); i++)
		{
			missiles.get(i).Draw(batch);
		}
		for(int i = 0; i < towers.size(); i++)
		{
			towers.get(i).Draw(batch);
		}
		
		if(targetEnemy != null)
			batch.draw(targetingCircle, targetEnemy.getPosition().x, targetEnemy.getPosition().y);
	
		if(/*gui.Building*/false)
		{
			Vector2 position = new Vector2((float)Math.floor(touchPos.y / GameConstants.tileSize), (float)Math.floor(touchPos.y / GameConstants.tileSize));
			batch.draw(currentlyBuildingTower, position.x, position.y);
			batch.draw(rangeHighlight, position.x*GameConstants.tileSize + GameConstants.tileSize/2-500/*RANGE*/, position.y * GameConstants.tileSize + GameConstants.tileSize/2 - 500/*RANGE*/, 0, 0, 500.0f*2.0f/*RANGE*2*/, 500.0f*2.0f/*RANGE*2*/, 1.0f, 1.0f, 0.0f);
		}
		
		if(selectedTower != null)
			batch.draw(rangeHighlight, selectedTower.getRangeHighlightRectangle().x, selectedTower.getRangeHighlightRectangle().y, selectedTower.getRangeHighlightRectangle().width, selectedTower.getRangeHighlightRectangle().height);
		
		// gui.draw(batch);
		
		if(currentLives <= 0)
		{
			// WinPopup.draw(false, batch);
			gameOver = true;
		}
		else if(gameOver)
		{
			// WinPopup.draw(true, batch);
		}
		
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
	
	private void generateEnemyInfo(String path)
			throws IOException {
		// Eventually do in thinkTank with parameters
		enemyInfo.put("basic", new EnemyStats("basic", "testEnemy", 20, 64, 5));
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

	private void createWave()
	{
		// waveHandler.loadWave(waveFile);
		// Add waves to enemyWave and float times also to waveTimer
		spawnEnemy(0.0f, "basic");
		spawnEnemy(0.5f, "tough");
		spawnEnemy(1.0f, "fast");
		Collections.sort(waveTime); // Thus waveTime.get(0) will be lowest, waveTime.get(1) next and so on.
	}
	
	private void spawnEnemy(float time, String enemyType)
	{
		System.out.println(enemyInfo.get(enemyType).goldYield);
		enemyWave.put(time, new Enemy(map.startPoint, enemyInfo.get(enemyType), map.getDirections(), enemiesAtlas.createSprite(enemyInfo.get(enemyType).getEnemyTexture()), miscAtlas.createSprite("healthBarRed"), miscAtlas.createSprite("healthBarYellow")));
		waveTime.add(time);
	}
	
	private void handleInput()
	{
		if(Gdx.input.isTouched())
		{
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
		}
	}

	private void handleEvents()
	{
		List<Event> events = eventHandler.getEvents();
		for(int i = 0; i < events.size(); i++)
		{
			Event e = events.get(i);
			if(e.type == EventType.Build)
			{
				buildTower(towerInfo.get(e.getTowerType()), e.getTilePosition());
			}
			else if(e.type == EventType.Sell)
			{
				for(int t = 0; t<towers.size(); t++)
                {
                    if (towers.get(t).getTilePosition() == e.getTilePosition())
                    {
                        selectedTower = null;
                        currentGold += towerInfo.get(towers.get(t).getTowerStats().getType()).getBuildCost() / 2;
                        towers.remove(t);
                    }
                }
			}
			else if(e.type == EventType.Upgrade)
			{
				for (int t = 0; t < towers.size(); t++)
                {
                    if (towers.get(t).getTilePosition() == e.getTilePosition())
                    {
                        upgradeTower(e.getTilePosition());
                    }
                }
			}
			else if(e.type == EventType.FocusFire)
			{
				
			}
		}
	}
	
	private void buildTower(TowerStats type, Vector2 position)
	{
		boolean canBuild = true;
		for(int i = 0; i<towers.size(); i++)
        {
            if (towers.get(i).getTilePosition() == position)
                canBuild = false;
        }
        if (currentGold < type.getBuildCost())
            canBuild = false;
        if (!map.canBuild((int)position.x, (int)position.y))
            canBuild = false;
        if (canBuild)
        {
            selectedTower = new Tower1(type, position, towersAtlas.createSprite(type.getTowerTexture(1)), towersAtlas.createSprite(type.getMissileTexture()));
            selectedTower.setColor(Color.RED);
            towers.add(selectedTower);
            currentGold -= type.getBuildCost();
        }
	}
	private void upgradeTower(Vector2 tilePosition)
	{
		
	}
	
	private void restartGame()
	{
		towers.clear();
		enemies.clear();
		missiles.clear();
		eventHandler.Clear();
		currentGold = startGold;
		currentLives = startingLives;
		
		createWave();
		
		gameOver = false;
        // gui.building = false;
		currentlyBuildingTower = null;
        totalGameTime = 0;
        // winPopup.Randomize();
        selectedTower = null;
        currentLives = startingLives;
        saved = false;
        savedParameters = false;
        // replayHandler.Clear();
        targetEnemy = null;
        if (useReplay)
        {
            //replayHandler.LoadReplay(replayString);
        }
	}
}
