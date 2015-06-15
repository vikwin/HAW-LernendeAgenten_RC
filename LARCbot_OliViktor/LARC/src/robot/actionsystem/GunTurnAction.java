package robot.actionsystem;

/**
 * GunTurnAction stellt die Drehung der Kanone um einen variablen Winkel dar.
 * @author Viktor Winkelmann
 *
 */
public class GunTurnAction extends Action {

	private double angle;
	
	public GunTurnAction(double degrees) {
		angle = degrees;
	}

	@Override
	public void start() {
		if (!started) {
			bot.setTurnGunRight(angle);
			started = true;
		}
		
	}

	@Override
	public void stop() {
		if (started)
			bot.setTurnGunLeft(0);
	}

	@Override
	public void update() {
		if (started && bot.getGunTurnRemaining() == 0)
			finished = true;
		
	}
	
	@Override
	public String toString() {
		return String.format("GunTurnAction um %f Grad", angle);
	}

}
