package src.state;

import src.utility.Position;

public class State {

	public static int ID_COUNTER = 0;
	
	public enum ratioState {
		BAD, NEUTRAL, GOOD;
	}
	public final int stateID;
	private Position selfPos;
	private Position enemyPos;
	private double energyRatio;
	private ratioState health;

	State(){
		this.stateID = ID_COUNTER;
		ID_COUNTER++;
	}
	
	public void setRatioState() {
		if (energyRatio > 0.9) {
			this.health = ratioState.BAD;
		} else if (energyRatio > 1.1) {
			this.health = ratioState.GOOD;
		} else {
			this.health = ratioState.NEUTRAL;
		}
	}

	public int getStateID() {
		return stateID;
	}
	
	public ratioState getHealth() {
		return health;
	}
	
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

	public double getEnergyRatio() {
		return energyRatio;
	}

	public void setEnergyRatio(double energyRatio) {
		this.energyRatio = energyRatio;
	}

}
