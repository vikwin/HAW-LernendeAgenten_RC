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
	private MyAction myAction;
	private int lastState;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				loadValueFunction(PATH);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
	 *
	 * Selects a random action with probability 1-sarsa_epsilon, and the action with the highest value otherwise. This is a quick'n'dirty implementation, it does not do
	 * tie-breaking.
	 * 
	 * @param theState
	 * @return
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

	public void saveValueFunction(String filename, double[][] vf) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
		for (int a = 0; a < NO_OF_ACTIONS; a++) {
			for (int s = 0; s < NO_OF_STATES; s++) {
				if (a == 0 && s == 0) {
					outputWriter.write(vf[a][s] + "");
				} else {
					outputWriter.newLine();
					outputWriter.write(vf[a][s] + "");
				}
			}
		}
		outputWriter.flush();
		outputWriter.close();
		System.out.println("SAVE WAS CALLE !!!");
	}

	/**
	 * Loads the value function from a file named theFileName. Must be called after init but before cleanup.
	 * 
	 * @param theFileName
	 */
	private void loadValueFunction(String theFileName) throws IOException {
		BufferedReader outputReader = null;
		outputReader = new BufferedReader(new FileReader(theFileName));
		for (int a = 0; a < NO_OF_ACTIONS; a++) {
			for (int s = 0; s < NO_OF_STATES; s++) {
				valueFunction[a][s] = Double.parseDouble(outputReader.readLine());
			}
		}
		outputReader.close();
	}
}
