package environment;

import robot.LARCRobot;
import state.State;
import state.State.EdgeState;

public class LARCEnvironment implements IEnvironment {

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

		// update currentState
		this.currentEnergyRatio = this.robot.getEnergyRatio();
		this.currentState.setEdgeState(this.robot.getMyPosition());
		this.currentState.setEnemyPosition(this.robot.getAngleToEnemy());
		this.currentState.setEnemyDirection(this.robot.getEnemyDirection());
		// this.currentState.setGunPosition(this.robot.getGunPostion());
		

		return this.currentState.getStateID();
	}

	@Override
	public int env_step(int action) {
		// update currentStateF
		this.currentEnergyRatio = this.robot.getEnergyRatio();
		this.currentState.setEdgeState(this.robot.getMyPosition());
		this.currentState.setEnemyPosition(this.robot.getAngleToEnemy());

		this.currentState.setEnemyDirection(this.robot.getEnemyDirection());
		// this.currentState.setGunPosition(this.robot.getGunPostion());
		this.calculateReward();

		// this.robot.oldGunAngleToEnemy = this.robot.currentGunAngleToEnemy; //??????????
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
		this.lastReward = 0;
		 if (this.currentEnergyRatio > this.previousEnergyRatio) {
		 this.lastReward = 1;
		 } else if (this.currentEnergyRatio < this.previousEnergyRatio) {
		 this.lastReward = -1;
		 } else {
		 this.lastReward = 0;
		 }
		 if(this.currentState.getEdgeState() == EdgeState.MID){
			 this.lastReward += 5;
		 }

		System.out.println(this.currentState.getEdgeState() + ": " + this.lastReward);
		
		this.robot.setCurrentReward(lastReward);
		return this.lastReward;
	}
}