package robot;

import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;

/**
 * Der RewardRobot ist eine abstrakte Robot Klasse. Dieser wertet Events aus und erstellt aus ihnen Belohnungen oder Bestrafungen.
 * 
 * Alle Belohnungen werden aufaddiert und können per getReward() abgefragt werden. Nach jedem Abruf wird der Wert resettet, sodass alle Events seit dem letzten Abruf als erledigt
 * gelten.
 * 
 * @author Oliver Niebsch
 *
 */
public abstract class RewardRobot extends AdvancedRobot {
	private static boolean multiplyBulletPower;
	private static double hitByBullet, bulletHitBullet, bulletHitEnemy, bulletHitWall;
	private static double hitByEnemy, hitRobot, hitWall;
	private static double winning, loosing;

	public static double selfHitByBulletcounter;
	public static double enemyHitCounter;
	public static double bulletwallhitcounter;
	public static double ramHitCounter;
	public static double rammedCounter;
	public static double wallRamCounter;
	public static double roundsWon;


	static {
		// Kugelevents
		hitByBullet = -1;
		bulletHitBullet = 0;
		bulletHitEnemy = 3;
		bulletHitWall = -3;
		multiplyBulletPower = false;

		// Rammen von Objekten
		hitByEnemy = -1;
		hitRobot = 1;
		hitWall = -3;
		// Add reward for staying in mid

		// Rundenende
		winning = 1;
		loosing = -1;
	}

	private double reward;

	public RewardRobot() {
		super();
		reward = 0.0;
	}

	public double getReward() {
		double r = reward;
		reward = 0;

		return r;
	}

	public void addReward(double value) {
		reward += value;
	}

	/* Events mit Kugeln */
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		selfHitByBulletcounter++;
		if (multiplyBulletPower)
			addReward(hitByBullet * event.getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(hitByBullet);
	}

	@Override
	public void onBulletHitBullet(BulletHitBulletEvent event) {
		if (multiplyBulletPower)
			addReward(bulletHitBullet * event.getBullet().getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(bulletHitBullet);
	}

	@Override
	public void onBulletHit(BulletHitEvent event) {
		enemyHitCounter++;
		if (multiplyBulletPower)
			addReward(bulletHitEnemy * event.getBullet().getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(bulletHitEnemy);
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		bulletwallhitcounter++;
		if (multiplyBulletPower)
			addReward(bulletHitWall * event.getBullet().getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(bulletHitWall);
	}

	/* Events, wenn etwas gerammt wurde */
	@Override
	public void onHitRobot(HitRobotEvent event) {
		if (event.isMyFault()) {
			ramHitCounter++;
			addReward(hitRobot);
		} else {
			rammedCounter++;
			addReward(hitByEnemy);
		}
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		wallRamCounter++;
		addReward(hitWall);
	}

	/* Events bei Ende der Runde */
	@Override
	public void onDeath(DeathEvent event) {
		addReward(loosing);
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		roundsWon++;
		addReward(winning);
	}
}
