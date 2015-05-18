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
	private static final int MAX_DIRECTION = 10, RESOLUTION = 1;
	private static int count = ((MAX_DIRECTION / RESOLUTION) * 2 + 1) * GunPower.values().length + 1;
	
	public static SimpleAttack byId(int id) {
		if (id == count - 1)
			return NOTHING;
		
		int possibleDir = (MAX_DIRECTION / RESOLUTION) * 2 + 1;
		return new SimpleAttack(GunPower.values()[(id / possibleDir)], (id % possibleDir) * RESOLUTION - MAX_DIRECTION);
	}
	
	public static int getActionCount() {
		return count;
	}
	
	private GunPower power;
	private double direction;
	
	public SimpleAttack(GunPower power, double direction) {
		this.power = power;
		
		if (direction > MAX_DIRECTION)
			this.direction = MAX_DIRECTION;
		else if (direction < -MAX_DIRECTION)
			this.direction = -MAX_DIRECTION;
		else			
			this.direction = direction;
	}

	public GunPower getPower() {
		return power;
	}

	/**
	 * Die Richtung, in die die Kanone gedreht werden soll, abhängig vom Gegner
	 * @return Die Richtung der Kanone von -MAX_DIRECTION bis +MAX_DIRECTION
	 */
	public double getDirection() {
		return direction;
	}
}
