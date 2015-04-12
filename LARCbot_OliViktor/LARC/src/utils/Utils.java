package utils;

import environment.Enemy;
import robocode.AdvancedRobot;

/**
 * Eine reine Utility Klasse zur Sammlung nützlicher Methoden. Die Meisten sind rein mathematischer Natur.
 * @author Viktor
 *
 */

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
	 * Berechnet aus einer Position und den relativen Angaben zur Position eines Gegners dessen absolute Position
	 * in Form eines Ortsvektors.
	 * @param selfPosition Eigene Position
	 * @param selfHeading Eigenes Heading (ohne Normalisierung)
	 * @param enemyBearing Be
	 * @param enemyDistance
	 * @return Ortsvektor des Gegners
	 */
	public static Vector2D relToAbsPosition(Vector2D selfPosition,
			double selfHeading, double enemyBearing, double enemyDistance) {
//		Vector2D helpVector = (new Vector2D(1, 0)).rotate(
//				normalizeHeading(selfHeading) + enemyBearing).multiply(enemyDistance);
//		return selfPosition.add(helpVector);
		
		Vector2D helpVector = new Vector2D(1, 0);
		
		double normalizedHeading = normalizeHeading(selfHeading);
		helpVector = helpVector.rotate(normalizedHeading + enemyBearing);
		helpVector = helpVector.multiply(enemyDistance);
		Vector2D result = selfPosition.add(helpVector);
		return result;

	}

	
	/**
	 * Berechnet den Ortsvektor eines Gegners anhand der eigenen Position und eines Enemy Objekts.
	 * @param selfBot Der eigene Bot
	 * @param enemy Der Gegnerische Bot
	 * @return Ortsvektor des Gegners
	 */
	public static Vector2D relToAbsPosition(AdvancedRobot selfBot,
			Enemy enemy) {
		return relToAbsPosition(Utils.getBotCoordinates(selfBot), selfBot.getHeading(), enemy.getBearing(), enemy.getDistance());
	}

	
	/**
	 * Liefert einen Ortsvektor für einen Bot.
	 * @param bot Der Bot
	 * @return Ortsvektor des Bots
	 */
	public static Vector2D getBotCoordinates(AdvancedRobot bot) {
		return new Vector2D(bot.getX(), bot.getY());
	}

	/**
	 * Rechnet Bogenmaß in Gradmaß um.
	 * @param radians Bogenmaß
	 * @return Gradmaß
	 */
	public static double radToDeg(double radians) {
		return radians * 180 / Math.PI;
	}
	
	/**
	 * Rechnet Graßmaß in Bogenmaß um.
	 * @param degrees Gradmaß
	 * @return Bogenmaß
	 */
	public static double degToRad(double degrees) {
		return degrees * Math.PI / 180;
	}
	
	
	/**
	 * Prüft ob zwei Rechtecke sich überlappen.
	 * @param r1UL Obere linke Ecke erstes Rechteck
	 * @param r1LR Untere rechte Ecke erstes Rechteck
	 * @param r2UL Obere linke Ecke zweites Rechteck
	 * @param r2LR Untere rechte Ecke zweites Rechteck
	 * @return
	 */
	public static boolean checkRectOverlap(Vector2D r1UL, Vector2D r1LR, Vector2D r2UL, Vector2D r2LR) {
		// Prüfe ob ein rechteck links vom anderen liegt
	    if (r1LR.getX() < r2UL.getX() || r2LR.getX() < r1UL.getX())
	        return false;
	 
	    // Prüfe ob ein Rechteck unter dem anderen liegt
	    if (r1UL.getY() < r2LR.getY() || r2UL.getY() < r1LR.getY()) 
	        return false;
	 
	    return true;
	}
	
	/**
	 * Liefert den Umfang eines Kreises.
	 * @param radius Durchmesser des Kreises
	 * @return Umfang
	 */
	public static double circlePerimeter(double radius) {
		return 2 * Math.PI * radius;
	}
}
