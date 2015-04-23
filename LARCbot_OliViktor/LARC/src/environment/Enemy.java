package environment;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import utils.Utils;
import utils.Vector2D;

/**
 * 	Die Klasse Enemy kapselt alle Informationen, die man über einen Gegner erhalten kann.
 * @author Viktor Winkelmann
 *
 */
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
	
	/**
	 * Liefert den eindeutigen Namen eines Gegners. Eindeutig heißt hier,
	 * dass in einem Match nie zwei Gegner den gleichen Namen haben. 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den eindeutigen Namen eines Gegners. Eindeutig heißt hier,
	 * dass in einem Match nie zwei Gegner den gleichen Namen haben. 
	 * @param name Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Liefert einen absoluten Ortsvektor des Gegners. 
	 * @return Ortsvektor
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * Setzt einen absoluten Ortsvektor des Gegners. 
	 * @param position Ortsvektor
	 */
	public void setPosition(Vector2D position) {
		this.position = position;
	}

	/**
	 * Liefert die Energie des Gegners.
	 * @return Energie
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Setzt die Energie des Gegners.
	 * @param energy Energie
	 */
	public void setEnergy(double energy) {
		this.energy = energy;
	}

	/**
	 * Liefert die Differenz der Energie des Gegners seit dem letzten Aufruf
	 * von getEnergyDelta().
	 * @return Differenz
	 */
	public double getEnergyDelta() {
		if (lastEnergy < 0)
			return 0;
		
		double delta = getEnergy() - lastEnergy;
		lastEnergy = getEnergy();
		
		return delta;
	}

	/**
	 * Liefert die Ausrichtung des Gegners im Bereich 0 bis 360 Grad.
	 * @return Richtung
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Setzt die Ausrichtung des Gegners im Bereich 0 bis 360 Grad.
	 * @return Richtung
	 */
	
	public void setHeading(double heading) {
		this.heading = heading % 360;
	}

	/**
	 * Liefert die Geschwindigkeit des Gegners.
	 * @return Geschwindigkeit
	 */
	public double getVelocity() {
		return velocity;
	}

	/**
	 * Setzt die Geschwindigkeit des Gegners.
	 * @param velocity Geschwindigkeit
	 */
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	/**
	 * Liefert die Distanz zum Gegner.
	 * @return Distanz
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Setzt die Distanz zum Gegner.
	 * @param distance Distanz
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * Liefert die Richtung in der sich der Gegner befindet, relativ zur eigenen Ausrichtung
	 * im Bereich -180 bis +180 Grad.
	 * @return Richtung
	 */
	public double getBearing() {
		return bearing;
	}

	/**
	 * Setzt die Richtung in der sich der Gegner befindet, relativ zur eigenen Ausrichtung
	 * im Bereich -180 bis +180 Grad.
	 * @param bearing Richtung
	 */
	
	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	/**
	 * Liefert die Zeit, zu der der Gegner zuletzt gesehen wurde.
	 * @return Zeit
	 */
	public long getLastSeen() {
		return lastSeen;
	}

	/**
	 * Setzt die Zeit, zu der der Gegner zuletzt gesehen wurde.
	 * @param lastSeen Zeit
	 */
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
