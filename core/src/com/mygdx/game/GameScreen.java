package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
	final Drop game;

	Texture coinImage;
	Texture starImage;
	Texture ggImage;
	Texture mushroomImage;
	Texture[] ItemsDropArray;
	TextureRegion backgroundTexture;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<coinDrops> coinsDrops;
	long lastDropTime;
	int dropsGathered;
	int speed = 200;



	public GameScreen(final Drop gam) {
		this.game = gam;

		mushroomImage = new Texture(Gdx.files.internal("mushroom.png"));
		starImage = new Texture(Gdx.files.internal("star.png"));
		coinImage = new Texture(Gdx.files.internal("coin.png"));
		ggImage = new Texture(Gdx.files.internal("gg.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);
		backgroundTexture = new TextureRegion(new Texture("background.png"), 0, 0, 1200, 799);

		ItemsDropArray = new Texture[] {coinImage, mushroomImage, starImage};

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;

		bucket.width = 64;
		bucket.height = 64;

		coinsDrops = new Array<>();
		spawnRaindrop();

	}

	private void spawnRaindrop() {
		Rectangle conDrops = new Rectangle();
		int type = 0;
		if(MathUtils.randomBoolean(0.25f)){
			type =0;
		}
		else if(MathUtils.randomBoolean(0.25f)){
			type = 1;
		}
		else if(MathUtils.randomBoolean(0.25f)){
			type = 2;
		}
		else if(MathUtils.randomBoolean(0.25f)){
			type = 3;
		}
		conDrops.x = MathUtils.random(0, 800 - 64);
		conDrops.y = 480;
		conDrops.width = 64;
		conDrops.height = 64;
		coinsDrops.add(new coinDrops(conDrops,type));
		lastDropTime = TimeUtils.nanoTime();
	}

	class coinDrops{
		Rectangle rectangle;
		int type;

		public coinDrops(Rectangle rectangle, int type) {
			this.rectangle = rectangle;
			this.type = type;
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(backgroundTexture, 0, 0);
		game.font.draw(game.batch, "Coins: " + dropsGathered, 0, 480);
		game.batch.draw(ggImage, bucket.x, bucket.y);
		for(coinDrops conDrops: coinsDrops) {
			game.batch.draw(ItemsDropArray[conDrops.type], conDrops.rectangle.x, conDrops.rectangle.y);
		}
		game.batch.end();

		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > 800 - 64)
			bucket.x = 800 - 64;
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrop();

		// движение капли, удаляем все капли выходящие за границы экрана
		// или те, что попали в ведро. Воспроизведение звукового эффекта
		// при попадании.
		Iterator<coinDrops> iter = coinsDrops.iterator();
		while (iter.hasNext()) {
			coinDrops coindrop = iter.next();
			coindrop.rectangle.y -= 200 * Gdx.graphics.getDeltaTime();
			if (coindrop.rectangle.y + 64 < 0)
				iter.remove();
			if (coindrop.rectangle.overlaps(bucket)) {
				dropsGathered++;
				dropSound.play();
				iter.remove();
			}
		}
		switch (dropsGathered){
			case 20:
				coinImage = new Texture(Gdx.files.internal("mushroom.png"));
				speed = 300;
				break;
			case 30:
				coinImage = new Texture(Gdx.files.internal("star.png"));
				speed = 400;
				break;
			case 50:
				speed = 800;
				break;
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		// воспроизведение фоновой музыки
		// когда отображается экрана
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		coinImage.dispose();
		ggImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}