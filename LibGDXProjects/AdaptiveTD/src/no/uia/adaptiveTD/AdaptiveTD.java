package no.uia.adaptiveTD;

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
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AdaptiveTD implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	private BitmapFont font;

	boolean unlockFrames = false;
	boolean onlyUpdates = false;
	float w, h;
	CharSequence UPS = "0";
	CharSequence FPS = "0";

	@Override
	public void create() {
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		if (unlockFrames)
			Gdx.graphics.setVSync(false);

		camera = new OrthographicCamera(1, h / w);
		camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();

		font = new BitmapFont();

		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);

		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	int updateC = 0;
	int updateT = 0;
	double timer = 1.0;
	int frameC = 0;
	int frameT = 0;
	double frameTimer = 0;

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
	}

	private void draw() {

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
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
}
