package robot;

public abstract class Action {
	private int id;

	public Action(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}
}
