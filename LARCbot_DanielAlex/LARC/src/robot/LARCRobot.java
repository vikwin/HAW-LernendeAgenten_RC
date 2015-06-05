package robot;

import java.awt.Color;
import java.awt.Graphics2D;

import robocode.DeathEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import utility.Position;
import agents.LARCAgent;
import environment.LARCEnvironment;

// Runtime VM Arguments:
// -Xmx512M -DNOSECURITY=true -Dsun.io.useCanonCaches=false

public class LARCRobot extends RewardRobot {
	// (2*9*5)*(1*8*8*3+4*5*8*3) //Ohne unerreichbare states = 53760 States/Actions
	public static final int NO_OF_STATES = 5 * 8 * 8 * 3; // my position * enemyErection * enemyPosition * enemyDistance
	public static final int NO_OF_ACTIONS = 2 * 9 * 5; // Fire * DriveDirection * GunOffset
	public static final int BULLETPOWER = 500; //
	public static final long STEP_TIME = 100;
	public static double[][] VALUE_FUNCTION = new double[NO_OF_ACTIONS][NO_OF_STATES];
	public static boolean STATE_REPEAT;
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
	private int scanDirection;
	private double gunTurnToEnemy;
	public int wallHitCounter;

	public LARCRobot() {
		this.enemyX = 0;
		this.enemyY = 0;
		this.wallHitCounter = 0;
		this.enemyDirection = 1;
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent(this);
		this.scanDirection = 1;
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
		while (true) {
			this.gunPostion = this.getGunHeading();
			this.selfEnergy = this.getEnergy();
			this.myPosition.setX(this.getX());
			this.myPosition.setY(this.getY());
			// default actions:
			// setTurnRadarRight(2000);
			// stepping
			if (this.getDistanceRemaining() == 0 && this.getTurnRemaining() == 0 ) {
				updateHeading();
				stateID = this.environment.env_step(actionID);
				actionID = this.agent.agent_step(stateID);
			}
			execute();
		}
	}

	public void move(double[] instructions) {
		// Gun Zielen:
		// setTurnGunRight(this.angleToEnemy + instructions[4]);

		// Panzer Fahren!:
		backOrAhead(instructions[1], instructions[0]);

		if (getGunTurnRemaining() == 0) {
			this.setTurnGunRight(gunTurnToEnemy + instructions[3]);
		}

		// Schießen:
		if (instructions[2] == 1.0) {
			double firePower = Math.min(BULLETPOWER / this.distanceToEnemy, 3); // 3 ist max möglicher wert für firepower
			setFire(firePower);
		}
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
	public void onScannedRobot(ScannedRobotEvent event) {
		scanDirection *= -1; // changes value from 1 to -1
		setTurnRadarRight(360 * scanDirection);

		// calculate enemy direction via oster algorithm:
		double velocity = event.getVelocity();
		if (velocity >= 0) {
			enemyDirection = event.getHeading();
		} else {
			enemyDirection = event.getHeading() - 180;
			if (enemyDirection < 0) {
				enemyDirection += 360;
			}
		}
		// System.out.println("Enemy Direction: " + enemyDirection);
		gunTurnToEnemy = Position.normalizeDegrees(getHeading() - getGunHeading() + event.getBearing());
		// setTurnGunRight(Position.normalizeDegrees(getHeading() - getGunHeading() + event.getBearing()));

		// update enemy-related state variables:
		this.currentGunAngleToEnemy = Math.abs(getHeading() - getGunHeading() + event.getBearing());
		this.angleToEnemy = event.getBearing();
		this.distanceToEnemy = event.getDistance();
		this.triangulateEnemyPosition();
		this.enemyEnergy = event.getEnergy();
		this.currentEnemyDistance = event.getDistance();

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
		this.agent.agent_end();
		this.environment.env_cleanup();
		this.agent.agent_cleanup();
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		super.onRobotDeath(event);
		this.agent.agent_end();
		this.environment.env_cleanup();
		this.agent.agent_cleanup();
	}

	@Override
	public void onPaint(Graphics2D g) {
		// Set the paint color to red
		// g.setColor(java.awt.Color.RED);
		//
		// // draw grid
		// for (int i = 0; i < this.getBattleFieldWidth(); i += LARCEnvironment.TILESIZE) {
		// for (int j = 0; j < this.getBattleFieldHeight(); j += LARCEnvironment.TILESIZE) {
		//
		// g.drawRect(i, j, LARCEnvironment.TILESIZE, LARCEnvironment.TILESIZE);
		// }
		// }
		// g.fillRect(((int) (selfGridPos.getX() * LARCEnvironment.TILESIZE)),
		// ((int) (selfGridPos.getY() * LARCEnvironment.TILESIZE)), LARCEnvironment.TILESIZE,
		// LARCEnvironment.TILESIZE);
		// g.fillRect(((int) (enemyGridPos.getX() * LARCEnvironment.TILESIZE)),
		// ((int) (enemyGridPos.getY() * LARCEnvironment.TILESIZE)), LARCEnvironment.TILESIZE,
		// LARCEnvironment.TILESIZE);
	}
}
