package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import robot.Action;
import environment.State;

public abstract class AbstractAgent {
	protected Map<Integer, List<LearnedAction>> actionList;
	protected AgentMode mode;
	
	protected AbstractAgent() {
		actionList = new HashMap<Integer, List<LearnedAction>>();
		mode = AgentMode.RNDLEARN;
	}
	
	public void setMode(AgentMode newMode) {
		mode = newMode;
	}
	
	public abstract Action getNextAction(State state);
}
