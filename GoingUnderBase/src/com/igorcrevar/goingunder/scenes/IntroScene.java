package com.igorcrevar.goingunder.scenes;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.IntroSceneButtons;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.MyFontDrawerBatch;
import com.igorcrevar.goingunder.utils.MyFontDrawerDefaultFont;

public class IntroScene implements IScene {
	private static final float MaximumAnimTimerValue = 3.0f;
	private IGameObject background;
	private GameData gameData = GameData.createForIntro();
	
	private SpriteBatch spriteBatch = new SpriteBatch();
	
	private GameManager gameManager;
		
	// buttons
	private IntroSceneButtons introButtons;
	
	private MyFontDrawerBatch myFontDrawerBatch = new MyFontDrawerBatch(new MyFontDrawerDefaultFont());
	
	private IActivityRequestHandler activityRequestHandler;
	
	private Player player;
	private int playerPos;	
	private String scoreInfo;
	private float animationTimer;
	private Random rnd = new Random();
	private int hintType;
	
	public IntroScene(IActivityRequestHandler activityRequestHandler) {
		this.activityRequestHandler = activityRequestHandler;
	}
	
	@Override
	public void create(ISceneManager sceneManager) {
		gameManager = sceneManager.getGameManager();		
		
		background = gameManager.getBackground();
				
		// my font
		myFontDrawerBatch.addNew("Going", 
				gameManager.getTextureAtlas("game").findRegion("title2"), 40, 1880, 35, 35, 20, 0.00001f);
		
		myFontDrawerBatch.addNew("Under", 
				gameManager.getTextureAtlas("game").findRegion("title2"), 90, 1610, 35, 35, 20, 0.00001f);
		
		myFontDrawerBatch.addNew("(c) WayILook@Games 2014", 
				gameManager.getTextureAtlas("game").findRegion("titlebubble"), 10, 95, 9, 9, 4, 0.00001f);
		
		introButtons = new IntroSceneButtons(sceneManager, activityRequestHandler, gameManager);
		
		player = gameManager.getPlayer();
	}
	
	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(false);
		background.init(gameData);			
		gameManager.playIntroMusic();
		
		player.init(gameData);
		animationTimer = 0.0f;
		playerPos = 0;
		scoreInfo = Integer.toString(gameManager.getTopScore());
		hintType = rnd.nextInt(3);
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();
		
		player.update(deltaTime);
		if (animationTimer >= 0.5f && playerPos == 0) {
			playerPos = 1;
			player.addVelocity(2.2f);
		}
		else if (animationTimer >= 2.0f && playerPos == 1) {
			playerPos = 2;
			player.addVelocity(-2.2f);
		}
		
		animationTimer += deltaTime;
		if (animationTimer >= MaximumAnimTimerValue) {
			playerPos = 0;
			animationTimer = 0.0f;
		}
		
		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());		
		spriteBatch.begin();
		// draw background
		background.draw(spriteBatch);
		// draw player
		player.draw(spriteBatch);
		spriteBatch.end();
		
		myFontDrawerBatch.draw();
		
		drawText(spriteBatch);
		introButtons.draw(spriteBatch);
	}

	@Override
	public void leave(ISceneManager sceneManager) {
		gameManager.stopIntroMusic();
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
	}
	
	@Override
	public void processTouchUp(ISceneManager sceneManager, int x, int y) {
		introButtons.check(gameData, x, y);
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		spriteBatch.dispose();
		myFontDrawerBatch.dispose();
	}
	
	private void drawText(SpriteBatch spriteBatch) {
		// draw text
		float screenWidth = 1 / GameData.AspectRatio * 1920.0f;
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, 1920);
		spriteBatch.begin();
		// draw score
		BitmapFont font = gameManager.getBitmapFont();
		font.setScale(1.0f);
		// draw top score above player
		float posX = ((player.getX() + gameData.CameraHalfWidth) / gameData.CameraHalfWidth) 
						/ 2.0f * screenWidth - font.getBounds(scoreInfo).width / 2.0f; 
		float posY = ((gameData.PlayerSizeY * 0.85f +  player.getY() + gameData.CameraHalfHeight)
						/ gameData.CameraHalfHeight) / 2.0f * 1920.0f;
		font.setColor(Color.BLACK);
		font.draw(spriteBatch, scoreInfo, posX + 5f, posY - 10f);
		
		font.setColor(Color.ORANGE);
		font.draw(spriteBatch, scoreInfo, posX, posY);
		
		String txt = getTutorialText();
		
		font.setColor(Color.BLACK);
		font.draw(spriteBatch, txt, (screenWidth - font.getBounds(txt).width) / 2.0f + 5.0f, 840);
		font.setColor(Color.ORANGE);
		font.draw(spriteBatch, txt, (screenWidth - font.getBounds(txt).width) / 2.0f, 850);		
		font.setColor(Color.WHITE);
		spriteBatch.end();
	}
	
	private String getTutorialText() {
		if (hintType == 0) {
			if (animationTimer < MaximumAnimTimerValue / 2.0f) {
				return "Multiple taps...";
			}
			return "...to accelerate more!";
		}
		else if (hintType == 1) {
			if (animationTimer < MaximumAnimTimerValue / 2.0f) {
				return "It's easier to play...";
			}
			return "...with both thumbs!";
		}
		
		if (animationTimer < MaximumAnimTimerValue / 4.0f) {
			return "Tap left...";
		}
		else if (animationTimer < MaximumAnimTimerValue / 2.0f) {
			return "...to push imp right!";
			
		}
		else if (animationTimer < MaximumAnimTimerValue / 4.0f * 3.0f) {
			return "Tap right...";
		}

		return "...to push imp left!";
	}
}
