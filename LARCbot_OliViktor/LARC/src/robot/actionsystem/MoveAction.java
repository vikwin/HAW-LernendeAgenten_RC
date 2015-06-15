package robot.actionsystem;

/**
 * MoveAction stellt die fahrt des Panzers um eine variablen Distanz dar.
 * @author Viktor Winkelmann
 *
 */
public class MoveAction extends Action {

	private static final double DISTANCE_PADDING = 40.0; // Wird benötigt, damit die Bewegungen weicher werden
	
	private double distance;

	public MoveAction(double distance) {
		this.distance = distance;
	}

	@Override
	public void start() {
		if (!started) {
			if (distance > 0)
				bot.setAhead(distance + DISTANCE_PADDING);
			else
				bot.setAhead(distance - DISTANCE_PADDING);
			started = true;
		}
	}

	@Override
	public void stop() {
		if (started)
			bot.setBack(0);
	}

	@Override
	public void update() {
		if (started && Math.abs(bot.getDistanceRemaining()) < DISTANCE_PADDING)
			finished = true;

	}

	@Override
	public String toString() {
		return String.format("MoveAction um %f Meter", distance);
	}

}
