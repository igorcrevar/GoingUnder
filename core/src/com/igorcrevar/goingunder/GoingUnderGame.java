package com.igorcrevar.goingunder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.igorcrevar.goingunder.scenes.GameLoadingScene;
import com.igorcrevar.goingunder.scenes.GameScene;
import com.igorcrevar.goingunder.scenes.IScene;
import com.igorcrevar.goingunder.scenes.IntroScene;
import com.igorcrevar.goingunder.scenes.SceneConstants;
import com.igorcrevar.goingunder.scenes.TutorialScene;
import com.igorcrevar.goingunder.statemachine.MyRandom;

public class GoingUnderGame extends ApplicationAdapter implements ISceneManager, InputProcessor {
	final int TutorialGamesCount = 3;
	private IScene introScene;
	private IScene gameScene;
	private GameManager gameManager;
	private IScene currentScene;
	private final IActivityRequestHandler requestHandler;
	public GoingUnderGame() {
		this(new DummyActivityRequestHandler());
	}

	public GoingUnderGame(IActivityRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	@Override
	public void create () {
		this.gameManager = new GameManager();
		this.setScene(SceneConstants.GameLoadingScene);
		// input processor
		Gdx.input.setCatchKey(Input.Keys.BACK,true);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (deltaTime > 0.041f) deltaTime = 0.041f; // make sure world does not update dramatically in one step
		currentScene.update(this, deltaTime);
	}
	
	@Override
	public void dispose () {
		currentScene.leave(this);
		if (gameScene != null && gameScene != currentScene) {
			gameScene.dispose(this);
		}
		if (introScene != null && introScene != currentScene) {
			introScene.dispose(this);
		}
		if (currentScene != null) {
			currentScene.dispose(this);
		}
		gameManager.dispose();
	}

	@Override
	public GameManager getGameManager() {
		return gameManager;
	}

	@Override
	public void setScene(String sceneName) {
		if (currentScene != null) {
			currentScene.leave(this);
		}

		switch (sceneName) {
			case SceneConstants.TutorialScene:
				currentScene = new TutorialScene(requestHandler);
				currentScene.create(this);
				break;
			case SceneConstants.GameLoadingScene:
				currentScene = new GameLoadingScene(requestHandler);
				break;
			case SceneConstants.GameScene:
				if (gameScene != null) {
					currentScene = gameScene;
				}
				else {
					gameScene = currentScene = new GameScene(new MyRandom(), requestHandler);
					currentScene.create(this);
				}
				break;
			case SceneConstants.IntroScene:
				if (introScene != null) {
					currentScene = introScene;
				}
				else {
					introScene = currentScene = new IntroScene(requestHandler);
					currentScene.create(this);
				}
				break;
			default:
				return;
		}

		currentScene.init(this);
	}

	@Override
	public void startGame() {
		// show tutorial for some number of games
		if (gameManager.getTotalGamesPlayed() > TutorialGamesCount) {
			setScene(SceneConstants.GameScene);
		}
		else {
			setScene(SceneConstants.TutorialScene);
		}
	}

	@Override
	public void finishGame() {
		gameManager.setGameStatus(GameManager.Status.NotActive);
		gameManager.saveScoreEtc();
		requestHandler.finishGame(gameManager);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.Z) {
			currentScene.processTouchDown(this, 0, Gdx.graphics.getHeight());
		}
		else if (keycode == Input.Keys.X) {
			currentScene.processTouchDown(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
			return currentScene.processBackKey(this);
		}

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		currentScene.processTouchDown(this, screenX, screenY);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
