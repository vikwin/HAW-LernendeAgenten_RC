package environment;
import java.util.HashMap;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import utils.Utils;


public class EnvironmentBuilder {
	
	private static final boolean DEBUG = true;
	
	private HashMap<String, Enemy> enemies = new HashMap<String, Enemy>();
	private AdvancedRobot selfBot;
	
	public EnvironmentBuilder(AdvancedRobot bot) {
		selfBot = bot;
	}
	
	public void computeScannedRobotEvent(ScannedRobotEvent event) {
		if (!enemies.containsKey(event.getName())) {
			newEnemyHook(event);
		} 
		
		Enemy enemy = enemies.get(event.getName()); 
		enemy.updateAllAttributes(event);
		
		if (DEBUG) {
		System.out.printf("Eigene Koordinaten: %s\n", Utils.getBotCoordinates(selfBot).toString());
		System.out.printf("Eigene Richtung: %s\n", Double.toString(selfBot.getHeading()));
		System.out.printf("Gegner Koordinaten: %s\n", enemy.getPosition().toString());
		}
	}
	
	private void newEnemyHook(ScannedRobotEvent event){
		enemies.put(event.getName(), new Enemy(event.getName(), selfBot));
		}
	
	

}
