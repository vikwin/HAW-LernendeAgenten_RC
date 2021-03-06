package agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import robot.LARCRobot;
import robot.RewardRobot;
import utility.Config;

public class LARCAgent implements IAgent {

	public static final String PATH = Config.getStringValue("AD_QValueFile", "qValues.csv");

	public static double[][] E_TRACE_FUNCTION;
	public static final double INITIAL_Q_VALUE = 0.0;
	public static final int LAMBDA_LIST_CAPACITY = /* 4; */Config.getIntValue("AD_Agent_ListCapacity");

	private static final double EPSILON = 0.1;// Config.getIntValue("AD_Agent_ExplorationRate") / 100.0; // Exploration rate
	private static final double GAMMA = 0.6;// Config.getIntValue("AD_Agent_DiscountRate") / 100.0; // Time Discount factor
	private static final double ALPHA = 0.6;// Config.getIntValue("AD_Agent_LearnRate") / 100.0; // learning rate (importance of new information)
	private static final double LAMBDA = 0.9;// Config.getIntValue("AD_Agent_Lambda") / 100.0; // Abschw�chungsfaktor

	private static boolean POLICY_FROZEN = Config.getBoolValue("AD_PolicyFrozen"); // lernen
	private static boolean EXPLORING_FROZEN = Config.getBoolValue("AD_ExploingFrozen"); // ausprobieren

//	static {
//		System.err.printf("ListCapacity: %d, Epsilon: %f, Gamma: %f, Alpha: %f, Lambda: %f\n", LAMBDA_LIST_CAPACITY,
//				EPSILON, GAMMA, ALPHA, LAMBDA);
//	}

	private Random randGenerator = new Random();
	private int previousActionInt;
	private int previousStateInt;
	private int currentActionInt;
	private int currentStateInt;
	private Action action;
	public static boolean DEBUG = false;
	public static boolean LOG = true;
	private LARCRobot myRobot;
	private double previousStateQValue;
	private double currentStateQValue;
	private LinkedList<int[]> lastStatesForLambda;
	private double currentQDeltaValue;

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
		currentActionInt = egreedy(stateInt);
		currentStateInt = stateInt;

		if (!POLICY_FROZEN) {
			this.eisgekuehlterSarsaLambda();
			// this.SARSA_onPolicy();
			// this.QLearning();
		}

		previousActionInt = currentActionInt;
		previousStateInt = currentStateInt;

		this.addToSarsaLambdaList(new int[] { previousActionInt, previousStateInt });

		this.action.setActionID(currentActionInt);
		this.myRobot.move(this.action.getMoveVector());

		return currentActionInt;
	}

	@Override
	public void agent_end() {

		if (!POLICY_FROZEN) {
			this.eisgekuehlterSarsaLambda(); // zuweisen des neu gelernten
			// q-wertes
			// this.SARSA_onPolicy();
			// this.QLearning();
		}

		if (this.myRobot.getNumRounds() - 1 == this.myRobot.getRoundNum() && !POLICY_FROZEN) {
			try {
				this.saveValueFunction(LARCAgent.PATH, LARCRobot.VALUE_FUNCTION);
			} catch (IOException e) {
				System.out.println("agent_end failure!");
			}
		}
	}

	@Override
	public void agent_cleanup() {
		// System.out.println("***** NUMBER OF TIMES MY ROBOT HIT THE WALL:  " +
		// this.myRobot.wallHitCounter);
		this.lastStatesForLambda.clear();
		this.previousActionInt = 0;
		this.previousStateInt = 0;
	}

	/********************************************************************************************************************/

	private void eisgekuehlterSarsaLambda() {
		computeSARSADelta();

		E_TRACE_FUNCTION[this.previousActionInt][this.previousStateInt] = 1;

		if (this.previousStateInt == 0) {
			this.myRobot.addReward(1.0);
		}

		for (int t = lastStatesForLambda.size() - 1; t >= 1; t--) {

			// traces:
			if (this.previousActionInt == this.lastStatesForLambda.get(t)[0]
					&& this.previousStateInt == this.lastStatesForLambda.get(t)[1]) {
				E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] = E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]]* GAMMA
						* LAMBDA + 1;
			} else {
				E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] *= GAMMA
						* LAMBDA;
			}

			LARCRobot.VALUE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]] += ALPHA
					* this.computeSARSADelta()
					* E_TRACE_FUNCTION[this.lastStatesForLambda.get(t)[0]][this.lastStatesForLambda.get(t)[1]];

			// Remove duplicate values from Sarsa list:
			if (LARCRobot.STATE_REPEAT) {
				this.lastStatesForLambda.pop();
				LARCRobot.STATE_REPEAT = false;
			}
		}
	}

	private double computeSARSADelta() {
		this.previousStateQValue = LARCRobot.VALUE_FUNCTION[previousActionInt][previousStateInt];
		this.currentStateQValue = LARCRobot.VALUE_FUNCTION[currentActionInt][currentStateInt];
		return (myRobot.getCurrentReward() + GAMMA * this.currentStateQValue - this.previousStateQValue);
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
					int[] temp = this.lastStatesForLambda.poll();
					E_TRACE_FUNCTION[temp[0]][temp[1]] = 0;
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
		if (!EXPLORING_FROZEN) {
			if (randGenerator.nextDouble() <= EPSILON) { // best option is
															// selected
															// 1-epsilon of the
															// time
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
		PrintWriter outputWriter = null;
		outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
		for (int a = 0; a < LARCRobot.NO_OF_ACTIONS; a++) {
			for (int s = 0; s < LARCRobot.NO_OF_STATES; s++) {
				if (s == 0 && a == 0) {
					outputWriter.write(valuefunction[a][s] + "");
				} else {
					outputWriter.println();
					outputWriter.write(valuefunction[a][s] + "");
				}
			}
		}
		outputWriter.flush();
		outputWriter.close();

		if (LOG) {
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\bulletwallhitcounter.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.bulletwallhitcounter / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\winpercentage.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.roundsWon / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\ramHitCounter.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.ramHitCounter / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\rammedCounter.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.rammedCounter / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\selfHitByBulletcounter.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.selfHitByBulletcounter / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\enemyHitCounter.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.enemyHitCounter / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();
			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\wallRamCounter.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.wallRamCounter / this.myRobot.getNumRounds()));

			outputWriter.flush();
			outputWriter.close();

			outputWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					"Z:\\win7\\Repos\\HAW-LernendeAgenten_RC\\LARCbot_DanielAlex\\totalScore.csv", true)));
			outputWriter.println(Double.toString(RewardRobot.wallRamCounter / LARCRobot.BATTLE_SCORE));

			outputWriter.flush();
			outputWriter.close();
		}
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
