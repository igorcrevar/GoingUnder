package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;

public class StaticBackground implements IGameObject {
	private Sprite sprite;
	private GameData gameData;
	
	public StaticBackground(GameManager gameManager) {
		this.sprite = new Sprite(gameManager.getTextureAtlas("game").findRegion("background"));
	}
	
	@Override
	public void init(Object odata) {
		gameData = (GameData)odata;
		if (sprite.getRegionWidth() > sprite.getRegionHeight()) {
			this.sprite.setOrigin(gameData.CameraHalfWidth, gameData.CameraHalfWidth);
			this.sprite.setSize(gameData.CameraHalfWidth * 2, gameData.CameraHalfHeight * 2);
			sprite.setPosition(-gameData.CameraHalfWidth, -gameData.CameraHalfHeight);			
		}
		else {
			this.sprite.setOrigin(gameData.CameraHalfHeight, gameData.CameraHalfWidth);
			this.sprite.setSize(gameData.CameraHalfHeight * 2, gameData.CameraHalfWidth * 2);
			this.sprite.setRotation(90);
			sprite.setPosition(-gameData.CameraHalfHeight, -gameData.CameraHalfWidth);
		}
	}

	@Override
	public void update(float deltaTime) {
		// must always move, because camera is moving
		this.sprite.setY(this.sprite.getY() + gameData.VelocityY * deltaTime);
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		sprite.draw(spriteBatch);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public void setIsEnabled(boolean value) {
		
	}
}
