package com.igorcrevar.goingunder.statemachine;

import com.igorcrevar.goingunder.ObstacleTypeEnum;

public class GeneratorStateMachine implements IGeneratorStateMachine {
	final private ObstacleTypeEnum[] firstChoices;
	final private ObstacleTypeEnum[][] stateMatrix;

	public GeneratorStateMachine(boolean simple) {
		firstChoices = new ObstacleTypeEnum[]{
				ObstacleTypeEnum.RightHole, ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.LeftRightHoles, ObstacleTypeEnum.LeftRightMiddleHoles
		};

		if (simple) {
			stateMatrix = new ObstacleTypeEnum[][]{
					new ObstacleTypeEnum[]{ // right hole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.LeftMiddleHole
					},
					new ObstacleTypeEnum[]{  // left hole
							ObstacleTypeEnum.RightHole, ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.RightMiddleHole
					},
					new ObstacleTypeEnum[]{ // left right holes
							ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.LeftMiddleHole, ObstacleTypeEnum.RightMiddleHole, ObstacleTypeEnum.LeftRightMiddleHoles
					},
					new ObstacleTypeEnum[]{  // MiddleHole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.RightHole, ObstacleTypeEnum.LeftRightHoles
					},
					new ObstacleTypeEnum[]{  // LeftMiddleHole
							ObstacleTypeEnum.RightHole, ObstacleTypeEnum.RightMiddleHole, ObstacleTypeEnum.MiddleHole
					},
					new ObstacleTypeEnum[]{ // RightMiddleHole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.LeftMiddleHole, ObstacleTypeEnum.MiddleHole
					},
					new ObstacleTypeEnum[]{ // LeftRightMiddleHoles
							ObstacleTypeEnum.LeftRightHoles, ObstacleTypeEnum.MiddleHole
					},
			};
		} else {
			stateMatrix = new ObstacleTypeEnum[][]{
					new ObstacleTypeEnum[]{ // right hole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.LeftMiddleHole, ObstacleTypeEnum.Left4Hole, ObstacleTypeEnum.Left4MiddleHole,
							ObstacleTypeEnum.LeftHole,
					},
					new ObstacleTypeEnum[]{  // left hole
							ObstacleTypeEnum.RightHole, ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.RightMiddleHole, ObstacleTypeEnum.Right4Hole, ObstacleTypeEnum.Right4MiddleHole,
							ObstacleTypeEnum.RightHole,
					},
					new ObstacleTypeEnum[]{ // left right holes
							ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.LeftMiddleHole, ObstacleTypeEnum.RightMiddleHole, ObstacleTypeEnum.LeftRightMiddleHoles
					},
					new ObstacleTypeEnum[]{  // MiddleHole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.RightHole, ObstacleTypeEnum.LeftRightHoles,
							ObstacleTypeEnum.Left4Hole, ObstacleTypeEnum.Right4Hole
					},
					new ObstacleTypeEnum[]{  // LeftMiddleHole
							ObstacleTypeEnum.RightHole, ObstacleTypeEnum.RightMiddleHole, ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.Right4Hole,
					},
					new ObstacleTypeEnum[]{ // RightMiddleHole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.LeftMiddleHole, ObstacleTypeEnum.MiddleHole, ObstacleTypeEnum.Left4Hole,
					},
					new ObstacleTypeEnum[]{ // LeftRightMiddleHoles
							ObstacleTypeEnum.LeftRightHoles, ObstacleTypeEnum.MiddleHole
					},

					new ObstacleTypeEnum[]{ // Left4Hole
							ObstacleTypeEnum.RightHole, ObstacleTypeEnum.RightMiddleHole
					},
					new ObstacleTypeEnum[]{ // Right4Hole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.LeftMiddleHole
					},
					new ObstacleTypeEnum[]{ // Left4MiddleHole
							ObstacleTypeEnum.RightHole, ObstacleTypeEnum.LeftRightHoles
					},
					new ObstacleTypeEnum[]{ // Right4MiddleHole
							ObstacleTypeEnum.LeftHole, ObstacleTypeEnum.LeftRightHoles
					},
			};
		}
	}

	@Override
	public ObstacleTypeEnum[] getFirst() {
		return firstChoices;
	}

	@Override
	public ObstacleTypeEnum[] getPossibilitiesFor(ObstacleTypeEnum type) {
		return stateMatrix[type.ordinal()];
	}
}
