package agents;

public interface IAgent {

	/**
	 * this method initialized the Agent.
	 * should be called before agent_start(); and env_start();
	 * but after env_init();
	 */
	public void agent_init();

	/**
	 * this method starts the Agent.
	 * should be called after env_start(); and agent_init();
	 * but before agent_step(); and env_step(); 
	 * @return ID of chosen action
	 */
	public int agent_start(int state);

	/**
	 * this method executes a step of the Agent.
	 * should be called after env_start(); and agent_start();
	 * but before agent_end(); and env_step(); 
	 * @return ID of chosen action
	 */
	public int agent_step(int state);

	/**
	 * this method executes the last step of the Agent.
	 * used for terminal learning 
	 * should be called after env_step(); and agent_step();
	 * but before agent_cleanup(); and env_cleanup(); 
	 */
	public void agent_end();

	/**
	 * this method cleans the Agent.
	 * can be used to release used memory.
	 * should be called after env_cleanup(); and agent_end();
	 */
	public void agent_cleanup();
}
