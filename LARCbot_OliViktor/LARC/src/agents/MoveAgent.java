package agents;

import java.util.List;
import java.util.Random;

import robot.Action;

public class MoveAgent extends AbstractAgent {
	private Random rnd;
	
	public MoveAgent() {
		super();
		rnd = new Random();
	}
	
	@Override
	public Action getNextAction(int stateID) {
		LearnedAction nextAction = null;
		switch (mode) {
		case RNDLEARN:
			List<LearnedAction> choosableActions = actionList.get(stateID);
			nextAction = choosableActions.get(rnd.nextInt(choosableActions.size()));
			lastActions.put(nextAction);
			break;
			
		case LEARNING:
			break;
			
		case FIGHTING:
			break;
		}
		
		return nextAction == null ? null : nextAction.getAction();
	}
	
	@Override
	public void addReward(int reinforcement) {
		lastActions.addReinforcement(reinforcement);
	}
}
