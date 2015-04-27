package agents;

import robot.LARCRobot;
import utility.Position;

public class MyAction {

	private final double SHORTDIST = 40.0;
	private final double LONGDIST = Math.sqrt(2 * SHORTDIST * SHORTDIST); // pythagoras für hypothenuse
	private LARCRobot myRobot;
	private SpecificAction mySpecificAction;

	public MyAction(LARCRobot myRobot) {
		this.myRobot = myRobot;
		this.mySpecificAction = SpecificAction.NORDFIRE_TURNLEFT; // whatever
	}

	// double[distance,angle,fire ]
	public double[] getMoveVector(SpecificAction action) {
		double[] array = new double[4];
		this.mySpecificAction = action;
		double currentHeading = this.myRobot.getHeading();
		double normHeading = Position.normalizeDegrees(currentHeading);
		switch (action) {

		case NORDFIRE_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[3] = 1;
			return array;
		case NORDFIRE_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[3] = 2;
			return array;
		case NORD_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[3] = 1;
			return array;
		case NORD_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[3] = 2;
			return array;
		case NORDOSTFIRE_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[3] = 1;
			return array;
		case NORDOSTFIRE_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[3] = 2;
			return array;
		case NORDOST_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[3] = 1;
			return array;
		case NORDOST_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[3] = 2;
			return array;
		case OSTFIRE_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[3] = 1;
			return array;
		case OSTFIRE_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[3] = 2;
			return array;
		case OST_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[3] = 1;
			return array;
		case OST_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[3] = 2;
			return array;
		case SUEDOSTFIRE_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[3] = 1;
			return array;
		case SUEDOSTFIRE_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[3] = 2;
			return array;
		case SUEDOST_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[3] = 1;
			return array;
		case SUEDOST_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[3] = 2;
			return array;
		case SUEDFIRE_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[3] = 1;
			return array;
		case SUEDFIRE_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[3] = 2;
			return array;
		case SUED_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[3] = 1;
			return array;
		case SUED_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[3] = 2;
			return array;
		case SUEDWESTFIRE_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[3] = 1;
			return array;
		case SUEDWESTFIRE_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[3] = 2;
			return array;
		case SUEDWEST_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[3] = 1;
			return array;
		case SUEDWEST_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[3] = 2;
			return array;
		case WESTFIRE_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[3] = 1;
			return array;
		case WESTFIRE_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[3] = 2;
			return array;
		case WEST_TURNLEFT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[3] = 1;
			return array;
		case WEST_TURNRIGHT:
			array[0] = this.SHORTDIST;
			array[2] = 0.0;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[3] = 2;
			return array;
		case NORDWESTFIRE_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[3] = 1;
			return array;
		case NORWESTFIRE_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[3] = 2;
			return array;
		case NORDWEST_TURNLEFT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[3] = 1;
			return array;
		case NORDWEST_TURNRIGHT:
			array[0] = this.LONGDIST;
			array[2] = 1.0;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[3] = 2;
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
}
