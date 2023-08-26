package com.igorcrevar.goingunder.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.igorcrevar.goingunder.GameData;

public class GameHelper {
	public static float screenX2WorldX(GameData gameData, float x) {
		return -gameData.CameraHalfWidth + 2.0f * gameData.CameraHalfWidth * x / Gdx.graphics.getWidth();
	}
	
	public static float screenY2WorldY(GameData gameData, float y) {
		return gameData.CameraHalfHeight - 2.0f * gameData.CameraHalfHeight * y / Gdx.graphics.getHeight();
	}
	
	public static float screenX2OtherX(float x, float worldX) {
		return worldX * x / Gdx.graphics.getWidth();
	}
	
	public static float screenY2OtherY(float y, float worldY) {
		return worldY * (Gdx.graphics.getHeight() - y) / Gdx.graphics.getHeight();
	}
	
	public static void clearScreen() {
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	public static boolean tapPointInsideRectangle(float x, float y, float rectX, float rectY, float rectWidth, float rectHeight) {
		return x >= rectX && x <= rectX + rectWidth && y >= rectY - rectHeight && y <= rectY;
	}
	
	/*public static void load(String fileName, Object object) {
		try {
			FileHandle fh = Gdx.files.external(fileName);
			Field[] fields = object.getClass().getFields();			
			if (fh.exists()) {
				String content = fh.readString();
				String[] rows = content.split("\n");
				for (String r : rows) {
					String[] data = r.split("=");
					if (data.length != 2) continue; 
					for (Field f : fields) {
						Class<?> type = f.getType();						
						if (f.getName().equals(data[0]) && type.isPrimitive()) {
							if (type.equals(boolean.class)) {
								f.setBoolean(object, Boolean.parseBoolean(data[1]));
							}
							else if (type.equals(float.class)) {
								f.setFloat(object, Float.parseFloat(data[1]));
							}
							else if (type.equals(int.class)) {
								f.setInt(object, Integer.parseInt(data[1]));
							}
							break;
						}
					}
				}
			}
			else {
				StringBuilder content = new StringBuilder();
				for (Field f : fields) {
					content.append(f.getName()).append('=');
					Class<?> type = f.getType();
					String tmp = "";
					if (type.equals(boolean.class)) {
						tmp = Boolean.toString(f.getBoolean(object));
					}
					else if (type.equals(float.class)) {
						tmp = Float.toString(f.getFloat(object));
					}
					else if (type.equals(int.class)) {
						tmp = Integer.toString(f.getInt(object));
					}
					content.append(tmp).append("\n");
				}

				fh.writeString(content.toString(), false);
			}
		}
		catch (Exception ex) {			
		}		
	}*/
}
