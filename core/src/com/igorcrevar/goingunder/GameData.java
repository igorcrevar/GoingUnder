package com.igorcrevar.goingunder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.igorcrevar.goingunder.statemachine.IMyRandom;

public class GameData {
	private static final float CameraHalfWidthValue = 2.9f;
	
	// begin level dependant
	public float BoundariesBouncingFactor;
	public float VelocityY;
	public float VelocityX;
	public float EmptySpaceSizeInTheMiddle;
	public float EmptySpaceSizeOnTheEnd;
	public float ObstacleGeneratorDistance;
	public float Friction;
	public float FishRandomProbability;
	// end level dependant	
	
	public float RotationSpeed = 20.0f;
	public float RotationMaxAngle = 10.0f;
	public float ObstacleScaleY = 0.5f;
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
	
	private int levelIdx = 0; // goes from 0...n - 1
	private final AGameLevel[] levels;

	public GameData(float cameraDist, AGameLevel[] levels) {
		// init world height
		float height = Gdx.graphics.getHeight();
		float width = Gdx.graphics.getWidth();
		if (Math.abs(width) < 5.0f || Math.abs(height) < 5.0f) {
			width = 480.0f;
			height = 800.0f;
		}

		this.CameraDist = cameraDist;
		this.CameraHalfWidth = CameraHalfWidthValue;
		this.CameraHalfHeight = height / width * CameraHalfWidthValue;
		this.levels = levels;
		this.levels[0].copyTo(this);
	}
	
	public void init(IMyRandom myRandom) {
		this.CameraYPosition = 0.0f;
		this.levelIdx = 0;
		this.levels[0].copyTo(this);
		myRandom.setGenerator(this.levels[0].getGeneratorStateMachine());
	}
	
	public void update(int score, IMyRandom myRandom) {
		// got to the next level if current one is on its end
		if (this.levels[this.levelIdx].isEndOfLevel(score) && this.levelIdx + 1 < this.levels.length) {
			this.levelIdx++;
			this.levels[this.levelIdx].copyTo(this);
			myRandom.setGenerator(this.levels[this.levelIdx].getGeneratorStateMachine());
		}
	}

	public static GameData createDefault(GameManager gameManager) {
		return new GameData(2.8f, GameDataLevelFactory.createLevels(gameManager));
	}
	
	public static GameData createForIntro(GameManager gameManager) {
		return new GameData(0.4f, GameDataLevelFactory.createDefaultLevel(gameManager));
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
		return this.levels[this.levelIdx];
	}
}
