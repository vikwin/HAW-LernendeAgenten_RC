package robot.actionsystem;

/**
 * TurnAction stellt die Drehung des Panzers um einen variablen Winkel dar.
 * @author Viktor Winkelmann
 *
 */
public class TurnAction extends Action {

	private double angle;

	public TurnAction(double degrees) {
		angle = degrees;
	}

	@Override
	public void start() {
		if (!started) {
			bot.setTurnRight(angle);
			started = true;
		}

	}

	@Override
	public void stop() {
		if (started)
			bot.setTurnLeft(0);
	}

	@Override
	public void update() {
		if (started && bot.getTurnRemaining() == 0) {
			finished = true;
		}
	}

	@Override
	public String toString() {
		return String.format("TurnAction um %f Grad", angle);
	}

}
