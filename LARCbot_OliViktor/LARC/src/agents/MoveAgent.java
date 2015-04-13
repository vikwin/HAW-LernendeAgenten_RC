package agents;

import java.util.Arrays;
import java.util.Random;

import robot.MoveAction;

public class MoveAgent extends AbstractAgent {
	private Random rnd;
	private int actionEnumSize;
	
	/**
	 * 
	 * @param gridFields Die Anzahl der Felder, die das Spielfeld als Grid hat.
	 */
	public MoveAgent(int gridFields) {
		super();
		rnd = new Random();
		
		actionEnumSize = MoveAction.values().length;
		
		actionList = new double[gridFields * actionEnumSize];
		Arrays.fill(actionList, 0.5);
	}
	
	public MoveAgent() {
		this(300);
	}
	
	@Override
	public MoveAction getNextAction(int stateID) {
		MoveAction nextAction = MoveAction.NOTHING;
		
		switch (mode) {
		case RNDLEARN:
			int actionID = rnd.nextInt(actionEnumSize);
			nextAction = MoveAction.values()[actionID];
			
			addToLastActionQueue(stateID * actionEnumSize + actionID);
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
