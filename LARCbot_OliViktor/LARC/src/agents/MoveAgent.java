package agents;

import java.util.List;
import java.util.Random;

import robot.Action;
import environment.State;

public class MoveAgent extends AbstractAgent {
	private Random rnd;
	
	public MoveAgent() {
		super();
		rnd = new Random();
	}
	
	@Override
	public Action getNextAction(State state) {
		LearnedAction nextAction = null;
		switch (mode) {
		case RNDLEARN:
			List<LearnedAction> choosableActions = actionList.get(state.getID());
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
