package agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import robot.LARCRobot;

public class LARCAgent implements IAgent {

	public static final String PATH = "qValues.csv";

	public static double[][] E_TRACE_FUNCTION;
	public static final double INITIAL_Q_VALUE = 0.0;
	public static final int LAMBDA_CAPACITY = 15;

	private static final double SARSA_EPSILON = 0.9; // Exploration rate
	private static final double SARSA_GAMMA = 0.9; // Time Discount factor
	private static final double SARSA_ALPHA = 0.5; // learning rate (importance of new information)
	private static final double LAMBDA_VALUE = 0.9; // Abschwächungsfaktor

	private Random randGenerator = new Random();
	private int previousActionInt;
	private int previousStateInt;
	private int currentActionInt;
	private int currentStateInt;
	private MyAction myAction;
	private boolean policyFrozen = true; // lernen
	private boolean exploringFrozen = true; // ausprobieren
	private LARCRobot myRobot;
	private double previousStateQValue;
	private double currentStateQValue;
	private LinkedList<int[]> lastStatesForLambda;
	private double currentQDataValue;

	public LARCAgent(LARCRobot myRobot) {
		this.myRobot = myRobot;
		this.myAction = new MyAction(this.myRobot);
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

		this.add(new int[] { previousActionInt, previousStateInt });

		return currentActionInt;
	}

	@Override
	public int agent_step(int stateInt) {
		SpecificAction nextAction = SpecificAction.values()[currentActionInt];
		this.myRobot.move(this.myAction.getMoveVector(nextAction));

		currentActionInt = egreedy(stateInt);
		currentStateInt = stateInt;

		if (!policyFrozen) {
			this.doTheSilenceOfTheLambda(); // zuweisen des neu gelernten q-wertes
		}

		previousActionInt = currentActionInt;
		previousStateInt = currentStateInt;

		this.add(new int[] { previousActionInt, previousStateInt });

		return currentActionInt;
	}

	@Override
	public void agent_end() {

		if (!policyFrozen) {
			this.doTheSilenceOfTheLambda(); // zuweisen des neu gelernten q-wertes
		}

		// TODO do not save when learning is frozen
		if (this.myRobot.getNumRounds() - 1 == this.myRobot.getRoundNum() && !policyFrozen) {
			try {
				this.saveValueFunction(LARCAgent.PATH, LARCRobot.VALUE_FUNCTION);
			} catch (IOException e) {
				System.out.println("SAFE FUNCTION HAS FUCKED US!");
			}
		}
	}

	@Override
	public void agent_cleanup() {
		this.lastStatesForLambda.clear();
		this.previousActionInt = 0;
		this.previousStateInt = 0;
	}

	/********************************************************************************************************************/
	public void doTheSilenceOfTheLambda() {
		this.computeQDelta();
		E_TRACE_FUNCTION[previousActionInt][previousStateInt]++;

		// Q(a,s) += alpha*delta*etrace(a,s)
		// etrace(a,s) *= gamme * lambda
		for (int i = lastStatesForLambda.size() - 1; i >= 1; i--) {
			LARCRobot.VALUE_FUNCTION[this.lastStatesForLambda.get(i)[0]][this.lastStatesForLambda.get(i)[1]] += SARSA_ALPHA
					* currentQDataValue
					* E_TRACE_FUNCTION[this.lastStatesForLambda.get(i)[0]][this.lastStatesForLambda.get(i)[1]];
			E_TRACE_FUNCTION[this.lastStatesForLambda.get(i)[0]][this.lastStatesForLambda.get(i)[1]] *= SARSA_GAMMA
					* LAMBDA_VALUE;
		}
	}

	public void computeQDelta() {
		this.previousStateQValue = LARCRobot.VALUE_FUNCTION[previousActionInt][previousStateInt];
		this.currentStateQValue = LARCRobot.VALUE_FUNCTION[currentActionInt][currentStateInt];
		this.currentQDataValue = myRobot.getCurrentReward() + SARSA_GAMMA * this.currentStateQValue
				- this.previousStateQValue;
	}

	public void add(int[] indexPair) {
		if (lastStatesForLambda.size() >= LAMBDA_CAPACITY) {
			this.lastStatesForLambda.poll();
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
			if (randGenerator.nextDouble() <= SARSA_EPSILON) { // best option is selected 1-epsilon of the time
				return randGenerator.nextInt(LARCRobot.NO_OF_ACTIONS);
			}
		}

		/* otherwise choose the greedy action */
		int maxIndex = 0;
		for (int a = 0; a < LARCRobot.NO_OF_ACTIONS; a++) {
			if (LARCRobot.VALUE_FUNCTION[a][theState] > LARCRobot.VALUE_FUNCTION[maxIndex][theState]) {
				maxIndex = a;
			}
		}
		return maxIndex;
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
		for (int a = 0; a < LARCRobot.NO_OF_ACTIONS; a++) {
			for (int s = 0; s < LARCRobot.NO_OF_STATES; s++) {
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
		System.out.println("Read valueFunction!!!");
	}
}
