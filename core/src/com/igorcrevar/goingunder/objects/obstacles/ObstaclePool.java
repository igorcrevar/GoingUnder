package com.igorcrevar.goingunder.objects.obstacles;

import java.util.ArrayList;

import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.ObstacleTypeEnum;

public class ObstaclePool implements IObstaclePool {

	private ObstacleObject[] obstacles;
	private ArrayList<ObstacleObject> visible;
	
	public ObstaclePool(int maxOnScreenAtOnce) {
		visible = new ArrayList<ObstacleObject>(maxOnScreenAtOnce);
		// create obstacle pool
		obstacles = new ObstacleObject[maxOnScreenAtOnce];
		for (short j = 0; j < maxOnScreenAtOnce; ++j) {
			obstacles[j] = new ObstacleObject();
		}
	}
	
	@Override
	public void init() {
		// disable all obstacles
		for (short j = 0; j < obstacles.length; ++j) {
			obstacles[j].setIsEnabled(false);
		}

		visible.clear(); // clear for any case - disabling should clear but this is more reliable
	}
	
	@Override
	public ArrayList<ObstacleObject> getAllVisibles() {
		return visible;
	}

	@Override
	public ObstacleObject getOne(ObstacleTypeEnum type, GameData gameData, GameManager gameManager) {
		for (int i = 0; i < obstacles.length; ++i) {
			ObstacleObject o = obstacles[i];
			if (!o.isEnabled()) {
				// must init new
				o.init(type, gameData, gameManager);
				// add to visibles
				visible.add(o);
				return o;
			}
		}
		
		return null;
	}

	@Override
	public void removeFromVisibles(ObstacleObject obj) {
		obj.setIsEnabled(false);
		visible.remove(obj);
	}
}
