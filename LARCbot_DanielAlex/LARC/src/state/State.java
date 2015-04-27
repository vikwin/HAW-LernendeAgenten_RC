package state;

import utility.Position;

public class State {

	public enum healthState {
		BAD, NEUTRAL, GOOD;
	}

	private Position selfPos;
	private Position enemyPos;
	private GunToEnemy gunToEnemy;

	// private healthState health;

	public State() {
	}

	// public void setHealthState(double energyRatio) {// Energy ratio: self/enemy
	// if (energyRatio < 0.7) {
	// this.health = healthState.BAD;
	// } else if (energyRatio > 1.3) {
	// this.health = healthState.GOOD;
	// } else {
	// this.health = healthState.NEUTRAL;
	// }
	// }

	public int getStateID() {
		int state = this.selfPos.getX() * 1 + this.selfPos.getY() * 15 + this.enemyPos.getX() * 15 * 20
				+ this.enemyPos.getY() * 15 * 20 * 15 + this.gunToEnemy.ordinal() * 15 * 20 * 15 * 20;
		// + this.health.ordinal() * 15 * 20 * 15 * 20;
		// System.out.println(state);
		return state;

	}

	// public healthState getHealth() {
	// return health;
	// }

	public Position getSelfPos() {
		return selfPos;
	}

	public void setSelfPos(Position selfPos) {
		this.selfPos = selfPos;
	}

	public Position getEnemyPos() {
		return enemyPos;
	}

	public void setEnemyPos(Position enemyPos) {
		this.enemyPos = enemyPos;
	}

	public GunToEnemy getGunToEnemy() {
		return gunToEnemy;
	}

	public void setGunToEnemy(GunToEnemy gunToEnemy) {
		this.gunToEnemy = gunToEnemy;
	}
}
