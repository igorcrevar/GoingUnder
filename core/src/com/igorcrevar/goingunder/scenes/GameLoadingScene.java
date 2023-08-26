package com.igorcrevar.goingunder.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.igorcrevar.goingunder.IActivityRequestHandler;
import com.igorcrevar.goingunder.ISceneManager;
import com.igorcrevar.goingunder.utils.BitmapFontDrawer;
import com.igorcrevar.goingunder.utils.GameHelper;

public class GameLoadingScene implements IScene {
	private final SpriteBatch spriteBatch;
	private boolean isDisposed;
	
	private IActivityRequestHandler activityRequestHandler;
	
	public GameLoadingScene(IActivityRequestHandler activityRequestHandler) {
		this.spriteBatch = new SpriteBatch();
		this.activityRequestHandler = activityRequestHandler;
	}
	
	@Override
	public void create(ISceneManager sceneManager) {
	}
	
	@Override
	public void init(ISceneManager sceneManager) {
		activityRequestHandler.showAds(true);
	}

	@Override
	public void update(ISceneManager sceneManager, float deltaTime) {
		GameHelper.clearScreen();
		if (sceneManager.getGameManager().updateLoading()) {
			sceneManager.setScene(SceneConstants.IntroScene);
			return;
		}

		BitmapFontDrawer bfDrawer = sceneManager.getGameManager().getBitmapFontDrawer();
		if (bfDrawer != null) {
			String txt = "loading...";
			bfDrawer.begin()
					.setColor(Color.WHITE)
					.setScale(1.0f)
					.draw(txt, 0.0f, 0.0f, BitmapFontDrawer.Flag.Center, BitmapFontDrawer.Flag.Center)
					.end();
		}
	}

	@Override
	public void leave(ISceneManager sceneManager) {
		dispose(sceneManager);
	}

	@Override
	public void processTouchDown(ISceneManager sceneManager, int x, int y) {
	}

	@Override
	public boolean processBackKey(ISceneManager sceneManager) {
		Gdx.app.exit();

		return true;
	}

	@Override
	public void dispose(ISceneManager sceneManager) {
		if (!isDisposed) {
			spriteBatch.dispose();
			isDisposed = true;
		}
	}
}