package environment;

import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;

/**
 * Konkrete Klasse zur Abbildung der Umwelt für den AttackAgent. Hierbei wird die Welt 
 * in einer Ringstruktur abgebildet, die den Abstand zum Gegner darstellt. Für den Gegner
 * ist außerdem bekannt, in welche der acht Himmelsrichtungen er sich bewegt.
 * 
 * Bei z. B. 10 Ringen würde sich somit folgende Anzahl möglicher Zustände ergeben:
 * 
 * 10 * 8  = 80 Zustände 
 * 
 * @author Viktor Winkelmann
 *
 */
public class SimpleAttackEnvironment implements Environment {

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
