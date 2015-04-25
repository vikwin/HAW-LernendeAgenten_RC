package environment;

public interface IEnvironment {

	/**
	 * this method initialized the Environment.
	 * should be called before agent_start(); and env_start();
	 */
	public void env_init();

	/**
	 * this method starts the Environment.
	 * should be called after env_init(); and agent_init();
	 * but before agent_start(); and env_step(); 
	 * @return
	 */
	public int env_start();

	/**
	 * this method executes a step of the Environment.
	 * should be called after agent_start(); and agent_start();
	 * but before agent_end(); and env_cleanup();
	 * @param action
	 * @return
	 */
	public int env_step(int action);

	/**
	 * this method cleans the Environment.
	 * can be used to release used memory.
	 * should be called after env_step(); and agent_end();
	 * but before agent_cleanup();
	 */
	public void env_cleanup();
}