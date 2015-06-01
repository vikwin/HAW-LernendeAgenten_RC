package robot;

import robocode.Rules;

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

	public static final SimpleAttack NOTHING = new SimpleAttack(null, 0);
	public static final int MAX_DEVIATION = 300, RESOLUTION = 30;
	private static int count = ((MAX_DEVIATION / RESOLUTION) * 2 + 1) * GunPower.values().length + 1;
	
	public static SimpleAttack byId(int id) {
		if (id == count - 1)
			return NOTHING;
		
		int possibleDir = (MAX_DEVIATION / RESOLUTION) * 2 + 1;
		return new SimpleAttack(GunPower.values()[(id / possibleDir)], (id % possibleDir) * RESOLUTION - MAX_DEVIATION);
	}
	
	public static int getActionCount() {
		return count;
	}
	
	private GunPower power;
	private double deviation;
	
	public SimpleAttack(GunPower power, double offset) {
		this.power = power;
		
		if (deviation > MAX_DEVIATION)
			this.deviation = MAX_DEVIATION;
		else if (deviation < -MAX_DEVIATION)
			this.deviation = -MAX_DEVIATION;
		else			
			this.deviation = offset;
	}

	public GunPower getPower() {
		return power;
	}

	/**
	 * Die Richtung, in die die Kanone gedreht werden soll, abhängig vom Gegner
	 * @return Die Richtung der Kanone von -MAX_DEVIATION bis +MAX_DEVIATION
	 */
	public double getDeviation() {
		return deviation;
	}
}
