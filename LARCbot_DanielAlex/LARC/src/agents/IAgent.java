package agents;

public interface IAgent {

	public void agent_init();

	public int agent_start(int state);

	public int agent_step(int state);

	public void agent_end();

	public void agent_cleanup();
}
