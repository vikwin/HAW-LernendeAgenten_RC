package robot;

import utils.Vector2D;

public enum ComplexMovement {
	UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT, NOTHING;
	
	private static double MOVE_RADIUS = 40.0;
	private static double MOVE_LEG_SIZE = Math.sqrt(2 * MOVE_RADIUS * MOVE_RADIUS);
	
	/**
	 * @param id die ID der SimpleMovement
	 * @return SimpleMovement, die zu der ID gehört
	 */
	public static ComplexMovement byId(int id) {
		return values()[id];
	}
	
	public Vector2D getMoveVector() {
		switch (this) {
		case UP:
			return new Vector2D(0, MOVE_RADIUS);
		case UPRIGHT:
			return new Vector2D(MOVE_LEG_SIZE, MOVE_LEG_SIZE);
		case DOWN:
			return new Vector2D(0, -MOVE_RADIUS);
		case DOWNLEFT:
			return new Vector2D(-MOVE_LEG_SIZE, -MOVE_LEG_SIZE);
		case DOWNRIGHT:
			return new Vector2D(MOVE_LEG_SIZE, -MOVE_LEG_SIZE);
		case LEFT:
			return new Vector2D(-MOVE_RADIUS, 0);
		case RIGHT:
			return new Vector2D(MOVE_RADIUS, 0);
		case UPLEFT:
			return new Vector2D(-MOVE_LEG_SIZE, MOVE_LEG_SIZE);
		default:
			return new Vector2D();
		}
	}
}
