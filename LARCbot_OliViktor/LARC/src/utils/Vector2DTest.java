package utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Vector2DTest {

	Vector2D a,b,c,d;
	double scalarA, scalarB, scalarC, scalarD;
	
	@Before
	public void setUp() throws Exception {
		a = new Vector2D(1,1);
		b = new Vector2D(2,2);
		c = new Vector2D(1,2);
		d = new Vector2D(2,1);
		
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
		assertEquals(Math.sqrt(2), a.length(), 0.005);
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
		assertEquals(1, b.distanceTo(d), 0.005);
	}
	
	@Test
	public void testRotate() {
		Vector2D rotated1 = a.rotate(45);
		Vector2D rotated2 = a.rotateRad(Math.PI / 4);
		
		assertEquals(0.0, rotated1.getX(), 0.005);
		assertEquals(Math.sqrt(2), rotated1.getY(), 0.005);
		
		assertEquals(0.0, rotated2.getX(), 0.005);
		assertEquals(Math.sqrt(2), rotated2.getY(), 0.005);
		
	}

}
