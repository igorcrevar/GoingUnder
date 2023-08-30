package com.igorcrevar.goingunder.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;

public class TutorialScene implements IScene {
	private static final float ButtonTimerPause = 0.5f;

	private GameData gameData;
	private GameManager gameManager;
	private SpriteBatch spriteBatch;
	
	private IActivityRequestHandler activityRequestHandler;
	private boolean isDisposed;
	
	private Player player;
	private IGameObject background;
	private Sprite button = new Sprite();
	private Sprite arrow = new Sprite();
	
	private int state;
	private float buttonAlpha;
	private float buttonTimer;
	private float pauseTimer;
	private float countingDownTimer;
	
	public TutorialScene(IActivityRequestHandler activityRequestHandler) {
		this.activityRequestHandler = activityRequestHandler;
	}
	
	@Override
	public void create(ISceneManager sceneManager) {
		this.gameManager = sceneManager.getGameManager();
		gameData = GameData.createForIntro();
		gameData.VelocityY = 0f;
		gameData.CameraDist = 2.8f;
		spriteBatch = new SpriteBatch();
		
		arrow.setRegion(gameManager.getTextureAtlas("game").findRegion("arrow"));
		arrow.setSize(0.4f, 0.5f);
		button.setRegion(gameManager.getTextureAtlas("game").findRegion("dummy"));
		button.setSize(gameData.CameraHalfWidth, gameData.CameraHalfHeight * 0.5f);
		
		player = gameManager.getPlayer();
		background = gameManager.getBackground();
	}

	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(false);
		
		player.init(gameData);
		background.init(gameData);
		state = 0;
		buttonTimer = 0f;
		pauseTimer = 0.0f;
		countingDownTimer = 0f;
	}

	private boolean isLeft() {
		return (state == 0 || state == 2 || state == 3);
	}
	
	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		if (state == 6) {
			doFinalCountdown(deltaTime, sceneManager);
		}		
		else {
			// count pause timer if needed (not eq 0f)
			if (pauseTimer != 0f) { 
				pauseTimer += deltaTime;
				if (pauseTimer >= 0.5f) {
					pauseTimer = 0.0f;
				}
			}
			
			doDefault(deltaTime);
			drawDefault();
		}
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		if (!isDisposed) {
			if (spriteBatch != null) {
				spriteBatch.dispose();
			}
			isDisposed = true;
		}
	}

	@Override
	public void leave(ISceneManager sceneManager) {
		dispose(sceneManager);
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
		// on last state or when pause is not over taps do not count
		// ...also skip touch if its colddown time
		if (state == 6 || pauseTimer != 0f || buttonTimer < ButtonTimerPause) {
			return;
		}
		
		boolean okYPos = GameHelper.screenY2WorldY(gameData, y) < -gameData.CameraHalfHeight + button.getHeight();
		if (isLeft()) {
			if (okYPos && x < Gdx.graphics.getWidth() / 2) {
				player.addVelocity(gameData.VelocityX);
				gameManager.playMoveSound();
				pauseTimer = 0.001f; // request pause
				buttonTimer = 0f;
				state += 1;
			}	
		}
		else if (okYPos && x > Gdx.graphics.getWidth() / 2) {
			player.addVelocity(-gameData.VelocityX);
			gameManager.playMoveSound();
			pauseTimer = 0.001f; // request pause
			buttonTimer = 0f;
			state += 1;
		}
	}

	@Override
	public boolean processBackKey(ISceneManager sceneManager) {
		sceneManager.setScene(SceneConstants.IntroScene);

		return true;
	}

	private void doDefault(float deltaTime) {
		float x, y;
		boolean isLeft = isLeft();
		// update arrow
		float arrowTimer = buttonTimer / 2f;
		x = isLeft ? -gameData.CameraHalfWidth / 2.0f - arrow.getWidth() / 2.0f : gameData.CameraHalfWidth / 2.0f - arrow.getWidth() / 2.0f; 
		y = Mathf.lerpBI(
			-gameData.CameraHalfHeight / 2f + 1.2f,
			-gameData.CameraHalfHeight / 2f + 0.4f, 
			arrowTimer - (float)Math.floor(arrowTimer));
		arrow.setPosition(x, y);
		// update button
		x = isLeft ? -gameData.CameraHalfWidth : 0f;
		y = -gameData.CameraHalfHeight;
		button.setPosition(x, y);
		buttonAlpha = Mathf.lerpBI(
			0.6f,
			0.1f,
			buttonTimer - (float)Math.floor(buttonTimer));
		// update player
		player.update(deltaTime);
		
		buttonTimer += deltaTime;
	}
	
	private void drawDefault() {
		GameHelper.clearScreen();
		// draw background and player and arrow and button
		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());		
		spriteBatch.begin();
		background.draw(spriteBatch);
		if (buttonTimer >= ButtonTimerPause) {
			arrow.draw(spriteBatch);
			button.draw(spriteBatch, buttonAlpha);
		}
		player.draw(spriteBatch);
		spriteBatch.end();

		// draw additional text. Is this necessary?
		BitmapFontDrawer bfDrawer = gameManager.getBitmapFontDrawer();
		String txt1 = isLeft() 
			? "Tap left screen side"
			: "Tap right screen side";
		String txt2 = isLeft() 
			? "to give a rightward impulse"
			: "to give a leftward impulse";
		float xpos = bfDrawer.getWidth() * (isLeft() ? 0.25f : 0.75f);
		bfDrawer.begin()
				.setScale(1.0f)
				.setColor(Color.ORANGE)
				.draw(txt1, 0f, 200f, BitmapFontDrawer.Flag.Center, BitmapFontDrawer.Flag.Center)
				.draw(txt2, 0f, 100f, BitmapFontDrawer.Flag.Center, BitmapFontDrawer.Flag.Center)
				.end();
	}
	
	private void doFinalCountdown(float deltaTime, ISceneManager sceneManager) {
		player.update(deltaTime);
		
		GameHelper.clearScreen();
		// draw background and player 
		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());		
		spriteBatch.begin();
		background.draw(spriteBatch);
		player.draw(spriteBatch);
		spriteBatch.end();
		
		// draw counter
		BitmapFontDrawer bfDrawer = gameManager.getBitmapFontDrawer();
		int no = (int)Math.round(Mathf.lerp(3.0f, 0.0f, countingDownTimer / 3.0f));
		String txt = no != 0 ? Integer.toString(no) : "Go!";
		bfDrawer.begin()
				.setScale(1.0f)
				.setColor(Color.WHITE)
				.draw(txt,  0.0f, bfDrawer.getHeight() * 0.3f, BitmapFontDrawer.Flag.Center)
				.end();
		
		countingDownTimer += deltaTime;
		// if counter reach limit start game!
		if (countingDownTimer >= 3.0f) {
			sceneManager.setScene(SceneConstants.GameScene);
		}		
	}
}
