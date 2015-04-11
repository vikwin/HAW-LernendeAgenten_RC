package environment;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import robot.LARCRobot;

public class LARCEnvironment implements EnvironmentInterface {

	public boolean[][] grid;
	public LARCRobot robot;

	public LARCEnvironment(LARCRobot robot) {
		this.robot = robot;
	}

	@Override
	public void env_cleanup() {
		// TODO Auto-generated method stub
	}

	@Override
	public String env_init() {
		this.grid = new boolean[(int) (robot.getBattleFieldWidth() / robot
				.getWidth())][(int) (robot.getBattleFieldHeight() / robot
				.getBattleFieldHeight())];
		return null;
	}

	@Override
	public String env_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observation env_start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reward_observation_terminal env_step(Action arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}