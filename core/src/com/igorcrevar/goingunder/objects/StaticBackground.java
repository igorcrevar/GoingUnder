package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;

public class StaticBackground implements IGameObject {
	private static final float SpeedFactor = 0.6f;
	
	private final Sprite sprite1;
	private final Sprite sprite2;

	private GameData gameData;

	private float yPos = 0.0f;

	public StaticBackground(GameManager gameManager) {
		sprite1 = new Sprite(gameManager.getTextureAtlas("game").findRegion("background"));
		sprite2 = new Sprite(gameManager.getTextureAtlas("game").findRegion("background"));
	}

	@Override
	public void init(Object odata) {
		gameData = (GameData)odata;
		yPos = 0.0f;
		sprite1.setSize(gameData.CameraHalfWidth*2, gameData.CameraHalfHeight*2);
		sprite2.setSize(gameData.CameraHalfWidth*2, gameData.CameraHalfHeight*2);
		sprite1.setX(-gameData.CameraHalfWidth);
		sprite2.setX(-gameData.CameraHalfWidth);
		sprite2.flip(false, true);
		sprite1.setY(gameData.getCameraBottom());
		sprite2.setY(gameData.getCameraBottom() - gameData.CameraHalfHeight*1.99f);
	}

	@Override
	public void update(float deltaTime) {
		float size = gameData.CameraHalfHeight*1.99f;
		// must always move, because camera is moving
		yPos -= gameData.VelocityY * deltaTime * SpeedFactor;
		if (yPos >= size * 2f) {
			yPos = 0.0f;
		}

		// after 2.0f first sprite goes bellow second one
		float offset = (float)Math.floor(yPos / size) * 2f;
	
		sprite1.setY(gameData.getCameraBottom() + yPos - size * offset);
		sprite2.setY(gameData.getCameraBottom() + yPos - size);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		sprite1.draw(spriteBatch);
		sprite2.draw(spriteBatch);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setIsEnabled(boolean value) {

	}
}
