package robot;

import utils.Vector2D;

public class MoveAction extends Action{
	private static double MOVE_RADIUS = 10.0;
	private static double MOVE_LEG_SIZE = Math.sqrt(2 * MOVE_RADIUS * MOVE_RADIUS);
	
	public static MoveAction UP = new MoveAction(1, new Vector2D(0, MOVE_RADIUS));
	public static MoveAction UPRIGHT = new MoveAction(1, new Vector2D(MOVE_LEG_SIZE, MOVE_LEG_SIZE));
	public static MoveAction RIGHT = new MoveAction(1, new Vector2D(MOVE_RADIUS, 0));
	public static MoveAction DOWNRIGHT = new MoveAction(1, new Vector2D(MOVE_LEG_SIZE, -MOVE_LEG_SIZE));
	public static MoveAction DOWN = new MoveAction(1, new Vector2D(0, -MOVE_RADIUS));
	public static MoveAction DOWNLEFT = new MoveAction(1, new Vector2D(-MOVE_LEG_SIZE, -MOVE_LEG_SIZE));
	public static MoveAction LEFT = new MoveAction(1, new Vector2D(-MOVE_RADIUS, 0));
	public static MoveAction UPLEFT = new MoveAction(1, new Vector2D(-MOVE_LEG_SIZE, MOVE_LEG_SIZE));
	
	private Vector2D dir;
	
	public MoveAction(int id, Vector2D direction) {
		super(id);
		dir = direction;
	}
}
