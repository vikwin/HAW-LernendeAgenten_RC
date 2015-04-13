package robot;

import utils.Vector2D;

public enum MoveAction implements Action {
	UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT, NOTHING;
	
	private static double MOVE_RADIUS = 10.0;
	private static double MOVE_LEG_SIZE = Math.sqrt(2 * MOVE_RADIUS * MOVE_RADIUS);
	
	@Override
	public int getID() {
		switch (this) {
		case UP:
			return 0;
		case UPRIGHT:
			return 1;
		case DOWN:
			return 2;
		case DOWNLEFT:
			return 3;
		case DOWNRIGHT:
			return 4;
		case LEFT:
			return 5;
		case RIGHT:
			return 6;
		case UPLEFT:
			return 7;
		case NOTHING:
			return 8;
		default:
			return -1;
		}
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
