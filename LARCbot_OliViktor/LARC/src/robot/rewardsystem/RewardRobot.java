package robot.rewardsystem;

import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.Rules;
import robot.actionsystem.ActorRobot;

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
	private static final double BULLET_REWARD = 3 / Rules.MAX_BULLET_POWER;		// Belohnung / Bestrafung für eine Kugel, wird mit der Power der Kugel multipliziert
	private static final double HIT_REWARD = 1;			// Belohnung / Bestrafung wenn der Roboter gegen etwas gegen fährt
	private static final double VICTORY_REWARD = 10;	// Belohnung für dem Sieg, Bestrafung für die Niederlage  
	
	private double reward;
	private double shotReward;
	
	public RewardRobot() {
		super();
		reward = 0.0;
		shotReward = 0.0;
	}
	
	public double getReward() {
		double r = reward;
		reward = 0;
		
		return r;
	}
	
	public double[] getReward(boolean extraShotreward) {
		double[] d;
		
		if (!extraShotreward) {
			d = new double[] {getReward()};
		} else {
			d = new double[]{getReward(), shotReward};
			shotReward = 0.0;
		}
		
		return d;
	}
	
	/* Events mit Kugeln */
	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		reward -= BULLET_REWARD * event.getPower();
	}
	
	@Override
	public void onBulletHitBullet(BulletHitBulletEvent event) {
		reward += 2 * BULLET_REWARD;
		shotReward += 2 * BULLET_REWARD;
	}
	
	@Override
	public void onBulletHit(BulletHitEvent event) {
		reward += 2 * BULLET_REWARD * event.getBullet().getPower();
		shotReward += 2 * BULLET_REWARD * event.getBullet().getPower();
	}
	
	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		reward -= BULLET_REWARD * event.getBullet().getPower();
		shotReward -= BULLET_REWARD * event.getBullet().getPower();
	}
	
	
	/* Events, wenn etwas gerammt wurde */
	@Override
	public void onHitRobot(HitRobotEvent event) {
		reward += HIT_REWARD;
	}
	
	@Override
	public void onHitWall(HitWallEvent event) {
		reward -= 5 * HIT_REWARD;
	}
	
	
	/* Events bei Ende der Runde*/
	@Override
	public void onDeath(DeathEvent event) {
		reward -= VICTORY_REWARD;
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent event) {
		reward += VICTORY_REWARD;
	}
}
