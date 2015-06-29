package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import utils.Utils;
import utils.Vector2D;
import utils.WaveSurf;

/**
 * Die Klasse Enemy kapselt alle Informationen, die man über einen Gegner
 * erhalten kann.
 * 
 * @author Viktor Winkelmann
 *
 */
public class Enemy {

	private static final boolean PAINT = false;
	private AdvancedRobot selfBot;
	private String name;
	private Vector2D position;
	private double energy, lastEnergy = -1, heading, velocity, distance,
			bearing;
	private long lastSeen;

	// Wave Surfing Attribute
	private static HashMap<String, double[]> globalSurfStats = new HashMap<>();
	private double[] surfStats;
	private ArrayList<EnemyWave> waves;
	private ArrayList<Integer> surfDirections;
	private ArrayList<Double> surfAbsBearings;

	public Enemy(ScannedRobotEvent scannedBot, AdvancedRobot selfBot) {
		this(scannedBot.getName(), selfBot);
		updateAllAttributes(scannedBot);

	}

	public Enemy(String name, AdvancedRobot selfBot) {
		this.name = name;
		this.selfBot = selfBot;
		
		if (!globalSurfStats.containsKey(name))
			globalSurfStats.put(name, new double[WaveSurf.BINS]);
		
		surfStats = globalSurfStats.get(name);
		waves = new ArrayList<>();
		surfDirections = new ArrayList<>();
		surfAbsBearings = new ArrayList<>();
	}

	/**
	 * Schnelles aktualisieren aller Attribute des Enemy's ohne Rückinfo.
	 * 
	 * @param scannedBot
	 *            Der gescannte Bot
	 */
	public void updateAllAttributes(ScannedRobotEvent scannedBot) {
		heading = scannedBot.getHeading();
		distance = scannedBot.getDistance();
		bearing = scannedBot.getBearing();
		lastSeen = scannedBot.getTime();

		addWaves(scannedBot);
		// Position und Energie müssen nach dem Hinzufügen der Waves
		// aktualisiert werden
		updatePosition();
		energy = scannedBot.getEnergy();

		updateWaves();
	}

	/**
	 * Prüft ob der Gegner seit dem letzten Tick möglicherweise eine Kugel
	 * abgefeuert hat und fügt ggfs. eine Wave hinzu.
	 * 
	 * @param scannedBot
	 */
	private void addWaves(ScannedRobotEvent scannedBot) {

		double lateralVelocity = selfBot.getVelocity()
				* Math.sin(scannedBot.getBearingRadians());
		double absBearing = scannedBot.getBearingRadians()
				+ selfBot.getHeadingRadians();
		double bulletPower = energy - scannedBot.getEnergy();

		surfDirections.add(0, lateralVelocity >= 0 ? 1 : -1);
		surfAbsBearings.add(0, absBearing + Math.PI);

		if (bulletPower < 3.01 && bulletPower > 0.09
				&& surfDirections.size() > 2) {
			EnemyWave ew = new EnemyWave();
			ew.fireTime = selfBot.getTime() - 1;
			ew.bulletVelocity = WaveSurf.bulletVelocity(bulletPower);
			ew.distanceTraveled = WaveSurf.bulletVelocity(bulletPower);
			ew.direction = surfDirections.get(2);
			ew.directAngle = surfAbsBearings.get(2);
			ew.fireLocation = new Vector2D(position.getX(), position.getY()); // Position
																				// im
																				// letzten
																				// Tick

			waves.add(ew);
		}
	}

	/**
	 * Aktualisiert bestehende Waves und entfernt hinfällige.
	 */
	private void updateWaves() {
		for (int x = 0; x < waves.size(); x++) {
			EnemyWave ew = waves.get(x);

			ew.distanceTraveled = (selfBot.getTime() - ew.fireTime)
					* ew.bulletVelocity;
			if (ew.distanceTraveled > Utils.getBotCoordinates(selfBot)
					.distanceTo(ew.fireLocation) + 50) {
				waves.remove(x);
				x--;
			}
		}
	}

	/**
	 * Gibt die nächstgelegene surfbare Wave zurück. Kann null zurück geben,
	 * falls keine Wave vorhanden ist.
	 * 
	 * @return Wave
	 */
	public EnemyWave getClosestSurfableWave() {
		double closestDistance = Double.MAX_VALUE;
		EnemyWave surfWave = null;

		for (EnemyWave ew : waves) {
			double distance = Utils.getBotCoordinates(selfBot).distanceTo(
					ew.fireLocation)
					- ew.distanceTraveled;

			if (distance > ew.bulletVelocity && distance < closestDistance) {
				surfWave = ew;
				closestDistance = distance;
			}
		}

		return surfWave;
	}

	
	/**
	 * Aktualisiert die Statistiken über das Verhalten des Gegners.
	 * 
	 * @param ew
	 *            Die EnemyWave, von der wir getroffen wurden
	 * @param targetLocation
	 *            Der Ort, an dem wir getroffen wurden
	 */
	public void logHit(EnemyWave ew, Vector2D targetLocation) {
		int index = WaveSurf.guessfactorIndex(ew, targetLocation);

		// Hier wird eine gaußkurvenartige Aktualisierung der Statistiken
		// erreicht
		for (int x = 0; x < WaveSurf.BINS; x++) {
			surfStats[x] += 1.0 / (Math.pow(index - x, 2) + 1);
		}
	}	

	/**
	 * Aktualisiert die Statistiken über das Verhalten des Gegners.
	 * 
	 * @param event
	 */
	public void updateWavesByBulletHit(HitByBulletEvent e) {
		if (waves.isEmpty())
			return;

		Vector2D hitBulletLocation = new Vector2D(e.getBullet().getX(), e
				.getBullet().getY());
		EnemyWave hitWave = null;

		// finde heraus welche Wave und getroffen haben könnte
		for (EnemyWave ew : waves) {
			if (Math.abs(ew.distanceTraveled
					- Utils.getBotCoordinates(selfBot).distanceTo(ew.fireLocation)) < 50
					&& Math.abs(WaveSurf.bulletVelocity(e.getBullet().getPower())
							- ew.bulletVelocity) < 0.001) {
				hitWave = ew;
				break;
			}
		}

		if (hitWave != null) {
			logHit(hitWave, hitBulletLocation);
			waves.remove(hitWave);
		}
	}
	
	/**
	 * Gibt den Gefahrenwert für eine angegebene Richtung und Wave zurück.
	 * Setzt orbitale Bewegung vorraus.
	 * 
	 * @param surfWave Die zu surfende Wave
	 * @param direction Die Richtung: -1 = entegen Uhrzeigesinn, +1 = im Uhrzeigesinn
	 * @return
	 */
	public double checkDanger(EnemyWave surfWave, int direction) {
		if (surfWave == null)
			return 0;
        int index = WaveSurf.guessfactorIndex(surfWave,
            WaveSurf.predictPosition(selfBot, surfWave, direction));
 
        return surfStats[index];
    }
	
	/**
	 * Gibt den normalisierten Gefahrenwert für eine angegebene Richtung und Wave zurück.
	 * Setzt orbitale Bewegung vorraus.
	 * 
	 * @param surfWave Die zu surfende Wave
	 * @param direction Die Richtung: -1 = entegen Uhrzeigesinn, +1 = im Uhrzeigesinn
	 * @return Normalisierte Gefahr zwischen 0 und 1
	 */
	public double checkNormalizedDanger(EnemyWave surfWave, int direction) {
		double max = 0.0;
		// finde maximales Element
		for (double e : surfStats) {
			max = Math.max(max, e);
		}
		
		if (max == 0.0)
			return 0;
		
		return checkDanger(surfWave, direction) / max;
	}

	/**
	 * Aktualisieren der absoluten Position.
	 * 
	 * @return Ortsvektor der neuen Position
	 */
	public Vector2D updatePosition() {
		position = Utils.relToAbsPosition(selfBot, this);
		return position;
	}

	/**
	 * Liefert den eindeutigen Namen eines Gegners. Eindeutig heißt hier, dass
	 * in einem Match nie zwei Gegner den gleichen Namen haben.
	 * 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den eindeutigen Namen eines Gegners. Eindeutig heißt hier, dass in
	 * einem Match nie zwei Gegner den gleichen Namen haben.
	 * 
	 * @param name
	 *            Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Liefert einen absoluten Ortsvektor des Gegners.
	 * 
	 * @return Ortsvektor
	 */
	public Vector2D getPosition() {
		return position;
	}

	/**
	 * Setzt einen absoluten Ortsvektor des Gegners.
	 * 
	 * @param position
	 *            Ortsvektor
	 */
	public void setPosition(Vector2D position) {
		this.position = position;
	}

	/**
	 * Liefert die Energie des Gegners.
	 * 
	 * @return Energie
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Setzt die Energie des Gegners.
	 * 
	 * @param energy
	 *            Energie
	 */
	public void setEnergy(double energy) {
		this.energy = energy;
	}

	/**
	 * Liefert die Differenz der Energie des Gegners seit dem letzten Aufruf von
	 * getEnergyDelta().
	 * 
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
	 * 
	 * @return Richtung
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Setzt die Ausrichtung des Gegners im Bereich 0 bis 360 Grad.
	 * 
	 * @return Richtung
	 */

	public void setHeading(double heading) {
		this.heading = heading % 360;
	}

	/**
	 * Liefert die Geschwindigkeit des Gegners.
	 * 
	 * @return Geschwindigkeit
	 */
	public double getVelocity() {
		return velocity;
	}

	/**
	 * Setzt die Geschwindigkeit des Gegners.
	 * 
	 * @param velocity
	 *            Geschwindigkeit
	 */
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	/**
	 * Liefert die Distanz zum Gegner.
	 * 
	 * @return Distanz
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Setzt die Distanz zum Gegner.
	 * 
	 * @param distance
	 *            Distanz
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * Liefert die Richtung in der sich der Gegner befindet, relativ zur eigenen
	 * Ausrichtung im Bereich -180 bis +180 Grad.
	 * 
	 * @return Richtung
	 */
	public double getBearing() {
		return bearing;
	}

	/**
	 * Setzt die Richtung in der sich der Gegner befindet, relativ zur eigenen
	 * Ausrichtung im Bereich -180 bis +180 Grad.
	 * 
	 * @param bearing
	 *            Richtung
	 */

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	/**
	 * Liefert die Zeit, zu der der Gegner zuletzt gesehen wurde.
	 * 
	 * @return Zeit
	 */
	public long getLastSeen() {
		return lastSeen;
	}

	/**
	 * Setzt die Zeit, zu der der Gegner zuletzt gesehen wurde.
	 * 
	 * @param lastSeen
	 *            Zeit
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

	public void doPaint(Graphics2D g) {
		if (!PAINT)
			return;
		
		// Zeichne Waves in blau ein
		g.setColor(new Color(0x00, 0x00, 0xff, 0x80));
		double circleSize;
		
		for (EnemyWave ew : waves) {
			circleSize = ew.distanceTraveled * 2;
			g.drawArc((int) (ew.fireLocation.getX() - circleSize / 2),
					(int) (ew.fireLocation.getY() - circleSize / 2),
					(int) circleSize, (int) circleSize, 0, 360);
		}

	}
}
