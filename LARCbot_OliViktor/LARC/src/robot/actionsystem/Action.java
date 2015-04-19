package robot.actionsystem;

import robocode.CustomEvent;

/**
 * Interface für Action Objekte. Eine Action ist eine atomare Handlung, die ein ActorRobot durchführen
 * kann. 
 * @author Viktor Winkelmann
 *
 */

public interface Action {
	
	public boolean hasFinished();
	
	public void start();
	
	public void stop();
	
	public void update();
	
	public void update(CustomEvent event);
	
	public void setActor(ActorRobot robot);
	
	
	
}
