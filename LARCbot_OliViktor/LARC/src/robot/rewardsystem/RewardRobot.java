package robot.rewardsystem;

import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robot.actionsystem.ActorRobot;
import utils.Config;

/**
 * Der RewardRobot ist eine abstrakte Robot Klasse. Dieser wertet Events
 * aus und erstellt aus ihnen Belohnungen oder Bestrafungen.
 * 
 * Alle Belohnungen werden aufaddiert und können per getReward() abgefragt
 * werden. Nach jedem Abruf wird der Wert resettet, sodass alle Events seit
 * dem letzten Abruf als erledigt gelten.   
 * 
 * @author Oliver Niebsch
 *
 */
public abstract class RewardRobot extends ActorRobot {
	private static boolean multiplyBulletPower;
	private static double hitByBullet, bulletHitBullet, bulletHitEnemy, bulletHitWall;
	private static double hitRobot, hitWall;
	private static double winning, loosing;
	
	static {
		hitByBullet = Config.getDoubleValue("Reward_HitByBullet");
		bulletHitBullet = Config.getDoubleValue("Reward_BulletHitBullet");
		bulletHitEnemy = Config.getDoubleValue("Reward_BulletHitEnemy");
		bulletHitWall = Config.getDoubleValue("Reward_BulletHitWall");
		multiplyBulletPower = Config.getBoolValue("Reward_MultBulletPower");
		
		hitRobot = Config.getDoubleValue("Reward_HitRobot");
		hitWall = Config.getDoubleValue("Reward_HitWall");
		
		winning = Config.getDoubleValue("Reward_Winning");
		loosing = Config.getDoubleValue("Reward_Loosing");
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
	
	/* Events mit Kugeln */
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		if (multiplyBulletPower)
			reward += hitByBullet * event.getPower();
		else
			reward += hitByBullet;
	}
	
	@Override
	public void onBulletHitBullet(BulletHitBulletEvent event) {
		if (multiplyBulletPower)
			reward += bulletHitBullet * event.getBullet().getPower();
		else
			reward += bulletHitBullet;
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		if (multiplyBulletPower)
			reward += bulletHitEnemy * event.getBullet().getPower();
		else
			reward += bulletHitEnemy;
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		if (multiplyBulletPower)
			reward += bulletHitWall * event.getBullet().getPower();
		else
			reward += bulletHitWall;
	}
	
	
	/* Events, wenn etwas gerammt wurde */
	@Override
	public void onHitRobot(HitRobotEvent event) {
		reward += hitRobot;
	}
	
	@Override
	public void onHitWall(HitWallEvent event) {
		reward += hitWall;
	}
	
	
	/* Events bei Ende der Runde*/
	@Override
	public void onDeath(DeathEvent event) {
		reward += winning;
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		reward += loosing;
	}
}
