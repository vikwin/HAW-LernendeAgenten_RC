package environment;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import utils.Utils;
import utils.Vector2D;

public class Enemy {
	
	private AdvancedRobot selfBot;
	private String name;
	private Vector2D position;
	private double energy, lastEnergy = -1, heading, velocity, distance, bearing;
	private long lastSeen;
	
	public Enemy(ScannedRobotEvent scannedBot, AdvancedRobot selfBot) {
		name = scannedBot.getName();
		this.selfBot = selfBot;		
		
		updateAllAttributes(scannedBot);
	}
	
	public Enemy(String name, AdvancedRobot selfBot) {
		this.name = name;
		this.selfBot = selfBot;
	}
	
	/**
	 * Schnelles aktualisieren aller Attribute des Enemy's ohne
	 * Rückinfo.
	 * @param scannedBot Der gescannte Bot
	 */
	public void updateAllAttributes(ScannedRobotEvent scannedBot) {
		energy = scannedBot.getEnergy();
		heading = scannedBot.getHeading();
		distance = scannedBot.getDistance();
		bearing = scannedBot.getBearing();
		lastSeen = scannedBot.getTime();
		updatePosition();
	}
	
	/**
	 * Aktualisieren der absoluten Position.
	 * @return Ortsvektor der neuen Position
	 */
	public Vector2D updatePosition() {
		position = Utils.relToAbsPosition(selfBot, this);
		return position;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public double getEnergyDelta() {
		if (lastEnergy < 0)
			return 0;
		
		double delta = getEnergy() - lastEnergy;
		lastEnergy = getEnergy();
		
		return delta;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enemy other = (Enemy) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
}
