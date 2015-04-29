package state;

import environment.LARCEnvironment;
import utility.Position;

public class State {

	private static final int MAXGRIDX = 800 / LARCEnvironment.TILESIZE;

	private static final int MAXGRIDY = 600 / LARCEnvironment.TILESIZE;

	public enum EdgeState {
		MID, LEFTEDGE, RIGHTEDGE, TOPEDGE, BOTTOMEDGE;
	}

	public enum EnemyPosition {
		NORD, NORDOST, OST, SUEDOST, SUED, SUEDWEST, WEST, NORDWEST;
	}

	public EdgeState edgeState;
	public EnemyPosition enemyPosition;
	public int gunPosition;

	public int getStateID() {
		int state = this.edgeState.ordinal() * EdgeState.values().length + this.enemyPosition.ordinal()
				* EdgeState.values().length + this.gunPosition * 36;
		return state;
	}

	public EdgeState getEdgeState() {
		return edgeState;
	}

	public void setEdgeState(Position position) {
		this.edgeState = EdgeState.MID;
		if (position.getX() == 0) {
			this.edgeState = EdgeState.LEFTEDGE;
		} else if (position.getX() == MAXGRIDX) {
			this.edgeState = EdgeState.RIGHTEDGE;
		} else if (position.getY() == 0) {
			this.edgeState = EdgeState.BOTTOMEDGE;
		} else if (position.getY() == MAXGRIDY) {
			this.edgeState = EdgeState.TOPEDGE;
		}
	}

	public EnemyPosition getEnemyPosition() {
		return enemyPosition;
	}

	public void setEnemyPosition(double angleToEnemy) {
		this.enemyPosition = EnemyPosition.NORD;
		if (angleToEnemy < -157.5) {
			this.enemyPosition = EnemyPosition.SUED;
		} else if (angleToEnemy < -112.5) {
			this.enemyPosition = EnemyPosition.SUEDWEST;
		} else if (angleToEnemy < -67.5) {
			this.enemyPosition = EnemyPosition.WEST;
		} else if (angleToEnemy < -22.5) {
			this.enemyPosition = EnemyPosition.NORDWEST;
		} else if (angleToEnemy > 157.5) {
			this.enemyPosition = EnemyPosition.SUED;
		} else if (angleToEnemy > 112.5) {
			this.enemyPosition = EnemyPosition.SUEDOST;
		} else if (angleToEnemy > 67.5) {
			this.enemyPosition = EnemyPosition.OST;
		} else if (angleToEnemy > 22.5) {
			this.enemyPosition = EnemyPosition.NORDOST;
		}
	}

	public int getGunPos() {
		return gunPosition;
	}

	public void setGunPosition(double gunPosition) {
		gunPosition -= (gunPosition % 10);
		this.gunPosition = (int) gunPosition / 10;
	}
}
