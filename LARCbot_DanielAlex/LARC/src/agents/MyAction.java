package agents;

import robot.LARCRobot;
import utility.Position;

public class MyAction {

	private final double TURN_STEP = 3.0;
	private final double SHORTDIST = 40.0;
	private final double LONGDIST = Math.sqrt(2 * SHORTDIST * SHORTDIST); // pythagoras für hypothenuse
	private LARCRobot myRobot;
	private SpecificAction mySpecificAction;

	public MyAction(LARCRobot myRobot) {
		this.myRobot = myRobot;
		this.mySpecificAction = SpecificAction.NORD; // whatever
	}

	// double[distanceToMove,angleToTurn,fire?,gun-turn-direction]
	public double[] getMoveVector(SpecificAction action) {
		double[] array = new double[4];
		this.mySpecificAction = action;
		double currentHeading = this.myRobot.getHeading();
		double normHeading = Position.normalizeDegrees(currentHeading);
		switch (action) {
		case NORDFIRE:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case NORD:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case NORDFIRE_TURNLEFT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case NORDFIRE_TURNLEFT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case NORDFIRE_TURNLEFT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case NORDFIRE_TURNLEFT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case NORDFIRE_TURNRIGHT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case NORDFIRE_TURNRIGHT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case NORDFIRE_TURNRIGHT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case NORDFIRE_TURNRIGHT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(0 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case NORDOSTFIRE:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case NORDOST:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case NORDOSTFIRE_TURNLEFT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case NORDOSTFIRE_TURNLEFT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case NORDOSTFIRE_TURNLEFT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case NORDOSTFIRE_TURNLEFT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case NORDOSTFIRE_TURNRIGHT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
		case NORDOSTFIRE_TURNRIGHT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
		case NORDOSTFIRE_TURNRIGHT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
		case NORDOSTFIRE_TURNRIGHT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(45 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case OSTFIRE:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case OST:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case OSTFIRE_TURNLEFT1:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case OSTFIRE_TURNLEFT2:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case OSTFIRE_TURNLEFT3:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case OSTFIRE_TURNLEFT4:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case OSTFIRE_TURNRIGHT1:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case OSTFIRE_TURNRIGHT2:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case OSTFIRE_TURNRIGHT3:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case OSTFIRE_TURNRIGHT4:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(90 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case SUEDOSTFIRE:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case SUEDOST:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case SUEDOSTFIRE_TURNLEFT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNLEFT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNLEFT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNLEFT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNRIGHT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNRIGHT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNRIGHT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case SUEDOSTFIRE_TURNRIGHT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(135 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case SUEDFIRE:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case SUED:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case SUEDFIRE_TURNLEFT1:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNLEFT2:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNLEFT3:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNLEFT4:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNRIGHT1:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNRIGHT2:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNRIGHT3:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case SUEDFIRE_TURNRIGHT4:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(180 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case SUEDWESTFIRE:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case SUEDWEST:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case SUEDWESTFIRE_TURNLEFT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNLEFT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNLEFT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNLEFT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNRIGHT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNRIGHT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNRIGHT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case SUEDWESTFIRE_TURNRIGHT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-135 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case WESTFIRE:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case WEST:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case WESTFIRE_TURNLEFT1:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case WESTFIRE_TURNLEFT2:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case WESTFIRE_TURNLEFT3:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case WESTFIRE_TURNLEFT4:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case WESTFIRE_TURNRIGHT1:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case WESTFIRE_TURNRIGHT2:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case WESTFIRE_TURNRIGHT3:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case WESTFIRE_TURNRIGHT4:
			array[0] = this.SHORTDIST;
			array[1] = Position.normalizeDegrees(-90 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
			return array;
		case NORDWESTFIRE:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = 0.0;
			return array;
		case NORWEST:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 0.0;

			array[3] = 0.0;
			return array;
		case NORDWESTFIRE_TURNLEFT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = 1 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNLEFT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = 2 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNLEFT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = 3 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNLEFT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = 4 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNRIGHT1:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = -1 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNRIGHT2:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = -2 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNRIGHT3:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = -3 * TURN_STEP;
			return array;
		case NORDWESTFIRE_TURNRIGHT4:
			array[0] = this.LONGDIST;
			array[1] = Position.normalizeDegrees(-45 - normHeading);
			array[2] = 1.0;

			array[3] = -4 * TURN_STEP;
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
