package robot;

import robocode.Rules;
import robocode.util.Utils;

// TODO Anpassen auf SimpleEnvironments
public class SimpleAttack {
	public static enum GunPower {
		LOW, MEDIUM, HIGH;
		
		public double toDouble() {
			switch (this) {
			case LOW:
				return Rules.MAX_BULLET_POWER / 3;
			case MEDIUM:
				return (2 * Rules.MAX_BULLET_POWER) / 3;
			case HIGH:
				return Rules.MAX_BULLET_POWER;
			default:
				return 0;
			}
		}
	}

	private GunPower power;
	private double direction;
	
	public static SimpleAttack NOTHING = new SimpleAttack(null, 360);
	
	public SimpleAttack(GunPower power, double direction) {
		this.power = power;
		this.direction = Utils.normalRelativeAngleDegrees(direction);
	}

	public GunPower getPower() {
		return power;
	}

	public double getDirection() {
		return direction;
	}
}
