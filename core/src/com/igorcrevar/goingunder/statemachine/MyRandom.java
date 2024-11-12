package com.igorcrevar.goingunder.statemachine;

import java.util.Random;

import com.igorcrevar.goingunder.ObstacleTypeEnum;

public class MyRandom implements IMyRandom {
	private IGeneratorStateMachine generator;
	private ObstacleTypeEnum current;
	private boolean isFirstRetrieved;
	private final Random random = new Random();

	public MyRandom() {
	}

	@Override
	public ObstacleTypeEnum getNext() {
		if (generator == null) {
			throw new NullPointerException();
		}

		ObstacleTypeEnum[] choices;
		if (!isFirstRetrieved) {
			choices = generator.getFirst();
			isFirstRetrieved = true;
		} else {
			choices = generator.getPossibilitiesFor(current);
		}

		int pos = random.nextInt(choices.length);
		current = choices[pos];
		return current;
	}

	@Override
	public void reset() {
		this.isFirstRetrieved = false;
	}

	@Override
	public void setGenerator(IGeneratorStateMachine generator) {
		this.generator = generator;
	}
}
