package robot.rewardsystem;

import java.util.Arrays;

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
	private static double hitByEnemy, hitRobot, hitWall;
	private static double winning, loosing;
	
	static {
		hitByBullet = Config.getDoubleValue("Reward_HitByBullet");
		bulletHitBullet = Config.getDoubleValue("Reward_BulletHitBullet");
		bulletHitEnemy = Config.getDoubleValue("Reward_BulletHitEnemy");
		bulletHitWall = Config.getDoubleValue("Reward_BulletHitWall");
		multiplyBulletPower = Config.getBoolValue("Reward_MultBulletPower");
		
		hitByEnemy = Config.getDoubleValue("Reward_HitByEnemy");
		hitRobot = Config.getDoubleValue("Reward_HitRobot");
		hitWall = Config.getDoubleValue("Reward_HitWall");
		
		winning = Config.getDoubleValue("Reward_Winning");
		loosing = Config.getDoubleValue("Reward_Loosing");
	}
	
	private double[] reward;
	
	public RewardRobot() {
		super();
		reward = new double[2];
		Arrays.fill(reward, 0.0);
	}
	
	public double getReward(int index) {
		double r = reward[index];
		reward[index] = 0;
		
		return r;
	}
	
	private void addReward(double value) {
		for (int i = 0; i < reward.length; i++) {
			reward[i] += value;
		}
	}
	
	/* Events mit Kugeln */
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
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
		if (multiplyBulletPower)
			addReward(bulletHitEnemy * event.getBullet().getPower() / robocode.Rules.MAX_BULLET_POWER);
		else
			addReward(bulletHitEnemy);
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
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
	
	
	/* Events bei Ende der Runde*/
	@Override
	public void onDeath(DeathEvent event) {
		addReward(winning);
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		addReward(loosing);
	}
}
