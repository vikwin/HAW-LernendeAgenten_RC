package src.agents;

import java.util.Random;

import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

public class LARCAgent implements AgentInterface {

	public static final int NO_OF_STATES = 270000;
	public static final int NO_OF_ACTIONS = 16;
	private Random randGenerator = new Random();
	private double sarsa_stepsize = 0.1; // TBD
	private double sarsa_epsilon = 0.1; // Lern-Faktor
	private double sarsa_gamma = 0.9; // Zeit Discount Factor
	private Action lastAction;
	private Observation lastObservation;
	private int[] stateList;
	private double[][] valueFunction = null;
    private boolean policyFrozen = false;
    private boolean exploringFrozen = false;

	@Override
	public void agent_init(String arg0) {
		this.stateList = new int[NO_OF_STATES];
		valueFunction = new double[NO_OF_ACTIONS][NO_OF_STATES];
	}
	   /**
     * Choose an action e-greedily from the value function and store the action
     * and observation.
     * @param observation
     * @return
     */
	@Override
	public Action agent_start(Observation observation) {
	    int newActionInt = egreedy(observation.getInt(0));

        /**
         * Create a structure to hold 1 integer action
         * and set the value
         */
        Action returnAction = new Action(1, 0, 0);
        returnAction.intArray[0] = newActionInt;

        lastAction = returnAction.duplicate();
        lastObservation = observation.duplicate();

        return returnAction;
	}

	@Override
	public Action agent_step(double arg0, Observation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void agent_end(double arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String agent_message(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void agent_cleanup() {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * Selects a random action with probability 1-sarsa_epsilon, and the action
	 * with the highest value otherwise. This is a quick'n'dirty implementation,
	 * it does not do tie-breaking.
	 * 
	 * @param theState
	 * @return
	 */
	private int egreedy(int theState) {
		if (!exploringFrozen) {
			if (randGenerator.nextDouble() <= sarsa_epsilon) {
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
}
