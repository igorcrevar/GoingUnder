package com.igorcrevar.goingunder.utils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

public class MyFontDrawer implements Disposable {
	private String value;
	private Mesh mesh;
	private float width;
	private float height;
	private float letterPadding;
	private float cellPadding;
	private TextureRegion textureRegion;
	private IMyFontDrawerFont font;
	private int visibleRectCnt = -1;
	private Matrix4 viewModelMatrix = new Matrix4();
	
	public MyFontDrawer(IMyFontDrawerFont font, String value, 
						TextureRegion textureRegion, 
						float startX, float startY,
						float width, float height, float letterPadding, float cellPadding) {
		this.font = font;
		this.value = value;
		this.width= width;
		this.height = height;
		this.letterPadding = letterPadding;
		this.cellPadding = cellPadding;
		this.textureRegion = textureRegion;
		init(startX, startY);
	}
	
	private void init(float startX, float startY) {
		this.calculateVisibleRectangles();
		mesh = new Mesh(true, getVertexCount(), getIndicesCount(),
                new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
		
		int vertexCount = getVertexCount();
		int indicesCount = getIndicesCount();
		
		short vIndex = 0;
		short iIndex = 0;
		float[] vertices = new float[vertexCount * 4];
		short[] indices = new short[indicesCount];
		
		byte numberOfSpaces = 0;
		for (int i = 0; i < value.length(); ++i) {
			char c = value.charAt(i);
			if (c == ' ') {
				++numberOfSpaces;
				continue;
			}
			
			for (int row = 0; row < font.getCharHeight(); ++row) {
				for (int col = 0; col < font.getCharWidth(); ++col) {
					if (font.isSet(c, row, col)) {							
						float x = startX;
						if (col > 0) {
							x += (cellPadding + width) * col;
						}
						if (i > 0) {
							x += (letterPadding + (font.getCharWidth() - 1) * (cellPadding + width) + width)  * (i - numberOfSpaces);
							x += numberOfSpaces * width * 2;
						}
						
						float y = startY;
						if (row > 0) {
							y -= (cellPadding + height) * row;
						}
						
						short vIndexReal = (short)(4 * vIndex);
						vertices[vIndexReal++] = x;   
						vertices[vIndexReal++] = y;
						vertices[vIndexReal++] = textureRegion.getU();
						vertices[vIndexReal++] = textureRegion.getV();														
						vertices[vIndexReal++] = x + width;   
						vertices[vIndexReal++] = y;
						vertices[vIndexReal++] = textureRegion.getU2();
						vertices[vIndexReal++] = textureRegion.getV();
						vertices[vIndexReal++] = x + width;
						vertices[vIndexReal++] = y - height;
						vertices[vIndexReal++] = textureRegion.getU2();
						vertices[vIndexReal++] = textureRegion.getV2();							
						vertices[vIndexReal++] = x;   
						vertices[vIndexReal++] = y - height;
						vertices[vIndexReal++] = textureRegion.getU();
						vertices[vIndexReal++] = textureRegion.getV2();
						
						indices[iIndex] = vIndex;
						indices[iIndex + 1] = (short)(vIndex + 1);
						indices[iIndex + 2] = (short)(vIndex + 2);
						indices[iIndex + 3] = (short)(vIndex);
						indices[iIndex + 4] = (short)(vIndex + 2);
						indices[iIndex + 5] = (short)(vIndex + 3);
								
						vIndex += 4;
						iIndex += 6;
					}
				}
			}
		}
		
		mesh.setIndices(indices);
		mesh.setVertices(vertices);
	}
	
	public String getValue() {
		return value;
	}
	
	public void translate(float x, float y) {
		viewModelMatrix.idt();
		viewModelMatrix.translate(x, y, 0.0f);
	}
	
	public void draw(ShaderProgram sp) {
		mesh.render(sp, GL20.GL_TRIANGLES);
	}

	private int getVertexCount() {
		return visibleRectCnt * 4;
	}
	
	private int getIndicesCount() {
		return visibleRectCnt * 6;
	}
	
	private void calculateVisibleRectangles() {
		visibleRectCnt = 0;
		for (int i = 0; i < value.length(); ++i) {
			char c = value.charAt(i);
			for (int j = 0; j < font.getCharHeight(); ++j) {
				for (int k = 0; k < font.getCharWidth(); ++k) {
					if (font.isSet(c, j, k)) {
						++visibleRectCnt;
					}
				}
			}
		}
	}
	
	public void dispose() {
		 mesh.dispose();
	}

	public Matrix4 getViewModelMatrix() {
		return viewModelMatrix;
	}
}
