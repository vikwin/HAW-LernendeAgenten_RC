package agents;

public enum AgentMode {
	RANDOM, SARSA_LAMBDA, Q_LEARNING, FIGHTING;
	
	public String toString() {
		String s = "";
		switch (this) {
		case RANDOM:
			s = "Zufälliges Verhalten";
			break;
		case FIGHTING:
			s = "Kämpfend (kein Lernen)";
			break;
		case Q_LEARNING:
			s = "Q-Learning";
			break;
		case SARSA_LAMBDA:
			s = "SARSA-Lambda";
			break;
		}
		
		return s;
	};
}
