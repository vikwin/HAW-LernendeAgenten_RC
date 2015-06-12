package environment;

import utils.Vector2D;

public class EnemyWave {
	public Vector2D fireLocation;
	public long fireTime;
	public double bulletVelocity, directAngle, distanceTraveled;
	public int direction;
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bulletVelocity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(directAngle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + direction;
		temp = Double.doubleToLongBits(distanceTraveled);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((fireLocation == null) ? 0 : fireLocation.hashCode());
		result = prime * result + (int) (fireTime ^ (fireTime >>> 32));
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
		EnemyWave other = (EnemyWave) obj;
		if (Double.doubleToLongBits(bulletVelocity) != Double
				.doubleToLongBits(other.bulletVelocity))
			return false;
		if (Double.doubleToLongBits(directAngle) != Double
				.doubleToLongBits(other.directAngle))
			return false;
		if (direction != other.direction)
			return false;
		if (Double.doubleToLongBits(distanceTraveled) != Double
				.doubleToLongBits(other.distanceTraveled))
			return false;
		if (fireLocation == null) {
			if (other.fireLocation != null)
				return false;
		} else if (!fireLocation.equals(other.fireLocation))
			return false;
		if (fireTime != other.fireTime)
			return false;
		return true;
	}
}
