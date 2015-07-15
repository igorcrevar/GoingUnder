package com.igorcrevar.goingunder.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;

public class TutorialScene implements IScene {
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
	private float arrowTimer;
	private float pauseTimer;
	private float countingDownTimer;
	
	public TutorialScene(IActivityRequestHandler activityRequestHandler) {
		this.activityRequestHandler = activityRequestHandler;
	}
	
	@Override
	public void create(ISceneManager sceneManager) {
		this.gameManager = sceneManager.getGameManager();
		gameData = GameData.createForIntro();
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
		arrowTimer = 0f;
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
		// on last state or when pause is not over taps dont count
		if (state == 6 || pauseTimer != 0f) {
			return;
		}
		
		boolean okYPos = GameHelper.screenY2WorldY(gameData, y) < -gameData.CameraHalfHeight + button.getHeight();
		if (isLeft()) {
			if (okYPos && x < Gdx.graphics.getWidth() / 2) {
				player.addVelocity(gameData.VelocityX);
				pauseTimer = 0.001f; // request pause
				buttonTimer = 0f;
				arrowTimer = 0f;
				state += 1;
			}	
		}
		else if (okYPos && x > Gdx.graphics.getWidth() / 2) {
			player.addVelocity(-gameData.VelocityX);
			pauseTimer = 0.001f; // request pause
			buttonTimer = 0f;
			arrowTimer = 0f;
			state += 1;
		}
	}

	@Override
	public void processTouchUp(ISceneManager sceneManager, int x, int y) {
	}
	
	private void doDefault(float deltaTime) {
		float x, y;
		boolean isLeft = isLeft();
		// update arrow
		x = isLeft ? -gameData.CameraHalfWidth / 2.0f - arrow.getWidth() / 2.0f : gameData.CameraHalfWidth / 2.0f - arrow.getWidth() / 2.0f; 
		y = Mathf.lerpBI(-gameData.CameraHalfHeight / 2f + 2.2f, -gameData.CameraHalfHeight / 2f + 0.2f, arrowTimer / 2.0f);
		arrow.setPosition(x, y);
		// update button
		x = isLeft ? -gameData.CameraHalfWidth : 0f;
		y = -gameData.CameraHalfHeight;
		button.setPosition(x, y);
		buttonAlpha = Mathf.lerpBI(0.6f, 0.1f, buttonTimer);
		// update player
		player.update(deltaTime);
		
		buttonTimer += deltaTime;
		if (buttonTimer >= 1f) {
			buttonTimer = 0.0f;
		}
		
		arrowTimer += deltaTime;		
		if (arrowTimer >= 2.0f) {
			arrowTimer = 0.0f;
		}
	}
	
	private void drawDefault() {
		GameHelper.clearScreen();
		// draw background and player and arrow and button
		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());		
		spriteBatch.begin();
		background.draw(spriteBatch);
		arrow.draw(spriteBatch);
		button.draw(spriteBatch, buttonAlpha);
		player.draw(spriteBatch);
		spriteBatch.end();
		// draw additional text. Is this necessary?
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
		spriteBatch.begin();
		BitmapFont font = gameManager.getBitmapFont();
		font.setScale(1.0f);
		font.setColor(Color.BLACK);
		/*
		String txt = "Tap to";
		font.draw(spriteBatch, txt, (screenWidth - font.getBounds(txt).width) / 2.0f + 5.0f, 1160);
		txt = isLeft() ? "push imp right" : "push imp left";
		font.draw(spriteBatch, txt, (screenWidth - font.getBounds(txt).width) / 2.0f + 5.0f, 1060);
		*/
		String txt = "Tap!";
		float base = isLeft() ? 0 : 540;
		font.draw(spriteBatch, txt, (540 - font.getBounds(txt).width) / 2.0f + 5.0f + base, 1100);		
		font.setColor(Color.ORANGE);
		font.draw(spriteBatch, txt, (540 - font.getBounds(txt).width) / 2.0f + base, 1110);
		spriteBatch.end();
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
		float screenWidth = 1 / GameData.AspectRatio * 1920.0f;
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, 1920);
		spriteBatch.begin();
		BitmapFont font = gameManager.getBitmapFont();
		font.setScale(1.0f);
		
		int no = (int)Math.round(Mathf.lerp(3.0f, 0.0f, countingDownTimer / 3.0f));
		String txt = no != 0 ? Integer.toString(no) : "Go!"; 		
		font.setColor(Color.BLACK);
		font.draw(spriteBatch, txt, (screenWidth - font.getBounds(txt).width) / 2.0f + 5.0f, 1895);
		font.setColor(Color.WHITE);
		font.draw(spriteBatch, txt, (screenWidth - font.getBounds(txt).width) / 2.0f, 1905);
		spriteBatch.end();
		
		countingDownTimer += deltaTime;
		// if counter reach limit start game!
		if (countingDownTimer >= 3.0f) {
			sceneManager.setScene(ISceneManager.GameScene);
		}		
	}
}
