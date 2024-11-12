package com.igorcrevar.goingunder.objects;

import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class StaticBackground implements IGameObject {
	private static final float RepeatFactor = 16f;
	private static final float SpeedFactor = 0.3f;

	private final Mesh mesh;
	private final Matrix4 viewModelMatrix;

	private final ShaderProgram sp;

	private GameData gameData;

	private float offset;

	public StaticBackground(GameManager gameManager) {
		sp = gameManager.getShaderProgram("background");

		mesh = new Mesh(true, 4, 6, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
		mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
		mesh.setVertices(new float[]{
				-0.5f, 0.5f,
				0.5f, 0.5f,
				0.5f, -0.5f,
				-0.5f, -0.5f,
		});

		viewModelMatrix = new Matrix4().setToOrtho(-0.5f, 0.5f, -0.5f, 0.5f, 0.0f, 1000.0f);
	}

	@Override
	public void init(Object odata) {
		gameData = (GameData) odata;
		offset = 0f;
	}

	@Override
	public void update(float deltaTime) {
		offset -= gameData.VelocityY * deltaTime * SpeedFactor;
		if (offset > 1.0f) {
			offset = offset - 1.0f;
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch) {
		sp.bind();
		sp.setUniformMatrix("u_projTrans", viewModelMatrix);
		sp.setUniformf("u_offset", offset);
		sp.setUniformf("u_repeat_factor", RepeatFactor);
		sp.setUniformf("u_screen", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sp.setUniformf("u_color_top", gameData.getLevel().getTopColor());
		sp.setUniformf("u_color_bottom", gameData.getLevel().getBottomColor());
		mesh.render(sp, GL20.GL_TRIANGLES);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setIsEnabled(boolean value) {
	}
}
