package utils;

import robocode.AdvancedRobot;

public abstract class Utils {
	
	/**
	 * Normalisiert Heading Winkel, so dass ein Winkel zwischen -180 und 180 Grad zurück gegeben wird.
	 * 180 / -180 Grad enspricht Westen, -90 Grad Norden, 0 Grad Osten, 90 Grad Süden.
	 * @param heading Original Heading
	 * @return Normalisiertes Heading
	 */
	public static double normalizeHeading(double heading) {
		int quadrant = (int) (heading / 90) % 4;

		switch (quadrant) {
		case 0:
		case 1:
		case 2:
			return heading - 90;
		case 3:
			return (heading - 270) - 180;
		default:
			return heading;
		}

	}

	/**
	 * Berechnet aus einer Position und den relativen Angaben zur Position eines Gegners seine absolute Position.
	 * @param selfPosition Eigene Position
	 * @param selfHeading Eigenes Heading (ohne Normalisierung)
	 * @param enemyBearing Be
	 * @param enemyDistance
	 * @return
	 */
	public static Vector2D relToAbsPosition(Vector2D selfPosition,
			double selfHeading, double enemyBearing, double enemyDistance) {
		Vector2D helpVector = (new Vector2D(1, 0)).rotate(
				selfHeading + enemyBearing).multiply(enemyDistance);
		return selfPosition.add(helpVector);
	}

	public static Vector2D getBotCoordinates(AdvancedRobot bot) {
		return new Vector2D(bot.getX(), bot.getY());
	}

}
