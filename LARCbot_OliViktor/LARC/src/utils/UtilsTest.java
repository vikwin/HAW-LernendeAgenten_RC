package utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class UtilsTest {

	private static final double MAX_DEVIATION = 0.0001; // Maximale Abweichung
														// für Werte

	private Vector2D a, b;

	@Before
	public void setUp() throws Exception {

		a = new Vector2D(2, 1);
		b = new Vector2D(1, 1);

	}

	@Test
	public void testRelToAbsPosition() {
		assertEquals(b, Utils.relToAbsPosition(a, 90, 180, 1));
		assertEquals(b, Utils.relToAbsPosition(a, 90, -180, 1));
		
		assertEquals(b, Utils.relToAbsPosition(a, 315, -45, 1));
	}

	@Test
	public void testNormalizeHeading() {
		assertEquals(-90.0, Utils.normalizeHeading(0), MAX_DEVIATION);
		assertEquals(-45.0, Utils.normalizeHeading(45), MAX_DEVIATION);
		assertEquals(0.0, Utils.normalizeHeading(90), MAX_DEVIATION);
		assertEquals(45.0, Utils.normalizeHeading(135), MAX_DEVIATION);
		assertEquals(90.0, Utils.normalizeHeading(180), MAX_DEVIATION);
		assertEquals(135.0, Utils.normalizeHeading(225), MAX_DEVIATION);
		assertEquals(180.0, Math.abs(Utils.normalizeHeading(270)),
				MAX_DEVIATION);
		assertEquals(-135.0, Utils.normalizeHeading(315), MAX_DEVIATION);
	}

	@Test
	public void testRadToDeg() {
		assertEquals(180.0, Utils.radToDeg(Math.PI), MAX_DEVIATION);
		assertEquals(360.0, Utils.radToDeg(2*Math.PI), MAX_DEVIATION);
		assertEquals(90.0, Utils.radToDeg(Math.PI/2), MAX_DEVIATION);
		assertEquals(-90.0, Utils.radToDeg(-Math.PI/2), MAX_DEVIATION);
		assertEquals(0.0, Utils.radToDeg(0.0), MAX_DEVIATION);
	}
	
	@Test
	public void testDegToRad() {
		assertEquals(Math.PI, Utils.degToRad(180.0), MAX_DEVIATION);
		assertEquals(2*Math.PI, Utils.degToRad(360.0), MAX_DEVIATION);
		assertEquals(Math.PI/2, Utils.degToRad(90.0), MAX_DEVIATION);
		assertEquals(-Math.PI/2, Utils.degToRad(-90.0), MAX_DEVIATION);
		assertEquals(0.0, Utils.degToRad(0.0), MAX_DEVIATION);
	}

}
