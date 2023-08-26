package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IGameObject {
	void init(Object data);
	void update(float deltaTime);
	void draw(SpriteBatch spriteBatch);
	boolean isEnabled();
	void setIsEnabled(boolean value);
}
