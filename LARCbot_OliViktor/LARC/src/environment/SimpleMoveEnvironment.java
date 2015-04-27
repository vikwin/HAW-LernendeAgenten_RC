package environment;

import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;

/**
 * Konkrete Klasse zur Abbildung der Umwelt für den MoveAgent. Hierbei wird die
 * Welt in einer Ringstruktur abgebildet, die den Abstand zum Gegner darstellt.
 * Außerdem gibt es für jede der 4 Wände ein Flag, dass zeigt ob sich der Bot
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

	private int ringCount, robotSize;
	private double ringThickness;
	
	private boolean[] nearWalls;

	public SimpleMoveEnvironment(int ringCount, int robotSize, int battleFieldWidth,
			int battleFieldHeight) {
		this.ringCount = ringCount;

		double battleFieldDiagonal = Math.sqrt(battleFieldHeight
				* battleFieldHeight + battleFieldWidth * battleFieldWidth);
		double botDiagonal = Math.sqrt(2.0) * robotSize / 2;
		
		ringThickness = (battleFieldDiagonal - botDiagonal) / ringCount;
		
		nearWalls = new boolean[4];

	}

	public SimpleMoveEnvironment(int ringCount, int robotSize, int battleFieldWidth,
			int battleFieldHeight, int enemyCount) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPaint(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStateCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
