package com.igorcrevar.goingunder.scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.collision.CollisionResolver;
import com.igorcrevar.goingunder.objects.EndGameButtons;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.ParticleEffect;
import com.igorcrevar.goingunder.objects.ActiveGameButtons;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.objects.Fishes;
import com.igorcrevar.goingunder.objects.obstacles.IObstaclePool;
import com.igorcrevar.goingunder.objects.obstacles.ObstacleObject;
import com.igorcrevar.goingunder.objects.obstacles.ObstaclePool;
import com.igorcrevar.goingunder.statemachine.IMyRandom;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;
import com.igorcrevar.goingunder.utils.MyFontDrawer;
import com.igorcrevar.goingunder.utils.MyFontDrawerBatch;
import com.igorcrevar.goingunder.utils.MyFontDrawerDefaultFont;

public class GameScene implements IScene {
	private Player player;
	private GameData gameData;
	private IGameObject background;
	private Fishes fishes;

	private ParticleEffect particles;

	private final SpriteBatch spriteBatch = new SpriteBatch();
	private final ShapeRenderer shapeRenderer = new ShapeRenderer();

	private final IMyRandom myRandom;
	private IObstaclePool obstaclePool;

	private float additionalTimer;
	private GameManager gameManager;

	private final int halfScreen;

	private final CollisionResolver collisionResolver = new CollisionResolver();

	// my font
	private final MyFontDrawerBatch myFontDrawerBatch = new MyFontDrawerBatch(new MyFontDrawerDefaultFont());
	private MyFontDrawer gameOverDrawer;
	private final IActivityRequestHandler activityRequestHandler;

	// buttons
	private EndGameButtons endGameButtons;

	// previous position of camera when obstacle is generated
	private float prevCameraYWhenObstacleIsGenerated;
	//
	private ActiveGameButtons activeGameButtons;
	private float resumingCounter;

	public GameScene(IMyRandom myRandom, IActivityRequestHandler activityRequestHandler) {
		this.myRandom = myRandom;
		this.activityRequestHandler = activityRequestHandler;
		this.halfScreen = Gdx.graphics.getWidth() / 2;
	}

	@Override
	public void create(ISceneManager sceneManager) {
		this.gameManager = sceneManager.getGameManager();
		this.gameData = GameData.createDefault(this.gameManager);
		this.obstaclePool = new ObstaclePool(this.gameData.MaxOnScreenAtOnce);

		player = gameManager.getPlayer();
		background = gameManager.getBackground();
		fishes = new Fishes(gameManager);
		particles = new ParticleEffect(gameManager, player, gameData);

		// my font
		gameOverDrawer = myFontDrawerBatch.addNew("Game Over",
				gameManager.getTextureAtlas("game").findRegion("titlebubble"),
				20, 40, 10, 0.00001f);

		// buttons
		endGameButtons = new EndGameButtons(sceneManager, activityRequestHandler, gameManager);
		activeGameButtons = new ActiveGameButtons(gameManager);
	}

	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(false);

		obstaclePool.init();
		gameData.init(myRandom);
		player.init(gameData);
		background.init(gameData);
		particles.init(gameData);
		fishes.init(gameData);

		additionalTimer = 0.0f;
		resumingCounter = 0.0f;
		myRandom.reset();
		gameManager.resetGame();
		gameManager.setGameStatus(GameManager.Status.Active);
		prevCameraYWhenObstacleIsGenerated = gameData.CameraYPosition + gameData.ObstacleGeneratorDistance;

		activeGameButtons.init();
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();

		switch (gameManager.getGameStatus()) {
			case Active:
				// update
				update(deltaTime);
				// obstacles generation
				obstacleGeneration();
				// detect collision
				if (collisionResolver.detect(player, obstaclePool.getAllVisibles(), gameData.DrawCollision)) {
					if (gameData.DieOnCollision) {
						playerDies(sceneManager);
					}
				}
				break;

			case NotActive:
				gameData.FishRandomProbability = 0.009f;
				background.update(deltaTime);
				particles.update(deltaTime);
				player.updateDie(additionalTimer, deltaTime);
				fishes.update(deltaTime);
				additionalTimer += deltaTime;

				break;

			case Resuming:
				resumingCounter += deltaTime;
				if (resumingCounter >= 4f) {
					gameManager.setGameStatus(GameManager.Status.Active);
				}

				break;
		}

		// draw
		draw();
	}

	@Override
	public void leave(ISceneManager sceneManager) {
	}

	private void update(float deltaTime) {
		background.update(deltaTime);
		player.update(deltaTime);
		particles.update(deltaTime);
		fishes.update(deltaTime);

		ArrayList<ObstacleObject> obstacles = obstaclePool.getAllVisibles();
		for (int i = obstacles.size() - 1; i >= 0; --i) {
			ObstacleObject oo = obstacles.get(i);
			float bottom = oo.getBottom();
			// check if obstacle is passed by player (for the first time)
			if (!oo.isPassed() && player.getY() < bottom) {
				gameManager.addScore(1);
				oo.markAsPassed();
				// update level if needed
				gameData.update(gameManager.getCurrentScore(), myRandom);
			}

			// obstacle is not on the screen anymore, mark it as free to use
			if (bottom > gameData.getCameraTop()) {
				// remove from visible obstacles pool
				obstaclePool.removeFromVisibles(oo);
			}
		}

		// update camera pos also
		gameData.CameraYPosition += deltaTime * gameData.VelocityY;
	}

	private void draw() {
		// draw background
		background.draw(spriteBatch);

		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		spriteBatch.begin();

		// draw fishes
		fishes.draw(spriteBatch);

		// draw particles
		particles.draw(spriteBatch);

		// draw obstacles
		ArrayList<ObstacleObject> obstacles = obstaclePool.getAllVisibles();
		for (int i = obstacles.size() - 1; i >= 0; --i) {
			obstacles.get(i).draw(spriteBatch);
		}

		// draw player
		player.draw(spriteBatch);

		spriteBatch.end();

		drawUI();
	}

	private void playerDies(ISceneManager sceneManager) {
		gameManager.playDieSound();
		activityRequestHandler.showAds(true);
		particles.setIsEnabled(false);
		// change state of a game, save score, call any other 3rd party service
		sceneManager.finishGame();
	}

	private void drawUI() {
		BitmapFontDrawer bfDrawer = gameManager.getBitmapFontDrawer();
		String score = Integer.toString(gameManager.getCurrentScore());
		GameManager.Status status = gameManager.getGameStatus();

		if (status == GameManager.Status.NotActive) {
			String best = Integer.toString(gameManager.getTopScore());
			String timesPlayed = Integer.toString(gameManager.getTotalGamesPlayed());
			String fishPunchCount = String.format("%d (%d)",
					gameManager.getFishPunchCount(), gameManager.getTopFishPunchCount());
			float initialY = bfDrawer.getHeight() * 1.6f;
			float endY = bfDrawer.getHeight() * 0.6f + gameOverDrawer.getHeight();
			float gameOverYPos = Mathf.lerp(initialY, endY, additionalTimer / 3f);

			gameOverDrawer.idt().translate(myFontDrawerBatch.getWidth() / 2f, gameOverYPos);
			myFontDrawerBatch.draw();

			gameOverYPos -= gameOverDrawer.getHeight() * 0.5f;

			bfDrawer.begin()
					.setScale(1.0f)
					.setColor(Color.ORANGE)
					.draw("Best", 150f, gameOverYPos - 350f)
					.draw("Plays", 150f, gameOverYPos - 450f)
					.draw("Fish", 150f, gameOverYPos - 550f)
					.setColor(Color.WHITE)
					.draw(best, 150f, gameOverYPos - 350f, BitmapFontDrawer.Flag.AlignTopOrRight)
					.draw(timesPlayed, 150f, gameOverYPos - 450f, BitmapFontDrawer.Flag.AlignTopOrRight)
					.draw(fishPunchCount, 150f, gameOverYPos - 550f, BitmapFontDrawer.Flag.AlignTopOrRight)
					.draw(score, 0.0f, bfDrawer.getHeight(), BitmapFontDrawer.Flag.Center, BitmapFontDrawer.Flag.Middle);
			if (gameData.DrawFPS) {
				bfDrawer.draw("fps:" + Gdx.graphics.getFramesPerSecond(), 26f, 65f);
			}

			bfDrawer.end();

			// draw buttons
			if (additionalTimer >= gameData.getAdsPause(gameManager.getCurrentScore())) {
				endGameButtons.draw(spriteBatch);
			}
		} else {
			// draw active buttons
			activeGameButtons.draw(spriteBatch);

			bfDrawer.begin()
					.setScale(1.0f)
					.setColor(Color.WHITE)
					.draw(score, 0.0f, bfDrawer.getHeight(), BitmapFontDrawer.Flag.Center, BitmapFontDrawer.Flag.Middle);

			if (status == GameManager.Status.Resuming) {
				int cnt = 3 - (int) resumingCounter;
				String txt = cnt <= 0 ? "Go!" : String.valueOf(cnt);
				float tmp = resumingCounter - (int) resumingCounter;
				float scale = Mathf.lerp(2.5f, 1.0f, tmp);

				bfDrawer.setScale(scale)
						.draw(txt, 0.0f, 0f, BitmapFontDrawer.Flag.Center, BitmapFontDrawer.Flag.Center);
			}

			if (gameData.DrawFPS) {
				bfDrawer.setScale(1.0f).draw("fps:" + Gdx.graphics.getFramesPerSecond(), 26f, 65f);
			}

			bfDrawer.end();

			// draw lights
			player.drawLights(shapeRenderer, gameData);
			// draw collision
			if (gameData.DrawCollision) {
				collisionResolver.draw(shapeRenderer, gameData);
			}
		}
	}

	private void obstacleGeneration() {
		// generate obstacles
		if (-gameData.CameraYPosition + prevCameraYWhenObstacleIsGenerated >= gameData.ObstacleGeneratorDistance) {
			obstaclePool.getOne(myRandom.getNext(), gameData, gameManager);
			prevCameraYWhenObstacleIsGenerated = gameData.CameraYPosition;
		}
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
		switch (gameManager.getGameStatus()) {
			case Active:
				// first check if pause button is pressed
				if (!activeGameButtons.check(x, y)) {
					gameManager.playMoveSound();
					if (x > halfScreen) {
						player.addVelocity(-gameData.VelocityX);
					} else {
						player.addVelocity(gameData.VelocityX);
					}

					particles.addNew();
				}

				break;

			case NotActive:
				if (additionalTimer >= gameData.getAdsPause(gameManager.getCurrentScore())) {
					endGameButtons.check(x, y);
				}
				break;

			case Paused:
				if (activeGameButtons.check(x, y)) {
					resumingCounter = 0.0f;
				}
				break;
		}
	}

	@Override
	public boolean processBackKey(ISceneManager sceneManager) {
		sceneManager.setScene(SceneConstants.IntroScene);

		return true;
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		spriteBatch.dispose();
		shapeRenderer.dispose();
		myFontDrawerBatch.dispose();
	}
}
