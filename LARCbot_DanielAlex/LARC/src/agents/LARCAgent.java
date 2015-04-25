package agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import robot.LARCRobot;

public class LARCAgent implements IAgent {

	public static final String PATH = "qValues.csv";

	public static final int NO_OF_STATES = 270000;
	public static final int NO_OF_ACTIONS = 16;
	public static final double INITIAL_Q_VALUE = 0.0;
	private Random randGenerator = new Random();
	// private double sarsa_stepsize = 0.1; // TBD
	private double sarsa_epsilon = 1.0; // Exploration rate
	private double sarsa_gamma = 0.9; // Time Discount factor
	private double sarsa_alpha = 0.5; // learning rate (importance of new information)
	private int lastAction;
	private int lastState;
	private MyAction myAction;
	private double[][] valueFunction = null;
	private boolean policyFrozen = false;
	private boolean exploringFrozen = false;
	private LARCRobot myRobot;
	private double oldQValue;
	private double nextQValue;
	private double newQValue;

	public LARCAgent(LARCRobot myRobot) {
		this.myRobot = myRobot;
		this.myAction = new MyAction(this.myRobot);
	}

	@Override
	public void agent_init() {
		valueFunction = new double[NO_OF_ACTIONS][NO_OF_STATES];
		if (!new File(PATH).isFile()) {
			for (int i = 0; i < NO_OF_ACTIONS; i++) {
				for (int j = 0; j < NO_OF_STATES; j++) {
					valueFunction[i][j] = INITIAL_Q_VALUE;
				}
			}
			try {
				this.saveValueFunction(PATH, valueFunction);
			} catch (IOException e) {
				System.out.println("Save ist fehlgeschlagen!!!");
				e.printStackTrace();
			}
		} else {
			try {
				loadValueFunction(PATH);
			} catch (IOException e) {
				System.out.println("Load ist fehlgeschlagen!!!");
				e.printStackTrace();
			}
		}
	}

	@Override
	public int agent_start(int state) {

		int newActionInt = egreedy(state);

		lastAction = newActionInt;
		lastState = state;

		SpecificAction nextAction = SpecificAction.values()[newActionInt];
		this.myRobot.move(this.myAction.getMoveVector(nextAction));

		return newActionInt;
	}

	@Override
	public int agent_step(int state) {
		int newActionInt = egreedy(state);

		SpecificAction nextAction = SpecificAction.values()[newActionInt];

		this.myRobot.move(this.myAction.getMoveVector(nextAction));

		// AGENT LEARNING:
		this.oldQValue = this.valueFunction[lastAction][lastState];
		this.nextQValue = this.valueFunction[newActionInt][state];

		// sarsa-function:
		this.newQValue = this.oldQValue + sarsa_alpha
				* (myRobot.getLastReward() + this.sarsa_gamma * this.nextQValue - this.oldQValue);

		if (!policyFrozen) {
			this.valueFunction[lastAction][lastState] = this.newQValue; // zuweisen des neu gelernten q-wertes
		}

		lastAction = newActionInt;
		lastState = state;

		return newActionInt;
	}

	@Override
	public void agent_end() {
		// AGENT TREMINAL LEARNING:
		this.oldQValue = this.valueFunction[lastAction][lastState];

		// sarsa-function:
		this.newQValue = this.oldQValue + sarsa_alpha * (myRobot.getLastReward() - this.oldQValue);

		if (!policyFrozen) {
			this.valueFunction[lastAction][lastState] = this.newQValue;
		}

		try {
			saveValueFunction(PATH, valueFunction);
		} catch (IOException e) {
			System.out.println("Save ist fehlgeschlagen!!!");
			e.printStackTrace();
		}
	}

	@Override
	public void agent_cleanup() {
		lastAction = 0;
		lastState = 0;
		valueFunction = null;
	}

	/**
	 * returns the best actionInt to a state if exploration is frozen.
	 * otherwise returns random action with a chance based on the exploration-rate.
	 * 
	 * @param theState
	 * @return maxIndex
	 */
	private int egreedy(int theState) {
		if (!exploringFrozen) {
			if (randGenerator.nextDouble() <= sarsa_epsilon) { // best option is selected 1-epsilon of the time
				return randGenerator.nextInt(NO_OF_ACTIONS);
			}
		}

		/* otherwise choose the greedy action */
		int maxIndex = 0;
		for (int a = 1; a < NO_OF_ACTIONS; a++) {
			if (valueFunction[a][theState] > valueFunction[maxIndex][theState]) {
				maxIndex = a;
			}
		}
		return maxIndex;
	}

	/**
	 * Saves the value function to a file named filePath.
	 * 
	 * @param filePath
	 * @param valueFunction
	 * @throws IOException
	 */
	public void saveValueFunction(String filePath, double[][] valuefunction) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filePath));
		for (int a = 0; a < NO_OF_ACTIONS; a++) {
			for (int s = 0; s < NO_OF_STATES; s++) {
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
		for (int a = 0; a < NO_OF_ACTIONS; a++) {
			for (int s = 0; s < NO_OF_STATES; s++) {
				valueFunction[a][s] = Double.parseDouble(outputReader.readLine());
			}
		}
		outputReader.close();
		System.out.println("Read valueFunction!!!");
	}
}
