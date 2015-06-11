package environment;

import java.awt.Graphics2D;
import java.util.HashMap;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robot.LARCbot;
import utils.Config;
import utils.Utils;
import utils.Vector2D;

/**
 * Diese Klasse sammelt Informationen über Gegner und nutzt diese um daraus die
 * Umwelt für die Agenten zu konstruieren.
 * 
 * @author Viktor Winkelmann
 *
 */
public class EnvironmentBuilder {

	private static final int ROBOT_SIZE = 40; // Die Seitenlänge der Bots in
												// Pixeln
	private static final int GRID_SIZE = Config.getIntValue("Env_GridSize");// Die
																			// Seitenlänge
																			// eines
																			// einzelnen
																			// Feldes
																			// (für
																			// MoveEnvironment)

	private static final boolean DEBUG = Config.getBoolValue("Env_Debug");
	private static final boolean PAINT_ATTACK_ENV = Config
			.getBoolValue("Env_PaintAttackEnv");
	private static final boolean PAINT_MOVE_ENV = Config
			.getBoolValue("Env_PaintMoveEnv");

	private HashMap<String, Enemy> enemies = new HashMap<String, Enemy>();
	private AdvancedRobot selfBot;
	private double selfBotLastEnergy;

	private Environment moveEnv, attackEnv;

	public EnvironmentBuilder(AdvancedRobot bot, boolean useComplexMoveEnv,
			boolean useComplexAttackEnv) {
		selfBot = bot;
		selfBotLastEnergy = selfBot.getEnergy();

		if (useComplexMoveEnv)
			moveEnv = new ComplexMoveEnvironment(ROBOT_SIZE, GRID_SIZE,
					(int) selfBot.getBattleFieldWidth(),
					(int) selfBot.getBattleFieldHeight());
		else
			moveEnv = new SimpleMoveEnvironment(10, ROBOT_SIZE,
					(int) selfBot.getBattleFieldWidth(),
					(int) selfBot.getBattleFieldHeight());

		if (useComplexAttackEnv)
			attackEnv = new ComplexAttackEnvironment(ROBOT_SIZE * 2,
					ROBOT_SIZE, Utils.getBotCoordinates(bot),
					bot.getGunHeading(), (int) selfBot.getBattleFieldWidth(),
					(int) selfBot.getBattleFieldHeight());
		else
			attackEnv = new SimpleAttackEnvironment(15, ROBOT_SIZE,
					(int) selfBot.getBattleFieldWidth(),
					(int) selfBot.getBattleFieldHeight());
	}

	/**
	 * Extrahiert die gewonnenen Informationen über einen Gegner aus einem
	 * ScannedRobotEvent und speichert diese im Objekt.
	 * 
	 * @param event
	 */
	public void computeScannedRobotEvent(ScannedRobotEvent event) {
		if (!enemies.containsKey(event.getName())) {
			newEnemyHook(event);
		}

		Enemy enemy = enemies.get(event.getName());
		enemy.updateAllAttributes(event);

		if (DEBUG) {
			System.out.printf("Eigene Koordinaten: %s\n", Utils
					.getBotCoordinates(selfBot).toString());
			System.out.printf("Eigene Richtung: %s\n",
					Double.toString(selfBot.getHeading()));
			System.out.printf("Gegner Koordinaten: %s\n", enemy.getPosition()
					.toString());
		}
	}

	private void newEnemyHook(ScannedRobotEvent event) {
		enemies.put(event.getName(), new Enemy(event.getName(), selfBot));
	}

	/**
	 * Veranlässt das Einzeichnen der Agent Umwelten in abhängigkeit der
	 * Konstanten PAINT_MOVE_ENV und PAINT_ATTACK_ENV.
	 * 
	 * @param g
	 */
	public void doPaint(Graphics2D g) {
		for (Enemy e : enemies.values())
			e.doPaint(g);
		
		if (PAINT_MOVE_ENV)
			moveEnv.doPaint(g);
		if (PAINT_ATTACK_ENV)
			attackEnv.doPaint(g);
	}

	/**
	 * Erstellt bzw. aktualiert die Umweltobjekte
	 */
	public void create() {
		moveEnv.update(enemies.values(), selfBot);
		attackEnv.update(enemies.values(), selfBot);
	}

	/**
	 * Liefert eine eindeutige ID für die aktuelle MoveAgent Umwelt.
	 * 
	 * @return Die ID
	 */
	public int getMoveEnvId() {
		return moveEnv.getId();
	}

	/**
	 * Liefert eine eindeutige ID für die aktuelle AttackAgent Umwelt.
	 * 
	 * @return Die ID
	 */
	public int getAttackEnvId() {
		return attackEnv.getId();
	}

	/**
	 * Liefert die Gesamtzahl möglicher Zustände in der AttackEnvironment.
	 * 
	 * @return Anzahl
	 */
	public int getAttackEnvStateCount() {
		return attackEnv.getStateCount();
	}

	/**
	 * Liefert die Gesamtzahl möglicher Zustände in der MoveEnvironment.
	 * 
	 * @return Anzahl
	 */
	public int getMoveEnvStateCount() {
		return moveEnv.getStateCount();
	}

	/**
	 * Gibt einen beliebigen Gegner zurück. Wird null, falls kein Gegner
	 * vorhanden.
	 * 
	 * @return Gegner
	 */
	public Enemy getEnemy() {
		Enemy enemy = null;
		for (Enemy e : enemies.values())
			enemy = e;

		return enemy;
	}

	/**
	 * Liefert eine Zahl für die Belohnung der Aktionen des Bots.
	 * 
	 * @return Die Belohnung
	 */
	public double getReward() {
		double reward = selfBot.getEnergy() - selfBotLastEnergy;
		selfBotLastEnergy = selfBot.getEnergy();

		for (Enemy e : enemies.values()) {
			reward -= e.getEnergyDelta();
		}

		return reward;
	}

	/**
	 * Diese Methode berechnet die neue Position eines Gegners nach Aufrechnung eines
	 * Offsets in Blickrichtung des Gegners. 
	 * @param enemy Der Gegner
	 * @param selfBot Der eigene Bot
	 * @param offset Das aufzurechnende Offset
	 * @return Die neue Position
	 */
	public static Vector2D addOffsetToEnemyPosition(Enemy enemy,
			AdvancedRobot selfBot, double offset) {
		double battleFieldDiagonal = Math
				.sqrt(selfBot.getBattleFieldHeight()
						* selfBot.getBattleFieldHeight()
						+ selfBot.getBattleFieldWidth()
						* selfBot.getBattleFieldWidth());

		// Faktor für Einberechnung des Offsets festlegen nach Distanz /
		// Maxdistanz (= Diagonale)
		double factor = ((LARCbot)selfBot).getPosition().distanceTo(enemy.getPosition())
				/ battleFieldDiagonal;

		Vector2D tmp = new Vector2D(0, offset * factor);
		tmp = tmp.rotate(enemy.getHeading());

		tmp = enemy.getPosition().add(tmp);

		return tmp;
	}

	public void onHitByBullet(HitByBulletEvent event) {
		for (Enemy e : enemies.values())
			e.updateWavesByBulletHit(event);
		
	}
}
