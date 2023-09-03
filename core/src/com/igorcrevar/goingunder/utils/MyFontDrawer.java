package com.igorcrevar.goingunder.utils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class MyFontDrawer implements Disposable {
	private Mesh mesh;
	private TextureRegion textureRegion;
	private IMyFontDrawerFont font;
	private float width;
	private float height;
	private int visibleRectCnt = -1;
	private Matrix4 viewModelMatrix = new Matrix4().idt();
	
	public MyFontDrawer(IMyFontDrawerFont font,
						String txt,
						TextureRegion textureRegion, 
						float voxelWidth, float voxelHeight,
						float letterPadding, float cellPadding) {
		this.font = font;
		this.textureRegion = textureRegion;
		init(txt, voxelWidth, voxelHeight, letterPadding, cellPadding);
	}
	
	private void init(
			String txt,
			float voxelWidth, float voxelHeight,
			float letterPadding,  float cellPadding) {
		this.calculateWidthAndHeight(txt, voxelWidth, voxelHeight, letterPadding, cellPadding);
		this.calculateVisibleRectangles(txt);
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
		for (int i = 0; i < txt.length(); ++i) {
			char c = txt.charAt(i);
			if (c == ' ') {
				++numberOfSpaces;
				continue;
			}
			
			for (int row = 0; row < font.getCharHeight(); ++row) {
				for (int col = 0; col < font.getCharWidth(); ++col) {
					if (font.isSet(c, row, col)) {							
						float x = 0f;
						if (col > 0) {
							x += (cellPadding + voxelWidth) * col;
						}
						if (i > 0) {
							x += (letterPadding + (font.getCharWidth() - 1) * (cellPadding + voxelWidth) + voxelWidth)  * (i - numberOfSpaces);
							x += numberOfSpaces * voxelWidth * 2;
						}
						
						float y = 0f;
						if (row > 0) {
							y -= (cellPadding + voxelHeight) * row;
						}
						
						short vIndexReal = (short)(4 * vIndex);
						vertices[vIndexReal++] = x;   
						vertices[vIndexReal++] = y;
						vertices[vIndexReal++] = textureRegion.getU();
						vertices[vIndexReal++] = textureRegion.getV();														
						vertices[vIndexReal++] = x + voxelWidth;
						vertices[vIndexReal++] = y;
						vertices[vIndexReal++] = textureRegion.getU2();
						vertices[vIndexReal++] = textureRegion.getV();
						vertices[vIndexReal++] = x + voxelWidth;
						vertices[vIndexReal++] = y - voxelHeight;
						vertices[vIndexReal++] = textureRegion.getU2();
						vertices[vIndexReal++] = textureRegion.getV2();							
						vertices[vIndexReal++] = x;   
						vertices[vIndexReal++] = y - voxelHeight;
						vertices[vIndexReal++] = textureRegion.getU();
						vertices[vIndexReal] = textureRegion.getV2();

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

	public MyFontDrawer idt() {
		viewModelMatrix.idt();
		return this;
	}

	public MyFontDrawer translate(float x, float y) {
		viewModelMatrix.translate(x, y, 0.0f);
		return this;
	}

	public MyFontDrawer rotateXYRad(float angle) {
		viewModelMatrix.rotateRad(Vector3.Z, angle);
		return this;
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
	
	private void calculateVisibleRectangles(String txt) {
		visibleRectCnt = 0;
		for (int i = 0; i < txt.length(); ++i) {
			char c = txt.charAt(i);
			for (int j = 0; j < font.getCharHeight(); ++j) {
				for (int k = 0; k < font.getCharWidth(); ++k) {
					if (font.isSet(c, j, k)) {
						++visibleRectCnt;
					}
				}
			}
		}
	}

	private void calculateWidthAndHeight(String txt,
										 float voxelWidth, float voxelHeight,
										 float letterPadding, float cellPadding) {
		int spacesNum = 0;
		for (int i = 0; i < txt.length(); ++i) {
			char c = txt.charAt(i);
			if (c == ' ') {
				spacesNum++;
			}
		}

		int num = txt.length() - spacesNum;
		int charWidth = this.font.getCharWidth();
		int charHeight = this.font.getCharHeight();
		this.width = voxelWidth * num * charWidth + (num-1) * letterPadding + cellPadding * num * (charWidth-1) + spacesNum * voxelWidth * 2;
		this.height = (cellPadding + voxelHeight) * charHeight;
	}

	public void dispose() {
		 mesh.dispose();
	}

	public Matrix4 getViewModelMatrix() {
		return viewModelMatrix;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
