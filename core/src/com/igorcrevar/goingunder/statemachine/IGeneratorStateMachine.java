package com.igorcrevar.goingunder.statemachine;

import com.igorcrevar.goingunder.ObstacleTypeEnum;

public interface IGeneratorStateMachine {
	ObstacleTypeEnum[] getFirst();

	ObstacleTypeEnum[] getPossibilitiesFor(ObstacleTypeEnum type);
}