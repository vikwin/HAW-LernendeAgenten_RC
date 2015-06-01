package environment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import robocode.AdvancedRobot;
import robot.SimpleAttack;
import utils.Utils;
import utils.Vector2D;

/**
 * Konkrete Klasse zur Abbildung der Umwelt für den AttackAgent. Hierbei wird
 * die Welt in einer Ringstruktur abgebildet, die den Abstand zum Gegner
 * darstellt. Für den Gegner ist außerdem bekannt, in welche der acht
 * Himmelsrichtungen er sich bewegt.
 * 
 * Bei z. B. 10 Ringen würde sich somit folgende Anzahl möglicher Zustände
 * ergeben:
 * 
 * 10 * 8 = 80 Zustände
 * 
 * @author Viktor Winkelmann
 *
 */
public class SimpleAttackEnvironment implements Environment {

	private final int ringCount, enemyCount, robotSize;
	private final double ringThickness, botPadding, battleFieldDiagonal;

	private Vector2D selfPosition;

	private boolean[] rings;

	private AdvancedRobot selfBot;
	private Collection<Enemy> enemies;
	private HashMap<Vector2D, Direction> enemyVectors; // Ortsvektor und
														// Blickrichtung
														// der Gegner
														// (Ortsvektor nur für
														// Visualisierung)

	/**
	 * Standardkonstruktor für einen Gegner.
	 * 
	 * @param ringCount
	 *            Anzahl der Ringe, in die das Spielfeld in der Diagonalen
	 *            eingeteilt wird
	 * @param robotSize
	 *            Größe des Robots selbst
	 * @param battleFieldWidth
	 *            Spielfeldbreite
	 * @param battleFieldHeight
	 *            Spielfeldhöhe
	 */
	public SimpleAttackEnvironment(int ringCount, int robotSize,
			int battleFieldWidth, int battleFieldHeight) {
		this(ringCount, robotSize, battleFieldWidth, battleFieldHeight, 1);
	}

	// Zweiter Konstruktor mit Gegneranzahl zunächst private, kann später Public
	// gemacht werden
	// um gegen mehrere Gegner antreten zu können.
	/**
	 * Konstruktor für variable Anzahl von Gegnern.
	 * 
	 * @param ringCount
	 *            Anzahl der Ringe, in die das Spielfeld in der Diagonalen
	 *            eingeteilt wird
	 * @param robotSize
	 *            Größe des Robots selbst
	 * @param battleFieldWidth
	 *            Spielfeldbreite
	 * @param battleFieldHeight
	 *            Spielfeldhöhe
	 * @param enemyCount
	 *            Anzahl von Gegnern
	 */
	public SimpleAttackEnvironment(int ringCount, int robotSize,
			int battleFieldWidth, int battleFieldHeight, int enemyCount) {
		this.ringCount = ringCount;
		this.enemyCount = enemyCount;
		this.robotSize = robotSize;

		this.selfPosition = new Vector2D();

		this.botPadding = Math.sqrt(2.0) * robotSize / 2;

		battleFieldDiagonal = Math.sqrt(battleFieldHeight * battleFieldHeight
				+ battleFieldWidth * battleFieldWidth);
		this.ringThickness = (battleFieldDiagonal - botPadding / 2) / ringCount;

		this.rings = new boolean[ringCount];
		this.enemies = new ArrayList<Enemy>();
		this.enemyVectors = new HashMap<>();

		clearEnv();
	}

	private void clearEnv() {
		enemyVectors.clear();

		for (int i = 0; i < rings.length; i++)
			rings[i] = false;
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		clearEnv();
		this.selfBot = selfBot;
		this.enemies = enemies;

		selfPosition = Utils.getBotCoordinates(selfBot);

		int ringNr;
		for (Enemy enemy : enemies) {
			ringNr = (int) ((enemy.getDistance() - botPadding) / ringThickness);

			if (ringNr > ringCount - 1)
				ringNr = ringCount - 1;

			rings[ringNr] = true;

			enemyVectors.put(enemy.getPosition(),
					Direction.byHeading(enemy.getHeading()));
		}

	}

	@Override
	public void doPaint(Graphics2D g) {
		// Zeichne die Ringstruktur in gelb ein
		g.setColor(new Color(0xdf, 0xff, 0x00, 0x80));
		double circleSize;
		for (int i = 0; i <= ringCount; i++) {
			circleSize = robotSize + i * ringThickness * 2;
			g.drawArc((int) (selfPosition.getX() - circleSize / 2),
					(int) (selfPosition.getY() - circleSize / 2),
					(int) circleSize, (int) circleSize, 0, 360);
		}

		// Zeichne die Ringzwischenräume, in denen sich ein Gegner befindet, in
		// rot ein
		g.setColor(new Color(0xff, 0x00, 0x00, 0x80));
		g.setStroke(new BasicStroke((float) ringThickness,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (int i = 0; i < ringCount; i++) {
			circleSize = robotSize + ringThickness + i * ringThickness * 2;
			if (rings[i])
				g.drawArc((int) (selfPosition.getX() - circleSize / 2),
						(int) (selfPosition.getY() - circleSize / 2),
						(int) circleSize, (int) circleSize, 0, 360);
		}

		// Zeichne die Richtung der Gegner in blau ein
		g.setColor(new Color(0x00, 0x00, 0xff, 0x80));
		GeneralPath triangle = new GeneralPath();
		triangle.moveTo(-robotSize / 4, 0f);
		triangle.lineTo(0f, robotSize);
		triangle.lineTo(robotSize / 4, 0f);
		triangle.closePath();

		for (Vector2D enemy : enemyVectors.keySet()) {
			AffineTransform at = new AffineTransform();
			at.setToTranslation(enemy.getX(), enemy.getY());
			at.rotate(-Utils.degToRad(enemyVectors.get(enemy).getHeading()));

			triangle.transform(at);
			g.fill(triangle);

			at.rotate(-Utils
					.degToRad(-2 * enemyVectors.get(enemy).getHeading()));
			triangle.transform(at);
		}

		// Zeichne die möglichen Attackziele in grün ein
		g.setColor(new Color(0x00, 0xff, 0x00, 0x80));
		Vector2D target;
		int pointSize = 4;
		for (Enemy enemy : enemies) {
			for (double i = -SimpleAttack.MAX_DEVIATION; i <= SimpleAttack.MAX_DEVIATION; i += SimpleAttack.RESOLUTION) {
				target = EnvironmentBuilder.addOffsetToEnemyPosition(enemy,
						selfBot, i);
				g.fillArc((int) (target.getX() - pointSize / 2),
						(int) (target.getY() - pointSize / 2), pointSize,
						pointSize, 0, 360);
			}

		}

	}

	/**
	 * Diese Methode geht davon aus, dass wir nur einen einzigen Gegner haben!
	 * Sollte es notwendig sein mehrere Gegner zu berücksichtigen MUSS diese
	 * Methode für ein korrektes Verhalten angepasst werden.
	 */
	@Override
	public int getId() {
		if (enemyVectors.size() == 0)
			return 0;

		int ringState = 0;
		for (int i = 0; i < ringCount; i++)
			if (rings[i])
				ringState = i;

		Direction direction = Direction.NORTH;
		for (Direction d : enemyVectors.values())
			direction = d;

		return 1 + ringState * Direction.values().length + direction.ordinal();
	}

	@Override
	public int getStateCount() {
		return (int) Math
				.pow(ringCount * Direction.values().length, enemyCount) + 1;
	}
}
