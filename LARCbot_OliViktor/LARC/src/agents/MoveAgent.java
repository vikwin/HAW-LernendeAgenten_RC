package agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.json.simple.parser.ParseException;

import utils.Config;

public class MoveAgent extends AbstractAgent {
	private static int SUCCESS_CHANCE = Config.getIntValue("Agent_SuccesChance"); // Erfolgswahrscheinlich, dass die bevorzugte Action ausgeführt wird, in Prozent

	private static Double[] actionList = null;
	private static int actionCounter = 0, fileCounter = 0;
	
	private static final String FILENAME = "move_agent" + FILE_SUFFIX;
	
	protected static void fillActionList(Double[] values) {
		actionList = values;		
	}
	
	static {
		if (LOAD_ON_START) {
			try {
				load("", FILENAME);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Random rnd;
	private int actionCount;
	
	/**
	 * @param environmentStateCount Anzahl der Zustände, die die Umwelt annehmen kann
	 * @param actionCount Anzahl der möglichen Aktionen
	 */
	public MoveAgent(int environmentStateCount, int actionCount) {
		super();
		rnd = new Random();
		
		this.actionCount = actionCount;
		
		if (actionList == null) {
			actionList = new Double[environmentStateCount * actionCount];
			Arrays.fill(actionList, new Double(0.0));
		}
	}
	
	@Override
	protected Double[] getActionList() {
		return actionList;
	}

	@Override
	protected int getStateFromId(int id) {
		return id / actionCount;
	}
	
	private int getActionWithMaxValue(int startID) {
		// Action mit dem höchsten Wert suchen
		double max = Double.MIN_VALUE;
		ArrayList<Integer> maxIDs = new ArrayList<Integer>();
		
		for (int i = 0; i < actionCount; i++) {
			if (actionList[startID + i] >= max) {
				if (actionList[startID + i] > max) {	
					max = actionList[startID + i];
					maxIDs.clear();
				}
				
				maxIDs.add(i);
			}
		}
		
		int size = maxIDs.size();
		if (size <= 0) {
//			System.out.println("MoveAgent Fehler>> " + size + " bei StartIndex " + startID);
			return rnd.nextInt(actionCount);
		}
		
		return maxIDs.get(rnd.nextInt(size));
	}
	
	@Override
	public int getNextAction(int stateID) {
		int actionID = -1;
		
		switch (mode) {
		case RNDLEARN:
			actionID = rnd.nextInt(actionCount);
			break;
			
		case LEARNING:
			int chance = rnd.nextInt(100);
			
			if (chance < SUCCESS_CHANCE) {
				actionID = getActionWithMaxValue(stateID * actionCount);
			} else {
				actionID = rnd.nextInt(actionCount);
			}
			break;
			
		case FIGHTING:
			actionID = getActionWithMaxValue(stateID * actionCount);
			break;
		}
		
		addToLastActionQueue(stateID * actionCount + actionID);
		
//		System.out.println("MoveAgent asked for next action and returns #" + actionID);
		
		return actionID;
	}
	
	@Override
	public void addReward(double reward) {
//		System.out.println("MoveAgent gets reward");
		if (mode != AgentMode.FIGHTING) {
			if (++actionCounter >= SAVE_TIMES) {
				save(TIMESTAMP + FILE_SUFFIX, "move_agent_" + fileCounter++);
				actionCounter = 0;
			}
			
//			System.out.print("MoveAgent > ");
			addRewardToLastActions(reward);
		}
	}

	@Override
	public void saveOnBattleEnd() {
		save("", FILENAME);
	}
	
	@Override
	protected double getMaxQForState(int stateID) {
		int actionID = getActionWithMaxValue(stateID * actionCount);
		return actionList[stateID * actionID + actionID];
	}
}
