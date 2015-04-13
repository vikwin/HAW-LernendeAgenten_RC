package src.environment;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import src.robot.LARCRobot;
import src.state.State;
import src.utility.Position;

public class LARCEnvironment implements EnvironmentInterface {

	public static final int TILESIZE = 40;

	private GridStates[][] grid;
	private int[] position;
	private LARCRobot robot;
	private double currentEnergyRatio;
	private double previousEnergyRatio;
	private State currentState;

	private Position selfPos;
	private Position enemyPos;

	public LARCEnvironment(LARCRobot robot) {
		this.robot = robot;
	}

	@Override
	public String env_init() {
		
		this.grid = new GridStates[(int) (robot.getBattleFieldWidth() / TILESIZE)][(int) (robot
				.getBattleFieldHeight() / TILESIZE)];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				grid[i][j] = GridStates.EMPTY;
			}
		}
		this.currentEnergyRatio = 1;

		position = new int[2];

		return null;
	}

	@Override
	public Observation env_start() {
		robot.getSelfGridPosition();
		robot.getEnemyGridPosition();

		Observation theObservation = new Observation();
		theObservation.setInt(0, this.currentState.getStateID());
		return theObservation;
	}

	@Override
	public Reward_observation_terminal env_step(Action arg0) {
		robot.getEnemyGridPosition();
		robot.getSelfGridPosition();
		this.currentState.setEnemyPos(enemyPos);
		this.currentState.setSelfPos(selfPos);
		this.currentState.setEnergyRatio(currentEnergyRatio);
		Observation theObservation = new Observation();
		theObservation.setInt(0, this.currentState.getStateID());
		Reward_observation_terminal RewardObs = new Reward_observation_terminal();
		RewardObs.setObservation(theObservation);
		RewardObs.setTerminal(this.gameOver());
		RewardObs.setReward(this.calculateReward());

		this.previousEnergyRatio = this.currentEnergyRatio;
		return null;
	}

	@Override
	public String env_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void env_cleanup() {
		// TODO Auto-generated method stub
	}

	private double calculateReward() {
		if (this.currentEnergyRatio > this.previousEnergyRatio) {
			return 1;
		} else if (this.currentEnergyRatio < this.previousEnergyRatio) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean gameOver() {
		return this.robot.getGameOver();
	}
}