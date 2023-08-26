package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;

public class StaticBackground implements IGameObject {
	private final Sprite sprite;
	private GameData gameData;

	private float yPos = 0.0f;

	public StaticBackground(GameManager gameManager) {
		this.sprite = new Sprite(gameManager.getTextureAtlas("game").findRegion("background"));
	}

	@Override
	public void init(Object odata) {
		gameData = (GameData)odata;
		// stupid libgdx: y position is bottom and height goes up!
		this.sprite.setBounds(
				-gameData.CameraHalfWidth, -gameData.CameraHalfHeight * 3,
				gameData.CameraHalfWidth * 2, gameData.CameraHalfHeight * 4);
		yPos = 0.0f;
	}

	@Override
	public void update(float deltaTime) {
		// must always move, because camera is moving
		yPos -= gameData.VelocityY * deltaTime;
		if (yPos >= gameData.CameraHalfHeight * 2) {
			this.sprite.setY(this.sprite.getY() - gameData.CameraHalfHeight * 2);
			yPos = 0.0f;
		}
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
