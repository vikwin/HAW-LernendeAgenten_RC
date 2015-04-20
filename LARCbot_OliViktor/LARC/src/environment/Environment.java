package environment;

import java.awt.Graphics2D;
import java.util.Collection;

import robocode.AdvancedRobot;

/**
 * Das Interface für Umwelt Klassen
 * @author Viktor Winkelmann
 *
 */
public interface Environment {
	
	/**
	 * Aktualisiert die Umwelt anhand der übergebenen Werte.
	 * 
	 * @param enemies Eine Sammlung der Gegnerobjekte
	 * @param selfBot Der eigene Bot
	 */
	public void update(Collection<Enemy> enemies, AdvancedRobot selfBot);
	
	
	/**
	 * Zeichnet die Umwelt grafisch ein, falls painting aktiviert.
	 * 
	 * @param g
	 */
	public void doPaint(Graphics2D g);
	
	
	/**
	 * Liefert eine für jede Umweltkonstellation eindeutige ID. Alle möglichen IDs
	 * stellen eine aufsteigende vollständige Ordnung beginnend bei 0 dar. 
	 * @return Die ID
	 */
	public int getId();


	/**
	 * Liefert die Gesamtzahl der möglichen Umweltzustände.
	 * @return Anzahl
	 */
	public int getStateCount();
	

}
