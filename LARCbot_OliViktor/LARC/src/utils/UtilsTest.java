package utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class UtilsTest {

	Vector2D a, b;
	
	@Before
	public void setUp() throws Exception {
		
		a = new Vector2D(2,1);
		b = new Vector2D(1,1);
	}

	@Test
	public void testRelToAbsPosition() {
		assertEquals(b, Utils.relToAbsPosition(a, 0, 180, 1));
	}
	
	@Test
	public void testNormalizeHeading(){
		assertEquals(-90.0, Utils.normalizeHeading(0), 0.005);
		assertEquals(-45.0, Utils.normalizeHeading(45), 0.005);
		assertEquals(0.0, Utils.normalizeHeading(90), 0.005);
		assertEquals(45.0, Utils.normalizeHeading(135), 0.005);
		assertEquals(90.0, Utils.normalizeHeading(180), 0.005);
		assertEquals(135.0, Utils.normalizeHeading(225), 0.005);
		assertEquals(180.0, Math.abs(Utils.normalizeHeading(270)), 0.005);
		assertEquals(-135.0, Utils.normalizeHeading(315), 0.005);
//		
//		assertEquals(0.0, Utils.normalizeHeading(90), 0.005);
//		assertEquals(45.0, Utils.normalizeHeading(45), 0.005);
//		assertEquals(90.0, Utils.normalizeHeading(0), 0.005);
//		assertEquals(135.0, Utils.normalizeHeading(315), 0.005);
//		assertEquals(180.0, Utils.normalizeHeading(270), 0.005);
//		assertEquals(-135.0, Utils.normalizeHeading(225), 0.005);
//		assertEquals(-90.0, Utils.normalizeHeading(180), 0.005);
//		assertEquals(-45.0, Utils.normalizeHeading(135), 0.005);
	}

}
