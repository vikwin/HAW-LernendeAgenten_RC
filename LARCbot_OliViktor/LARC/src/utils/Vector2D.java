package utils;

import java.util.Locale;

/**
 * Die Klasse Vector2D stellt eine Hilfsklasse zur Berechnung von Koordinaten dar. In erster Linie ist die Klasse zur
 * Nutzung als Ortsvektor gedacht, eine Nutzung als Richtungsvektor ist aber ebenfalls möglich.
 * Alle Methoden arbeiten nicht-destruktiv, um Fehlern bei der Nutzung vorzubeugen. 
 * @author Viktor Winkelmann
 *
 */
public class Vector2D {
	
	private static final double MAX_DEVIATION = 0.00001; // Maximale Koordinaten Abweichung für Objektgleichheit
	
	private double x,y;
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Liefert den X-Koordinaten Anteil.
	 * @return X-Koordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Liefert den Y-Koordinaten Anteil.
	 * @return Y-Koordinate
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Addiert einen Vektor.
	 * @param other Zu addierender Vektor
	 * @return Neuer Vektor
	 */
	public Vector2D add(Vector2D other)	{
		return new Vector2D(x + other.x, y + other.y);
	}
	
	/**
	 * Subtrahiert einen Vektor.
	 * @param other Zu subtrahierender Vektor
	 * @return Neuer Vektor
	 */
	public Vector2D subtract(Vector2D other) {
		return new Vector2D(x - other.x, y - other.y);
	}
	
	/**
	 * Multipliziert den Vektor mit einem Skalar.
	 * @param scalar Skalar
	 * @return Neuer Vektor
	 */
	public Vector2D multiply(double scalar)	{
		return new Vector2D(x * scalar, y * scalar);
	}
	
	/**
	 * Dividiert den Vektor durch einen Skalar.
	 * @param scalar Skalar
	 * @return Neuer Vektor
	 */
	public Vector2D divide(double scalar)	{
		return new Vector2D(x / scalar, y / scalar);
	}
	
	/**
	 * Berechnet die Länge des Vektors.
	 * @return Vektorlänge
	 */
	public double length(){
		return Math.sqrt(x*x +y*y);
	}
	
	/**
	 * Berechnet die Distanz zu einem anderen Vektor.
	 * @param other Anderer Vektor
	 * @return Distanz
	 */
	public double distanceTo(Vector2D other) {
		return other.subtract(this).length();
	}
	
	/**
	 * Dreht den Vektor um einen Winkel in Grad.
	 * Die Drehung erfolgt um den Vektorursprung im mathematisch
	 * positiver Richtung (gegen den Uhrzeigesinn).
	 * @param angleDegrees Winkel in Grad
	 * @return Neuer Vektor
	 */
	public Vector2D rotate(double angleDegrees) {
		return rotateRad(angleDegrees * Math.PI / 180);
	}

	/**
	 * Dreht den Vektor um einen Winkel im Bogenmaß.
	 * Die Drehung erfolgt um den Vektorursprung im mathematisch
	 * negativer Richtung (im Uhrzeigesinn).
	 * @param angleRadians Winkel in Rad
	 * @return Neuer Vektor
	 */
	public Vector2D rotateRad(double angleRadians) {
		angleRadians = angleRadians * -1;
		return new Vector2D(x * Math.cos(angleRadians) - y * Math.sin(angleRadians), x * Math.sin(angleRadians) + y * Math.cos(angleRadians));
	}
	
	/**
	 * Liefert eine String Darstellung der Vektorkoordinaten. 
	 */
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "X: %1$,.3f, Y: %2$,.3f", x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Vector2D other = (Vector2D) obj;
		if (Math.abs(x - other.x) > MAX_DEVIATION)
			return false;
		if (Math.abs(y - other.y) > MAX_DEVIATION)
			return false;
		return true;
	}
	
 
}

