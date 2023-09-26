package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.utils.Mathf;

public class Player implements IGameObject {
	public final float InitialAngle = 90.0f;
	
	private final Sprite gameObject;
	
	private float velocityX;
	private GameData gameData;
	private float rotationAngle;
	
	private boolean dieAnimationStarted;
	private float diePositionX;
	private float diePositionY;
	
	private final TextureRegion[] liveTextureRegions;
	private final TextureRegion[] dieTextureRegions;
	private float animationTimer;
	private int currentFrame;
	
	public Player(TextureRegion[] liveTextureRegions, TextureRegion[] dieTextureRegions) {
		this.gameObject = new Sprite();
		this.liveTextureRegions = liveTextureRegions;
		this.dieTextureRegions = dieTextureRegions;
	}

	public void addVelocity(float inc) {
		velocityX += inc;
		if (velocityX > gameData.MaxVelocityX) {
			velocityX = gameData.MaxVelocityX;
		}
		else if (velocityX < -gameData.MaxVelocityX) {
			velocityX = -gameData.MaxVelocityX;
		}
	}
	
	@Override
	public void init(Object data) {
		this.gameData = (GameData)data;
		this.rotationAngle = InitialAngle;
		this.velocityX = 0.0f;
		gameObject.setSize(gameData.PlayerSizeX, gameData.PlayerSizeY);
		gameObject.setOrigin(gameData.PlayerSizeX / 2.0f, gameData.PlayerSizeY / 2.0f);
		gameObject.setScale(1.0f, 1.0f);
		gameObject.setRotation(0.0f);
		gameObject.setPosition(-gameData.PlayerSizeX / 2, -gameData.PlayerSizeY / 2 + gameData.CameraDist);
		gameObject.setRegion(liveTextureRegions[0]);
		this.dieAnimationStarted = false;
		this.animationTimer = 0.0f;
		this.currentFrame = 0;
	}

	@Override
	public void update(float deltaTime) {
		// rotation
		float minAngle = InitialAngle - gameData.RotationMaxAngle;
		float maxAngle = InitialAngle + gameData.RotationMaxAngle;
		if (velocityX > 0.0f) {
			rotationAngle -= gameData.RotationSpeed * deltaTime;
			rotationAngle = Math.max(rotationAngle, minAngle);
		} else if (velocityX < 0.0f) {
			rotationAngle += gameData.RotationSpeed * deltaTime;
			rotationAngle = Math.min(rotationAngle, maxAngle);
		} else if (rotationAngle > InitialAngle) {
			rotationAngle -= gameData.RotationSpeed * deltaTime;
			rotationAngle = Math.max(rotationAngle, InitialAngle);
		} else if (rotationAngle < InitialAngle) {
			rotationAngle += gameData.RotationSpeed * deltaTime;
			rotationAngle = Math.min(rotationAngle, InitialAngle);
		}
		
		// set rotation
		this.gameObject.setRotation(getAngle());

		// calculate new position
		float newX = gameObject.getX() + velocityX * deltaTime;
		float newY = gameObject.getY() + gameData.VelocityY * deltaTime;
						
		// bouncing on left/right boundaries
		this.gameObject.setPosition(newX, newY);
		if (velocityX > 0.0f && newX + gameData.PlayerSizeX  >= gameData.CameraHalfWidth || 
			velocityX < 0.0f && newX <= -gameData.CameraHalfWidth) {
			velocityX = Math.abs(velocityX * gameData.BoundariesBouncingFactor);
			if (newX > 0.0f) {
				velocityX = -velocityX;
				newX = gameData.CameraHalfWidth - gameData.PlayerSizeX;
			}
			else {
				newX = -gameData.CameraHalfWidth;
			}
		}
		
		// update position and rotation
		this.gameObject.setPosition(newX, newY);
		
		// update X velocity (with friction)
		if (velocityX > 0.0f) {
			velocityX -= gameData.Friction * deltaTime;
			if (velocityX < 0.0f) {
				velocityX = 0.0f;
			}
		} else if (velocityX < 0.0f) {
			velocityX += gameData.Friction * deltaTime;
			if (velocityX > 0.0f) {
				velocityX = 0.0f;
			}
		}
		
		// update animation
		updateAnimation(liveTextureRegions, deltaTime);
	}
	
	@Override
	public void draw(SpriteBatch spriteBatch) {
		gameObject.draw(spriteBatch);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setIsEnabled(boolean value) {
		
	}
	
	public void updateDie(float additionalTimer, float deltaTime) {
		if (!dieAnimationStarted) {
			dieAnimationStarted = true;
			animationTimer = 0.0f;
			currentFrame = 0;
			gameObject.setRegion(dieTextureRegions[0]);
			diePositionX = this.gameObject.getX();
			diePositionY = this.gameObject.getY();
		}
		
		float playerDiedAnimTime1 = 1.0f;
		if (additionalTimer <= playerDiedAnimTime1) {
			float realTime = additionalTimer / playerDiedAnimTime1;
			float scale = Mathf.lerp(1.0f, 4.0f, realTime);
			float nx = Mathf.lerp(diePositionX, -gameObject.getWidth() / 2, realTime);
			float ny = Mathf.lerp(diePositionY, gameData.CameraYPosition, realTime);
			gameObject.setScale(scale, scale);
			gameObject.setPosition(nx, ny);
		} else if (gameObject.getScaleY() > 0.0f) {
			float realTimer = (additionalTimer - playerDiedAnimTime1) / 1.5f;
			float scale = Mathf.lerp(4.0f, 0.0f, realTimer);
			gameObject.setScale(scale);
			gameObject.rotate(2.0f * 360.0f * deltaTime);
		}
		
		// animation
		updateAnimation(dieTextureRegions, deltaTime);
	}
	
	public Rectangle getBoundingRectangle() {
		return gameObject.getBoundingRectangle();
	}
	
	public float getAngle() {
		return rotationAngle > InitialAngle ? rotationAngle - InitialAngle : rotationAngle + (360.0f - InitialAngle);
	}
	
	public float getX() {
		return gameObject.getX() + gameData.PlayerSizeX / 2.0f;
	}
	
	public float getY() {
		return gameObject.getY() + gameData.PlayerSizeY / 2.0f;
	}

	public float getVelocityX() {
		return this.velocityX;
	}
	
	private void updateAnimation(TextureRegion[] textures, float deltaTime) {
		// [0 - 0.99]
		float tmp = animationTimer - (int)animationTimer + 0.0001f;
		// calculate current fame
		int desiredFrame = (int)Math.ceil(tmp * textures.length) - 1;
		if (desiredFrame < 0) desiredFrame = 0;
		else if (desiredFrame >= textures.length) desiredFrame = textures.length - 1;
		// only swap textures if needed
		if (desiredFrame != currentFrame) {
			currentFrame = desiredFrame;
			gameObject.setRegion(textures[currentFrame]);
		}
		
		animationTimer += deltaTime;
	}
	
	private Vector2[] points = null;
	private Color tmpColor2 = new Color(1f, 1f, 1f, 0.2f); 
	private Color tmpColor1 = new Color(1f, 1f, 1f, 0.0f);
	public void drawLights(ShapeRenderer shapeRenderer, GameData gameData) {
		if (dieAnimationStarted) return;
		if (points == null) {
			points = new Vector2[6];
			for (int i = 0; i < points.length; ++i) {
				points[i] = new Vector2();
			}
		}
		// draw lights
		float inX = gameObject.getX() + gameData.PlayerSizeX / 2.0f;
		float inY = gameObject.getY() + gameData.PlayerSizeY / 2.0f - gameData.CameraYPosition;
		float angle = (float)(getAngle() / 180.f * Math.PI);
		float maxY = -2.2f;
		points[2].set(- 0.15f, - 0.4f);
		points[1].set(+ 0.15f, - 0.4f);
		points[0].set(- 0.9f, maxY);		
		points[5].set(+ 0.15f, - 0.4f);
		points[4].set(+ 0.9f, maxY);
		points[3].set(- 0.9f, maxY);
		for (int i = 0; i < points.length; ++i) {
			points[i].rotateRad(angle);
			points[i].add(inX, inY);
		}
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.begin(ShapeType.Filled);
		for (int i = 0; i < points.length / 3; ++i) {
			Vector2 p1 = points[i * 3];
			Vector2 p2 = points[i * 3 + 1];
			Vector2 p3 = points[i * 3 + 2];
			Color cl = i == 0 ? tmpColor2 : tmpColor1;
			shapeRenderer.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, tmpColor1, cl, tmpColor2);
		}
		shapeRenderer.end();
	}
}

