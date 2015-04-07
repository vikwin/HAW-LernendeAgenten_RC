package environment;

import utils.Vector2D;

public class Enemy {
	
	private String name;
	private Vector2D position;
	
	public Enemy(String name, Vector2D position) {
		this.name = name;
		this.position = position;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPosition(Vector2D position) {
		this.position = position;
	}
	
	public Vector2D getPosition(){
		return this.position;
	}
	
	public double getX(){
		return this.position.getX();
	}

	public double getY(){
		return this.position.getY();
	}
}
