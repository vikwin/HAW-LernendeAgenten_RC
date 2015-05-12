package agents;

import static org.junit.Assert.*;

import org.junit.Test;

public class ActionQueueTest {
	@Test
	public void testOfferAndPoll() {
		ActionQueue queue = new ActionQueue(4);

		// Queue befüllen
		assertTrue(queue.offer(1));
		assertTrue(queue.offer(2));
		assertTrue(queue.offer(3));
		assertTrue(queue.offer(4));
		assertEquals("~4, 3, 2, 1>", queue.toString());

		// volle Queue darf nichts aufnehmen
		assertFalse(queue.offer(5));

		// Elemente aus der Queue entfernen
		assertEquals(new Integer(1), queue.poll());
		assertEquals("~4, 3, 2>", queue.toString());

		// neue Elemente rausholen
		assertTrue(queue.offer(5));
		assertEquals("~5, 4, 3, 2>", queue.toString());
	}

	@Test
	public void testSize() {
		ActionQueue queue = new ActionQueue(6);
		assertTrue(queue.size() == 0);

		// Queue befüllen
		queue.offer(1);
		queue.offer(2);
		assertTrue(queue.size() == 2);
	}

	@Test
	public void testContains() {
		ActionQueue queue = new ActionQueue(4);

		// Queue befüllen
		queue.offer(1);
		queue.offer(2);
		queue.offer(3);
		queue.offer(4);
		
		assertTrue(queue.contains(3));
		assertFalse(queue.contains(6));
	}

}
