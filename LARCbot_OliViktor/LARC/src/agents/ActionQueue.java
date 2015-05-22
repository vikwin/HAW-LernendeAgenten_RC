package agents;

import java.util.Iterator;
import java.util.LinkedList;

public class ActionQueue {
	private int free;
	private Integer newestItem;
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
		newestItem = null;
	}
	
	/**
	 * @return Einen Iterator, der vom Kopf bis zum Ende der Queue durchläuft.
	 */
	public Iterator<Integer> iterator() {
		return new Itr(elements.size() - 1, -1);
	}
	
	/**
	 * @return Einen Iterator, der vom Ende bis zum Kopf der Queue durchläuft.
	 */
	public Iterator<Integer> reverseIterator() {
		return new Itr(0, 1);
	}
	
	/**
	 * Fügt das angegebene Element der Queue hinzu, sofern diese noch nicht voll ist.
	 * Wenn das Element schon enthalten ist, wird der alte Wert gelöscht und
	 * das Element als neustes Element in der Queue hinzugefügt.
	 * 
	 * @return false, wenn Queue bereits voll ist, true sonst.
	 */
	public boolean offer(Integer elem) {
		if (newestItem == null) {
			newestItem = elem;
			return true;
		}
		
		if (contains(newestItem)) {
			delete(elements.indexOf(newestItem));
		}
		
		if (free <= 0)
			return false;
		
		elements.add(0, newestItem);
		free--;
		newestItem = elem;
		return true;
	}
	
	/**
	 * Gibt das Element am Kopf der Queue aus
	 * und entfern dieses.
	 * @return Das Element am Kopf der Queue.
	 */
	public Integer poll() {
		return delete(size() - 1);
	}
	
	private Integer delete(int index) {
		if (elements.isEmpty())
			return null;
		
		free++;
		int polled = elements.get(index);
		elements.remove(index);
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
	
	/**
	 * Gibt das Element zurück, was als letztes erfolgreich der Queue angeboten wurde. (siehe offer)
	 * @return das neuste Element
	 */
	public Integer getNewestElement() {
		return newestItem;
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
