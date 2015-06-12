package utils;

import robocode.AdvancedRobot;
import environment.EnemyWave;

/**
 * Utility Klasse speziell für Umsetzung von Wave Surfing.
 * 
 * @author Viktor Winkelmann
 *
 */
public abstract class WaveSurf {
	public static final int BINS = 47;

	/**
	 * Berechnet die ungefähre Fluggeschwindigkeit einer Kugel anhand des
	 * beobachteten Energieverlusts beim Abfeuern.
	 * 
	 * @param energyImpact
	 *            Energieverlust
	 * @return Tempo
	 */
	public static double bulletVelocity(double energyImpact) {
		return 20.0 - 3.0 * energyImpact;
	}

	public static double maxEscapeAngle(double velocity) {
		return Math.asin(8.0 / velocity);
	}

	public static double limit(double min, double value, double max) {
		return Math.max(min, Math.min(value, max));
	}

	public static Vector2D predictPosition(AdvancedRobot robot,
			EnemyWave surfWave, int direction) {
		Vector2D predictedPosition = new Vector2D(robot.getX(), robot.getY());
		double predictedVelocity = robot.getVelocity();
		double predictedHeading = robot.getHeadingRadians();
		double maxTurning, moveAngle, moveDir;

		int counter = 0; // number of ticks in the future
		boolean intercepted = false;

		do { // the rest of these code comments are rozu's
			moveAngle = wallSmoothing(predictedPosition,
					surfWave.fireLocation.angleTo(predictedPosition)
							+ (direction * (Math.PI / 2)), direction, 160)
					- predictedHeading;
			moveDir = 1;

			if (Math.cos(moveAngle) < 0) {
				moveAngle += Math.PI;
				moveDir = -1;
			}

			moveAngle = robocode.util.Utils.normalRelativeAngle(moveAngle);

			// maxTurning is built in like this, you can't turn more then this
			// in one tick
			maxTurning = Math.PI / 720d
					* (40d - 3d * Math.abs(predictedVelocity));
			predictedHeading = robocode.util.Utils
					.normalRelativeAngle(predictedHeading
							+ limit(-maxTurning, moveAngle, maxTurning));

			// this one is nice ;). if predictedVelocity and moveDir have
			// different signs you want to brake down
			// otherwise you want to accelerate (look at the factor "2")
			predictedVelocity += (predictedVelocity * moveDir < 0 ? 2 * moveDir
					: moveDir);
			predictedVelocity = limit(-8, predictedVelocity, 8);

			// calculate the new predicted position
			predictedPosition = project(predictedPosition, predictedHeading,
					predictedVelocity);

			counter++;

			if (predictedPosition.distanceTo(surfWave.fireLocation) < surfWave.distanceTraveled
					+ (counter * surfWave.bulletVelocity)
					+ surfWave.bulletVelocity) {
				intercepted = true;
			}
		} while (!intercepted && counter < 500);

		return predictedPosition;
	}

	public static double wallSmoothing(Vector2D botLocation, double angle,
			int orientation, double stick) {
		while (!(project(botLocation, angle, stick).inRectangle(18, 18, 764, 564))) {
			angle += orientation * 0.05;
		}
		return angle;
	}

	public static Vector2D project(Vector2D sourceLocation, double angle,
			double length) {
		return new Vector2D(sourceLocation.getX() + Math.sin(angle) * length,
				sourceLocation.getY() + Math.cos(angle) * length);
	}
	
	/**
	 * Berechnet zunächst, in welche Richtung der Gegner geschossen hat um uns
	 * zu treffen. Anschließend wird der Richtung ein GuessFactor Index
	 * zugeordnet.
	 * 
	 * @param ew
	 *            Die EnemyWave, von der wir getroffen wurden
	 * @param targetLocation
	 *            Der Ort, an dem wir getroffen wurden
	 * @return Der GuessFactor Index
	 */
	public static int guessfactorIndex(EnemyWave ew, Vector2D targetLocation) {
		double offsetAngle = (ew.fireLocation.angleTo(targetLocation) - ew.directAngle);
		double factor = robocode.util.Utils.normalRelativeAngle(offsetAngle)
				/ WaveSurf.maxEscapeAngle(ew.bulletVelocity) * ew.direction;

		return (int) WaveSurf.limit(0, (factor * ((BINS - 1) / 2))
				+ ((BINS - 1) / 2), BINS - 1);
	}
	
	public static void setBackAsFront(AdvancedRobot robot, double goAngle) {
        double angle =
            robocode.util.Utils.normalRelativeAngle(goAngle - robot.getHeadingRadians());
        if (Math.abs(angle) > (Math.PI/2)) {
            if (angle < 0) {
                robot.setTurnRightRadians(Math.PI + angle);
            } else {
                robot.setTurnLeftRadians(Math.PI - angle);
            }
            robot.setBack(100);
        } else {
            if (angle < 0) {
                robot.setTurnLeftRadians(-1*angle);
           } else {
                robot.setTurnRightRadians(angle);
           }
            robot.setAhead(100);
        }
	}
}
