package environment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;
import utils.Utils;
import utils.Vector2D;

/**
 * Konkrete Klasse zur Abbildung der Umwelt für den MoveAgent. Hierbei wird die
 * Welt in einer Ringstruktur abgebildet, die den Abstand zum Gegner darstellt.
 * Außerdem gibt es für jede der 4 Wände ein Flag, das zeigt ob sich der Bot
 * nahe an dieser befindet.
 * 
 * Bei z. B. 10 Ringen würde sich somit folgende Anzahl möglicher Zustände
 * ergeben:
 * 
 * 10 * 2 * 2 * 2 * 2 = 160 Zustände
 * 
 * @author Viktor Winkelmann
 *
 */
public class SimpleMoveEnvironment implements Environment {

	private static final double WALL_DISTANCE = 40; // Abstand, unter dem eine
													// Wand als nah eingestuft
													// wird

	private final int ringCount, enemyCount, robotSize, battleFieldHeight,
			battleFieldWidth;
	private final double ringThickness, botPadding;

	private Vector2D selfPosition;

	private boolean[] nearWalls, rings;

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
	public SimpleMoveEnvironment(int ringCount, int robotSize,
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
	private SimpleMoveEnvironment(int ringCount, int robotSize,
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

		this.nearWalls = new boolean[4];
		this.rings = new boolean[ringCount];

		clearEnv();
	}

	private void clearEnv() {
		for (int i = 0; i < nearWalls.length; i++)
			nearWalls[i] = false;

		for (int i = 0; i < rings.length; i++)
			rings[i] = false;
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		clearEnv();

		selfPosition = Utils.getBotCoordinates(selfBot);

		int ringNr;
		for (Enemy enemy : enemies) {
			ringNr = (int) ((enemy.getDistance() - botPadding) / ringThickness);

			if (ringNr > ringCount - 1)
				ringNr = ringCount - 1;

			rings[ringNr] = true;
		}

		// Linke, Obere, Rechte, Untere Wand nahe?
		nearWalls[0] = selfBot.getX() < WALL_DISTANCE;
		nearWalls[1] = (selfBot.getBattleFieldHeight() - selfBot.getY()) < WALL_DISTANCE;
		nearWalls[2] = (selfBot.getBattleFieldWidth() - selfBot.getX()) < WALL_DISTANCE;
		nearWalls[3] = selfBot.getY() < WALL_DISTANCE;
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

		// Zeichne nahe Wände in blau ein
		g.setColor(new Color(0x00, 0x00, 0xff, 0x80));
		g.setStroke(new BasicStroke());

		if (nearWalls[0]) {
			// linke Wand
			g.fillRect(-robotSize, 0, robotSize, battleFieldHeight);
		}

		if (nearWalls[1]) {
			// obere Wand
			g.fillRect(0, battleFieldHeight, battleFieldWidth, robotSize);
		}

		if (nearWalls[2]) {
			// rechte Wand
			g.fillRect(battleFieldWidth, 0, robotSize, battleFieldHeight);

		}

		if (nearWalls[3]) {
			// untere Wand
			g.fillRect(0, -robotSize, battleFieldWidth, robotSize);
		}
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

		for (int i = 0; i < 4; i++)
			if (nearWalls[i])
				wallState += Math.pow(2, i);

		return ringState * 16 + wallState;
	}

	@Override
	public int getStateCount() {
		return (int) Math.pow(16 * (ringCount + 1), enemyCount); // + 1, weil es
																	// sein kann
																	// dass kein
																	// Gegner da
																	// ist
	}

}
