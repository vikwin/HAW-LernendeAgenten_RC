package robot;

import robot.actionsystem.Action;
import robot.actionsystem.NothingAction;
import robot.actionsystem.OrbitalMoveAction;
import environment.Enemy;

public enum OrbitalMovement {
	CLOCKWISE, COUNTERCLOCKWISE, CLOCKWISE_APPROACH, COUNTERCLOCKWISE_APPROACH, CLOCKWISE_DEPART, COUNTERCLOCKWISE_DEPART, NOTHING;
	
	private static double MOVE_RADIUS = 20.0;
	private static double ENEMY_DISTANCE_STEP = MOVE_RADIUS * 0.75;
	
	/**
	 * @param id die ID der OrbitalMovement
	 * @return OrbitalMovement, die zu der ID gehört
	 */
	public static OrbitalMovement byId(int id) {
		return values()[id];
	}
	
	public Action getMoveAction(Enemy enemy) {
		if (enemy == null)
			return new NothingAction();
		
		switch (this) {
		case CLOCKWISE:
			return new OrbitalMoveAction(enemy, MOVE_RADIUS, 0);
		case CLOCKWISE_APPROACH:
			return new OrbitalMoveAction(enemy, MOVE_RADIUS, -ENEMY_DISTANCE_STEP);
		case CLOCKWISE_DEPART:
			return new OrbitalMoveAction(enemy, MOVE_RADIUS, ENEMY_DISTANCE_STEP);
		case COUNTERCLOCKWISE:
			return new OrbitalMoveAction(enemy, -MOVE_RADIUS, 0);
		case COUNTERCLOCKWISE_APPROACH:
			return new OrbitalMoveAction(enemy, -MOVE_RADIUS, -ENEMY_DISTANCE_STEP);
		case COUNTERCLOCKWISE_DEPART:
			return new OrbitalMoveAction(enemy, -MOVE_RADIUS, ENEMY_DISTANCE_STEP);
		case NOTHING:
		default:
			return new NothingAction();
		}
	}
}
