package environment;
import java.util.HashMap;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import utils.Utils;


public class EnvironmentBuilder {
	
	private HashMap<String, Enemy> enemies = new HashMap<String, Enemy>();
	private AdvancedRobot bot;
	
	public EnvironmentBuilder(AdvancedRobot bot) {
		this.bot = bot;
	}
	
	public void computeScannedRobotEvent(ScannedRobotEvent event) {
		if (!enemies.containsKey(event.getName())) {
			newEnemyHook(event);
		}
		
		

	}
	
	private void newEnemyHook(ScannedRobotEvent event){
//		event.
//		Enemy e = new Enemy(event.getName(), );
//		
//		
//		enemies.put(e.getName(), e);
		System.out.printf("Eigene Koordinaten: %s\n", Utils.getBotCoordinates(bot).toString());
		System.out.printf("Eigene Richtung: %s\n", Double.toString(bot.getHeading()));
		System.out.printf("Gegner Koordinaten: %s\n", Utils.relToAbsPosition(Utils.getBotCoordinates(bot), Utils.normalizeHeading(bot.getHeading()), event.getBearing(), event.getDistance()).toString());
	}
	
	

}
