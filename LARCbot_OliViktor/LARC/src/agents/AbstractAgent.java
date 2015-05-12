package agents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utils.Config;

public abstract class AbstractAgent {
	private static final double LEARN_RATE = Config
			.getIntValue("Agent_LearnRate") / 100.0;
	private static final double DISCOUNT_RATE = Config
			.getIntValue("Agent_DiscountRate") / 100.0;
	private static final double LAMBDA = Config.getIntValue("Agent_Lambda") / 100.0;
	private static final int QUEUE_SIZE = Config.getIntValue("Agent_QueueSize");
	 private static final double REWARD_CAP = 50;

	protected static final int SAVE_TIMES = Config
			.getIntValue("Agent_SaveTimes");
	protected static final String TIMESTAMP = new SimpleDateFormat(
			"dd_MM_yyyy_HH_mm_ss").format(new Date()), ENEMY;
	protected static final boolean LOAD_ON_START = Config
			.getBoolValue("Agent_LoadOnStart");

	protected AgentMode mode;

	private ActionQueue lastActionQueue;
	
	private HashMap<Integer, Double> eValues;

	static {
		if (Config.getBoolValue("StartBattle"))
			ENEMY = "_" + Config.getStringValue("EnemyRobot");
		else
			ENEMY = "";

		new File("LARCAgents/" + TIMESTAMP).mkdirs();
	}

	protected AbstractAgent() {
		mode = AgentMode.LEARNING;

		eValues = new HashMap<Integer, Double>();
		lastActionQueue = new ActionQueue(QUEUE_SIZE);
	}

	public void setMode(AgentMode newMode) {
		mode = newMode;
	}

	protected abstract Double[] getActionList();
	protected abstract int getStateFromId(int id);

	protected static void fillActionList(Double[] values) {

	}

	/**
	 * Speichert gelernte Informationen des Bots in einer Datei ab.
	 * 
	 * @param filename
	 *            Der Dateiname
	 * @throws IOException
	 *             Wenn die angebene Datei nicht vorhanden ist oder nicht
	 *             zugreifbar
	 */
	public void save(final String directory, final String filename) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					ZipOutputStream zos = new ZipOutputStream(
							new FileOutputStream("LARCAgents\\" + directory
									+ "\\" + filename + ".zip"));
					ZipEntry entry = new ZipEntry(filename + ".json");

					zos.putNextEntry(entry);

					StringBuilder actionListString = new StringBuilder("["); // JSONValue.toJSONString(actionList);
					Double[] actionList = getActionList();
					for (double d : actionList) {
						actionListString.append(d + ",");
					}
					actionListString.delete(actionListString.length() - 1,
							actionListString.length());
					actionListString.append("]");

					zos.write(actionListString.toString().getBytes());
					zos.close();
				} catch (IOException e) {
					System.err.printf(
							"### Fehler beim Speichern des Agents: %s ###\n",
							e.getMessage());
				}
			}
		});

		t.start();
	}

	@SuppressWarnings("unchecked")
	public static void load(String directory, String filename)
			throws IOException, ParseException {
		File f = new File("LARCAgents\\" + directory + "\\" + filename + ".zip");
		if (f.exists() && f.isFile()) {
			ZipFile zf = new ZipFile("LARCAgents\\" + directory + "\\"
					+ filename + ".zip");
			ZipEntry ze = zf.getEntry(filename + ".json");

			InputStreamReader reader = new InputStreamReader(
					zf.getInputStream(ze));
			JSONParser parser = new JSONParser();

			List<Double> obj = (List<Double>) ((JSONArray) parser.parse(reader));
			fillActionList(obj.toArray(new Double[0]));

			reader.close();
			zf.close();
		}
	}

	protected void addToLastActionQueue(int id) {
		if (!lastActionQueue.offer(id)) {
			int polledID = lastActionQueue.poll();
			if (!lastActionQueue.contains(polledID))
				eValues.remove(polledID);
			
			lastActionQueue.offer(id);
		}
		
		if (!eValues.containsKey(id))
			eValues.put(id, 0.0);
	}
	
	private double e(int sa) {
		return eValues.get(sa);
	}
	
	private void setE(int sa, double value) {
		if (eValues.containsKey(sa))
			eValues.put(sa, value);
	}
	
	private void sarsa_lambda(double reward, double alpha, double gamma, double lambda) {
		double delta;
		int sa, sa_;
		boolean end = false;
		
		if (lastActionQueue.size() <= 2)
			return;

		// Debug Ausgaben
		System.out.println(lastActionQueue.toString());
		System.out.println(eValues.toString());
		
		Iterator<Integer> it = lastActionQueue.iterator();
		Double[] Q = getActionList();
		
		sa_ = it.next();
		sa = it.next();
		
		delta = reward + gamma * Q[sa_] - Q[sa];
		setE(sa, e(sa) + 1);
		
		while (!end) {
			Q[sa] = Math.max(Math.min(Q[sa] + alpha * delta * e(sa), REWARD_CAP), -REWARD_CAP);
			
			// replace traces
			if (sa == sa_)
				setE(sa, 1 + gamma * lambda * e(sa));
			else if(getStateFromId(sa) == getStateFromId(sa_))
				setE(sa, 0);
			else
				setE(sa, gamma * lambda * e(sa));
				
			// Debug Ausgaben
			System.out.printf("sa: %d, Q: %f, e: %f, delta: %f\n", sa, Q[sa], e(sa), delta);
			
			if (it.hasNext())
				sa = it.next();
			else
				end = true;
		};
		
		System.out.println();
	}

	protected void addRewardToLastActions(double reward) {
		sarsa_lambda(reward, LEARN_RATE, DISCOUNT_RATE, LAMBDA);
	}

	/**
	 * Gibt die nächste Aktion an
	 * 
	 * @param stateID
	 *            Die ID des aktuellen Zustands der Umwelt
	 * @return Die ID der nächsten Aktion
	 */
	public abstract int getNextAction(int stateID);

	public abstract void addReward(double reward);

	public abstract void saveOnBattleEnd();
}
