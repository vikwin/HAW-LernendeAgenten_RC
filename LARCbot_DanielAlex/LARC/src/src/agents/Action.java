package src.agents;

import src.robot.LARCRobot;

public class MyAction {

	private final double SHORTDIST = 40.0;
	private final double LONGDIST = Math.sqrt(2 * SHORTDIST * SHORTDIST); // pythagoras für hypothenuse
	private LARCRobot myRobot;

	public MyAction(LARCRobot myRobot) {
		this.myRobot = myRobot;
	}

	// double[distance,angle,fire ]
	public double[] getMoveVector(SpecificAction action) {
 double[] array = new double[3];
		switch (action) {
case NORDFIRE: 
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(0.0 - this.myRobot.getHeading());
	array[2] = 1.0;
	return array;
case NORD: 
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(0.0 - this.myRobot.getHeading()); 
	array[2] = 0.0;
	return array;
case NORDOSTFIRE:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(45.0 - this.myRobot.getHeading());  
	array[2] = 1.0;
	return array;
case NORDOST:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(45.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;
case OSTFIRE: 
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(90.0 - this.myRobot.getHeading()); ; 
	array[2] = 1.0;
	return array;
case OST:
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(90.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;
case SUEDOSTFIRE:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(135.0 - this.myRobot.getHeading()); ; 
	array[2] = 1.0;
	return array;
case SUEDOST:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(135.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;
case SUEDFIRE: 
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(180.0 - this.myRobot.getHeading()); ; 
	array[2] = 1.0;
	return array;
case SUED:
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(180.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;
case SUEDWESTFIRE:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(225.0 - this.myRobot.getHeading()); ; 
	array[2] = 1.0;
	return array;
case SUEDWEST:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(225.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;
case WESTFIRE:
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(270.0 - this.myRobot.getHeading()); ; 
	array[2] = 1.0;
	return array;
case WEST:
	array[0] = this.SHORTDIST; 
	array[1] = Math.abs(270.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;
case NORWESTFIRE:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(315.0 - this.myRobot.getHeading()); ; 
	array[2] = 1.0;
	return array;
case NORDWEST:
	array[0] = this.LONGDIST; 
	array[1] = Math.abs(315.0 - this.myRobot.getHeading()); ; 
	array[2] = 0.0;
	return array;


default:
	return null;
}
	}
}
