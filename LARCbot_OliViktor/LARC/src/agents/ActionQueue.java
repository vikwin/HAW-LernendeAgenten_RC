package agents;

/**
 * Diese Klasse verwaltet {@link LearnedAction} in einer Queue mit fester Größe.
 * Wenn die Queue voll ist und ein neues Element hinzugefügt wird, wird das älteste
 * Element der Queue mit dem neuen Element überschrieben.
 * @author Oliver
 */
public class ActionQueue {
	private int size, end;
	private LearnedAction[] queue;
	private double discount;
	
	/**
	 * Initialisiert die Queue.
	 * @param size Feste Größe der Queue
	 * @param discount Discount-Rate für addReinforcement.
	 */
	public ActionQueue(int size, double discount) {
		this.size = size;
		this.queue = new LearnedAction[size];
		this.end = 0;
		
		this.discount = discount;
	}
	
	/**
	 * Fügt eine {@link LearnedAction} zu der Queue hinzu.
	 * @param action Die hinzuzufügende Action
	 */
	public void put(LearnedAction action) {
		queue[end] = action;
		end = (end + 1) % size;
	}
	
	/**
	 * Fügt den angebenen Wert als Belohnung oder Bestrafung zu allen
	 * Actions in der Queue hinzu.
	 * Dabei wird der Wert mit der bei der Initialisierung anegebenen
	 * Discount-Rate abgeschwächt.
	 * @param reinforcement Die Belohnung (positiver Wert) oder Bestrafung (negativer Wert)
	 */
	public void addReinforcement(int reinforcement) {
		boolean incNoc = true;
		int i = end;
		double d = discount;
		
		do {
			i = (i + 1) % size;
			
			if (queue[i] == null) {
				break;
			}
			
			queue[i].addReinforcement(reinforcement * d, incNoc);
			
			incNoc = false;
			d *= d;
		} while (i != end);
	}
}
