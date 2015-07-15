package com.igorcrevar.goingunder.statemachine;

import com.igorcrevar.goingunder.ObstacleTypeEnum;

public interface IMyRandom {
	ObstacleTypeEnum getNext();
	void reset();
	void setGenerator(IGeneratorStateMachine generator);
}
