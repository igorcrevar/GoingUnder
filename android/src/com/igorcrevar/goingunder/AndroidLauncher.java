package com.igorcrevar.goingunder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.Task;

public class AndroidLauncher extends AndroidApplication {
	private AchievementsClient achievementClient;
	private LeaderboardsClient leaderboardsClient;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGps();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GoingUnderGame(new IActivityRequestHandler() {
			private static final String Url = "https://play.google.com/store/apps/details?id=com.igorcrevar.goingunder.android";

			@Override
			public void showAds(boolean show) {

			}

			@Override
			public void rate() {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(Url));
				startActivity(i);
			}

			@Override
			public void share(int score) {
				String txt = String.format(getString(R.string.share_template), score, Url);
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, txt);
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}

			@Override
			public void finishGame(GameManager gameManager) {
				long score = gameManager.getCurrentScore();
				int totalPlays = gameManager.getTotalGamesPlayed();
				int totalScoresEver = gameManager.getTotalScoreSum();

				if (leaderboardsClient != null) {
					leaderboardsClient.submitScore(getString(R.string.leaderboard_high_score), score);
				}

				if (achievementClient != null) {
					if (score >= 5) {
						achievementClient.unlock(getString(R.string.achievement_gamer_wanna_be));
					}

					if (score >= 20) {
						achievementClient.unlock(getString(R.string.achievement_things_getting_better));
					}

					if (totalPlays >= 50) {
						achievementClient.unlock(getString(R.string.achievement_persistence_is_key));
					}

					if (totalScoresEver >= 400) {
						achievementClient.unlock(getString(R.string.achievement_almost_there));
					}

					if (score >= 60) {
						achievementClient.unlock(getString(R.string.achievement_rock_star));
					}

					if (score >= 100) {
						achievementClient.unlock(getString(R.string.achievement_master_com));
					}

					if (score >= 150) {
						achievementClient.unlock(getString(R.string.achievement_the_king_of_kong));
					}
				}
			}

			@Override
			public void showAchievements() {
				if (achievementClient != null) {
					achievementClient.getAchievementsIntent().addOnSuccessListener(
							intent -> startActivityForResult(intent, 0x666)
					);
				}
			}

			@Override
			public void showLeaderboards() {
				if (leaderboardsClient != null) {
					String ln = getString(R.string.leaderboard_high_score);
					leaderboardsClient.getLeaderboardIntent(ln).addOnSuccessListener(
							intent -> startActivityForResult(intent,  0x777)
					);
				}
			}
		}), config);
	}

	private void initGps() {
		GoogleSignInOptions opt = new GoogleSignInOptions.Builder(
				GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
		).build();
		GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, opt);

		googleSignInClient.silentSignIn().addOnCompleteListener(
				task -> {
					if (task.isSuccessful()) {
						achievementClient = Games.getAchievementsClient(
								this, task.getResult());
						leaderboardsClient = Games.getLeaderboardsClient(
								this, task.getResult());
					} else {
						Log.e("Error", "signInError", task.getException());
					}
				}
		);
	}
}
