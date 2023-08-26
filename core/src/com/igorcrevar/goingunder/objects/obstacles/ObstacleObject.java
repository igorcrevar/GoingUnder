package com.igorcrevar.goingunder.objects.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.ObstacleTypeEnum;

// Sizes for 800x480 385 x 49, 297 x 49, 190 x 49, 139 x 49, 68 x 49
// 0.845 , 1.69 , 2.2995 ,  3.5 , 3.745 , 4.66
public class ObstacleObject {
	private static final int MaxParts = 3;
	private static final float DefaultYPos = 20000.0f;
	
	private ObstacleTypeEnum obstacleType;
	private Sprite[] partsObject;
	
	private boolean doesPlayerPassObstacle;
	private boolean isEnabled;
	
	private IObstaclePool obstaclePool;
	private int partsCount;
	
	private float[] tmp = new float[MaxParts];
	
	public ObstacleObject(IObstaclePool obstaclePool) {
		this.obstaclePool = obstaclePool;
		this.obstacleType = ObstacleTypeEnum.LeftHole;
		
		this.partsObject = new Sprite[MaxParts];
		for (int i = 0; i < MaxParts; ++i) {
			this.partsObject[i] = new Sprite();
			this.partsObject[i].setPosition(0.0f, DefaultYPos);
		}
		
		partsCount = 1;
		this.setIsEnabled(false);
	}
	
	private void initOnePart(float posY, float cameraHalfWidth, float sizeY,
			float emptySpaceSizeInTheMiddle, float emptySpaceSizeOnTheEnd) {
		float availableWidth = 0.0f;
		float posX = 0.0f;
		
		switch (this.obstacleType) {
		// case RightHole:
		default:
			availableWidth = cameraHalfWidth * 2 - emptySpaceSizeOnTheEnd;
			posX = availableWidth / 2 - cameraHalfWidth;
			break;
		case LeftHole:
			availableWidth = cameraHalfWidth * 2 - emptySpaceSizeOnTheEnd;
			posX = availableWidth / 2 - cameraHalfWidth + emptySpaceSizeOnTheEnd;
			break;
		case LeftRightHoles:
			availableWidth = (cameraHalfWidth - emptySpaceSizeOnTheEnd) * 2;
			posX = availableWidth / 2 - cameraHalfWidth + emptySpaceSizeOnTheEnd;
			break;			
		}
		
		Sprite part = partsObject[0];
		part.setSize(availableWidth, sizeY);
		part.setPosition(posX - availableWidth / 2.0f, posY);
	}
	
	private void initMoreThanOnePart(float posY, float cameraHalfWidth,
			float sizeY, float emptySpaceSizeInTheMiddle, float emptySpaceSizeOnTheEnd) {
		float availableWidth = cameraHalfWidth * 2;
		float[] width = tmp;
		int widths = 0;
		float size = 0.0f;
		
		switch (obstacleType) {
		//case LeftMiddleHole:
		default:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 2;
			width[0] = size;
			width[1] = availableWidth - emptySpaceSizeInTheMiddle - size;
			widths = 2;
			break;
		case RightMiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 2;
			width[0] = availableWidth - emptySpaceSizeInTheMiddle - size;
			width[1] = size;
			widths = 2;
			break;
		case MiddleHole:
			size = (availableWidth - emptySpaceSizeInTheMiddle) / 2;
			width[0] = size;
			width[1] = size;
			widths = 2;
			break;
		case LeftRightMiddleHoles:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 2;
			width[0] = size;
			width[1] = availableWidth - 2 * emptySpaceSizeInTheMiddle - 2 * size;
			width[2] = size;
			widths = 3;
			break;
			
		case Left4Hole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 4;
			width[0] = size;
			width[1] = availableWidth - size - emptySpaceSizeInTheMiddle;
			widths = 2;
			break;
		case Left4MiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) * 0.75f;
			width[0] = size;
			width[1] = availableWidth - size - emptySpaceSizeInTheMiddle;
			widths = 2;
			break;
		case Right4Hole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 4;
			width[0] = availableWidth - size - emptySpaceSizeInTheMiddle;
			width[1] = size;
			widths = 2;
			break;
		case Right4MiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) * 0.75f;
			width[0] = availableWidth - size - emptySpaceSizeInTheMiddle;
			width[1] = size;
			widths = 2;
			break;
		}
		
		float posX = -cameraHalfWidth;
		for (int i = 0; i < widths; ++i) {
			Sprite part = partsObject[i];
			part.setSize(width[i], sizeY);
			part.setPosition(posX, posY);
			posX += emptySpaceSizeInTheMiddle + width[i];
		}
	}
	
	public int getPartsCount() {
		return partsCount;
	}
	
	private void initPartsCount() {
		switch (obstacleType) {
		case LeftHole: case RightHole: case LeftRightHoles:		
			partsCount = 1;
			break;
		case LeftRightMiddleHoles:
			partsCount = 3;
			break;
		default:
			//case MiddleHole: case LeftMiddleHole: case RightMiddleHole: 
			//case Left4Hole: case Right4Hole: case Left4MiddleHole: case Right4MiddleHole
			partsCount = 2;
			break;
		}		
	}

	public void init(ObstacleTypeEnum obstacleType, GameData gameData, GameManager gameManager) {
		this.obstacleType = obstacleType;
		this.initPartsCount();
		float positionY = gameData.getCameraBottom() - gameData.ObstacleScaleY;
		
		switch (partsCount) {
		case 1:
			initOnePart(positionY, gameData.CameraHalfWidth, 
					gameData.ObstacleScaleY,
					gameData.EmptySpaceSizeInTheMiddle, gameData.EmptySpaceSizeOnTheEnd);
			break;
		default:
			initMoreThanOnePart(positionY, gameData.CameraHalfWidth, gameData.ObstacleScaleY,
					gameData.EmptySpaceSizeInTheMiddle, gameData.EmptySpaceSizeOnTheEnd);
			break;	
		}
		
		// init textures
		for (int i = 0; i < partsCount; ++i) {
			Sprite part = partsObject[i];
			gameData.resolveObstacleTexture(part, gameManager);
		}
		
		// enable obstacle
		this.setIsEnabled(true);
	}

	public void draw(SpriteBatch spriteBatch) {
		for (int i = 0; i < partsCount; ++i) {
			partsObject[i].draw(spriteBatch);
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setIsEnabled(boolean value) {
		if (!value) {
			setYPosition(DefaultYPos);
			// remove from visible obstacles pool
			obstaclePool.removeFromVisibles(this);
		}
		
		isEnabled = value;
		doesPlayerPassObstacle = false;
	}
	
	public float getBottom() {
		Sprite part = partsObject[0];
		return part.getY() - part.getHeight();
	}
	
	public boolean isPassed() {
		return doesPlayerPassObstacle;
	}
	
	public void markAsPassed() {
		doesPlayerPassObstacle = true;
	}
	
	public Rectangle getBoundRectangle(int i) {
		return partsObject[i].getBoundingRectangle();
	}
	
	private void setYPosition(float y) {
		for (int i = 0; i < partsCount; ++i) {
			Sprite part = partsObject[i];
			part.setY(y);
		}
	}
}
