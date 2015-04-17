package src.environment;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import src.robot.LARCRobot;
import src.state.State;
import src.utility.Position;

public class LARCEnvironment implements EnvironmentInterface {

	public static final int TILESIZE = 40;

	private static final int BATTLEFIELDWIDTH = 800;

	private static final int BATTLEFIELDHEIGHT = 600;

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
		this.currentState = new State();
	}

	@Override
	public String env_init() {

		this.grid = new GridStates[(int) (BATTLEFIELDWIDTH / TILESIZE)][(int) (BATTLEFIELDHEIGHT / TILESIZE)];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = GridStates.EMPTY;
			}
		}
		this.currentEnergyRatio = 1;

		position = new int[2];

		TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
		theTaskSpecObject.setEpisodic();
		theTaskSpecObject.setDiscountFactor(0.9);

		// Specify that there will be an integer observation [0,269999] for the state
		theTaskSpecObject.addDiscreteObservation(new IntRange(0, 269999));
		// Specify that there will be an integer action [0,15]
		theTaskSpecObject.addDiscreteAction(new IntRange(0, 15));
		// Specify the reward range [-100,10]
		theTaskSpecObject.setRewardRange(new DoubleRange(-1.0, 1.0));

		theTaskSpecObject.setExtra("SampleMinesEnvironment(Java) by Brian Tanner.");

		String taskSpecString = theTaskSpecObject.toTaskSpec();
		TaskSpec.checkTaskSpec(taskSpecString);

		return taskSpecString;
	}

	@Override
	public Observation env_start() {
		// robot.getSelfGridPosition();
		// robot.getEnemyGridPosition();

		Observation theObservation = new Observation(1, 0, 0);
		theObservation.setInt(0, this.currentState.getStateID());
		return theObservation;
	}

	@Override
	public Reward_observation_terminal env_step(Action action) {
		System.out.println("environmentstep");
		robot.getEnemyGridPosition();
		robot.getSelfGridPosition();
		
		this.currentState.setEnemyPos(enemyPos);
		this.currentState.setSelfPos(selfPos);
		this.currentState.setEnergyRatio(currentEnergyRatio);
		
		Observation theObservation = new Observation(1, 0, 0);
		theObservation.setInt(0, this.currentState.getStateID());
		Reward_observation_terminal RewardObs = new Reward_observation_terminal();
		RewardObs.setObservation(theObservation);
		RewardObs.setTerminal(this.gameOver());
		RewardObs.setReward(this.calculateReward());

		this.previousEnergyRatio = this.currentEnergyRatio;
		return RewardObs;
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