package com.igorcrevar.goingunder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.igorcrevar.goingunder.statemachine.IMyRandom;

public class GameData {
	private static final float CameraHalfWidthValue = 2.9f;
	private static final float CameraHalfHeightValue;
	public static float AspectRatio;
	
	// begin level dependant
	public float BoundariesBouncingFactor;
	public float VelocityY;
	public float VelocityX;
	public float EmptySpaceSizeInTheMiddle;
	public float EmptySpaceSizeOnTheEnd;
	public float ObstacleGeneratorDistance;
	public float Friction;
	// end level dependant	
	
	public float RotationSpeed = 20.0f;
	public float RotationMaxAngle = 10.0f;
	public float ObstacleScaleY = 0.5f;;
	public int MaxOnScreenAtOnce = 4;
	public float MaxVelocityX = 8.0f;
	public float CameraYPosition = 0.0f;
	public float PlayerSizeX = 1.0f;
	public float PlayerSizeY = 1.0f;
	
	public float CameraDist;
	public float CameraHalfWidth;
	public float CameraHalfHeight;	
	
	public boolean DieOnCollision = true;
	public boolean DrawCollision = false;
	public boolean DrawFPS = false;
	
	public float BubbleSpeedY;
	public float BubbleSpeedYPotential;
	public float BubbleSize = 0.15f;
	
	private int level = 0; // goes from 0...n - 1
	private AGameLevel[] levels;
	private AGameLevel currentGameLevel;
	
	public void init(IMyRandom myRandom) {
		CameraYPosition = 0.0f;
		setCurrentGameLevel(0, myRandom);
	}
	
	public void update(int score, IMyRandom myRandom) {
		// next level if current one is on his end
		if (currentGameLevel.isEndOfLevel(score)) {
			setCurrentGameLevel(this.level + 1, myRandom);
		}
	}
	
	private void setCurrentGameLevel(int newLevel, IMyRandom myRandom) {
		this.level = newLevel;
		this.currentGameLevel = this.levels[this.level];
		this.currentGameLevel.copyTo(this);
		myRandom.setGenerator(this.currentGameLevel.getGeneratorStateMachine());
	}
	
	static {
		// init world height		
		float height = Gdx.graphics.getHeight();
		float width = Gdx.graphics.getWidth();
		if (Math.abs(width) < 5.0f || Math.abs(height) < 5.0f) {
			width = 480.0f;
			height = 800.0f;
		}
		
		AspectRatio = height / width;
		CameraHalfHeightValue = AspectRatio * CameraHalfWidthValue;
	}
	
	public static GameData createDefault(GameManager gameManager) {
		GameData playerData = new GameData();
		playerData.CameraDist = 2.8f;
		playerData.CameraHalfHeight = CameraHalfHeightValue;
		playerData.CameraHalfWidth = CameraHalfWidthValue;
		playerData.levels = GameDataLevelFactory.createLevels(gameManager);

		return playerData;
	}
	
	public static GameData createForIntro() {
		GameData playerData = new GameData();
		playerData.Friction = 3.5f;
		playerData.VelocityX = 2.4f;
		playerData.VelocityY = -2.4f;
		playerData.CameraDist = 0.4f;
		playerData.CameraHalfHeight = CameraHalfHeightValue;
		playerData.CameraHalfWidth = CameraHalfWidthValue;
		
		return playerData;
	}
	
	public float getCameraTop() {
		return CameraYPosition + CameraHalfHeight;
	}
	
	public float getCameraBottom() {
		return CameraYPosition - CameraHalfHeight;
	}

	public float getCameraYCenter() {
		return CameraYPosition;
	}
	
	public void setProjectionMatrix(Matrix4 matrix) {
		matrix.setToOrtho(-CameraHalfWidth, CameraHalfWidth, getCameraBottom(), getCameraTop(), 0.0f, 1000.0f);
	}
	
	public void setProjectionMatrix(Matrix4 matrix, float yOffset) {
		matrix.setToOrtho(-CameraHalfWidth, CameraHalfWidth, getCameraBottom() - yOffset, getCameraTop() - yOffset, 0.0f, 1000.0f);
	}

	public float getAdsPause(float score) {
		return score < 4 ? 2.5f : 3.0f;
	}

	public AGameLevel getLevel() {
		return currentGameLevel;
	}
}
