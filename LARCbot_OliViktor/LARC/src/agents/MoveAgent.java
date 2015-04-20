package agents;

import java.util.Arrays;
import java.util.Random;

import robot.Movement;

public class MoveAgent extends AbstractAgent {
	private static int SUCCESS_CHANCE = 80;		// Erfolgswahrscheinlich, dass die bevorzugte Action ausgeführt wird, in Prozent
	
	private Random rnd;
	private int actionEnumSize, normalizedSuccessChance;
	
	/**
	 * 
	 * @param gridFields Die Anzahl der Felder, die das Spielfeld als Grid hat.
	 */
	public MoveAgent(int gridFields) {
		super();
		rnd = new Random();
		
		actionEnumSize = Movement.values().length;
		normalizedSuccessChance = SUCCESS_CHANCE - (Math.floorDiv(100 - SUCCESS_CHANCE, actionEnumSize - 1));
		
		actionList = new Double[gridFields * gridFields * actionEnumSize];
		Arrays.fill(actionList, new Double(0.0));
	}
	
	public MoveAgent() {
		this(300);
	}
	
	private int getActionWithMaxValue(int startID) {
		// Action mit dem höchsten Wert suchen
		double max = -1000000;
		int maxID = -1;
		
		for (int i = 0; i < actionEnumSize; i++) {
			if (actionList[startID + i] >= max) {
				if (actionList[startID + i] != max || rnd.nextBoolean()) {
					max = actionList[startID + i];
					maxID = i;
				}
			}
		}
		
		return maxID;
	}
	
	public Movement getNextAction(int stateID) {
		int actionID = -1;
		
		switch (mode) {
		case RNDLEARN:
			actionID = rnd.nextInt(actionEnumSize);
			break;
			
		case LEARNING:
			int chance = rnd.nextInt(100);
			
			if (chance < normalizedSuccessChance) {
				actionID = getActionWithMaxValue(stateID * actionEnumSize);
			} else {
				actionID = rnd.nextInt(actionEnumSize);
			}
			break;
			
		case FIGHTING:
			actionID = getActionWithMaxValue(stateID * actionEnumSize);
			break;
		}
		
		addToLastActionQueue(stateID * actionEnumSize + actionID);
		
		System.out.println("MoveAgent asked for next action and returns #" + actionID);
		
		return Movement.values()[actionID];
	}
	
	@Override
	public void addReward(double reward) {
		System.out.println("MoveAgent gets reward");
		if (mode != AgentMode.FIGHTING) {
			addRewardToLastActions(reward);
		}
	}
}
