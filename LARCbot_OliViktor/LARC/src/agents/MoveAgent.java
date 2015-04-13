package agents;

import java.util.Arrays;
import java.util.Random;

import robot.MoveAction;

public class MoveAgent extends AbstractAgent {
	private Random rnd;
	
	public MoveAgent() {
		super();
		rnd = new Random();
	}
	
	@Override
	public MoveAction getNextAction(int stateID) {
		MoveAction nextAction = MoveAction.NOTHING;
		
		if (!actionList.containsKey(stateID)) {
			double[] defaultReward = new double[MoveAction.values().length];
			Arrays.fill(defaultReward, 0.5);
			
			actionList.put(stateID, defaultReward);
		}
		
		switch (mode) {
		case RNDLEARN:
			double[] actions = actionList.get(stateID);
			int actionID = rnd.nextInt(actions.length);
			nextAction = MoveAction.values()[actionID];
			
			addToLastActionQueue(stateID, actionID);
			break;
			
		case LEARNING:
			break;
			
		case FIGHTING:
			break;
		}
		
		return nextAction;
	}
	
	@Override
	public void addReward(int reward) {
		addRewardToLastActions(reward);
	}
}
