package com.igorcrevar.goingunder;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.igorcrevar.goingunder.scenes.GameScene;
import com.igorcrevar.goingunder.scenes.IScene;
import com.igorcrevar.goingunder.scenes.IntroScene;
import com.igorcrevar.goingunder.statemachine.MyRandom;

public class GameListener implements ApplicationListener, ISceneManager, InputProcessor {
	// keep to main scenes always in memory
	private IScene introScene;
	private IScene gameScene;
	
	private GameManager gameManager;
	private IScene currentScene;
	private IActivityRequestHandler requestHandler;
	
	public GameListener(IActivityRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}
	
	@Override
	public void create() {
		this.gameManager = new GameManager();
		this.requestHandler.setGameManager(this.gameManager);
		this.setScene(ISceneManager.GameLoadingScene);
		// input processor
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
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
	public void pause() {
		gameManager.savePreferences();	
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (deltaTime > 0.041f) deltaTime = 0.041f; // make sure world does not update dramatically in one step
		currentScene.update(this, deltaTime);
	}

	@Override
	public void resize(int arg0, int arg1) {
		
	}

	@Override
	public void resume() {
		
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
		case ISceneManager.TutorialScene:
			currentScene = new com.igorcrevar.goingunder.scenes.TutorialScene(requestHandler);
			currentScene.create(this);
			break;
		case ISceneManager.GameLoadingScene:
			currentScene = new com.igorcrevar.goingunder.scenes.GameLoadingScene(requestHandler);
			break;
		case ISceneManager.GameScene:
			if (gameScene != null) {
				currentScene = gameScene;
			}
			else {
				gameScene = currentScene = new GameScene(new MyRandom(), requestHandler);
				currentScene.create(this);
			}
			break;
		case ISceneManager.IntroScene:
			if (introScene != null) {
				currentScene = introScene;
			}
			else {
				introScene = currentScene = new IntroScene(requestHandler);
				currentScene.create(this);
			}
			break;
		}
		
		currentScene.init(this);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.Z) {
			currentScene.processTouchDown(this, 0, 0);
		}
		else if (keycode == Keys.X) {
			currentScene.processTouchDown(this, Gdx.graphics.getWidth(), 0);
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			if (currentScene == gameScene) {
				if (gameManager.isGameActive()) {
					setScene(ISceneManager.IntroScene);
				}
			}
			else if (currentScene instanceof com.igorcrevar.goingunder.scenes.TutorialScene) {
				setScene(ISceneManager.IntroScene);
			}
			else {
				Gdx.app.exit();
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		currentScene.processTouchDown(this, screenX, screenY);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		currentScene.processTouchUp(this, screenX, screenY);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startGame() {
		// show tutorial if this is first game or (second game and player not made 5 already) 
		if (gameManager.getTotalGamesPlayed() >= 2 || gameManager.getTotalScoreSum() > 5) {
			setScene(ISceneManager.GameScene);
		}
		else {
			setScene(ISceneManager.TutorialScene);
		}
	}

	@Override
	public void finishGame() {
		gameManager.saveScoreEtc();
		requestHandler.finishGame(gameManager.getCurrentScore());
	}
}
