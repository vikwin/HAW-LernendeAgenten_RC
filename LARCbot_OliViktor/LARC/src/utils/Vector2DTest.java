package utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Vector2DTest {
	
	private static final double MAX_DEVIATION = 0.0001; // Maximale Abweichung für Werte

	private Vector2D a,b,c,d,e,f,g,h,i,j;
	private double scalarA, scalarB, scalarC, scalarD;
	
	@Before
	public void setUp() throws Exception {
		a = new Vector2D(1,1);
		b = new Vector2D(2,2);
		c = new Vector2D(2,0);
		d = new Vector2D(2,1);
		e = new Vector2D(-2,2);
		f = new Vector2D(-2,-2);
		g = new Vector2D(0,2);
		h = new Vector2D(-1,1);
		i = new Vector2D(1,-1);
		j = new Vector2D(-1,-1);
		
		scalarA = 1.0;
		scalarB = 0.0;
		scalarC = 2.0;
		scalarD = -1.0;
	}

	@Test
	public void testEquals() {
		assertEquals(a, new Vector2D(1,1));
		assertNotEquals(a, new Vector2D(2,2));
	}
	
	@Test
	public void testAdd() {
		assertEquals(new Vector2D(3,3), a.add(b));
	}
	
	@Test
	public void testSubtract() {
		assertEquals(new Vector2D(-1,-1), a.subtract(b));
	}
	
	@Test
	public void testLength() {
		assertEquals(Math.sqrt(2), a.length(), MAX_DEVIATION);
	}
	
	@Test
	public void testMultiply() {
		assertEquals(new Vector2D(1, 1), a.multiply(scalarA));
		assertEquals(new Vector2D(0, 0), a.multiply(scalarB));
		assertEquals(new Vector2D(4, 4), b.multiply(scalarC));
		assertEquals(new Vector2D(-2, -2), b.multiply(scalarD));
	}
	
	@Test
	public void testDistance() {
		assertEquals(1, b.distanceTo(d), MAX_DEVIATION);
	}
	
	@Test
	public void testRotate() {
		Vector2D rotatedA1 = a.rotate(45);
		Vector2D rotatedA2 = a.rotateRad(Math.PI / 4);
		Vector2D rotatedA3 = a.rotate(-45);
		
		Vector2D rotatedH1 = h.rotate(45);
		Vector2D rotatedH2 = h.rotate(-45);
		
		Vector2D rotatedI1 = i.rotate(45);
		Vector2D rotatedI2 = i.rotate(-45);
		
		Vector2D rotatedJ1 = j.rotate(45);
		Vector2D rotatedJ2 = j.rotate(-45);
		
		// a -> Quadrant oben rechts
		assertEquals(Math.sqrt(2), rotatedA1.getX(), MAX_DEVIATION);
		assertEquals(0.0, rotatedA1.getY(), MAX_DEVIATION);
		assertEquals(Math.sqrt(2), rotatedA2.getX(), MAX_DEVIATION);
		assertEquals(0.0, rotatedA2.getY(), MAX_DEVIATION);
		assertEquals(0.0, rotatedA3.getX(), MAX_DEVIATION);
		assertEquals(Math.sqrt(2), rotatedA3.getY(), MAX_DEVIATION);

		// h -> Quadrant oben links
		assertEquals(0.0, rotatedH1.getX(), MAX_DEVIATION);
		assertEquals(Math.sqrt(2), rotatedH1.getY(), MAX_DEVIATION);
		assertEquals(-Math.sqrt(2), rotatedH2.getX(), MAX_DEVIATION);
		assertEquals(0.0, rotatedH2.getY(), MAX_DEVIATION);
		
		// i -> Quadrant unten rechts
		assertEquals(0.0, rotatedI1.getX(), MAX_DEVIATION);
		assertEquals(-Math.sqrt(2), rotatedI1.getY(), MAX_DEVIATION);
		assertEquals(Math.sqrt(2), rotatedI2.getX(), MAX_DEVIATION);
		assertEquals(0.0, rotatedI2.getY(), MAX_DEVIATION);

		// j -> Quadrant unten links
		assertEquals(-Math.sqrt(2), rotatedJ1.getX(), MAX_DEVIATION);
		assertEquals(0.0, rotatedJ1.getY(), MAX_DEVIATION);
		assertEquals(0.0, rotatedJ2.getX(), MAX_DEVIATION);
		assertEquals(-Math.sqrt(2), rotatedJ2.getY(), MAX_DEVIATION);		
	}
	
	@Test
	public void testGetHeading() {
		assertEquals(0.0, g.getHeading(), MAX_DEVIATION);
		assertEquals(45.0, a.getHeading(), MAX_DEVIATION);
		assertEquals(45.0, b.getHeading(), MAX_DEVIATION);
		assertEquals(90.0, c.getHeading(), MAX_DEVIATION);
		assertEquals(315.0, e.getHeading(), MAX_DEVIATION);
		assertEquals(225.0, f.getHeading(), MAX_DEVIATION);
	}
	
	@Test
	public void testGetNormalHeading() {
		assertEquals(-90.0, g.getNormalHeading(), MAX_DEVIATION);
		assertEquals(-45.0, a.getNormalHeading(), MAX_DEVIATION);
		assertEquals(-45.0, b.getNormalHeading(), MAX_DEVIATION);
		assertEquals(0.0, c.getNormalHeading(), MAX_DEVIATION);
		assertEquals(-135.0, e.getNormalHeading(), MAX_DEVIATION);
		assertEquals(135.0, f.getNormalHeading(), MAX_DEVIATION);
	}
	
	@Test
	public void testAngleTo() {
		assertEquals(0.0, a.angleTo(b), MAX_DEVIATION);
		assertEquals(180.0, b.angleTo(f), MAX_DEVIATION);
		assertEquals(90.0, g.angleTo(c), MAX_DEVIATION);
		assertEquals(-90.0, c.angleTo(g), MAX_DEVIATION);
	}

}
