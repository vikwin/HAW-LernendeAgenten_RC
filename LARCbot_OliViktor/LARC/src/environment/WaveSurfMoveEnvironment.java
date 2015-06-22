package environment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;
import utils.Utils;
import utils.Vector2D;

/**
 * Konkrete Klasse zur Abbildung der Umwelt für den MoveAgent. Es wird davon
 * ausgegangen, dass ausschließlich orbitale Bewegungen um den Gegner genutzt
 * werden. Hierbei wird die Welt in einer Ringstruktur abgebildet, die den
 * Abstand zum Gegner darstellt. WaveSurfing wird verwendet, um zu Erahnen wo
 * sich die Kugeln des Gegners gerade befinden können. Hieraus wird die Gefahr
 * für eine Drehbewegung um den Gegner jeweils im und engegengesetzt zum
 * Uhrzeigesinn abgeschätzt. Die Gefahren werden dabei in 3 Stufen eingeteilt.
 * Außerdem wird für beide Bewegungsrichtungen angezeigt, ob sich eine Wand in
 * der Nähe befindet.
 * 
 * Bei z. B. 10 Ringen würde sich somit folgende Anzahl möglicher Zustände
 * ergeben:
 * 
 * 10 * 3 * 3 * 2 * 2 = 360 Zustände
 * 
 * @author Viktor Winkelmann
 *
 */
public class WaveSurfMoveEnvironment implements Environment {

	private enum Danger {
		NONE, LOW, HIGH;

		public static Danger getByValue(double danger) {
			if (danger == 0.0)
				return Danger.NONE;
			else if (danger < 0.5)
				return Danger.LOW;
			else
				return Danger.HIGH;

		}
	}

	private static final double WALL_DISTANCE = 100; // Abstand, unter dem eine
														// Wand als nah
														// eingestuft
														// wird

	private final int ringCount, enemyCount, robotSize, battleFieldHeight,
			battleFieldWidth;
	private final double ringThickness, botPadding;

	private Vector2D selfPosition;

	private boolean[] rings;
	private boolean wallToLeft, wallToRight;

	private Danger dangerLeft, dangerRight;

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
	public WaveSurfMoveEnvironment(int ringCount, int robotSize,
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
	private WaveSurfMoveEnvironment(int ringCount, int robotSize,
			int battleFieldWidth, int battleFieldHeight, int enemyCount) {
		this.ringCount = ringCount;
		this.enemyCount = enemyCount;
		this.robotSize = robotSize;
		this.battleFieldHeight = battleFieldHeight;
		this.battleFieldWidth = battleFieldWidth;

		this.selfPosition = new Vector2D();

		this.botPadding = Math.sqrt(2.0) * robotSize / 2;

		double battleFieldDiagonal = Math.sqrt(battleFieldHeight
				* battleFieldHeight + battleFieldWidth * battleFieldWidth);
		this.ringThickness = (battleFieldDiagonal - botPadding / 2) / ringCount;

		this.rings = new boolean[ringCount];

		clearEnv();
	}

	private void clearEnv() {
		wallToLeft = false;
		wallToRight = false;

		dangerLeft = Danger.NONE;
		dangerRight = Danger.NONE;

		for (int i = 0; i < rings.length; i++)
			rings[i] = false;
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		clearEnv();

		selfPosition = Utils.getBotCoordinates(selfBot);

		int ringNr = 0;

		Enemy anEnemy = null;

		for (Enemy enemy : enemies) {
			ringNr = (int) ((enemy.getDistance() - botPadding) / ringThickness);

			if (ringNr > ringCount - 1)
				ringNr = ringCount - 1;

			rings[ringNr] = true;
			anEnemy = enemy;
		}

		Vector2D wallDistance = new Vector2D(WALL_DISTANCE, 0);
		// Linke Seite nahe Wand?
		wallToLeft = !selfPosition.add(
				wallDistance.rotate(Utils.normalizeHeading(selfBot
						.getHeading()))).inRectangle(0, 0, battleFieldWidth,
				battleFieldHeight);

		// Rechte Seite nahe Wand?
		wallToRight = !selfPosition.add(
				wallDistance.rotate(-180
						+ Utils.normalizeHeading(selfBot.getHeading())))
				.inRectangle(0, 0, battleFieldWidth, battleFieldHeight);

		if (anEnemy == null)
			return;

		dangerLeft = Danger.getByValue(anEnemy.checkNormalizedDanger(
				anEnemy.getClosestSurfableWave(), 1));
		dangerRight = Danger.getByValue(anEnemy.checkNormalizedDanger(
				anEnemy.getClosestSurfableWave(), -1));
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

		// Zeichne nahe Wände in grün ein
		g.setColor(new Color(0x00, 0xff, 0x00, 0xff));
		if (wallToLeft)
			g.drawChars("WALL TO LEFT".toCharArray(), 0, 12, 50, 30);

		if (wallToRight)
			g.drawChars("WALL TO RIGHT".toCharArray(), 0, 13, 200, 30);

		// Zeichne Gefahrenstufen in grün ein
		g.setColor(new Color(0x00, 0xff, 0x00, 0xff));
		String leftDanger = "DANGER TO LEFT: " + dangerLeft.toString();
		g.drawChars(leftDanger.toCharArray(), 0, leftDanger.length(), 50, 50);
		String rightDanger = "DANGER TO RIGHT: " + dangerRight.toString();
		g.drawChars(rightDanger.toCharArray(), 0, rightDanger.length(), 200, 50);

	}

	/**
	 * Diese Methode geht davon aus, dass wir nur einen einzigen Gegner haben!
	 * Sollte es notwendig sein mehrere Gegner zu berücksichtigen MUSS diese
	 * Methode für ein korrektes Verhalten angepasst werden.
	 */
	@Override
	public int getId() {
		int ringState = 0;

		for (int i = 1; i < ringCount; i++)
			if (rings[i - 1])
				ringState = i;

		int wallState = 0;
		if (wallToLeft)
			wallState += 1;
		if (wallToRight)
			wallState += 2;

		int dangerState = 0;
		dangerState += dangerLeft.ordinal();
		dangerState += dangerRight.ordinal() * Danger.values().length;

		return ringState * 36 + dangerState * 4 + wallState;
	}

	@Override
	public int getStateCount() {
		// + 1, weil es sein kann dass kein Gegner da ist
		return (int) Math.pow(36 * (ringCount + 1), enemyCount);
	}

}
