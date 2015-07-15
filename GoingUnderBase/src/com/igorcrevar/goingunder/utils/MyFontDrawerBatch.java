package com.igorcrevar.goingunder.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

public class MyFontDrawerBatch implements Disposable {
	private ShaderProgram sp;
	private IMyFontDrawerFont myFont;
	private ArrayList<MyFontDrawer> fonts = new ArrayList<MyFontDrawer>(4);
	private Matrix4 projectionMatrix = new Matrix4();
	
	public MyFontDrawerBatch(IMyFontDrawerFont myFont) {
		this.myFont = myFont;
		
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n"
                + "precision mediump float;\n" 
                + "#endif\n"
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = texture2D(u_texture, v_texCoords);\n" //
				+ "}";
		// make an actual shader from our strings
		sp = new ShaderProgram(vertexShader, fragmentShader);		
		// check there's no shader compile errors
		if (sp.isCompiled() == false)
			throw new IllegalStateException(sp.getLog());
	}
	
	public MyFontDrawer addNew(String value, 
			TextureRegion textureRegion, 
			float startX, float startY,
			float width, float height, float letterPadding, float cellPadding) {
		MyFontDrawer drawer = new MyFontDrawer(myFont, value, textureRegion, 
				startX, startY, width, height, letterPadding, cellPadding);
		fonts.add(drawer);
		return drawer;
	}
	
	public void draw() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		sp.begin();
		sp.setUniformi("u_texture", 0);
		for (MyFontDrawer fd : fonts) {
			projectionMatrix.setToOrtho2D(0, 0, 1080, 1920);
			sp.setUniformMatrix("u_projTrans", projectionMatrix.mul(fd.getViewModelMatrix()));
			fd.draw(sp);	
		}
		sp.end();
	}

	@Override
	public void dispose() {
		for (MyFontDrawer fd : fonts) {
			fd.dispose();
		}
		sp.dispose();
	}
}
