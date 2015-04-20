package robot.actionsystem;

/**
 * MoveAction stellt die fahrt des Panzers um eine variablen Distanz dar.
 * @author Viktor Winkelmann
 *
 */
public class MoveAction extends Action {

	private double distance;

	public MoveAction(double distance) {
		this.distance = distance;
	}

	@Override
	public void start() {
		if (!started) {
			bot.setAhead(distance);
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
		if (started && bot.getDistanceRemaining() == 0)
			finished = true;

	}

	@Override
	public void setActor(ActorRobot robot) {
		bot = robot;
	}

	@Override
	public String toString() {
		return String.format("MoveAction um %f Meter", distance);
	}

}
