package agents;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.parser.ParseException;

import robot.Attack;
import robot.Attack.GunPower;

public class AttackAgent extends AbstractAgent {
	private static int SUCCESS_CHANCE = 80; // Erfolgswahrscheinlich, dass die
											// bevorzugte Action ausgeführt
											// wird, in Prozent

	private static Double[] actionList;
	
	protected static void fillActionList(Double[] values) {
		actionList = values;		
	}
	
	static {
		if (LOAD_ON_START) {
			try {
				load("attack_agent");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Random rnd;
	private int numberOfActions, normalizedSuccessChance;
	private LinkedBlockingQueue<Integer> lastShotActions;
	
	private int actionCounter, fileCounter;

	/**
	 * @param ringfields
	 *            Die Anzahl der Felder, die insgesamt in den Ringen zur
	 *            Verfügung stehen.
	 */
	public AttackAgent(int ringfields) {
		super();
		rnd = new Random();

		numberOfActions = (360 / 10) * 3 + 1;
		normalizedSuccessChance = SUCCESS_CHANCE
				- (Math.floorDiv(100 - SUCCESS_CHANCE, numberOfActions - 1));

		actionList = new Double[ringfields * numberOfActions];
		Arrays.fill(actionList, new Double(0.0));
		
		lastShotActions = new LinkedBlockingQueue<Integer>();
		
		actionCounter = 0;
		fileCounter = 0;
	}
	
	@Override
	protected Double[] getActionList() {
		return actionList;
	}

	private int getActionWithMaxValue(int startID) {
		// Action mit dem höchsten Wert suchen
		double max = -1000000;
		int maxID = -1;

		for (int i = 0; i < numberOfActions; i++) {
			if (actionList[startID + i] >= max) {
				if (actionList[startID + i] != max || rnd.nextBoolean()) {
					max = actionList[startID + i];
					maxID = i;
				}
			}
		}

		return maxID;
	}

	public Attack getNextAction(int stateID) {
		int actionID = -1;

		switch (mode) {
		case RNDLEARN:
			actionID = rnd.nextInt(numberOfActions);
			break;

		case LEARNING:
			int chance = rnd.nextInt(100);

			if (chance < normalizedSuccessChance) {
				actionID = getActionWithMaxValue(stateID * numberOfActions);
			} else {
				actionID = rnd.nextInt(numberOfActions);
			}
			break;

		case FIGHTING:
			actionID = getActionWithMaxValue(stateID * numberOfActions);
			break;
		}

		addToLastActionQueue(stateID * numberOfActions + actionID);

//		System.out.println("AttackAgent asked for next action and returns #"
//				+ (actionID % 36));

		if (actionID == numberOfActions - 1) {
			// Nothing Action
			return Attack.NOTHING;
		}
		
		try {
			lastShotActions.put(stateID * numberOfActions + actionID);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return new Attack(GunPower.values()[actionID / 36], (actionID % 36) * 10);
	}

	@Override
	public void addReward(double reward) {
//		System.out.println("AttackAgent gets reward: " + reward);
		if (mode != AgentMode.FIGHTING) {
			if (++actionCounter >= SAVE_TIMES) {
				save(TIMESTAMP + "\\attack_agent_" + fileCounter++);
			}
			
			addRewardToLastActions(reward);
		}
	}
	
	public void addReward(double reward, double shotReward) {
		if (mode != AgentMode.FIGHTING) {
			Integer shotActionId = lastShotActions.poll();
			if (shotActionId != null) {
				actionList[shotActionId] += shotReward;
			}
			
			addRewardToLastActions(reward);
		}
	}

	@Override
	public void saveOnBattleEnd() {
		save("LARCAgents\\attack_agent");
	}
}
