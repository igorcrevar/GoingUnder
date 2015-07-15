package com.igorcrevar.goingunder.scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.collision.CollisionResolver;
import com.igorcrevar.goingunder.objects.EndGameButtons;
import com.igorcrevar.goingunder.objects.IGameObject;
import com.igorcrevar.goingunder.objects.ParticleEffect;
import com.igorcrevar.goingunder.objects.PauseButtonObject;
import com.igorcrevar.goingunder.objects.Player;
import com.igorcrevar.goingunder.objects.obstacles.IObstaclePool;
import com.igorcrevar.goingunder.objects.obstacles.ObstacleObject;
import com.igorcrevar.goingunder.objects.obstacles.ObstaclePool;
import com.igorcrevar.goingunder.statemachine.IMyRandom;
import com.igorcrevar.goingunder.utils.GameHelper;
import com.igorcrevar.goingunder.utils.Mathf;
import com.igorcrevar.goingunder.utils.MyFontDrawer;
import com.igorcrevar.goingunder.utils.MyFontDrawerBatch;
import com.igorcrevar.goingunder.utils.MyFontDrawerDefaultFont;

public class GameScene implements IScene {
	
	private Player player;
	private GameData gameData = GameData.createDefault();
	private IGameObject background;
	
	private ParticleEffect particles;
		
	private SpriteBatch spriteBatch = new SpriteBatch();
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	private IMyRandom myRandom;
	private IObstaclePool obstaclePool;
	
	private float additionalTimer;
	private GameManager gameManager;
	
	private CollisionResolver collisionResolver = new CollisionResolver();
	
	// my font
	private MyFontDrawerBatch myFontDrawerBatch = new MyFontDrawerBatch(new MyFontDrawerDefaultFont());
	private MyFontDrawer myFontDrawer;
	private IActivityRequestHandler activityRequestHandler;
	
	// buttons
	private EndGameButtons endGameButtons;
	
	// this is just hack because we dont want to call saving of score/etc imediatelly when player dies
	private boolean playerScoreUpdated;
	// previous position of camera when obstacle is generated
	private float prevCameraYWhenObstacleIsGenerated;
	//
	private PauseButtonObject pauseButton = new PauseButtonObject();
	
	public GameScene(IMyRandom myRandom, IActivityRequestHandler activityRequestHandler) {
		this.myRandom = myRandom;
		this.activityRequestHandler = activityRequestHandler;
		this.obstaclePool = new ObstaclePool(gameData);
	}
	
	@Override
	public void create(ISceneManager sceneManager) {
		this.gameManager = sceneManager.getGameManager();
		
		player = gameManager.getPlayer();
		background = gameManager.getBackground();
		
		particles = new ParticleEffect(gameManager, player, gameData);
		
		// my font
		myFontDrawer = myFontDrawerBatch.addNew("Game Over", 
						gameManager.getTextureAtlas("game").findRegion("titlebubble"), 0, 0, 20, 40, 20, 0.00001f);
		
		// buttons
		endGameButtons = new EndGameButtons(sceneManager, activityRequestHandler, gameManager);
	}
	
	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(false);
		
		gameData.init(myRandom);
		player.init(gameData);
		background.init(gameData);
		obstaclePool.init(gameData);
		
		particles.init(gameData);
		
		additionalTimer = 0.0f;
		myRandom.reset();
		gameManager.startGame();
		playerScoreUpdated = false;
		if (gameManager.getTotalScoreSum() <= 5) {
			prevCameraYWhenObstacleIsGenerated = gameData.CameraYPosition - gameData.ObstacleGeneratorDistance * 0.1f;
		}
		else {
			prevCameraYWhenObstacleIsGenerated = gameData.CameraYPosition + gameData.ObstacleGeneratorDistance * 0.8f;	
		}
		
		pauseButton.init(gameManager);
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();
		
		if (gameManager.isGameActive()) {
			if (pauseButton.isGameActive()) {
				// update
				update(deltaTime);
				// obstacles generation
				obstacleGeneration(deltaTime);
				// detect collision
				if (gameData.DieOnCollision && collisionResolver.detect(player, obstaclePool.getAllVisibles())) {
					gameManager.finishGame();
				}
			}
			else {
				pauseButton.update(deltaTime);
			}
		}
		else {
			if (additionalTimer == 0.0f) {
				gameManager.playDieSound();
				activityRequestHandler.showAds(true);
			}
			
			// save score, call 
			if (additionalTimer > 0.8f && !playerScoreUpdated) {
				playerScoreUpdated = true;
				sceneManager.finishGame();
			}
			
			particles.setIsEnabled(false);
			player.updateDie(additionalTimer, deltaTime);
			additionalTimer += deltaTime;
		}
		
		// draw
		draw(deltaTime);
	}

	@Override
	public void leave(ISceneManager sceneManager) {
	}
	
	private void update(float deltaTime) {
		background.update(deltaTime);
		player.update(deltaTime);
		
		particles.update(deltaTime);
		
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
				oo.setIsEnabled(false);
			}
		}
		
		// update camera pos also
		gameData.CameraYPosition += deltaTime * gameData.VelocityY;
	}
	
	private void draw(float deltaTime) {
		gameData.setProjectionMatrix(spriteBatch.getProjectionMatrix());
		spriteBatch.begin();
		// draw background
		background.draw(spriteBatch);
		
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
		
		// draw game over 
		if (!gameManager.isGameActive()) {
			float initialY = 1920 + 400;
			float endY = 860;
			float cr = Mathf.lerp(initialY, endY, additionalTimer / 2.5f);			
			myFontDrawer.translate(50, cr + 400);
			myFontDrawerBatch.draw();
			spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
			spriteBatch.begin();
			BitmapFont font = gameManager.getBitmapFont();
			font.setScale(1.0f);
			cr += 50.0f;
			
			font.setColor(Color.BLACK);			
			font.draw(spriteBatch, "Score", 455f, cr - 10.0f);
			font.draw(spriteBatch, "Best", 505f, cr - 180.0f);			
			font.setColor(Color.ORANGE);			
			font.draw(spriteBatch, "Score", 450, cr);
			font.draw(spriteBatch, "Best", 500, cr - 170);
			
			String score = Integer.toString(gameManager.getCurrentScore());
			String best = Integer.toString(gameManager.getTopScore());
			float pos1X = 660 - font.getBounds(score).width;
			float pos2X = 660 - font.getBounds(best).width;
			
			font.setColor(Color.BLACK);
			font.draw(spriteBatch, score, pos1X + 5.0f, cr - 95f);			
			font.draw(spriteBatch, best, pos2X + 5.0f, cr - 265f);
			
			font.setColor(Color.WHITE);			
			font.draw(spriteBatch, score, pos1X, cr - 85f);			
			font.draw(spriteBatch, best, pos2X, cr - 255f);
			
			spriteBatch.end();
			
			// draw score
			drawScore();
			
			// draw buttons
			if (additionalTimer >= gameData.getAdsPause(gameManager.getCurrentScore())) {
				endGameButtons.draw(spriteBatch);
			}
		}		
		else {
			drawScore();
			
			gameData.setProjectionMatrix(shapeRenderer.getProjectionMatrix(), gameData.CameraYPosition);
			// draw lights
			player.drawLights(shapeRenderer, gameData);
			// draw collision
			if (gameData.DrawCollision) {			
				shapeRenderer.begin(ShapeType.Line);
				collisionResolver.draw(shapeRenderer, gameData);
				shapeRenderer.end();
			}
		}
	}
	
	private void drawScore() {
		// draw text
		float screenWidth = 1 / GameData.AspectRatio * 1920.0f;
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, 1920);
		spriteBatch.begin();
		BitmapFont font = gameManager.getBitmapFont();
		// draw pause button and fonts (counting)
		pauseButton.draw(font, spriteBatch);
		// draw score
		String score = Integer.toString(gameManager.getCurrentScore());
		font.setScale(1.0f);
		font.setColor(Color.BLACK);
		float middlePos = screenWidth / 2.0f - font.getBounds(score).width / 2.0f;
		font.draw(spriteBatch, score, middlePos + 5.0f, 1895);
		font.setColor(Color.WHITE);
		font.draw(spriteBatch, score, middlePos, 1905);		
		if (gameData.DrawFPS) {
			font.draw(spriteBatch, "fps:" + Gdx.graphics.getFramesPerSecond(), 26, 65);
		}
		spriteBatch.end();
	}
	
	private void obstacleGeneration(float delta) {
		// generate obstacles
		if (-gameData.CameraYPosition + prevCameraYWhenObstacleIsGenerated >= gameData.ObstacleGeneratorDistance) {		
			obstaclePool.getOne(myRandom.getNext(), gameData, gameManager);
			prevCameraYWhenObstacleIsGenerated = gameData.CameraYPosition;
		}	
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
		if (gameManager.isGameActive()) {
			// first check if pause button is pressed and exit if it is
			if (pauseButton.isTouched(gameData, x, y) || !pauseButton.isGameActive()) {
				return;
			}
			
			gameManager.playMoveSound();
			if (x > Gdx.graphics.getWidth() / 2) {
				player.addVelocity(-gameData.VelocityX);
			}
			else {
				player.addVelocity(gameData.VelocityX);	
			}
			
			particles.addNew();
		}
	}
	
	@Override
	public void processTouchUp(ISceneManager sceneManager, int x, int y) {
		if (additionalTimer >= gameData.getAdsPause(gameManager.getCurrentScore())) {
			endGameButtons.check(gameData, x, y);
		}
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		spriteBatch.dispose();
		shapeRenderer.dispose();
		myFontDrawerBatch.dispose();
	}
}
