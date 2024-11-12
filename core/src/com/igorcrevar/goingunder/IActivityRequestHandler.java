package com.igorcrevar.goingunder;

public interface IActivityRequestHandler {
	void showAds(boolean show);

	void rate();

	void share(int score);

	void finishGame(GameManager gameManager);

	void showAchievements();

	void showLeaderboards();
}
