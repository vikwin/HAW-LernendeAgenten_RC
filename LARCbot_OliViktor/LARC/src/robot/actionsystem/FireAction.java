package robot.actionsystem;

/**
 * FireAction stellt das Abfeuern eines Schusses mit variabler Power dar.
 * @author Viktor Winkelmann
 *
 */
public class FireAction extends Action {
	
	double power;
	
	public FireAction(double power) {
		this.power = power;
	}
	
	@Override
	public void start() {
		if (!started) {
			bot.setFire(power);
			started = true;
		}	
	}

	@Override
	public void stop() {
	}

	@Override
	public void update() {
		if (started && bot.getGunHeat() == 0) {
			finished = true;
		}
	}
	
	@Override
	public String toString() {
		return String.format("FireAction mit Power %f", power);
	}

}
