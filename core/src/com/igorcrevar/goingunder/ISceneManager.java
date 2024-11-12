package com.igorcrevar.goingunder;

public interface ISceneManager {
	GameManager getGameManager();

	void setScene(String sceneName);

	void startGame();

	void finishGame();
}
