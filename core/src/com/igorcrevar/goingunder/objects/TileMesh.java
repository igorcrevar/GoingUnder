package com.igorcrevar.goingunder.objects;

import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TileMesh {
	private static final int MapSize = 128;
	private static final float PI2 = (float)(Math.PI / 2.0);
	
	private final Mesh mesh;
	private final Matrix4 viewModelMatrix;
	
	private final ShaderProgram sp;

	private Texture texture;
	private Texture mapTexture;

	private final Vector2 offset = new Vector2();
	private final Matrix4 heightMatrix = new Matrix4();
	private final Vector2 horizontalDir = new Vector2();
	private final Vector2 verticalDir = new Vector2();
	private float scale;
	
	public TileMesh(GameManager gameManager) {
		sp = gameManager.getShaderProgram("mode7");

		texture = gameManager.getTexture("background.png");
		mapTexture = createDummyMapTexture();

		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		final float floor_h = h / 2.0f;
		mesh = new Mesh(true, 4, 6, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
		mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
		mesh.setVertices(new float[]{
			0, floor_h,
			w, floor_h,
			w, 0,
			0, 0,
		});

		viewModelMatrix = new Matrix4().setToOrtho2D(0f, 0f, w, h);
	}

	public void init() {
	}

	public void update(GameData gameData, float deltaTime) {
		float rotation = gameData.getRotation();

		horizontalDir.set((float)Math.cos(rotation), (float)Math.sin(rotation));
		verticalDir.set((float)Math.cos(rotation + PI2), (float)Math.sin(rotation + PI2));
		offset.set(gameData.getOffset());
		scale = gameData.getZoomFactor();
	}

	public void draw() {
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		final float floor_h = h / 2.0f;

		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
		
		texture.bind(0);
		mapTexture.bind(1);
		
		sp.bind();
		sp.setUniformi("u_texture_tile", 0);
		sp.setUniformi("u_texture_map", 1);
		sp.setUniformf("u_offset", offset);
		sp.setUniformf("u_abcd", horizontalDir.x, horizontalDir.y, verticalDir.x, verticalDir.y);
		sp.setUniformf("u_origin", w / 2.0f, 0);
		sp.setUniformf("u_screen_size", w, h * 2 / 3);
		sp.setUniformf("u_scale", scale);
		sp.setUniformMatrix("u_height", heightMatrix);

		viewModelMatrix.idt().setToOrtho2D(0f, 0f, w, h);
		sp.setUniformi("u_map_index", 0);
		sp.setUniformMatrix("u_projTrans", viewModelMatrix);
		mesh.render(sp, GL20.GL_TRIANGLES);

		// viewModelMatrix.idt().setToOrtho2D(0f, 0f, w, h).translate(0.0f, h-floor_h, 0.0f);
		// sp.setUniformi("u_map_index", 1);
		// sp.setUniformMatrix("u_projTrans", viewModelMatrix);
		// mesh.render(sp, GL20.GL_TRIANGLES);
	}

	public boolean isEnabled() {
		return true;
	}

	public void setIsEnabled(boolean value) {
	}

	public Texture getMapTexture() {
		return mapTexture;
	}

	private Texture createDummyMapTexture() {
		Pixmap pixmap = new Pixmap(MapSize, MapSize, Pixmap.Format.RGBA8888);
		
		final Color color = new Color();
		for (int i = 0; i < MapSize; i++) {
			for (int j = 0; j < MapSize; j++) {
				final int value = (i + j) % 4;
				color.set(value / 255f, value / 255f, 1.0f, 1f);
				pixmap.drawPixel(j, i, Color.rgba8888(color));
			}	
		}
		
		Texture txt = new Texture(pixmap);
		pixmap.dispose();

		return txt;
	}
}
