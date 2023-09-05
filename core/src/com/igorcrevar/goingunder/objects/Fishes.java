package com.igorcrevar.goingunder.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.utils.Mathf;

public class Fishes implements IGameObject {
    private final static int MaxFishes = 6;
    private final float RandomProbability = 0.007f;

    private class Fish {
        private final Sprite gameObject = new Sprite();

        private float speed;
        private float endX;
        private float endY;
        private float startX;
        private float startY;
        private int currentFrame;
        private float startTime;
        private boolean dirChanged;

        public Fish() {
        }

        public void set(GameData gd, TextureRegion[] textures, float time) {
            float sizeX = 0.8f;
            float sizeY = (float)textures[0].getRegionHeight() / textures[0].getRegionWidth() * sizeX;
            
            this.speed = 0.16f + (float)Math.random() * 0.2f;
            boolean bottomFish = Math.random() > 0.3f; // better chance for bottom
            // if upper part then go downwards. bottom part has more chances to go upwards
            if (bottomFish) {
                this.startY = (float)(-gameData.CameraHalfHeight * Math.random());
                this.endY = this.startY + (float)(Math.random() * gd.CameraHalfHeight * 0.5f) - 0.15f * gd.CameraHalfHeight; 
            } else {
                this.startY = (float)(gameData.CameraHalfHeight * 0.6f * Math.random());
                this.endY = this.startY - (float)(Math.random() * gd.CameraHalfHeight * 0.35);
            }
            this.startY += gd.getCameraYCenter(); // inc for camera position
            this.endY += gd.getCameraYCenter(); // inc for camera position

            boolean isMovingRight = Math.random() > 0.5;
            if (isMovingRight) {
                this.startX = -gd.CameraHalfWidth - sizeX;
                this.endX = gd.CameraHalfWidth;
            } else {
                this.endX = -gd.CameraHalfWidth - sizeX;
                this.startX = gd.CameraHalfWidth;
            }

            this.updateRotation();
            this.currentFrame = -100;
            this.updateAnimation(textures, time);
            this.gameObject.setSize(sizeX, sizeY);
            this.gameObject.setPosition(this.startX, this.startY);
            this.startTime = time;
            this.dirChanged = false;
        }

        public boolean update(float playerX, float playerY, float cameraTop, TextureRegion[] textures, float time) {
            float realTime = (time - startTime) * speed;
            float x = Mathf.lerp(this.startX, this.endX, realTime);
            float y = Mathf.lerp(this.startY, this.endY, realTime);

            // turn if near player!
            float xDiff = x + gameObject.getWidth()/2f - playerX;
            float yDiff = y + gameObject.getHeight()/2f - playerY;
            float playerDist = xDiff*xDiff + yDiff*yDiff;
            if (playerDist < 1f && !dirChanged) {
                this.dirChanged = true;
                this.speed = this.speed * 2f;
                this.endX = this.startX;
                this.startX = x;
                this.startY = y;
                this.startTime = time;
                this.updateRotation();
            }

            gameObject.setPosition(x, y);
            updateAnimation(textures, time);

            if (this.startX < this.endX) {
                return y <= cameraTop && x < this.endX;
            }

            return y <= cameraTop && x > this.endX;
        }

        private void updateRotation() {
            double angle = Math.atan2(this.endY-this.startY, Math.abs(this.endX-this.startX));
            if (this.endX < this.startX) {
                angle = 2 * Math.PI - angle;
            }
            this.gameObject.setRotation((float)(angle / Math.PI * 180f));
        }

        private void updateAnimation(TextureRegion[] textures, float time) {
            // calculate current fame
            int desiredFrame = (int)Math.floor(((time * 2) % 1.0f) * textures.length);
            // only swap textures if needed
            if (desiredFrame != currentFrame) {
                currentFrame = desiredFrame;
                gameObject.setRegion(textures[currentFrame]);
                gameObject.setFlip(startX < endX, false);
            }
        }

        public void draw(SpriteBatch sb) {
            gameObject.draw(sb);
        }
    }

    private final Fish[] fishes = new Fish[MaxFishes];
    private final TextureRegion[] animation = new TextureRegion[3];
    private int aliveFishes;
    private float animationTimer;
    private boolean isEnabled;
    private GameData gameData;
    private final Player player;

    public Fishes(GameManager gameManager) {
        player = gameManager.getPlayer();
        animation[0] = gameManager.getTextureAtlas("game").findRegion("fish11");
        animation[1] = gameManager.getTextureAtlas("game").findRegion("fish12");
        animation[2] = gameManager.getTextureAtlas("game").findRegion("fish13");
        for (int i = 0; i < MaxFishes; i++) {
            fishes[i] = new Fish();
        }
    }

    @Override
    public void init(Object data) {
        this.gameData = (GameData)data;
        this.animationTimer = 0.0f;
        this.aliveFishes = 0;
        this.isEnabled = true;
    }

    @Override
    public void update(float deltaTime) {
        this.animationTimer += deltaTime;

        for (int i = 0; i < this.aliveFishes; i++) {
            boolean isAlive = this.fishes[i].update(
                player.getX(),
                player.getY(),
                gameData.getCameraTop(),
                animation,
                animationTimer);
            if (!isAlive) {
                this.aliveFishes--;
                Fish tmp = this.fishes[i];
                this.fishes[i] = this.fishes[this.aliveFishes];
                this.fishes[this.aliveFishes] = tmp;
            }
        }

        if (this.aliveFishes < MaxFishes && isEnabled) {
            // randomly generate fish
            if (Math.random() < RandomProbability) {
                this.fishes[this.aliveFishes].set(gameData, animation, animationTimer);
                this.aliveFishes++;
            }
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        for (int i = 0; i < this.aliveFishes; i++) {
            this.fishes[i].draw(spriteBatch);
        }
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setIsEnabled(boolean value) {
        this.isEnabled = value;
    }
}

