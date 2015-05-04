package environment;

import robot.LARCRobot;
import state.GunToEnemy;
import state.State;

public class LARCEnvironment implements IEnvironment {

	public static final int TILESIZE = 50;

	private LARCRobot robot;
	private double currentEnergyRatio;
	private double previousEnergyRatio;
	private State currentState;
	private int lastReward;

	public LARCEnvironment(LARCRobot robot) {
		this.robot = robot;
		this.currentState = new State();
	}

	@Override
	public void env_init() {
		this.currentEnergyRatio = 1;
		this.lastReward = 0;

	}

	@Override
	public int env_start() {

		// update Robot grid position
		this.robot.getSelfGridPosition();
		this.robot.getEnemyGridPosition();

		// update currentState
		this.currentEnergyRatio = this.robot.getEnergyRatio();
		this.currentState.setEdgeState(this.robot.getSelfGridPos());
		this.currentState.setEnemyPosition(this.robot.getAngleToEnemy());
		this.currentState.setGunPosition(this.robot.getGunPostion());

		return this.currentState.getStateID();
	}

	@Override
	public int env_step(int action) {

		// update Robot grid position
		this.robot.getSelfGridPosition();
		this.robot.getEnemyGridPosition();

		// update currentStateF
		this.currentEnergyRatio = this.robot.getEnergyRatio();
		this.currentState.setEdgeState(this.robot.getSelfGridPos());
		this.currentState.setEnemyPosition(this.robot.getAngleToEnemy());
		this.currentState.setGunPosition(this.robot.getGunPostion());
		this.calculateReward();

		this.robot.oldGunAngleToEnemy = this.robot.currentGunAngleToEnemy;
		this.previousEnergyRatio = this.currentEnergyRatio;
		return this.currentState.getStateID();
	}

	@Override
	public void env_cleanup() {
		// TODO Auto-generated method stub
	}

	/**
	 * calculates the Reward based on the current State and the chosen action
	 * 
	 * @return reward
	 */
	private double calculateReward() {
		if (this.currentEnergyRatio > this.previousEnergyRatio) {
			this.lastReward = 10;
		} else if (this.currentEnergyRatio < this.previousEnergyRatio) {
			this.lastReward = -3;
		} else {
			this.lastReward = 0;
		}
		if (this.robot.currentGunAngleToEnemy < this.robot.oldGunAngleToEnemy) {
			this.lastReward += 3;
		} else {
			this.lastReward -= 3;
		}
		if (this.robot.getSelfGridPos().getX() == 0 || this.robot.getSelfGridPos().getX() == State.MAXGRIDX - 1
				|| this.robot.getSelfGridPos().getY() == 0 || this.robot.getSelfGridPos().getY() == State.MAXGRIDY - 1) {
			this.lastReward -= 5; 
		}
		this.robot.setCurrentReward(lastReward);
		return this.lastReward;
	}

	public GunToEnemy calculateGunToEnemy() {
		// TODO delete
		GunToEnemy gunToEnemy = GunToEnemy.AHEAD;
		if (this.robot.getHeading() - this.robot.getGunHeading() + this.robot.getAngleToEnemy() > 0.0) {
			gunToEnemy = GunToEnemy.RIGHT;
		} else if ((this.robot.getHeading() - this.robot.getGunHeading() + this.robot.getAngleToEnemy() < 0.0)) {
			gunToEnemy = GunToEnemy.LEFT;
		}
		return gunToEnemy;
	}
}