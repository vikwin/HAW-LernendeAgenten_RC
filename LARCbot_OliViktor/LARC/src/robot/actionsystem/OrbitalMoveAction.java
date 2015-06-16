package robot.actionsystem;

import utils.Vector2D;
import environment.Enemy;

public class OrbitalMoveAction extends Action {
	private static final double DISTANCE_PADDING = 40.0; // Wird benötigt, damit die Bewegungen weicher werden  
	private double orbitDistance, enemyDistanceOffset, angleCorrectionOffset;
	private Enemy enemy;

	/**
	 * Eine Bewegung um einen angegebenen Gegner in einer Kreisbahn um ihn
	 * herum
	 * 
	 * @param enemy
	 *            Der Gegner der umkreist werden soll
	 * @param orbitDistance
	 *            Die Distanz, die in der Kreisbahn zurückgelegt werden soll.
	 *            Positive werden = Im Uhrzeigesinn
	 * @param enemyDistanceOffset
	 *            Eine Erhöhung (=positive Werte) oder Senkung (=negative Werte)
	 *            der Distanz zum Gegner
	 */
	public OrbitalMoveAction(Enemy enemy, double orbitDistance,
			double enemyDistanceOffset) {
		this.enemy = enemy;
		this.orbitDistance = orbitDistance;
		this.enemyDistanceOffset = enemyDistanceOffset;
	}

	@Override
	public void start() {
		if (started)
			return;

		// Errechne zunächst den Winkeloffset, der einberechnet werden muss um die
		// Distanz zum Gegner zu erhöhen bzw. zu senken. Dann setze Bewegungen entsprechend.
		Vector2D moveVector = (new Vector2D(Math.abs(orbitDistance), 0))
				.add(new Vector2D(0, enemyDistanceOffset));
		if (orbitDistance >= 0) {
			bot.setAhead(moveVector.length() + DISTANCE_PADDING);
			angleCorrectionOffset = moveVector
					.getNormalHeading();
		} else {
			bot.setBack(moveVector.length() + DISTANCE_PADDING);
			angleCorrectionOffset = moveVector.getNormalHeading() * -1;
		}
		
		update();		
		started = true;
	}


	
	@Override
	public void stop() {
		if (started) {
			bot.setBack(0);
			bot.setTurnRight(0);
		}
	}

	@Override
	public void update() {
		if (!started )
			return;

		if (Math.abs(bot.getDistanceRemaining()) < DISTANCE_PADDING || enemy == null) {
			finished = true;
			return;
		}
		
		if (enemy.getBearing() >= 0)
			bot.setTurnRight(enemy.getBearing() - 90 + angleCorrectionOffset);
		else
			bot.setTurnRight(enemy.getBearing() - 270 + angleCorrectionOffset);

	}

	@Override
	public String toString() {
		return String.format(
				"OrbitalMovement um %f Meter mit Distanz Offset zum Gegner %f",
				orbitDistance, enemyDistanceOffset);
	}

}
