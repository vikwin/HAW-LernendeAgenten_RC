package agents;

import robot.Action;

public class LearnedAction {
	private double value;		// Insgesamte Bewertung der Action-State-Kombination
	private int noc;		// NumberOfCalls - Anzahl der Ausführungen der Action-State-Kombination
	
	private Action action;

	public LearnedAction(Action action, int stateID) {
		this.action = action;
		this.value = 0;
		this.noc = 0; 
	}
	
	public Action getAction() {
		return action;
	}
	
	public double getValue() {
		return value;
	}
	
	public void addReward(double value, boolean incNoc) {
		this.value += value;
		this.noc += incNoc ? 1 : 0;
	}
}
