package agents;

import java.io.Serializable;

import robot.Action;
import environment.State;

public class LearnedAction implements Serializable {
	private int value;		// Insgesamte Bewertung der Action-State-Kombination
	private int noc;		// NumberOfCalls - Anzahl der Ausführungen der 
	
	private Action action;
	private State state;

	public LearnedAction(State state, Action action, int stateID) {
		this.state = state;
		this.action = action;
		this.value = 0;
		this.noc = 0;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void addReinforcement(int value) {
		this.value += value;
		this.noc++;
	}
}
