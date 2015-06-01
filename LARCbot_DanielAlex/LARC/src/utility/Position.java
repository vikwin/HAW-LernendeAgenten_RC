package utility;

import agents.LARCAgent;

public class Position {

	private double x;
	private double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public static void printdebug(String toPrint) {
		if (LARCAgent.DEBUG) {
			System.out.println(toPrint);
		}
	}

	// TESTED TO PERFECTION:
	public static double normalizeDegrees(double degrees) {
		while (degrees > 180)
			degrees -= 360;
		while (degrees < -180)
			degrees += 360;
		return degrees;
	}
}
