package com.igorcrevar.goingunder.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class BitmapFontDrawer implements Disposable  {
    public enum Flag {
        Base,
        Center,
        Middle,
        AlignTopOrRight,
    }

    private final float width;

    private final float height;

    private final BitmapFont font;

    private final GlyphLayout glyphLayout;

    private Color color;

    private SpriteBatch spriteBatch = new SpriteBatch();

    private float shadowOffsetX;

    private float shadowOffsetY;

    private Color shadowColor;

    public BitmapFontDrawer(BitmapFont font, float width) {
        float aspect = (float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        this.font = font;
        this.width = width;
        this.height = (float)Math.floor(width * aspect);
        this.glyphLayout = new GlyphLayout();
        this.shadowColor = new Color(0, 0, 0, 0.5f);
        this.shadowOffsetX = 8.0f;
        this.shadowOffsetY = 6.0f;
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

    public BitmapFontDrawer begin() {
        spriteBatch.begin();
        return this;
    }

    public BitmapFontDrawer end() {
        spriteBatch.end();
        return this;
    }


    public BitmapFontDrawer setColor(Color color) {
        this.color = color;
        return this;
    }

    public BitmapFontDrawer setShadow(Color color, float offsetX, float offsetY) {
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        this.shadowColor = color;
        return this;
    }

    public BitmapFontDrawer setScale(float scale) {
        font.getData().setScale(scale);
        return this;
    }

    public BitmapFontDrawer draw(String txt, float x, float y) {
        return draw(txt, x, y, Flag.Base, Flag.Base);
    }

    public BitmapFontDrawer draw(String txt, float x, float y, Flag xOptions) {
        return draw(txt, x, y, xOptions, Flag.Base);
    }

    public BitmapFontDrawer draw(String txt, float x, float y, Flag xOptions, Flag yOptions) {
        float finalX = x, finalY = y;

        if (xOptions != Flag.Base || yOptions != Flag.Base) {
            this.glyphLayout.setText(font, txt);
        }

        switch (xOptions) {
            case Center:
                finalX = (width - this.glyphLayout.width) / 2.0f + x;
                break;
            case Middle:
                finalX = x - this.glyphLayout.width / 2.0f;
                break;
            case AlignTopOrRight:
                finalX = this.width - this.glyphLayout.width - x;
                break;
        }

        switch (yOptions) {
            case Center:
                finalY = (height - this.glyphLayout.height) / 2.0f + y;
                break;
            case Middle:
                finalY = y - this.glyphLayout.height / 2.0f;
                break;
            case AlignTopOrRight:
                finalY = this.height - this.glyphLayout.height - y;
                break;
        }

        if (shadowOffsetX != 0.0 || shadowOffsetY != 0.0) {
            font.setColor(shadowColor);
            font.draw(spriteBatch, txt, finalX + shadowOffsetX, finalY - shadowOffsetY);
        }

        font.setColor(color);
        font.draw(spriteBatch, txt, finalX, finalY);

        return this;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
