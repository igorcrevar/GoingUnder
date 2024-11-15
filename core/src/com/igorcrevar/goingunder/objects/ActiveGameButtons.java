package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.utils.GameHelper;

public class ActiveGameButtons extends GameButtons {
	private final static int ExtraSize = 200;
	private final GameManager gameManager;

	public ActiveGameButtons(final GameManager gameManager) {
		super(1, 1080.0f);
		this.gameManager = gameManager;

		addButton(new GameButton(getRegionForPausedButton(false), 90.0f, getHeight() - 20.0f, 120.0f, 80.0f) {
			@Override
			protected boolean onClick() {
				switch (gameManager.getGameStatus()) {
					case Paused:
						this.changeTexture(getRegionForPausedButton(false));
						gameManager.setGameStatus(GameManager.Status.Resuming);
						return true;
					case Active:
						this.changeTexture(getRegionForPausedButton(true));
						gameManager.setGameStatus(GameManager.Status.Paused);
						return true;
					default:
						return false;
				}
			}

			@Override
			public boolean check(float x, float y) {
				if (!isEnabled || !GameHelper.tapPointInsideRectangle(
						x, y, 0, ActiveGameButtons.this.height,
						sprite.getWidth() + ExtraSize,
						sprite.getHeight() + ExtraSize)) {
					return false;
				}

				return this.onClick();
			}
		});
	}

	public void init() {
		gameButtons[0].changeTexture(getRegionForPausedButton(false));
	}

	private TextureAtlas.AtlasRegion getRegionForPausedButton(boolean isPaused) {
		return gameManager.getTextureAtlas("game").findRegion(
				isPaused ? "resume" : "pause");
	}
}
