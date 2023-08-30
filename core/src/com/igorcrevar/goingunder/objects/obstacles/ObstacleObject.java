package com.igorcrevar.goingunder.objects.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.ObstacleTypeEnum;

// Sizes for 800x480 385 x 49, 297 x 49, 190 x 49, 139 x 49, 68 x 49
// 0.845 , 1.69 , 2.2995 ,  3.5 , 3.745 , 4.66
public class ObstacleObject {
	private static final int MaxParts = 3;
	private static final int MaxSprites = 20;
	
	private enum PartType {
		Left, Right, Both
	}
	
	private ObstacleTypeEnum obstacleType;
	private boolean doesPlayerPassObstacle;
	private boolean isEnabled;
	
	private int partsCount;
	private int spritesCount;
	
	private final float[] widths = new float[MaxParts];
	private final PartType[] types = new PartType[MaxParts];
	private final Sprite[] sprites = new Sprite[MaxSprites];
	private final Rectangle[] collisionRects = new Rectangle[MaxParts];
	
	public ObstacleObject() {
		for (int i = 0; i < this.collisionRects.length; ++i) {
			this.collisionRects[i] = new Rectangle();
		}
		for (int i = 0; i < this.sprites.length; ++i) {
			this.sprites[i] = new Sprite();
		}
		
		this.setIsEnabled(false);
	}
	
	private void initParts(float posY, float cameraHalfWidth,
			float sizeY, float emptySpaceSizeInTheMiddle, 
			float emptySpaceSizeOnTheEnd, AtlasRegion[] textures,
			float endPartSize, float normalPartSize) {
		this.partsCount = 2; // most of obstacles has two parts
		float availableWidth = cameraHalfWidth * 2f;
		float size = 0.0f;
		float posX = -cameraHalfWidth;

		switch (obstacleType) {
		case RightHole:
			this.partsCount = 1;
			types[0] = PartType.Right;
			widths[0] = availableWidth - emptySpaceSizeOnTheEnd;
			break;
		case LeftHole:
			this.partsCount = 1;
			types[0] = PartType.Left;
			widths[0] = availableWidth - emptySpaceSizeOnTheEnd;
			posX += emptySpaceSizeOnTheEnd;
			break;
		case LeftRightHoles:
			this.partsCount = 1;
			types[0] = PartType.Both;
			widths[0] = (cameraHalfWidth - emptySpaceSizeOnTheEnd) * 2;
			posX += emptySpaceSizeOnTheEnd;
			break;

		// more than one parts
		case LeftMiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 2;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = size;
			widths[1] = availableWidth - emptySpaceSizeInTheMiddle - size;
			break;
		case RightMiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 2;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = availableWidth - emptySpaceSizeInTheMiddle - size;
			widths[1] = size;
			break;
		case MiddleHole:
			size = (availableWidth - emptySpaceSizeInTheMiddle) / 2;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = size;
			widths[1] = size;
			break;
		case LeftRightMiddleHoles:
			this.partsCount = 3;
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 2;
			types[0] = PartType.Right;
			types[1] = PartType.Both;
			types[2] = PartType.Left;
			widths[0] = size;
			widths[1] = availableWidth - 2 * emptySpaceSizeInTheMiddle - 2 * size;
			widths[2] = size;
			break;
			
		case Left4Hole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 4;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = size;
			widths[1] = availableWidth - size - emptySpaceSizeInTheMiddle;
			break;
		case Left4MiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) * 0.75f;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = size;
			widths[1] = availableWidth - size - emptySpaceSizeInTheMiddle;
			break;
		case Right4Hole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) / 4;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = availableWidth - size - emptySpaceSizeInTheMiddle;
			widths[1] = size;
			break;
		case Right4MiddleHole:
			size = (availableWidth / 2 - emptySpaceSizeInTheMiddle) * 0.75f;
			types[0] = PartType.Right;
			types[1] = PartType.Left;
			widths[0] = availableWidth - size - emptySpaceSizeInTheMiddle;
			widths[1] = size;
			break;
		}
		
		int spritesCount = 0;
		
		for (int i = 0; i < this.partsCount; ++i) {
			collisionRects[i].set(posX, posY, widths[i], sizeY);
			if (textures.length == 1) {
				sprites[spritesCount].setBounds(posX, posY, widths[i], sizeY);
				sprites[spritesCount].setRegion(textures[0]);
				spritesCount++;
			} else {				
				spritesCount += createMultiSprite(
					spritesCount, posX, posY, widths[i], sizeY, types[i], textures,
					endPartSize, normalPartSize);
			}

			posX += emptySpaceSizeInTheMiddle + widths[i];
		}

		this.spritesCount = spritesCount;
	}

	private int createMultiSprite(
		int indx, float x, float y, 
		float width, float height,
		PartType type, AtlasRegion[] textures,
		float endPartSize, float normalPartSize) {
		int endParts = type == PartType.Both ? 2 : 1;
		
		if (type == PartType.Both || type == PartType.Left) {
			sprites[indx].setBounds(x, y, endPartSize, height);
			sprites[indx].setRegion(textures[0]);
			indx += 1;
			x    += endPartSize;
		}

		int parts = Math.round((width - endParts * endPartSize) / normalPartSize);
		float normalPartWidth = (width - endParts * endPartSize) / parts;
		
		for (int i = 0; i < parts; i++) {
			sprites[indx].setBounds(x, y, normalPartWidth, height);
			sprites[indx].setRegion(textures[1]);
			x    += normalPartWidth;
			indx += 1;
		}

		if (type == PartType.Both || type == PartType.Right) {
			sprites[indx].setBounds(x, y, endPartSize, height);
			sprites[indx].setRegion(textures[2]);
		}

		return parts + endParts;
	}
	
	public int getPartsCount() {
		return partsCount;
	}
	
	public void init(ObstacleTypeEnum obstacleType, GameData gameData, GameManager gameManager) {
		this.obstacleType = obstacleType;
		initParts(gameData.getCameraBottom() - gameData.ObstacleScaleY, 
				  gameData.CameraHalfWidth,
				  gameData.ObstacleScaleY,
				  gameData.EmptySpaceSizeInTheMiddle,
				  gameData.EmptySpaceSizeOnTheEnd,
				  gameData.getLevel().getTextures(),
				  gameData.getLevel().getEndPartSize(),
				  gameData.getLevel().getPartSize());
		this.setIsEnabled(true);
	}

	public void draw(SpriteBatch spriteBatch) {
		for (int i = 0; i < this.spritesCount; ++i) {
			sprites[i].draw(spriteBatch);
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setIsEnabled(boolean value) {
		if (!value) {
			this.partsCount = this.spritesCount = 0;
		}
		
		isEnabled = value;
		doesPlayerPassObstacle = false;
	}
	
	public float getBottom() {
		return sprites[0].getY() - sprites[0].getHeight();
	}
	
	public boolean isPassed() {
		return doesPlayerPassObstacle;
	}
	
	public void markAsPassed() {
		doesPlayerPassObstacle = true;
	}
	
	public Rectangle getBoundRectangle(int i) {
		return collisionRects[i];
	}
}
