package agents;

import java.util.Iterator;
import java.util.LinkedList;

public class ActionQueue {
	private int free;
	private LinkedList<Integer> elements;
	
	private class Itr implements Iterator<Integer> {
		int index, delta;
		
		public Itr(int begin, int delta) {
			this.index = begin;
			this.delta = delta;
		}
		
		@Override
		public boolean hasNext() {
			return index >= 0 && index < elements.size();
		}

		@Override
		public Integer next() {
			Integer i = elements.get(index);
			index += delta;
			return i;
		}
	}
	
	/**
	 * Erstellt eine Queue mit der angegebenen Kapazität.
	 * @param capacity die Kapazität
	 */
	public ActionQueue(int capacity) {
		elements = new LinkedList<Integer>();
		free = capacity;
	}
	
	/**
	 * @return Einen Iterator, der vom Kopf bis zum Ende der Queue durchläuft.
	 */
	public Iterator<Integer> iterator() {
		return new Itr(0, 1);
	}
	
	/**
	 * @return Einen Iterator, der vom Ende bis zum Kopf der Queue durchläuft.
	 */
	public Iterator<Integer> reverseIterator() {
		return new Itr(elements.size() - 1, -1);
	}
	
	/**
	 * Fügt das angegebene Element der Queue hinzu,
	 * sofern diese noch nicht voll ist.
	 * @return false, wenn Queue bereits voll ist, true sonst.
	 */
	public boolean offer(Integer elem) {
		if (free <= 0)
			return false;
		
		elements.add(0, elem);
		free--;
		return true;
	}
	
	/**
	 * Gibt das Element am Kopf der Queue aus
	 * und entfern dieses.
	 * @return Das Element am Kopf der Queue.
	 */
	public Integer poll() {
		if (elements.isEmpty())
			return null;
		
		free++;
		int polled = elements.getLast();
		elements.removeLast();
		return polled;
	}

	/**
	 * @return Anazhl der Elemente in der Queue.
	 */
	public int size() {
		return elements.size();
	}

	/**
	 * Prüft, ob das angebene Element in der Queue
	 * vorhanden ist.
	 * @param elem das gesuchte Element
	 * @return true, wenn das Element gefunden wurde, false sonst
	 */
	public boolean contains(int elem) {
		return elements.contains(elem);
	}
	
	@Override
	public String toString() {
		if (elements.size() == 0)
			return "~>";
		
		String s = "";
		
		for (int i = 0; i < elements.size(); i++) {
			s += ", " + elements.get(i);
		}
		
		return "~" + s.substring(2) + ">";
	}
}
