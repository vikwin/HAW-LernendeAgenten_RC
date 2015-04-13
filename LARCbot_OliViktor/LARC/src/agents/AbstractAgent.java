package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import robot.Action;

public abstract class AbstractAgent {
	protected Map<Integer, List<LearnedAction>> actionList;
	protected ActionQueue lastActions;
	protected AgentMode mode;
	
	private static double DISCOUNT_RATE = 0.9;
	private static int QUEUE_SIZE = 10;
	
	protected AbstractAgent() {
		actionList = new HashMap<Integer, List<LearnedAction>>();
		lastActions = new ActionQueue(QUEUE_SIZE, DISCOUNT_RATE);
		mode = AgentMode.RNDLEARN;
	}
	
	public void setMode(AgentMode newMode) {
		mode = newMode;
	}
	
	public abstract Action getNextAction(int stateID);
	
	public abstract void addReward(int reinforcement);
}
