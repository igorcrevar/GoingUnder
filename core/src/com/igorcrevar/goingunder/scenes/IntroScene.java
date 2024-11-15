package com.igorcrevar.goingunder.scenes;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.ParticleEffect;
import com.igorcrevar.goingunder.objects.IntroSceneButtons;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;
import com.igorcrevar.goingunder.utils.MyFontDrawer;
import com.igorcrevar.goingunder.utils.MyFontDrawerBatch;
import com.igorcrevar.goingunder.utils.MyFontDrawerDefaultFont;

public class IntroScene implements IScene {
	private static final float MaximumAnimTimerValue = 6.0f;

	private IGameObject background;
	private GameData gameData;

	private final SpriteBatch spriteBatch = new SpriteBatch();

	private final ShapeRenderer shapeRenderer = new ShapeRenderer();

	private GameManager gameManager;

	// buttons
	private IntroSceneButtons introButtons;

	private final MyFontDrawerBatch myFontDrawerBatch = new MyFontDrawerBatch(new MyFontDrawerDefaultFont());

	private final IActivityRequestHandler activityRequestHandler;

	private Player player;
	private int playerDir;
	private String scoreInfo;
	private float animationTimer;
	private float titleAnimateTimer;
	private final Random rnd = new Random();
	private int hintType;

	private ParticleEffect particles;

	private final MyFontDrawer[] titleDrawers = new MyFontDrawer[2];

	public IntroScene(IActivityRequestHandler activityRequestHandler) {
		this.activityRequestHandler = activityRequestHandler;
	}

	@Override
	public void create(ISceneManager sceneManager) {
		gameManager = sceneManager.getGameManager();
		gameData = GameData.createForIntro(gameManager);

		player = gameManager.getPlayer();
		background = gameManager.getBackground();
		particles = new ParticleEffect(gameManager, player, gameData);

		// my font
		titleDrawers[0] = myFontDrawerBatch.addNew("Going", gameManager.getTextureAtlas("game").findRegion("title2"), 25, 25, 20, 0.00001f);
		titleDrawers[1] = myFontDrawerBatch.addNew("Under", gameManager.getTextureAtlas("game").findRegion("title2"), 25, 25, 20, 0.00001f);

		MyFontDrawer copyright = myFontDrawerBatch.addNew("WayILook@Games 2014", gameManager.getTextureAtlas("game").findRegion("titlebubble"), 8, 8, 4, 0.00001f);
		copyright.idt().translate(myFontDrawerBatch.getWidth() / 2.0f, 140f);

		introButtons = new IntroSceneButtons(sceneManager, activityRequestHandler, gameManager);
	}

	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(false);
		background.init(gameData);
		gameManager.playIntroMusic();
		player.init(gameData);
		particles.init(gameData);
		gameData.CameraYPosition = 0.0f;
		animationTimer = 0.0f;
		titleAnimateTimer = 0.0f;
		playerDir = 0;
		scoreInfo = Integer.toString(gameManager.getTopScore());
		hintType = rnd.nextInt(4);
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();

		// animate title
		// [0..PI/2], [PI..PI*1.5] = 1, [PI/2..PI], [PI*1/5..PI*2] = 2
		int titleSpeedFactorIndex = (int) (titleAnimateTimer / Math.PI * 2); // [0,1,2,3]
		float titleSpeedFactor = (titleSpeedFactorIndex & 1) * 2f + 2f;
		float titleX = (float) Math.sin(titleAnimateTimer) * 80f;
		float titleY = titleX * titleX * 0.02f;
		float titleAngle = Mathf.lerpBI(-0.1f, 0.2f, titleAnimateTimer / (float) Math.PI / 2);

		final float h1 = titleDrawers[0].getHeight();
		final float h2 = titleDrawers[1].getHeight();

		final float scale = (float) (0.9 + Math.sin(titleAnimateTimer) * 0.1);

		titleDrawers[0].idt().translate(titleX + myFontDrawerBatch.getWidth() / 2.0f, titleY + myFontDrawerBatch.getHeight() - h1 / 2.0f - 20.0f).rotateXYRad(titleAngle).scale(scale, scale, scale);
		titleDrawers[1].idt().translate(titleX + myFontDrawerBatch.getWidth() / 2.0f, titleY + myFontDrawerBatch.getHeight() - h1 - h2 / 2.0f - 20.0f).rotateXYRad(titleAngle).scale(scale, scale, scale);
		titleAnimateTimer = (titleAnimateTimer + deltaTime * titleSpeedFactor) % ((float) Math.PI * 2);

		background.update(deltaTime);
		player.update(deltaTime);
		particles.update(deltaTime);
		gameData.CameraYPosition += deltaTime * gameData.VelocityY; // update camera pos also

		// 0.0 left 1.5 right 3.0 right 4.5 left
		int newPlayerDir = (int) (animationTimer / 1.5f) + 1; // [1,2,3,4]
		if (playerDir != newPlayerDir) {
			playerDir = newPlayerDir;
			int tmpDir = (playerDir % 4) / 2; // [1, 2, 3, 4] -> [0, 1, 1, 0]
			player.addVelocity(2.2f * (tmpDir * 2 - 1));
			particles.addNew();
		}

		animationTimer += deltaTime;
		if (animationTimer >= MaximumAnimTimerValue) {
			animationTimer = 0.0f;
			hintType = (hintType + 1) % 4;
		}

		background.draw(spriteBatch); // draw background
		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		spriteBatch.begin();
		particles.draw(spriteBatch); // draw particles
		player.draw(spriteBatch); // draw player
		spriteBatch.end();

		myFontDrawerBatch.draw();

		drawText();
		introButtons.draw(spriteBatch);

		player.drawLights(shapeRenderer, gameData);
	}

	@Override
	public void leave(ISceneManager sceneManager) {
		gameManager.stopIntroMusic();
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
		introButtons.check(x, y);
	}

	@Override
	public boolean processBackKey(ISceneManager sceneManager) {
		Gdx.app.exit();

		return true;
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		spriteBatch.dispose();
		myFontDrawerBatch.dispose();
	}

	private void drawText() {
		BitmapFontDrawer bfDrawer = gameManager.getBitmapFontDrawer();
		// draw top score above player
		float posX = ((player.getX() + gameData.CameraHalfWidth) / gameData.CameraHalfWidth) / 2.0f * bfDrawer.getWidth();
		float playerY = player.getY() - gameData.CameraYPosition;
		float posY = ((gameData.PlayerSizeY * 1.05f + playerY + gameData.CameraHalfHeight) / gameData.CameraHalfHeight) / 2.0f * bfDrawer.getHeight();

		bfDrawer.begin().setColor(Color.ORANGE).setScale(1.0f).draw(scoreInfo, posX, posY, BitmapFontDrawer.Flag.Middle, BitmapFontDrawer.Flag.Middle).draw(getTutorialText(), 0.0f, 800f, BitmapFontDrawer.Flag.Center).end();
	}

	private String getTutorialText() {
		switch (hintType) {
			case 0:
				if (animationTimer < MaximumAnimTimerValue / 2.0f) {
					return "Multiple taps...";
				}
				return "...to accelerate more!";
			case 1:
				if (animationTimer < MaximumAnimTimerValue / 2.0f) {
					return "It's easier to play...";
				}
				return "...with both thumbs!";
			case 2:
				if (animationTimer < MaximumAnimTimerValue / 2.0f) {
					return "Fish can't hurt you...";
				}
				return "...at all!";
			default:
				if (animationTimer < MaximumAnimTimerValue / 4.0f) {
					return "Tap left screen side...";
				} else if (animationTimer < MaximumAnimTimerValue / 2.0f) {
					return "...to push our hero right!";
				} else if (animationTimer < MaximumAnimTimerValue / 4.0f * 3.0f) {
					return "Tap right screen side...";
				}

				return "...to push our hero left!";
		}
	}
}
