package robot;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import environment.Environment;

public class LARCbot extends AdvancedRobot{
	private boolean scanFinished = false;
	private Environment env = new Environment();
	
	@Override
	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		
		while (true) {
			scanEnvironment();
			createReducedEnvironments();
			
			
			execute();
		}
			
		
	}

	private void createReducedEnvironments() {
		// überspringe Aufruf, falls fertiger Scan wartet
		if (!scanFinished)
			return;
		
		//TODO 
		
		
		scanFinished = false;
	}

	private void scanEnvironment() {
		// überspringe Aufruf, falls letzter Scan noch läuft oder noch nicht verarbeitet wurde
		if (getRadarTurnRemaining() > 0 || scanFinished)
			return;
		
		setTurnRadarRight(360);
	}
	
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		System.out.printf("Gegner %s in Richtung %s, Abstand %s\n", event.getName(), Double.toString(event.getBearing()), Double.toString(event.getHeading()));
		env.computeScannedRobotEvent(event);
	}
	
	
	
	
	
}
