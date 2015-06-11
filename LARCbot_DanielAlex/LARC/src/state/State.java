package state;

import utility.Position;

public class State {

	public static final int MAX_X = 740;

	public static final int MIN_X = 60;
	public static final int MAX_Y = 540; 

	public static final int MIN_Y = 60; 

	public enum EdgeState {
		MID, LEFTEDGE, RIGHTEDGE, TOPEDGE, BOTTOMEDGE;
	}

	public enum EnemyPosition {
		NORD, NORDOST, OST, SUEDOST, SUED, SUEDWEST, WEST, NORDWEST;
	}

	public enum EnemyDistance {
		CLOSE, INBETWEEN, FAR;
	}

	public EdgeState edgeState;
	public EnemyPosition enemyPosition;
	public EnemyPosition enemyDirection;
	public EnemyDistance enemyDistance;

	// public int gunPosition;

	public int getStateID() {
		int state = 
				  this.edgeState.ordinal()
				+ this.enemyPosition.ordinal() * EdgeState.values().length
				+ this.enemyDirection.ordinal() * EdgeState.values().length * EnemyPosition.values().length
				+ this.enemyDistance.ordinal() * EdgeState.values().length * EnemyPosition.values().length * EnemyPosition.values().length;
		return state;
	}

	public EdgeState getEdgeState() {
		return edgeState;
	}

	public EnemyPosition getEnemyDirection() {
		return enemyDirection;
	}

	public EnemyDistance getEnemyDistance() {
		return enemyDistance;
	}

	public void setEnemyDistance(double distance) {
		this.enemyDistance = EnemyDistance.CLOSE;
		if (distance >= 400) {
			this.enemyDistance = EnemyDistance.FAR;
		} else if (distance >= 200) {
			this.enemyDistance = EnemyDistance.INBETWEEN;
		}
	}

	public void setEdgeState(Position position) {
		this.edgeState = EdgeState.MID;
		if (position.getX() < MIN_X) {
			this.edgeState = EdgeState.LEFTEDGE;
		} else if (position.getX() > MAX_X) {
			this.edgeState = EdgeState.RIGHTEDGE;
		} else if (position.getY() < MIN_Y) {
			this.edgeState = EdgeState.BOTTOMEDGE;
		} else if (position.getY() > MAX_Y) {
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

	public void setEnemyDirection(double angleToEnemy) {
		this.enemyDirection = EnemyPosition.NORD;
		if (angleToEnemy < -157.5) {
			this.enemyDirection = EnemyPosition.SUED;
		} else if (angleToEnemy < -112.5) {
			this.enemyDirection = EnemyPosition.SUEDWEST;
		} else if (angleToEnemy < -67.5) {
			this.enemyDirection = EnemyPosition.WEST;
		} else if (angleToEnemy < -22.5) {
			this.enemyDirection = EnemyPosition.NORDWEST;
		} else if (angleToEnemy > 157.5) {
			this.enemyDirection = EnemyPosition.SUED;
		} else if (angleToEnemy > 112.5) {
			this.enemyDirection = EnemyPosition.SUEDOST;
		} else if (angleToEnemy > 67.5) {
			this.enemyDirection = EnemyPosition.OST;
		} else if (angleToEnemy > 22.5) {
			this.enemyDirection = EnemyPosition.NORDOST;
		}
	}
}
