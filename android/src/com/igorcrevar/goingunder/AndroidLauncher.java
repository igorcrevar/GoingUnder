package com.igorcrevar.goingunder;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;

public class AndroidLauncher extends AndroidApplication {
	public interface Callable {
		void execute();
	}

	private static final int RC_ACHIEVEMENT_UI = 0x666; // 9003;
	private static final int RC_LEADERBOARD_UI = 0x777;

	private LeaderboardsClient leaderboardsClient;

	private AchievementsClient achievementClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				if (achievementClient == null) {
					AndroidLauncher.this.trySignInGps(() ->
							achievementClient.getAchievementsIntent()
									.addOnSuccessListener(intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI)));
				} else {
					achievementClient.getAchievementsIntent()
							.addOnSuccessListener(intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI));
				}
			}

			@Override
			public void showLeaderboards() {
				final String ln = getString(R.string.leaderboard_high_score);

				if (leaderboardsClient == null) {
					AndroidLauncher.this.trySignInGps(() ->
							leaderboardsClient.getLeaderboardIntent(ln)
									.addOnSuccessListener(intent -> startActivityForResult(intent, RC_LEADERBOARD_UI)));
				} else {
					leaderboardsClient.getLeaderboardIntent(ln)
							.addOnSuccessListener(intent -> startActivityForResult(intent, RC_LEADERBOARD_UI));
				}
			}
		}), config);
	}

	private void initGps() {
		PlayGamesSdk.initialize(this);
		PlayGames.getGamesSignInClient(this).isAuthenticated().addOnCompleteListener(authTask -> {
			// if already authenticated -> pick clients...
			if (authTask.isSuccessful() && authTask.getResult().isAuthenticated()) {
				achievementClient = PlayGames.getAchievementsClient(this);
				leaderboardsClient = PlayGames.getLeaderboardsClient(this);
			}
		});
	}

	private void trySignInGps(Callable callback) {
		PlayGames.getGamesSignInClient(this).signIn().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult().isAuthenticated()) {
				achievementClient = PlayGames.getAchievementsClient(this);
				leaderboardsClient = PlayGames.getLeaderboardsClient(this);
				callback.execute();
			} else {
				showErrorDialog(getString(R.string.gps_info));
				Log.e("going_under", "can not log into google play", task.getException());
			}
		});
	}

	private void showErrorDialog(String message) {
		this.runOnUiThread(() -> new AlertDialog.Builder(AndroidLauncher.this)
				.setTitle(R.string.information).setMessage(message)
				.setPositiveButton("OK",
						(dialog, which) -> dialog.dismiss()).create().show());
	}
}
