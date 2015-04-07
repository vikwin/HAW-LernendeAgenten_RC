package robot;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import environment.EnvironmentBuilder;

public class LARCbot extends AdvancedRobot{
	private boolean scanFinished = false;
	private EnvironmentBuilder envBuilder;
	
	
	public LARCbot() {
		this.envBuilder = new EnvironmentBuilder(this);
	}
	
	@Override
	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		
		while (true) {
			doScan();
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

	private void doScan() {
		// überspringe Aufruf, falls letzter Scan noch läuft oder noch nicht verarbeitet wurde
		if (getRadarTurnRemaining() > 0 || scanFinished)
			return;
		
		setTurnRadarRight(360);
	}
	
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		System.out.printf("Gegner %s in Richtung %s, Abstand %s\n", event.getName(), Double.toString(event.getBearing()), Double.toString(event.getHeading()));
		envBuilder.computeScannedRobotEvent(event);
	}
	
	
	
	
	
}
