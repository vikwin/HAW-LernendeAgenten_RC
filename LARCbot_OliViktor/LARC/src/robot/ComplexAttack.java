package robot;

import robocode.Rules;
import robocode.util.Utils;

public class ComplexAttack {
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
	
	public static ComplexAttack NOTHING = new ComplexAttack(null, 360);
	
	public ComplexAttack(GunPower power, double direction) {
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
