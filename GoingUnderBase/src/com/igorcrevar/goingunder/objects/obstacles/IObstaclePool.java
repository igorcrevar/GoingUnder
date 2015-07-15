package com.igorcrevar.goingunder.objects.obstacles;

import java.util.ArrayList;

import com.igorcrevar.goingunder.GameData;
import com.igorcrevar.goingunder.GameManager;
import com.igorcrevar.goingunder.ObstacleTypeEnum;

public interface IObstaclePool {
	void init(GameData gameData);
	ObstacleObject getOne(ObstacleTypeEnum type, GameData gameData, GameManager gameManager);
	ArrayList<ObstacleObject> getAllVisibles();
	void removeFromVisibles(ObstacleObject obj);
}
