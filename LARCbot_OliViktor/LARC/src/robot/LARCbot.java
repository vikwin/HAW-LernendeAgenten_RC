package robot;

import java.awt.Graphics2D;
import java.util.Arrays;

import robocode.CustomEvent;
import robocode.RadarTurnCompleteCondition;
import robocode.ScannedRobotEvent;
import robot.actionsystem.Action;
import robot.actionsystem.ActorRobot;
import robot.actionsystem.MoveAction;
import robot.actionsystem.SerialAction;
import robot.actionsystem.TurnAction;
import utils.Utils;
import utils.Vector2D;
import agents.MoveAgent;
import environment.EnvironmentBuilder;

public class LARCbot extends ActorRobot {

	private enum RadarState {
		STOPPED, SCANNING, SCANFINISHED;
	}

	// private boolean waitBeforeRescan = false;
	private EnvironmentBuilder envBuilder;

	private MoveAgent moveAgent;

	private RadarState radarState;

	private Action lastMoveAgentAction = null, lastAttackAgentAction = null;

	public LARCbot() {
		this.moveAgent = new MoveAgent();
		this.radarState = RadarState.STOPPED;
	}

	@Override
	public void run() {
		super.run();
		createEvents();
		// Der Environmentbuilder muss aus der run Methode heraus initialisiert
		// werden, weil sonst der Zugriff auf die Eigenschaften des Spielfelds
		// verwehrt wird
		this.envBuilder = new EnvironmentBuilder(this);

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);

		doScan();

		while (true) {
			act();
			updateActions();
			execute();
		}

	}

	private void createEvents() {

		// Radar Conditions
		RadarTurnCompleteCondition radCond = new RadarTurnCompleteCondition(
				this);
		radCond.setName("radarturn_completed");
		this.addCustomEvent(radCond);

		System.out.println("CreateEvents in LARCbot");

	}

	@Override
	public void onCustomEvent(CustomEvent event) {
		// super.onCustomEvent(event);

		String name = event.getCondition().getName();

		switch (name) {
		case "radarturn_completed":
			radarState = RadarState.SCANFINISHED;
			break;
		}

	}

	private void act() {
		// Radar updaten
		switch (radarState) {
		case SCANFINISHED:
			envBuilder.create();
			moveAgent.addReward(envBuilder.getReward());
			// TODO Attackagent
			doScan();
			break;
		case STOPPED:
			doScan();
			break;
		default:
			break;
		}

		// MoveAgent updaten
		if (lastMoveAgentAction == null || lastMoveAgentAction.hasFinished()) {
			lastMoveAgentAction = getSerialActionByMovement(moveAgent
					.getNextAction(envBuilder.getMoveEnvId()));
			this.addAction(lastMoveAgentAction);
			updateActions();
		}

		// TODO Attackagent updaten
	}

	private SerialAction getSerialActionByMovement(Movement movement) {
		TurnAction turn = null;
		MoveAction move = null;

		if (movement == Movement.NOTHING)
			return new SerialAction(Arrays.asList(new Action[] {}));

		Vector2D destination = getPosition().add(movement.getMoveVector());

		double rotationAngle = destination.subtract(getPosition())
				.getNormalHeading();

		double distance = getPosition().distanceTo(destination);

		if (rotationAngle >= -90 && rotationAngle <= 90) {
			// Nach rechts oder links drehen und vorwärts fahren
			turn = new TurnAction(rotationAngle);
			move = new MoveAction(distance);

		} else if (rotationAngle >= 0 && rotationAngle > 90) {
			// Nach links drehen und rückwärts fahren
			turn = new TurnAction(rotationAngle - 180);
			move = new MoveAction(-distance);

		} else if (rotationAngle < 0 && rotationAngle < -90) {
			// Nach rechts drehen und rückwärts fahren
			turn = new TurnAction(180 + rotationAngle);
			move = new MoveAction(distance);
		}

		return new SerialAction(Arrays.asList(new Action[] { turn, move }));
	}

	/**
	 * Liefert einen Ortsvektor des Bots.
	 * 
	 * @return Ortsvektor
	 */
	public Vector2D getPosition() {
		return Utils.getBotCoordinates(this);
	}

	private void doScan() {
		setTurnRadarRight(360);
		radarState = RadarState.SCANNING;
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		envBuilder.computeScannedRobotEvent(event);
	}

	@Override
	public void onPaint(Graphics2D g) {
		envBuilder.doPaint(g);

	}

	@Deprecated
	private boolean isMoving() {
		return getTurnRemaining() != 0 || getDistanceRemaining() != 0;
	}

	@Deprecated
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

}
