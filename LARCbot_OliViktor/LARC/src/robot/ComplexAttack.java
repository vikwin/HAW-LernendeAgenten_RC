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
	
	public static ComplexAttack NOTHING = new ComplexAttack(null, 360);
	private static int count = (360 / 10) * 3 + 1;
	
	/**
	 * @param id die ID der SimpleAttack
	 * @return SimpleAttack, die zu der ID gehört
	 */
	public static ComplexAttack byId(int id) {
		if (id == count - 1)
			return NOTHING;
		
		return new ComplexAttack(GunPower.values()[id / 36], (id % 36) * 10);
	}
	
	public static int getActionCount() {
		return count;
	}

	private GunPower power;
	private double direction;
	
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
