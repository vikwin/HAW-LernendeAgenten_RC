package robot;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.GunTurnCompleteCondition;
import robocode.MoveCompleteCondition;
import robocode.RadarTurnCompleteCondition;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;
import utils.Utils;
import utils.Vector2D;
import agents.MoveAgent;
import environment.EnvironmentBuilder;

public class LARCbot extends AdvancedRobot {

	private enum MoveState {
		STOPPED, TURNING, MOVING;
		
		public static boolean tickWaiting = false;
	}

	private enum AttackState {
		STOPPED, TURNING, FIRING;
		
		public static boolean tickWaiting = false;
	}

	private enum RadarState {
		STOPPED, SCANNING, SCANFINISHED;
	}

	// private boolean waitBeforeRescan = false;
	private EnvironmentBuilder envBuilder;

	private MoveAgent moveAgent;

	private MoveState moveState;
	private AttackState attackState;
	private RadarState radarState;

	public LARCbot() {
		this.moveAgent = new MoveAgent();
		this.moveState = MoveState.STOPPED;
		this.attackState = AttackState.STOPPED;
		this.radarState = RadarState.STOPPED;
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

		createEvents();

		doScan();

		while (true) {

			// setTurnGunRight(1);
			act();

			execute();
		}

	}
	
	private void act() {
		// Radar updaten
		switch (radarState) {
			case SCANFINISHED:
				envBuilder.create();
				moveAgent.addReward(envBuilder.getReward());
			case STOPPED:
				doScan();
				radarState = RadarState.SCANNING;
				break;
		}
		
		// MoveAgent updaten
		switch (moveState) {
			case STOPPED:
				if (moveState.tickWaiting) {
					computeAction(moveAgent.getNextAction(envBuilder.getMoveEnvId()));
					moveState.tickWaiting = false;
					moveState = MoveState.MOVING;
				}
					
		}
		
		
		if (!isMoving())
			// moveTo(new Vector2D(50,50));
			computeAction(moveAgent.getNextAction(envBuilder.getMoveEnvId()));
		// computeAction(attackAgent.getNextAction(envBuilder.getAttackEnvId()));
	}


	private void createEvents() {

		// Allgemeine Conditions
		RadarTurnCompleteCondition radCond = new RadarTurnCompleteCondition(
				this);
		radCond.setName("radarturn_completed");
		this.addCustomEvent(radCond);

		// Conditions für den MoveAgent
		MoveCompleteCondition movCond = new MoveCompleteCondition(this);
		movCond.setName("ma_botmove_completed");
		this.addCustomEvent(movCond);

		TurnCompleteCondition turnCond = new TurnCompleteCondition(this);
		turnCond.setName("ma_botturn_completed");
		this.addCustomEvent(turnCond);

		// Conditions für den AttackAgent
		GunTurnCompleteCondition gunTurnCond = new GunTurnCompleteCondition(
				this);
		gunTurnCond.setName("aa_gunturn_completed");
		this.addCustomEvent(gunTurnCond);
		
		this.addCustomEvent(new Condition("aa_gunfire_completed") {
			
			@Override
			public boolean test() {
				return attackState == AttackState.FIRING && getGunHeat() == 0;
			}
		});

	}

	@Override
	public void onCustomEvent(CustomEvent event) {
		String name = event.getCondition().getName();

		switch (name) {
		case "ma_botturn_completed":
			break;
		case "ma_botmove_completed":
			break;
		case "aa_gunturn_completed":
			break;
		case "aa_gunfire_completed":
			break;
		case "radarturn_completed":
			radarState = RadarState.SCANFINISHED;
			break;
		default:
			System.out.println("Unbekannte CostomEvent Condition: " + name);
		}

	}

	
	private boolean isMoving() {
		return getTurnRemaining() != 0 || getDistanceRemaining() != 0;
	}

	private void computeAction(MoveAction action) {
		moveTo(getPosition().add(action.getMoveVector()));
	}

	private void computeAction(AttackAction action) {
		// TODO

	}

	private void moveTo(Vector2D destination) {
		double rotationAngle = destination.subtract(getPosition())
				.getNormalHeading();
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
		if (getRadarTurnRemaining() > 0)
			return;

		setTurnRadarRight(360);
		radarState = RadarState.SCANNING;
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
		envBuilder.computeScannedRobotEvent(event);
	}

	@Override
	public void onPaint(Graphics2D g) {
		envBuilder.doPaint(g);

	}
}
