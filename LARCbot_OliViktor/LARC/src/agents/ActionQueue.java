package agents;

import java.util.Iterator;

public class ActionQueue {
	private int[] elements;
	private int count, endIndex;
	
	private class Itr implements Iterator<Integer> {
		int index, delta;
		
		public Itr(int begin, int delta) {
			this.index = begin;
			this.delta = delta;
		}
		
		@Override
		public boolean hasNext() {
			return index >= 0 && index < elements.length;
		}

		@Override
		public Integer next() {
			Integer i = elements[index];
			index += delta;
			return i;
		}
	}
	
	/**
	 * Erstellt eine Queue mit der angegebenen Kapazität.
	 * @param capacity die Kapazität
	 */
	public ActionQueue(int capacity) {
		elements = new int[capacity];
		count = 0;
		endIndex = -1;
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
		return new Itr(count - 1, -1);
	}
	
	/**
	 * Fügt das angegebene Element der Queue hinzu,
	 * sofern diese noch nicht voll ist.
	 * @return false, wenn Queue bereits voll ist, true sonst.
	 */
	public boolean offer(Integer elem) {
		if (count >= elements.length)
			return false;
		
		endIndex = (endIndex + 1) % elements.length;
		elements[endIndex] = elem;
		count++;
		return true;
	}
	
	/**
	 * Gibt das Element am Kopf der Queue aus
	 * und entfern dieses.
	 * @return Das Element am Kopf der Queue.
	 */
	public Integer poll() {
		if (endIndex < 0)
			return null;
		
		count--;
		return elements[endIndex--];
	}

	/**
	 * @return Anazhl der Elemente in der Queue.
	 */
	public int size() {
		return count;
	}

	/**
	 * Prüft, ob das angebene Element in der Queue
	 * vorhanden ist.
	 * @param elem das gesuchte Element
	 * @return true, wenn das Element gefunden wurde, false sonst
	 */
	public boolean contains(int elem) {
		for (int i = 0; i < elements.length; i++) {
			if (elem == elements[i])
				return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String s = "";
		
		for (int i = 0; i < count; i++) {
			s += ", " + elements[i];
		}
		
		return "~~~" + s.substring(2) + ">";
	}
}
