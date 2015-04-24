package environment;

public interface IEnvironment {

	public void env_init();

	public int env_start();

	public int env_step(int action);

	public void env_cleanup();
}