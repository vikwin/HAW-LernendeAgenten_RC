package robot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import robocode.BattleEndedEvent;
import robocode.CustomEvent;
import robocode.HitByBulletEvent;
import robocode.RadarTurnCompleteCondition;
import robocode.RoundEndedEvent;
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
import utils.WaveSurf;
import agents.AttackAgent;
import agents.MoveAgent;
import environment.Enemy;
import environment.EnemyWave;
import environment.EnvironmentBuilder;
import environment.EnvironmentBuilder.AttackEnvironments;
import environment.EnvironmentBuilder.MoveEnvironments;

public class LARCbot extends RewardRobot {
	// private static final boolean USE_WAVE_SURF = true;

	// Art der Umgebungen
	private static final AttackEnvironments ATTACK_ENV = AttackEnvironments
			.values()[Config.getIntValue("Robot_AttackEnv")];
	private static final MoveEnvironments MOVE_ENV = MoveEnvironments.values()[Config
			.getIntValue("Robot_MoveEnv")];

	private double enemyBearing = 0.0;
	private boolean lastRadarTurnRight = false; // Flag für die letzte
												// Bewegungsrichtung des Radars
	private boolean scanning;

	private EnvironmentBuilder envBuilder;

	private MoveAgent moveAgent;
	private AttackAgent attackAgent;

	private Action lastMoveAgentAction = null, lastAttackAgentAction = null;

	public LARCbot() {
		enemyBearing = 0.0;
		lastRadarTurnRight = false;
		scanning = false;
	}

	@Override
	public void run() {
		super.run();

		setBodyColor(Color.ORANGE);
		setBulletColor(Color.RED);
		setGunColor(Color.RED);
		getName();

		createEvents();
		// Der Environmentbuilder muss aus der run Methode heraus initialisiert
		// werden, weil sonst der Zugriff auf die Eigenschaften des Spielfelds
		// verwehrt wird
		try {
			envBuilder = new EnvironmentBuilder(this, MOVE_ENV, ATTACK_ENV);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		int moveActionCount = 0, attackActionCount = 0;
		switch (MOVE_ENV) {
		case SIMPLE_MOVE:
			moveActionCount = SimpleMovement.values().length;
			break;
		case COMPLEX_MOVE:
			moveActionCount = ComplexMovement.values().length;
			break;
		case WAVESURF_MOVE:
			moveActionCount = OrbitalMovement.values().length;
			break;
		}

		switch (ATTACK_ENV) {
		case SIMPLE_ATTACK:
			attackActionCount = SimpleAttack.getActionCount();
			break;
		case COMPLEX_ATTACK:
			attackActionCount = ComplexAttack.getActionCount();
			break;
		}

		moveAgent = new MoveAgent(envBuilder.getMoveEnvStateCount(),
				moveActionCount);
		attackAgent = new AttackAgent(envBuilder.getAttackEnvStateCount(),
				attackActionCount);

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
			System.out
					.println("LARCBot stopped because of ThreadDeath Exception");
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
			scanning = false;
			break;
		}
	}

	private void act() {
		// Radar updaten
		if (!scanning)
			doScan();

		// Agents updaten und neue Actions holen
		if (lastMoveAgentAction == null || lastMoveAgentAction.hasFinished()) {

			lastMoveAgentAction = getActionByMovement(moveAgent
					.getNextAction(envBuilder.getMoveEnvId()));
			this.addAction(lastMoveAgentAction);

			moveAgent.addReward(getReward(0));
		}

		if (lastAttackAgentAction == null
				|| lastAttackAgentAction.hasFinished()) {

			lastAttackAgentAction = getActionByAttack(attackAgent
					.getNextAction(envBuilder.getAttackEnvId()));
			this.addAction(lastAttackAgentAction);

			attackAgent.addReward(getReward(1));
		}
	}

	private Action getActionByMovement(int movementId) {
		boolean nothing = true;
		TurnAction turn = null;
		MoveAction move = null;
		Vector2D destination = null;

		switch (MOVE_ENV) {
		case COMPLEX_MOVE:
			ComplexMovement complexMovement = ComplexMovement.byId(movementId);

			if (complexMovement != ComplexMovement.NOTHING) {
				nothing = false;
				destination = getPosition()
						.add(complexMovement.getMoveVector());
			}

			break;
		case SIMPLE_MOVE:
			SimpleMovement simpleMovement = SimpleMovement.byId(movementId);

			if (simpleMovement != SimpleMovement.NOTHING) {
				nothing = false;
				destination = getPosition().add(simpleMovement.getMoveVector());
			}

			break;
		case WAVESURF_MOVE:
			OrbitalMovement orbitalMovement = OrbitalMovement.byId(movementId);
			if (orbitalMovement != OrbitalMovement.NOTHING) {
				nothing = false;
				Enemy enemy = envBuilder.getLockedEnemy();
				return orbitalMovement.getMoveAction(enemy);
			}
			
			break;
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

		// return new SerialAction(Arrays.asList(new Action[] { turn, move }));
		return new ConcurrentAction(Arrays.asList(new Action[] { turn, move }));
	}

	private Action getActionByAttack(int attackId) {
		double gunTurnDirection = 0, firePower = 0;

		switch (ATTACK_ENV) {
		case COMPLEX_ATTACK:
			ComplexAttack complexAttack = ComplexAttack.byId(attackId);

			if (complexAttack == ComplexAttack.NOTHING) {
				return new NothingAction();
			} else {
				gunTurnDirection = complexAttack.getDirection();
				firePower = complexAttack.getPower().toDouble();
			}

			break;

		case SIMPLE_ATTACK:
			SimpleAttack simpleAttack = SimpleAttack.byId(attackId);

			Enemy enemy = envBuilder.getEnemy();

			if (simpleAttack == SimpleAttack.NOTHING || enemy == null) {
				return new NothingAction();
			} else {
				firePower = simpleAttack.getPower().toDouble();
				Vector2D vectorToTarget = EnvironmentBuilder
						.addOffsetToEnemyPosition(enemy, this,
								simpleAttack.getDeviation()).subtract(
								getPosition());

				double enemyAngle = vectorToTarget.getHeading();
				gunTurnDirection = enemyAngle - getGunHeading();
			}

			break;
		}

		if (gunTurnDirection > 180)
			gunTurnDirection -= 360;
		else if (gunTurnDirection < -180)
			gunTurnDirection += 360;

		GunTurnAction gunturn = new GunTurnAction(gunTurnDirection);

		FireAction fire = new FireAction(firePower);

		return new SerialAction(Arrays.asList(new Action[] { gunturn, fire }));
	}

	
	
	/**
	 * Einaches Wavesurfing anstatt MoveAgent.
	 */
	public void doSurf() {
		Enemy enemy = envBuilder.getLockedEnemy();
		if (enemy == null)
			return;

		EnemyWave surfWave = enemy.getClosestSurfableWave();

		if (surfWave == null) {
			return;
		}

		double dangerLeft = enemy.checkDanger(surfWave, -1);
		double dangerRight = enemy.checkDanger(surfWave, 1);

		Vector2D myLocation = getPosition();
		double goAngle = surfWave.fireLocation.angleTo(myLocation);
		if (dangerLeft < dangerRight) {
			goAngle = WaveSurf.wallSmoothing(myLocation, goAngle
					- (Math.PI / 2), -1, 160);
		} else {
			goAngle = WaveSurf.wallSmoothing(myLocation, goAngle
					+ (Math.PI / 2), 1, 160);
		}

		WaveSurf.setBackAsFront(this, goAngle);
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
		double turn;
		if (Math.abs(enemyBearing) > 0.0) {
			turn = enemyBearing - getRadarHeading();
			if (lastRadarTurnRight) {
				turn -= 10;
				lastRadarTurnRight = false;
				enemyBearing = 0;
			} else {
				turn += 10;
				lastRadarTurnRight = true;
			}
		} else {
			// Bisher noch kein Gegner gefunden, weiter rundum scannen
			turn = lastRadarTurnRight ? -10 : 10;
		}

		// turn normalisieren
		if (turn > 180)
			turn -= 360;
		else if (turn < -180)
			turn += 360;

		setTurnRadarRight(turn);
		scanning = true;
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		// Scannevent verarbeiten
		envBuilder.computeScannedRobotEvent(event);

		// Umwelt aktualisieren
		envBuilder.create();

		// Position des Gegner (nur fürs Radar!) aktualisieren
		enemyBearing = getHeading() + event.getBearing();
	}

	@Override
	public void onPaint(Graphics2D g) {
		envBuilder.doPaint(g);

	}

	@Override
	public void onRoundEnded(RoundEndedEvent event) {
		super.onRoundEnded(event);

		attackAgent.onRoundEnded();
		moveAgent.onRoundEnded();
	}

	@Override
	public void onBattleEnded(BattleEndedEvent event) {
		super.onBattleEnded(event);

		attackAgent.saveOnBattleEnd();
		moveAgent.saveOnBattleEnd();
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		super.onHitByBullet(event);

		envBuilder.onHitByBullet(event);
	}
}
