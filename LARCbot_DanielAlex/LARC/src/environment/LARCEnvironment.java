package environment;

import robot.LARCRobot;
import state.State;

public class LARCEnvironment implements IEnvironment {

	public static final int TILESIZE = 40;

	private static final int BATTLEFIELDWIDTH = 800;

	private static final int BATTLEFIELDHEIGHT = 600;

	private GridStates[][] grid;
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
		this.grid = new GridStates[(int) (BATTLEFIELDWIDTH / TILESIZE)][(int) (BATTLEFIELDHEIGHT / TILESIZE)];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = GridStates.EMPTY;
			}
		}
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
		this.currentState.setSelfPos(this.robot.getSelfGridPos());
		this.currentState.setEnemyPos(this.robot.getEnemyGridPos());
		this.currentState.setHealthState(currentEnergyRatio);

		return this.currentState.getStateID();
	}

	@Override
	public int env_step(int action) {
		
		// update Robot grid position
		this.robot.getSelfGridPosition();
		this.robot.getEnemyGridPosition();

		// update currentStateF
		this.currentEnergyRatio = this.robot.getEnergyRatio();
		this.currentState.setSelfPos(this.robot.getSelfGridPos());
		this.currentState.setEnemyPos(this.robot.getEnemyGridPos());
		this.currentState.setHealthState(currentEnergyRatio);

		this.calculateReward();

		this.previousEnergyRatio = this.currentEnergyRatio;
		return this.currentState.getStateID();
	}

	@Override
	public void env_cleanup() {
		// TODO Auto-generated method stub
	}

	/**
	 * calculates the Reward based on the current State and the chosen action
	 * @return reward
	 */
	private double calculateReward() {
		if (this.currentEnergyRatio > this.previousEnergyRatio) {
			this.lastReward = 1;
		} else if (this.currentEnergyRatio < this.previousEnergyRatio) {
			this.lastReward = -1;
		} else {
			this.lastReward = 0;
		}
		this.robot.setLastReward(lastReward);
		return this.lastReward;
	}
}