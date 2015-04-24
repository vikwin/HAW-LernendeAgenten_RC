package agents;

import robot.LARCRobot;

public class MyAction {

	private final double SHORTDIST = 40.0;
	private final double LONGDIST = Math.sqrt(2 * SHORTDIST * SHORTDIST); // pythagoras für hypothenuse
	private LARCRobot myRobot;
	private SpecificAction mySpecificAction;

	public MyAction(LARCRobot myRobot) {
		this.myRobot = myRobot;
		this.mySpecificAction = SpecificAction.NORDFIRE; // whatever
	}

	// double[distance,angle,fire ]
	public double[] getMoveVector(SpecificAction action) {
		double[] array = new double[4];
		this.mySpecificAction = action;
		double currentHeading = this.myRobot.getHeading();
		double normHeading = this.normalizeDegrees(currentHeading);
		switch (action) {
		case NORDFIRE:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(0 - normHeading);
			return array;
		case NORD:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(0 - normHeading);
			return array;
		case NORDOSTFIRE:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(45 - normHeading);
			return array;
		case NORDOST:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(45 - normHeading);
			return array;
		case OSTFIRE:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(90 - normHeading);
			return array;
		case OST:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(90 - normHeading);
			return array;
		case SUEDOSTFIRE:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(135 - normHeading);
			return array;
		case SUEDOST:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(135 - normHeading);
			return array;
		case SUEDFIRE:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(180 - normHeading);
			return array;
		case SUED:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(180 - normHeading);
			return array;
		case SUEDWESTFIRE:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(-135 - normHeading);
			return array;
		case SUEDWEST:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(-135 - normHeading);
			return array;
		case WESTFIRE:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(-90 - normHeading);
			return array;
		case WEST:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(-90 - normHeading);
			return array;
		case NORWESTFIRE:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = this.normalizeDegrees(-45 - normHeading);
			return array;
		case NORDWEST:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = this.normalizeDegrees(-45 - normHeading);
			return array;

		default:
			return null;
		}
	}

	public SpecificAction getMySpecificAction() {
		return mySpecificAction;
	}

	public void setMySpecificAction(SpecificAction mySpecificAction) {
		this.mySpecificAction = mySpecificAction;
	}

	public double normalizeDegrees(double degrees) {
		while (degrees > 180)
			degrees -= 360;
		while (degrees < -180)
			degrees += 360;
		return degrees;
	}
}
