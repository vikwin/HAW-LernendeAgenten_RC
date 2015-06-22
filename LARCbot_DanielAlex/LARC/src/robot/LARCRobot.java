package robot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.BattleEndedEvent;
import robocode.DeathEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import state.State;
import utility.Position;
import agents.LARCAgent;
import environment.LARCEnvironment;

// Runtime VM Arguments:
// -Xmx512M -DNOSECURITY=true -Dsun.io.useCanonCaches=false

public class LARCRobot extends RewardRobot {
	// (8*5)*((4*5*3*8)+(5*8*3*8)) //Ohne unerreichbare states = 57600 States/Actions
	public static final int NO_OF_STATES = 9 * 8 * 8 * 3; // my position * enemyErection * enemyPosition * enemyDistance
	public static final int NO_OF_ACTIONS = 8 * 5; // * DriveDirection * GunOffsetFire
	public static final int BULLETPOWER = 500; //
	public static final long STEP_TIME = 100;
	public static final int DRAW_OFFSET = 50;
	public static double[][] VALUE_FUNCTION = new double[NO_OF_ACTIONS][NO_OF_STATES];
	public static int BATTLE_SCORE;
	public static boolean STATE_REPEAT;
	public double bulletPower;
	public double currentGunAngleToEnemy;
	public double oldGunAngleToEnemy = 0;
	public LARCEnvironment environment;
	private LARCAgent agent;
	private double enemyX;
	private double enemyY;
	private double angleToEnemy;
	private double distanceToEnemy;
	private double selfEnergy;
	private double enemyEnergy;
	private double energyRatio;
	private double currentReward;
	private double previousReward;
	private double gunPostion;
	private double enemyDirection;
	private Position myPosition;
	private double currentDistance;
	private double currentEnemyDistance;
	private double currentHeading;
	public int wallHitCounter;
	private boolean dontAdjustGun;
	private double gunTurnToEnemy;
	private static String[] HIMMELSRICHTUNGEN = new String[] { "N", "NO", "O", "SO", "S", "SW", "W", "NW" };

	public LARCRobot() {
		this.enemyX = 0;
		this.enemyY = 0;
		this.wallHitCounter = 0;
		this.enemyDirection = 1;
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent(this);
	}

	@Override
	public void run() {
		this.execute();
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustGunForRobotTurn(true);
		this.setColors(Color.CYAN, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW);
		// init
		this.myPosition = new Position(this.getX(), this.getY());
		this.environment.env_init();
		this.agent.agent_init();
		int stateID = this.environment.env_start();
		int actionID = this.agent.agent_start(stateID);
		setTurnRadarRight(2000);
		try {
			while (true) {
				this.gunPostion = this.getGunHeading();
				this.myPosition.setX(this.getX());
				this.myPosition.setY(this.getY());

				// stepping
				if (this.getDistanceRemaining() == 0 && this.getTurnRemaining() == 0) {
					updateHeading();
					stateID = this.environment.env_step(actionID);
					actionID = this.agent.agent_step(stateID);
				} else if (this.getGunTurnRemaining() == 0) {
					this.dontAdjustGun = false;
					fire(Math.min(BULLETPOWER / this.distanceToEnemy, 3));
				}
				execute();

			}
		} catch (ThreadDeath ex) {
		}
	}

	public void move(double[] instructions) {
		this.dontAdjustGun = true;
		// Gun Zielen:
		// setTurnGunRight(this.angleToEnemy + instructions[4]);
		// if (getGunTurnRemaining() == 0) {
		// this.setTurnGunRight(instructions[3]);
		// }

		// Panzer Fahren!:
		backOrAhead(instructions[1], instructions[0]);

		// // Schießen:
		// if (instructions[2] == 1.0) {
		// // firePower = Math.min(BULLETPOWER / this.distanceToEnemy, 3); // 3 ist max möglicher wert für firepower
		// setFire(bulletPower);
		// }
	}

	public void updateHeading() {
		this.currentHeading = this.getHeading();
	}

	private void updateEnergyRatio() {
		if (this.enemyEnergy > 0) {
			this.energyRatio = this.selfEnergy / this.enemyEnergy; // Energy ratio: self/enemy
		}
	}

	private void triangulateEnemyPosition() {
		// Normalisierter Winkel zwischen uns und Gegner (Radiant)
		double angle = Math.toRadians(((this.getHeading() + angleToEnemy) % 360));

		// Dreiecks Winkelformeln lÃ¶sen nach:
		this.enemyX = (this.getX() + Math.sin(angle) * this.distanceToEnemy); // Gegenkathete
		this.enemyY = (this.getY() + Math.cos(angle) * this.distanceToEnemy); // Ankathete
	}

	private void backOrAhead(double turnDegrees, double distance) {
		if (turnDegrees > 90) {
			currentDistance = -distance;
			setTurnRight(turnDegrees - 180);
		} else if (turnDegrees < -90) {
			currentDistance = -distance;
			setTurnRight(turnDegrees + 180);
		} else {
			this.currentDistance = distance;
			setTurnRight(turnDegrees);
		}
		setAhead(this.currentDistance);
	}

	public double getEnemyDirection() {
		return enemyDirection;
	}

	public double getEnemyX() {
		return enemyX;
	}

	public double getEnemyY() {
		return enemyY;
	}

	public double getCurrentReward() {
		return currentReward;
	}

	public void setCurrentReward(double d) {
		this.currentReward = d;
	}

	public double getEnergyRatio() {
		return energyRatio;
	}

	public double getAngleToEnemy() {
		return angleToEnemy;
	}

	public void setAngleToEnemy(double angleToEnemy) {
		this.angleToEnemy = angleToEnemy;
	}

	public double getGunPostion() {
		return gunPostion;
	}

	public Position getMyPosition() {
		return myPosition;
	}

	public double getPreviousReward() {
		return previousReward;
	}

	public void setPreviousReward(double previousReward2) {
		this.previousReward = previousReward2;
	}

	public double getCurrentHeading() {
		return currentHeading;
	}

	public double getCurrentEnemyDistance() {
		return currentEnemyDistance;
	}

	public void setCurrentEnemyDistance(double currentEnemyDistance) {
		this.currentEnemyDistance = currentEnemyDistance;
	}

	/*************************************************************************************************************************/
	@Override
	public void onScannedRobot(ScannedRobotEvent e) {

		this.bulletPower = Math.min(BULLETPOWER / this.distanceToEnemy, 3); // 3 ist max möglicher wert für firepower

		double myX = getX();
		double myY = getY();
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
		double enemyHeading = e.getHeadingRadians();
		double enemyVelocity = e.getVelocity();

		double deltaTime = 0;
		double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
		double predictedX = enemyX, predictedY = enemyY;
		while ((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)) {
			predictedX += Math.sin(enemyHeading) * enemyVelocity;
			predictedY += Math.cos(enemyHeading) * enemyVelocity;
			if (predictedX < 18.0 || predictedY < 18.0 || predictedX > battleFieldWidth - 18.0
					|| predictedY > battleFieldHeight - 18.0) {
				predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0);
				predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
				break;
			}
		}
		double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));

		setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));

		if (!this.dontAdjustGun) {
			setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
		}

		// calculate enemy direction via oster algorithm:
		double velocity = e.getVelocity();
		if (velocity >= 0) {
			enemyDirection = e.getHeading();
		} else {
			enemyDirection = e.getHeading() - 180;
			if (enemyDirection < 0) {
				enemyDirection += 360;
			}
		}
		// System.out.println("Enemy Direction: " + enemyDirection);
		gunTurnToEnemy = Position.normalizeDegrees(getHeading() - getGunHeading() + e.getBearing());

		// update enemy-related state variables:
		this.currentGunAngleToEnemy = Math.abs(getHeading() - getGunHeading() + e.getBearing());
		this.angleToEnemy = e.getBearing();
		this.distanceToEnemy = e.getDistance();
		this.triangulateEnemyPosition();
		this.enemyEnergy = e.getEnergy();
		this.currentEnemyDistance = e.getDistance();

		this.updateEnergyRatio();
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		super.onHitWall(event);
		this.wallHitCounter++;
	}

	@Override
	public void onDeath(DeathEvent event) {
		super.onDeath(event);
		this.environment.env_cleanup();
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		super.onRobotDeath(event);
		this.environment.env_cleanup();
	}

	@Override
	public void onBattleEnded(BattleEndedEvent event) {
		super.onBattleEnded(event);
		BATTLE_SCORE = event.getResults().getScore();
		this.agent.agent_end();
		this.agent.agent_cleanup();
	}

	// MID, LEFTEDGE, RIGHTEDGE, TOPEDGE, BOTTOMEDGE, NEXTLEFTEDGE, NEXTRIGHTEDGE, NEXTTOPEDGE, NEXTBOTTOMEDGE;
//	@Override
//	public void onPaint(Graphics2D g) {
//		// Set the paint color to red
//		g.setColor(java.awt.Color.RED);
//		// draw grid
//		g.drawRect(0, 0, State.MIN_X, 600);
//		g.drawRect(State.MAX_X, 0, State.MIN_X, 600);
//		g.setColor(java.awt.Color.BLUE);
//		g.drawRect(State.MIN_X, 0, State.MAX_X - State.MIN_X, State.MIN_Y);
//		g.drawRect(State.MIN_X, State.MAX_Y, State.MAX_X - State.MIN_X, State.MIN_Y);
//
//		// Next:
//		g.setColor(java.awt.Color.CYAN);
//		g.drawRect(State.NEXTMIN_X, State.MIN_X, State.NEXTMAX_X - State.NEXTMIN_X, State.MIN_Y);
//		g.drawRect(State.NEXTMIN_X, State.NEXTMAX_Y, State.NEXTMAX_X - State.NEXTMIN_X, State.MIN_Y);
//		g.setColor(java.awt.Color.ORANGE);
//		g.drawRect(State.MIN_X, State.MIN_Y, State.MIN_X, State.MAX_Y - State.MIN_Y);
//		g.drawRect(State.NEXTMAX_X, State.MIN_Y, State.MIN_X, State.MAX_Y - State.MIN_Y);
//
//		g.setColor(java.awt.Color.GREEN);
//		// MID:
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][0]), 350, 200 + 30 * j);
//		}
//
//		// LEFT
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][1]), 10, 70 + DRAW_OFFSET
//					* (j + 1));
//		}
//
//		// RIGHT
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][2]), 750, 70 + DRAW_OFFSET
//					* (j + 1));
//		}
//
//		// TOP
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][3]), 150 + DRAW_OFFSET
//					* (j + 1), 590);
//		}
//
//		// BOTTOM
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][4]), 150 + DRAW_OFFSET
//					* (j + 1), 10);
//		}
//
//		// MIDLEFT
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][5]), 70, 70 + DRAW_OFFSET
//					* (j + 1));
//		}
//
//		// MIDRIGHT
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][6]), 690, 70 + DRAW_OFFSET
//					* (j + 1));
//		}
//
//		// MIDTOP
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][7]), 150 + DRAW_OFFSET
//					* (j + 1), 530);
//		}
//
//		// MIDBOTTOM
//		for (int j = 0; j < VALUE_FUNCTION.length; j++) {
//			g.drawString(HIMMELSRICHTUNGEN[j] + " " + round(LARCRobot.VALUE_FUNCTION[j][8]), 150 + DRAW_OFFSET
//					* (j + 1), 70);
//		}
//
//	}

	private double round(double toRound) {
		toRound = toRound * 100;
		toRound = Math.round(toRound);
		return toRound / 100;
	}
}
