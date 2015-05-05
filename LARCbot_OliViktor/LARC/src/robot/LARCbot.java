package robot;

import java.awt.Graphics2D;
import java.util.Arrays;

import robocode.BattleEndedEvent;
import robocode.CustomEvent;
import robocode.RadarTurnCompleteCondition;
import robocode.ScannedRobotEvent;
import robot.actionsystem.Action;
import robot.actionsystem.ConcurrentAction;
import robot.actionsystem.FireAction;
import robot.actionsystem.GunTurnAction;
import robot.actionsystem.MoveAction;
import robot.actionsystem.NothingAction;
import robot.actionsystem.SerialAction;
import robot.actionsystem.TurnAction;
import robot.rewardsystem.RewardRobot;
import utils.Config;
import utils.Utils;
import utils.Vector2D;
import agents.AttackAgent;
import agents.MoveAgent;
import environment.EnvironmentBuilder;

public class LARCbot extends RewardRobot {
	// Gibt an, ob das einfache Belohnungssystem (per Energieäderungen) oder das
	// Event basierte System verwendet werden soll
	private static final boolean USE_SIMPLE_REWARD_SYSTEM = Config
			.getBoolValue("Robot_SimpleReward");

	// Komplexe oder Simple Umgebungen benutzen
	private static final boolean COMPLEX_ATTACK_ENV = Config
			.getBoolValue("Robot_UseExtendedAttackEnv");
	private static final boolean COMPLEX_MOVE_ENV = Config
			.getBoolValue("Robot_UseExtendedMoveEnv");

	private enum RadarState {
		STOPPED, SCANNING, SCANFINISHED;
	}

	private EnvironmentBuilder envBuilder;

	private MoveAgent moveAgent;
	private AttackAgent attackAgent;

	private RadarState radarState;

	private Action lastMoveAgentAction = null, lastAttackAgentAction = null;

	public LARCbot() {
		this.radarState = RadarState.STOPPED;
	}

	@Override
	public void run() {
		super.run();

		createEvents();
		// Der Environmentbuilder muss aus der run Methode heraus initialisiert
		// werden, weil sonst der Zugriff auf die Eigenschaften des Spielfelds
		// verwehrt wird
		envBuilder = new EnvironmentBuilder(this, COMPLEX_MOVE_ENV,
				COMPLEX_ATTACK_ENV);
		moveAgent = new MoveAgent(envBuilder.getMoveEnvStateCount(),
				COMPLEX_MOVE_ENV ? ComplexMovement.values().length
						: SimpleMovement.values().length);
		attackAgent = new AttackAgent(envBuilder.getAttackEnvStateCount(),
				COMPLEX_ATTACK_ENV ? ComplexAttack.getActionCount()
						: SimpleAttack.getActionCount());

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);

		doScan();

		try {
			while (true) {
				if (getEnergy() <= 0)
					doNothing();
				else {
					act();
					updateActions();
					execute();
				}
			}
		} catch (ThreadDeath e) {
			System.out.println("LARCBot stopped because of ThreadDeath Exception");
		}

	}

	private void createEvents() {

		// Radar Conditions
		RadarTurnCompleteCondition radCond = new RadarTurnCompleteCondition(
				this);
		radCond.setName("radarturn_completed");
		this.addCustomEvent(radCond);
	}

	@Override
	public void onCustomEvent(CustomEvent event) {
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

			double reward = 0.0;
			if (USE_SIMPLE_REWARD_SYSTEM)
				reward = envBuilder.getReward(); // Belohnung für die Agents
													// anhand der
													// Energieverändeurng
			else
				reward = getReward(); // Belohnung für die Agents anhand
										// diverser Events
			moveAgent.addReward(reward);
			attackAgent.addReward(reward);

			doScan();
			break;
		case STOPPED:
			doScan();
			break;
		default:
			break;
		}

		// MoveAgent updaten und neue Action holen
		if (lastMoveAgentAction == null || lastMoveAgentAction.hasFinished()) {
			lastMoveAgentAction = getActionByMovement(moveAgent
					.getNextAction(envBuilder.getMoveEnvId()));
			this.addAction(lastMoveAgentAction);
			updateActions();
		}

		// AttackAgent updaten und neue Action holen
		if (lastAttackAgentAction == null
				|| lastAttackAgentAction.hasFinished()) {
			lastAttackAgentAction = getActionByAttack(attackAgent
					.getNextAction(envBuilder.getAttackEnvId()));
			this.addAction(lastAttackAgentAction);
			updateActions();
		}
	}
	
	private double getNearestEnemyBearing() {
		double a = envBuilder.getNearestEnemyAngle();
		System.out.println("Winkel zum Gegner: " + a);
		return a;
	}

	@SuppressWarnings("unused")
	private void aimAtEnemy() {
		this.addAction(new GunTurnAction(getNearestEnemyBearing()));
	}

	private Action getActionByMovement(int movementId) {
		boolean nothing = false;
		TurnAction turn = null;
		MoveAction move = null;
		Vector2D destination = null;
		
		if (COMPLEX_MOVE_ENV) {
			ComplexMovement movement = ComplexMovement.byId(movementId);

			if (movement == ComplexMovement.NOTHING)
				nothing = true;
			else
				destination = getPosition().add(movement.getMoveVector());
		} else {
			SimpleMovement movement = SimpleMovement.byId(movementId);

			if (movement == SimpleMovement.NOTHING)
				nothing = true;
			else
				destination = getPosition().add(movement.getMoveVector());
		}
		
		
		if (nothing)
			return new NothingAction();
		
		double rotationAngle = destination.subtract(getPosition())
				.getNormalHeading() - Utils.normalizeHeading(getHeading());

		double distance = getPosition().distanceTo(destination);

		if (rotationAngle >= -90 && rotationAngle <= 90) {
			// Nach rechts oder links drehen und vorwärts fahren (Winkel
			// zwischen -90 und 90)
			turn = new TurnAction(rotationAngle);
			move = new MoveAction(distance);

		} else if (rotationAngle > 90) {
			// Nach links drehen und rückwärts fahren (Winkel über 90)
			turn = new TurnAction(rotationAngle - 180);
			move = new MoveAction(-distance);

		} else {
			// Nach rechts drehen und rückwärts fahren (Winkel unter -90)
			turn = new TurnAction(180 + rotationAngle);
			move = new MoveAction(distance);
		}

//		return new SerialAction(Arrays.asList(new Action[] { turn, move }));
		return new ConcurrentAction(Arrays.asList(new Action[] { turn, move }));
	}

	private Action getActionByAttack(int attackId) {
		boolean nothing = false;
		double gunTurnDirection = 0, firePower = 0;
		
		if (COMPLEX_ATTACK_ENV) {
			ComplexAttack attack = ComplexAttack.byId(attackId);

			if (attack == ComplexAttack.NOTHING) {
				nothing = true;
			} else {
				gunTurnDirection = attack.getDirection();
				firePower = attack.getPower().toDouble();
			}
		} else {
			SimpleAttack attack = SimpleAttack.byId(attackId);
			
			if (attack == SimpleAttack.NOTHING) {
				nothing = true;
			} else {
				gunTurnDirection = getNearestEnemyBearing() - getGunHeading() + attack.getDirection();
				firePower = attack.getPower().toDouble();
			}
		}
		
		if (nothing)
			return new NothingAction();

		GunTurnAction gunturn = new GunTurnAction(robocode.util.Utils.normalRelativeAngleDegrees(gunTurnDirection));
		FireAction fire = new FireAction(firePower);

		return new SerialAction(
				Arrays.asList(new Action[] { gunturn, fire }));
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

	@Override
	public void onBattleEnded(BattleEndedEvent event) {
		super.onBattleEnded(event);

		attackAgent.saveOnBattleEnd();
		moveAgent.saveOnBattleEnd();
	}
}
