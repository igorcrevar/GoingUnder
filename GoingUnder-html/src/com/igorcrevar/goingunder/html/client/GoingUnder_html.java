package com.igorcrevar.goingunder.html.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.igorcrevar.goingunder.GameListener;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.IActivityRequestHandler;

public class GoingUnder_html extends GwtApplication {
	 @Override
     public GwtApplicationConfiguration getConfig () {
		 //GwtApplicationConfiguration config = new GwtApplicationConfiguration(384, 640);
		 // GwtApplicationConfiguration config = new GwtApplicationConfiguration(480, 800);
		 GwtApplicationConfiguration config = new GwtApplicationConfiguration(420, 700);
		 return config;
     }

     @Override
     public ApplicationListener getApplicationListener () {
    	 return new GameListener(new IActivityRequestHandler() {
    		private CustomDialog dialog = new CustomDialog();
    		final class CustomDialog extends DialogBox implements com.google.gwt.event.dom.client.ClickHandler {
    			public CustomDialog() {
					
					Button closeButton = new Button("Close", this);
					HTML msg = new HTML("<p style=\"text-align:center\">Sorry :( Not implemented yet.</p>", true);
					
					DockPanel dock = new DockPanel();
					dock.setSpacing(4);
					
					dock.add(closeButton, DockPanel.SOUTH);
					dock.add(msg, DockPanel.NORTH);
					// dock.add(new Image("images/yourImage.jpg"), DockPanel.CENTER);
					
					dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_RIGHT);
					dock.setWidth("100%");
					setWidget(dock);
    			}
    			
    			public CustomDialog setTitleText(String txt) {
    				setText("Information");
    				return this;
    			}

    			@Override
				public void onClick(ClickEvent event) {
					hide();
				}	 
        	};
        	 
        	private GameManager gameManager;
			@Override
			public void showLeaderboards() {
				dialog.setTitleText("Leader-boards").show();
			}
			
			@Override
			public void showAds(boolean show) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void showAchievements() {
				dialog.setTitleText("Achievements").show();
			}
			
			@Override
			public void share(int score) {
				rate();
			}
			
			@Override
			public void setGameManager(GameManager gameManager) {
				this.gameManager = gameManager;
			}
			
			@Override
			public void rate() {
				Window.open("https://play.google.com/store/apps/details?id=com.igorcrevar.goingunder.android","_blank","");
			}
			
			@Override
			public void loginGPGS() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean getSignedInGPGS() {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public void finishGame() {
				// TODO Auto-generated method stub
				
			}
		});
     }
}
