package environment;

enum Direction {
	NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

	/**
	 * Gibt eine Himmelsrichtung anhand einer übergebenen Blickrichtung
	 * zurück. Die Blickrichtung darf zwischen 0 und 360 Grad liegen.
	 * 
	 * @param heading
	 *            Blickrichtung
	 * @return Himmelsrichtung
	 */
	public static Direction byHeading(double heading) {
		heading %= 360;

		if (heading < 22.5 || heading >= 337.5)
			return Direction.NORTH;
		else if (heading < 67.5)
			return Direction.NORTHEAST;
		else if (heading < 112.5)
			return Direction.EAST;
		else if (heading < 157.5)
			return Direction.SOUTHEAST;
		else if (heading < 202.5)
			return Direction.SOUTH;
		else if (heading < 247.5)
			return Direction.SOUTHWEST;
		else if (heading < 292.5)
			return Direction.WEST;
		else
			return Direction.NORTHWEST;
	}
	
	public double getHeading() {
		return ordinal() * 45;
	}
}