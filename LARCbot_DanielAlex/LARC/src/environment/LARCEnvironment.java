package environment;

import robot.LARCRobot;
import state.State;
import state.State.EdgeState;

public class LARCEnvironment implements IEnvironment {

	private LARCRobot robot;
	public State currentState;
	private double currentReward;

	public LARCEnvironment(LARCRobot robot) {
		this.robot = robot;
		this.currentState = new State();
	}

	@Override
	public void env_init() {
		this.currentReward = 0;
	}

	@Override
	public int env_start() {

		// update currentState
		this.currentState.setEdgeState(this.robot.getMyPosition());
		this.currentState.setEnemyPosition(this.robot.getAngleToEnemy());
		this.currentState.setEnemyDirection(this.robot.getEnemyDirection());
		this.currentState.setEnemyDistance(this.robot.getCurrentEnemyDistance());
		// this.currentState.setGunPosition(this.robot.getGunPostion());

		return this.currentState.getStateID();
	}

	@Override
	public int env_step(int action) {
		// update currentStateF
		// System.out.println("EdgeState: " + currentState.edgeState);
		// System.out.println("EnemyPosition: " + currentState.enemyPosition);
		// System.out.println("EnemyDirection: " + currentState.enemyDirection);
		// System.out.println("Distance: " + currentState.enemyDistance);
		this.currentState.setEdgeState(this.robot.getMyPosition());
		this.currentState.setEnemyPosition(this.robot.getAngleToEnemy());

		this.currentState.setEnemyDirection(this.robot.getEnemyDirection());
		this.currentState.setEnemyDistance(this.robot.getCurrentEnemyDistance());
		// this.currentState.setGunPosition(this.robot.getGunPostion());
		this.calculateReward();

		// this.robot.oldGunAngleToEnemy = this.robot.currentGunAngleToEnemy; //??????????
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
	private void calculateReward() {
		// this.previousReward = currentReward;
		// this.currentReward = 0;
		// if (this.currentEnergyRatio > this.previousEnergyRatio) {
		// this.currentReward += 1;
		// } else if (this.currentEnergyRatio < this.previousEnergyRatio) {
		// this.currentReward -= 1;
		// }
		if (this.currentState.getEdgeState() == EdgeState.MID) {
			// System.out.println("EdgeStage: " + this.currentState.getEdgeState());
			this.robot.addReward(1);
		}
		// System.out.println("Reward: " + this.currentReward);

		// System.out.println(this.currentState.getEdgeState() + ": " + this.currentReward);

		this.robot.setPreviousReward(currentReward);
		this.currentReward = this.robot.getReward();
		this.robot.setCurrentReward(this.currentReward);
	}
}