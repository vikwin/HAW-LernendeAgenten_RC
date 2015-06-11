package utils;

/**
 * Utility Klasse speziell für Umsetzung von Wave Surfing.
 * 
 * @author Viktor Winkelmann
 *
 */
public abstract class WaveSurf {

	/**
	 * Berechnet die ungefähre Fluggeschwindigkeit einer Kugel
	 * anhand des beobachteten Energieverlusts beim Abfeuern. 
	 * @param energyImpact Energieverlust
	 * @return Tempo
	 */
	public static double bulletVelocity(double energyImpact) {
        return 20.0 - 3.0 * energyImpact;
    }
 
	
	
    public static double maxEscapeAngle(double velocity) {
        return Math.asin(8.0/velocity);
    }
    
    
    public static double limit(double min, double value, double max) {
        return Math.max(min, Math.min(value, max));
    }

}
