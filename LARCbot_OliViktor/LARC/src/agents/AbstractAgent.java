package agents;

import java.util.HashMap;
import java.util.Map;

import robot.Action;

public abstract class AbstractAgent {
	private static double DISCOUNT_RATE = 0.9;
	private static int QUEUE_SIZE = 10;

	protected Map<Integer, double[]> actionList;
	protected ActionQueue lastActions;
	protected AgentMode mode;
	
	private int[][] lastActionQueue;
	private int queueEndIndex;
	
	protected AbstractAgent() {
		actionList = new HashMap<Integer, double[]>();
//		lastActions = new ActionQueue(QUEUE_SIZE, DISCOUNT_RATE);
		mode = AgentMode.RNDLEARN;
		
		lastActionQueue = new int[QUEUE_SIZE][2];
		queueEndIndex = 0;
	}
	
	public void setMode(AgentMode newMode) {
		mode = newMode;
	}
	
	protected void addToLastActionQueue(int stateID, int actionID) {
		lastActionQueue[queueEndIndex] = new int[] {stateID, actionID};
		queueEndIndex = (queueEndIndex + 1) % QUEUE_SIZE;
	}
	
	protected void addRewardToLastActions(int reward) {
		int i = queueEndIndex;
		double d = 1;
		
		do {
			i = (i + 1) % QUEUE_SIZE;
			
			if (lastActionQueue[i] == null) {
				break;
			}
			
			actionList.get(lastActionQueue[i][0])[lastActionQueue[i][1]] += reward * d;
			
			d *= DISCOUNT_RATE;
		} while (i != queueEndIndex);
	}
	
	public abstract Action getNextAction(int stateID);
	
	public abstract void addReward(int reward);
}
