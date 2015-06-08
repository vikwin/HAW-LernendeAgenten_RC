package agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import robot.LARCRobot;

public class LARCAgent implements IAgent {

	public static final String PATH = "qValues.csv";

	public static double[][] E_TRACE_FUNCTION;
	public static final double INITIAL_Q_VALUE = 0.0;
	public static final int LAMBDA_LIST_CAPACITY = 3;

	private static final double EPSILON = 0.2; // Exploration rate
	private static final double GAMMA = 0.9; // Time Discount factor
	private static final double ALPHA = 0.5; // learning rate (importance of new information)
	private static final double LAMBDA_VALUE = 0.8; // Abschwächungsfaktor

	private Random randGenerator = new Random();
	private int previousActionInt;
	private int previousStateInt;
	private int currentActionInt;
	private int currentStateInt;
	private Action action;
	private boolean policyFrozen = false; // lernen
	private boolean exploringFrozen = false; // ausprobieren
	public static boolean DEBUG = false;
	private LARCRobot myRobot;
	private double previousStateQValue;
	private double currentStateQValue;
	private LinkedList<int[]> lastStatesForLambda;
	private double currentQDeltaValue;
	private double currentSARSADeltaValue;

	public LARCAgent(LARCRobot myRobot) {
		this.myRobot = myRobot;
		this.action = new Action(this.myRobot);
	}

	@Override
	public void agent_init() {
		this.lastStatesForLambda = new LinkedList<int[]>();
		E_TRACE_FUNCTION = new double[LARCRobot.NO_OF_ACTIONS][LARCRobot.NO_OF_STATES];
		File file = new File(PATH);
		if (file.isFile() && this.myRobot.getRoundNum() == 0) {
			try {
				loadValueFunction(PATH);
			} catch (IOException e) {
				System.out.println("Load ist fehlgeschlagen!!!");
				e.printStackTrace();
			}
		}
	}

	@Override
	public int agent_start(int stateInt) {

		currentActionInt = egreedy(stateInt);

		previousActionInt = currentActionInt;
		previousStateInt = stateInt;

		this.addToSarsaLambdaList(new int[] { previousActionInt, previousStateInt });

		return currentActionInt;
	}

	@Override
	public int agent_step(int stateInt) {
		this.action.setActionID(currentActionInt);
		this.myRobot.move(this.action.getMoveVector());

		currentActionInt = egreedy(stateInt);
		currentStateInt = stateInt;

		if (!policyFrozen) {
			this.eisgekuehlterSarsaLambda();
			// this.SARSA_onPolicy();
			// this.QLearning();
		}

		previousActionInt = currentActionInt;
		previousStateInt = currentStateInt;

		this.addToSarsaLambdaList(new int[] { previousActionInt, previousStateInt });

		return currentActionInt;
	}

	@Override
	public void agent_end() {

		if (!policyFrozen) {
			this.eisgekuehlterSarsaLambda(); // zuweisen des neu gelernten q-wertes
			// this.SARSA_onPolicy();
			// this.QLearning();
		}

		if (this.myRobot.getNumRounds() - 1 == this.myRobot.getRoundNum() && !policyFrozen) {
			try {
				this.saveValueFunction(LARCAgent.PATH, LARCRobot.VALUE_FUNCTION);
			} catch (IOException e) {
				System.out.println("agent_end failure!");
			}
		}
	}

	@Override
	public void agent_cleanup() {
		// System.out.println("***** NUMBER OF TIMES MY ROBOT HIT THE WALL:  " + this.myRobot.wallHitCounter);
		this.lastStatesForLambda.clear();
		this.previousActionInt = 0;
		this.previousStateInt = 0;
	}

	/********************************************************************************************************************/
	public void SARSA_onPolicy() {
		// LARCRobot.VALUE_FUNCTION[this.previousActionInt][this.previousStateInt] += SARSA_ALPHA
		// * (this.myRobot.getCurrentReward() + SARSA_GAMMA * LARCRobot.VALUE_FUNCTION[this.currentActionInt][this.currentStateInt] -
		// LARCRobot.VALUE_FUNCTION[this.previousActionInt][this.previousStateInt]);
		// this.previousActionInt = this.currentActionInt;
		// this.previousStateInt = this.currentStateInt;
	}

	private void eisgekuehlterSarsaLambda() {
		computeSARSADelta();

		E_TRACE_FUNCTION[previousActionInt][previousStateInt]++;

		// LinkedList iterator that descends LIFO over the values:
		// this.descendingIterator = this.lastStatesForLambda.descendingIterator();

		// while (this.descendingIterator.hasNext()) {
		// int[] coordinates = this.descendingIterator.next();
		// LARCRobot.VALUE_FUNCTION[coordinates[0]][coordinates[1]] += SARSA_ALPHA * this.currentSARSADeltaValue
		// * E_TRACE_FUNCTION[coordinates[0]][coordinates[1]];
		//
		// E_TRACE_FUNCTION[coordinates[0]][coordinates[1]] *= SARSA_GAMMA * LAMBDA_VALUE;
		// }

		// Q(a,s) += alpha*delta*etrace(a,s)
		// etrace(a,s) *= gamme * lambda

		for (int t = lastStatesForLambda.size() - 1; t >= 1; t--) {

			LARCRobot.VALUE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] += ALPHA
					* currentSARSADeltaValue
					* E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]];

			System.out.println("Q Wert: " + LARCRobot.VALUE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]]);
				
			// replacing traces
			if (this.currentStateInt == this.lastStatesForLambda.get(t)[1]) {
				E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] = 1;
			} else {
				E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] *= GAMMA
						* LAMBDA_VALUE;
			}
			if (LARCRobot.STATE_REPEAT) {
				this.lastStatesForLambda.pop();
				LARCRobot.STATE_REPEAT = false;
			}
			// 1996 Sutton Singh
			// if (this.previousStateInt == this.lastStatesForLambda.get(t)[1]
			// && this.previousActionInt == this.lastStatesForLambda.get(t)[0]) {
			// E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] = 1
			// + SARSA_GAMMA * LAMBDA_VALUE * oldE;
			// } else if (this.previousStateInt == this.lastStatesForLambda.get(t)[1]
			// && this.previousActionInt != this.lastStatesForLambda.get(t)[0]) {
			// E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] = 0;
			// } else {
			// E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] = SARSA_GAMMA
			// * LAMBDA_VALUE * oldE;
			// }
		}
	}

	private void computeSARSADelta() {
		this.previousStateQValue = LARCRobot.VALUE_FUNCTION[previousActionInt][previousStateInt];
		this.currentStateQValue = LARCRobot.VALUE_FUNCTION[currentActionInt][currentStateInt];
		this.currentSARSADeltaValue = myRobot.getPreviousReward() + GAMMA * this.currentStateQValue
				- this.previousStateQValue;
	}

	private void QLearning() {
		computeQDelta();
		LARCRobot.VALUE_FUNCTION[this.previousActionInt][this.previousStateInt] += ALPHA * this.currentQDeltaValue;
	}

	private void computeQDelta() {
		this.previousStateQValue = LARCRobot.VALUE_FUNCTION[previousActionInt][previousStateInt];
		this.currentStateQValue = LARCRobot.VALUE_FUNCTION[fetchMaxActionint(currentStateInt)][currentStateInt];
		this.currentQDeltaValue = myRobot.getPreviousReward() + GAMMA * this.currentStateQValue
				- this.previousStateQValue;
	}

	public void addToSarsaLambdaList(int[] indexPair) {
		if (!this.lastStatesForLambda.isEmpty()) {
			if (!lastStatesForLambda.peek().equals(indexPair)) {
				while (lastStatesForLambda.contains(indexPair))
					lastStatesForLambda.remove(indexPair);

				if (lastStatesForLambda.size() >= LAMBDA_LIST_CAPACITY) {
					this.lastStatesForLambda.poll();
				}
			} else {
				LARCRobot.STATE_REPEAT = true;
			}
		}
		this.lastStatesForLambda.add(indexPair);
	}

	/**
	 * returns the best actionInt to a state if exploration is frozen. otherwise returns random action with a chance based on the exploration-rate.
	 * 
	 * @param theState
	 * @return maxIndex
	 */
	private int egreedy(int theState) {
		if (!exploringFrozen) {
			if (randGenerator.nextDouble() <= EPSILON) { // best option is selected 1-epsilon of the time
				return randGenerator.nextInt(LARCRobot.NO_OF_ACTIONS);
			}
		}

		/* otherwise choose the greedy action */
		return fetchMaxActionint(theState);
	}

	private int fetchMaxActionint(int theState) {
		int maxIndex = 0;
		List<Integer> actionIntList = new ArrayList<Integer>();
		actionIntList.add(0);
		for (int a = 1; a < LARCRobot.NO_OF_ACTIONS; a++) {
			if (LARCRobot.VALUE_FUNCTION[a][theState] > LARCRobot.VALUE_FUNCTION[maxIndex][theState]) {
				actionIntList.clear();
				maxIndex = a;
				actionIntList.add(a);
			} else if (LARCRobot.VALUE_FUNCTION[a][theState] == LARCRobot.VALUE_FUNCTION[maxIndex][theState]) {
				actionIntList.add(a);
			}
		}
		return actionIntList.get(new Random().nextInt(actionIntList.size()));
	}

	/**
	 * Saves the value function to a file named filePath.
	 * 
	 * @param filePath
	 * @param LARCRobot
	 *            .VALUE_FUNCTION
	 * @throws IOException
	 */
	public void saveValueFunction(String filePath, double[][] valuefunction) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filePath));
		for (int s = 0; s < LARCRobot.NO_OF_STATES; s++) {
			for (int a = 0; a < LARCRobot.NO_OF_ACTIONS; a++) {
				if (a == 0 && s == 0) {
					outputWriter.write(valuefunction[a][s] + "");
				} else {
					outputWriter.newLine();
					outputWriter.write(valuefunction[a][s] + "");
				}
			}
		}
		outputWriter.flush();
		outputWriter.close();
		System.out.println("Saved valueFunction!!!");
	}

	/**
	 * Loads the value function from a file named theFileName.
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	private void loadValueFunction(String filePath) throws IOException {
		BufferedReader outputReader = null;
		outputReader = new BufferedReader(new FileReader(filePath));
		for (int a = 0; a < LARCRobot.NO_OF_ACTIONS; a++) {
			for (int s = 0; s < LARCRobot.NO_OF_STATES; s++) {
				LARCRobot.VALUE_FUNCTION[a][s] = Double.parseDouble(outputReader.readLine());
			}
		}
		outputReader.close();
		System.out.println("Read valueFunction!");
	}
}
