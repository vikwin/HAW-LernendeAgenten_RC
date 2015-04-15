package src.robot;

import java.awt.Graphics2D;

import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import src.agents.LARCAgent;
import src.environment.LARCEnvironment;
import src.utility.Position;

public class LARCRobot extends AdvancedRobot {

	private LARCEnvironment environment;
	private LARCAgent agent;

	private double enemyX;
	private double enemyY;
	private double angleToEnemy;
	private double distanceToEnemy;
	private boolean gameOver;

	private Position selfGridPos;
	private Position enemyGridPos;

	public LARCRobot() {

		enemyX = 0;
		enemyY = 0;
		this.gameOver = false;
		this.environment = new LARCEnvironment(this);
		this.agent = new LARCAgent();
		AgentLoader theAgentLoader = new AgentLoader(agent);
		// Create an environmentloader that will start the environment when its
		// run method is called
		EnvironmentLoader theEnvironmentLoader = new EnvironmentLoader(environment);

		// Create threads so that the agent and environment can run
		// asynchronously
		Thread agentThread = new Thread(theAgentLoader);
		Thread environmentThread = new Thread(theEnvironmentLoader);

		// Start the threads
		agentThread.start();
		environmentThread.start();

	}

	@Override
	public void run() {
		this.setAdjustRadarForGunTurn(true);
		while (true) {
			setTurnRadarLeft(360);
			execute();
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		// point gun towards enemy:
		setTurnGunRight(getHeading() - getGunHeading() + event.getBearing());

		this.angleToEnemy = event.getBearing();
		this.distanceToEnemy = event.getDistance();
		this.triangulateEnemyPosition();
	}

	private void triangulateEnemyPosition() {
		// Normalisierter Winkel zwischen uns und Gegner (Radiant)
		double angle = Math.toRadians(((this.getHeading() + angleToEnemy) % 360));

		// Dreiecks Winkelformeln lösen nach:
		this.enemyX = (this.getX() + Math.sin(angle) * this.distanceToEnemy); // Gegenkathete
		this.enemyY = (this.getY() + Math.cos(angle) * this.distanceToEnemy); // Ankathete
	}

	public void getSelfGridPosition() {
		selfGridPos.setX(((int) (this.getX() / LARCEnvironment.TILESIZE)));
		selfGridPos.setY(((int) (this.getY() / LARCEnvironment.TILESIZE)));
	}

	public void getEnemyGridPosition() {
		enemyGridPos.setX(((int) (this.getEnemyX() / LARCEnvironment.TILESIZE)));
		enemyGridPos.setY(((int) (this.getEnemyY() / LARCEnvironment.TILESIZE)));
	}

	@Override
	public void onDeath(DeathEvent event) {
		this.gameOver = true;
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		this.gameOver = true;
	}

	public boolean getGameOver() {
		return this.gameOver;
	}

	public double getEnemyX() {
		return enemyX;
	}

	public double getEnemyY() {
		return enemyY;
	}

	@Override
	public void onPaint(Graphics2D g) {
		// Set the paint color to red
		g.setColor(java.awt.Color.RED);
		// draw grid
		for (int i = 0; i < this.getBattleFieldWidth(); i += LARCEnvironment.TILESIZE) {
			for (int j = 0; j < this.getBattleFieldHeight(); j += LARCEnvironment.TILESIZE) {

				g.drawRect(i, j, LARCEnvironment.TILESIZE, LARCEnvironment.TILESIZE);
			}
		}
		g.fillRect(((int) (selfGridPos.getX() * LARCEnvironment.TILESIZE)),
				((int) (selfGridPos.getY() * LARCEnvironment.TILESIZE)), LARCEnvironment.TILESIZE,
				LARCEnvironment.TILESIZE);
		g.fillRect(((int) (enemyGridPos.getX() * LARCEnvironment.TILESIZE)),
				((int) (enemyGridPos.getY() * LARCEnvironment.TILESIZE)), LARCEnvironment.TILESIZE,
				LARCEnvironment.TILESIZE);
	}
}
