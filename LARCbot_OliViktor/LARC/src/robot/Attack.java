package robot;

public class Attack {
	public static enum GunPower {
		LOW, MEDIUM, HIGH;
	}

	private GunPower power;
	private int direction;
	
	public static Attack NOTHING = new Attack(null, 360);
	
	public Attack(GunPower power, int direction) {
		this.power = power;
		this.direction = direction;
	}

	public GunPower getPower() {
		return power;
	}

	public int getDirection() {
		return direction;
	}
}
