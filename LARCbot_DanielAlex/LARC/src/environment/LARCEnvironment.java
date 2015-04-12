package environment;

import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

import robot.LARCRobot;

public class LARCEnvironment implements EnvironmentInterface {

	public static final int tileSize = 40;

	public boolean[][] grid;
	public LARCRobot robot;
	WorldDescription theWorld;

	public LARCEnvironment(LARCRobot robot) {
		this.robot = robot;
	}

	@Override
	public String env_init() {
		this.grid = new boolean[(int) (robot.getBattleFieldWidth() / tileSize)][(int) (robot
				.getBattleFieldHeight() / tileSize)];

		theWorld = new WorldDescription(grid,1,1);
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

	@Override
	public String env_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void env_cleanup() {
		// TODO Auto-generated method stub
	}

	class WorldDescription {
		boolean[][] grid;
		int startRow;
		int startCol;
		int numRows;
		int numCols;

		public WorldDescription(boolean[][] grid, int startRow, int startCol) {
			this.grid = grid;
			this.startRow = startRow;
			this.startCol = startCol;
			this.numRows = grid.length;
			this.numCols = grid[0].length;
		}

		public int getNumStates() {
			return numRows * numCols;
		}

		public void updatePosion() {
		}
	}
}