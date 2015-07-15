package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.utils.GameHelper;

public class EndGameButtons {
	// buttons
	private GameButton[] buttons;
	private final GameManager gameManager;
	
	public EndGameButtons(final ISceneManager sceneManager,
			final IActivityRequestHandler activityRequestHandler,
			final GameManager gameManager) {
		this.gameManager = gameManager;
		buttons = new GameButton[3];

		float buttonsY = 500;

		addButton(new GameButton(getTextureRegion("intro"), 90, buttonsY, 240, 240) {
			@Override
			protected void onClick() {
				sceneManager.setScene(ISceneManager.IntroScene);
			}
		});

		addButton(new GameButton(getTextureRegion("playagain"), 750, buttonsY, 240, 240) {
			@Override
			protected void onClick() {
				sceneManager.startGame();
			}
		});

		addButton(new GameButton(getTextureRegion("share"), 420, buttonsY, 240, 240) {
			@Override
			protected void onClick() {
				activityRequestHandler.share(gameManager.getCurrentScore());
			}
		});
	}

	private GameButton addButton(GameButton gb) {
		for (int i = 0; i < buttons.length; ++i) {
			if (buttons[i] == null) {
				buttons[i] = gb;
				break;
			}
		}

		return gb;
	}

	public void check(GameData gameData, float x, float y) {
		x = GameHelper.screenX2OtherX(x, 1080);
		y = GameHelper.screenY2OtherY(y, 1920);
		for (GameButton gb : buttons) {
			if (gb.check(x, y)) {
				break;
			}
		}
	}

	public void draw(SpriteBatch spriteBatch) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1080, 1920);
		spriteBatch.begin();
		for (GameButton gb : buttons) {
			gb.draw(spriteBatch);
		}
		spriteBatch.end();		
	}

	private TextureRegion getTextureRegion(String name) {
		TextureRegion tr = gameManager.getTextureAtlas("widgets").findRegion(name);
		return tr;
	}
}
