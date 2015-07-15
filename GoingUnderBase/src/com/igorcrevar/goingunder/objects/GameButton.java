package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.igorcrevar.goingunder.utils.GameHelper;

public abstract class GameButton {
	private Sprite sprite = new Sprite();
	private boolean isEnabled;
	
	public GameButton(TextureRegion region, float x, float y) {
		this(region, x, y, 1.0f, 0.98f);
	}
	
	public GameButton(TextureRegion region, float x, float y, float width, float height) {
		sprite.setRegion(region);
		sprite.setBounds(x , y - height, width, height);
		isEnabled = true;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		sprite.draw(spriteBatch);
	}
	
	public boolean check(float x, float y) {
		if (isEnabled && GameHelper.tapPointInsideRectangle(x, y, sprite.getX(), sprite.getY() + sprite.getHeight(), sprite.getWidth(), sprite.getHeight())) {
			onClick();
			return true;
		}
		
		return false;
	}
	
	public void translate(float posX, float posY) {
		sprite.setPosition(posX,  posY);
	}
	
	public void changeTexture(TextureRegion region) {
		sprite.setRegion(region);
	}
	
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	protected abstract void onClick();
}
