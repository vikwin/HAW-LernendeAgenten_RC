package robot;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import utils.Utils;
import utils.Vector2D;
import agents.MoveAgent;
import environment.EnvironmentBuilder;

public class LARCbot extends AdvancedRobot {
	private boolean waitBeforeRescan = false;
	private EnvironmentBuilder envBuilder;
	
	private MoveAgent moveAgent;

	public LARCbot() {
		this.moveAgent = new MoveAgent();
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
			act();
			
			execute();	
		}

	}

	private void act() {
		// überspringe Aufruf, falls Scan noch läuft
		if (getRadarTurnRemaining() > 0)
			return;
		
		envBuilder.create();
		
		//computeAction(moveAgent.getNextAction(envBuilder.getMoveEnvId()));
		
		waitBeforeRescan = false;
	}
	
	private void computeAction(Action action) {
		
		
	}
	
	private void moveTo(Vector2D destination) {
		double rotationAngle = getPosition().angleTo(destination);
		double distance = getPosition().distanceTo(destination);
		
		if (rotationAngle >= 0 && rotationAngle <= 90) { 
			// Nach rechts drehen und vorwärts fahren
			setTurnRight(rotationAngle);
			setAhead(distance);
			
		} else if (rotationAngle >= 0 && rotationAngle > 90) {
			// Nach links drehen und rückwärts fahren
			setTurnLeft(rotationAngle - 90);
			setBack(distance);
			
		} else if (rotationAngle < 0 && rotationAngle >= -90) {
			// Nach links drehen und vorwärts fahren
			setTurnLeft(-rotationAngle);
			setAhead(distance);
			
		} else if (rotationAngle < 0 && rotationAngle < -90) {
			// Nach rechts drehen und rückwärts fahren
			setTurnRight(rotationAngle);
			setAhead(distance);
		}
		
	}

	private void doScan() {
		// überspringe Aufruf, falls letzter Scan noch läuft oder noch nicht
		// verarbeitet wurde
		if (getRadarTurnRemaining() > 0 || waitBeforeRescan)
			return;

		setTurnRadarRight(360);
		waitBeforeRescan = true;
	}

	/**
	 * Liefert einen Ortsvektor des Bots.
	 * 
	 * @return Ortsvektor
	 */
	public Vector2D getPosition() {
		return Utils.getBotCoordinates(this);
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


