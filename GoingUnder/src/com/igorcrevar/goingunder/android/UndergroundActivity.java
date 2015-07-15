package com.igorcrevar.goingunder.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.igorcrevar.goingunder.GameListener;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;

@SuppressLint("HandlerLeak")
public class UndergroundActivity extends AndroidApplication implements GameHelperListener, IActivityRequestHandler  {
	//private static final String GooglePlayUrl = "http://www.amazon.com/gp/mas/dl/android?p=com.igorcrevar.goingunder.android"; 
	private static final String GooglePlayUrl = "https://play.google.com/store/apps/details?id=com.igorcrevar.goingunder.android";
	private static final String AdUnitId = "ca-app-pub-5576202321158199/8618910462";
	private static final boolean DEBUG = false;

	private final boolean areAdsEnabled = true;
	private final boolean areServicesEnabled = true;
	
	private AdView adView;
	private boolean adIsVisible = false;
	
    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;
    
    private GameHelper mHelper;
    private GameManager gameManager;
    
    private enum SignInReason {
    	None, Achievements, Leaderboards
    }
    
    private SignInReason signInReason = SignInReason.None;
    
    protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
        	if (!areAdsEnabled) return;
        	
            switch(msg.what) {
                case SHOW_ADS:
                	if (!adIsVisible) {
                		adIsVisible = true;
                		adView.setVisibility(View.VISIBLE);
                	}
                    break;
                case HIDE_ADS:
                	if (adIsVisible) {
                		adIsVisible = false;
                    	adView.setVisibility(View.GONE);
                	}                    
                    break;
            }
        }
    };
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Create the layout
        RelativeLayout layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create the libgdx View
        View gameView = initializeForView(new GameListener(this));
        // Add the libgdx view
        layout.addView(gameView);

        // ad mob
        if (areAdsEnabled) {
	        // Create and setup the AdMob view
	        adView = new AdView(this);
	        adView.setAdUnitId(AdUnitId);
	        adView.setAdSize(AdSize.BANNER);
	        // adView.setAdListener(new ToastAdListener(this));
	        AdRequest adRequest = new AdRequest.Builder()
	        				   .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        				   .addTestDevice("425C0AD9758E9E82898D6CDAA982B111")
	        				   .build();
	        adView.loadAd(adRequest);
	                
	        // Add the AdMob view
	        RelativeLayout.LayoutParams adParams = 
	            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
	                    RelativeLayout.LayoutParams.WRAP_CONTENT);
	        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	        adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	
	        layout.addView(adView, adParams);
	        adView.setVisibility(View.GONE);
        }

        // achievements and leaderboards
        if (areServicesEnabled) {
        	SharedPreferences sp = getSharedPreferences("GOOGLE_PLAY", Context.MODE_PRIVATE);
        	boolean automaticSignIn = sp.getBoolean("signed_in", false);
        	mHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        	mHelper.setConnectOnStart(automaticSignIn);
        	if (DEBUG) {
        		mHelper.enableDebugLog(true);
        	}
	    	mHelper.setup(this);
        }
       
       // Hook it all up
       setContentView(layout);
	}
	
	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}
	
    @Override
    protected void onPause() {
    	if (areAdsEnabled) {
    		adView.pause();
    	}
    	
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (areAdsEnabled) {
    		adView.resume();
    	}
    }

    @Override
    protected void onDestroy() {
    	if (areAdsEnabled) {
    		 adView.destroy();
    	}
        super.onDestroy();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        if (areServicesEnabled) {
        	mHelper.onStart(this);
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (areServicesEnabled) {
        	mHelper.onStop();
        }
    }
    
    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (areServicesEnabled) {
        	mHelper.onActivityResult(request, response, data);
        }
    }

	@Override
	public void rate() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(GooglePlayUrl));
		this.startActivity(i);
	}

	@Override
	public void share(int score) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "I just got " + Integer.toString(score) + " points in Going Under! " + GooglePlayUrl);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	@Override
	public void finishGame(long score) {
		// not signed, just skip
		if (!areServicesEnabled || !getSignedInGPGS() || gameManager == null) {
			return;
		}
		
		//int bestScore = gameManager.getTopScore();
		int totalPlays = gameManager.getTotalGamesPlayed();
		int totalScoresEver = gameManager.getTotalScoreSum();
		
		Games.Leaderboards.submitScore(mHelper.getApiClient(), getString(R.string.leaderboard_high_score), score);
		
		if (score >= 8) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_gamer_wanna_be));
		}

		if (score >= 20) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_things_getting_better));
		}		
		
		if (totalPlays >= 50) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_persistence_is_key));
		}		
		
		if (totalScoresEver >= 400) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_almost_there));
		}
		
		if (score >= 60) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_rock_star));
		}
		
		if (score >= 100) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_master_com));
		}
		
		if (score >= 150) {
			Games.Achievements.unlock(mHelper.getApiClient(), getString(R.string.achievement_the_king_of_kong));
		}
	}

	@Override
	public void showAchievements() {
		if (!areServicesEnabled) {
			return;
		}

		if (getSignedInGPGS()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(mHelper.getApiClient()), 0x666);
		} 
		else {
			signInReason = SignInReason.Achievements;
			loginGPGS();
		}
	}

	@Override
	public void showLeaderboards() {
		if (!areServicesEnabled) {
			return;
		}
		
		if (getSignedInGPGS()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mHelper.getApiClient(), "CgkIzuC6j_4JEAIQAA"), 0x777);
		} 
		else {
			signInReason = SignInReason.Leaderboards;
			loginGPGS();
		}
	}
	
	@Override
	public boolean getSignedInGPGS() {
		return areServicesEnabled && mHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		if (!areServicesEnabled) {
			return;
		}

		try {
			runOnUiThread(new Runnable(){
				public void run() {
					mHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}
	}

	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
		// if signed in sucessifull mark that so next time sign in will be done automatically
		SharedPreferences sp = getSharedPreferences("GOOGLE_PLAY", Context.MODE_PRIVATE);
		sp.edit().putBoolean("signed_in", true).commit();
		
		// call action which call sign in process
		switch (signInReason) {
		case Achievements:
			showAchievements();
			break;
		case Leaderboards:
			showLeaderboards();
			break;
		default:
			break;
		}
		
		signInReason = SignInReason.None;
	}

	@Override
	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}
}
