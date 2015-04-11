package agents;

import java.util.List;
import java.util.Random;

import robot.Action;
import environment.State;

public class MoveAgent extends AbstractAgent {
	private Random rnd;
	LearnedAction lastAction, currentAction;
	
	public MoveAgent() {
		super();
		rnd = new Random();
		lastAction = null;
	}
	
	@Override
	public Action getNextAction(State state) {
		Action nextAction = null;
		switch (mode) {
		case RNDLEARN:
			lastAction = currentAction;

			List<LearnedAction> choosableActions = actionList.get(state.getID());
			currentAction = choosableActions.get(rnd.nextInt(choosableActions.size()));
			nextAction = currentAction.getAction();
			break;
		case LEARNING:
			break;
		case FIGHTING:
			break;
		default:
			break;
		}
		
		return nextAction;
	}
	
	public void addReinforcement(int reinforcement) {
		lastAction.addReinforcement(reinforcement);
	}
}
