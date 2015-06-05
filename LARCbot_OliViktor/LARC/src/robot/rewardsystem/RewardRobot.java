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
import utility.Position;

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

	private int selfhitcounter;
	private int enemyhitcounter;
	private int bulletwallhitcounter;

	static {
		// Kugelevents
		hitByBullet = -1;
		bulletHitBullet = 0;
		bulletHitEnemy = 1;
		bulletHitWall = -1;
		multiplyBulletPower = false;

		// Rammen von Objekten
		hitByEnemy = -1;
		hitRobot = 1;
		hitWall = -1;

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

	private void addReward(double value) {
		reward += value;
	}

	/* Events mit Kugeln */
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		this.selfhitcounter++;
		Position.printdebug("Getroffen worden: " + this.selfhitcounter);
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
		this.enemyhitcounter++;
		Position.printdebug("Getroffen: " + this.enemyhitcounter);
		if (multiplyBulletPower)
			addReward(bulletHitEnemy * event.getBullet().getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(bulletHitEnemy);
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		this.bulletwallhitcounter++;
		Position.printdebug("Wand getroffen: " + this.bulletwallhitcounter);
		if (multiplyBulletPower)
			addReward(bulletHitWall * event.getBullet().getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(bulletHitWall);
	}

	/* Events, wenn etwas gerammt wurde */
	@Override
	public void onHitRobot(HitRobotEvent event) {
		if (event.isMyFault())
			addReward(hitRobot);
		else
			addReward(hitByEnemy);
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		addReward(hitWall);
	}

	/* Events bei Ende der Runde */
	@Override
	public void onDeath(DeathEvent event) {
		addReward(winning);
	}

	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		addReward(loosing);
	}
}
