package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.utils.GameHelper;

public class GameButtons {
	final protected float width;
	final protected float height;
	final protected GameButton[] gameButtons;

	public GameButtons(int size, float width) {
		this.gameButtons = new GameButton[size];
		float aspect = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		this.width = width;
		this.height = aspect * width;
	}

	public boolean check(float x, float y) {
		x = GameHelper.screenX2OtherX(x, width);
		y = GameHelper.screenY2OtherY(y, height);
		for (GameButton gb : gameButtons) {
			if (gb.check(x, y)) {
				return true;
			}
		}

		return false;
	}

	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		spriteBatch.begin();
		for (GameButton gb : gameButtons) {
			gb.draw(spriteBatch);
		}
		spriteBatch.end();
	}

	protected void addButton(GameButton gb) {
		for (int i = 0; i < gameButtons.length; ++i) {
			if (gameButtons[i] == null) {
				gameButtons[i] = gb;
				break;
			}
		}
	}

	protected float getHeight() {
		return height;
	}

	protected float getWidth() {
		return width;
	}
}
