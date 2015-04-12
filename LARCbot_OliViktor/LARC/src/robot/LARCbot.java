package robot;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import environment.EnvironmentBuilder;

public class LARCbot extends AdvancedRobot {
	private boolean waitBeforeRescan = false;
	private EnvironmentBuilder envBuilder;

	public LARCbot() {
	}

	@Override
	public void run() {
		// Der Environmentbuilder muss aus der run Methode heraus initialisiert
		// werden, weil sonst der Zugriff auf die Eigenschaften des Spielfelds
		// verwehrt wird
		this.envBuilder = new EnvironmentBuilder(this);

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);

		while (true) {
			doScan();
			setTurnGunRight(1);
			createReducedEnvironments();
			
			execute();	
		}

	}

	private void createReducedEnvironments() {
		// überspringe Aufruf, falls Scan noch läuft
		if (getRadarTurnRemaining() > 0)
			return;
		
		envBuilder.create();
		waitBeforeRescan = false;
	}

	private void doScan() {
		// überspringe Aufruf, falls letzter Scan noch läuft oder noch nicht
		// verarbeitet wurde
		if (getRadarTurnRemaining() > 0 || waitBeforeRescan)
			return;

		setTurnRadarRight(360);
		waitBeforeRescan = true;
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
//		System.out.printf("Gegner %s in Richtung %s, Abstand %s\n",
//				event.getName(), Double.toString(event.getBearing()),
//				Double.toString(event.getHeading()));
		envBuilder.computeScannedRobotEvent(event);
	}

	@Override
	public void onPaint(Graphics2D g) {
		envBuilder.doPaint(g);
	}

}
