package agents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
	// private static final double REWARD_CAP = 5;

	protected static final int SAVE_TIMES = Config
			.getIntValue("Agent_SaveTimes");
	protected static final String TIMESTAMP = new SimpleDateFormat(
			"dd_MM_yyyy_HH_mm_ss").format(new Date()), ENEMY;
	protected static final boolean LOAD_ON_START = Config
			.getBoolValue("Agent_LoadOnStart");

	protected AgentMode mode;

	private int[] lastActionQueue;
	private int queueEndIndex;

	static {
		if (Config.getBoolValue("StartBattle"))
			ENEMY = "_" + Config.getStringValue("EnemyRobot");
		else
			ENEMY = "";

		new File("LARCAgents/" + TIMESTAMP).mkdirs();
	}

	protected AbstractAgent() {
		mode = AgentMode.LEARNING;

		lastActionQueue = new int[QUEUE_SIZE];
		Arrays.fill(lastActionQueue, -1);
		queueEndIndex = 0;
	}

	public void setMode(AgentMode newMode) {
		mode = newMode;
	}

	protected abstract Double[] getActionList();
	protected abstract double[] getEArray();
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
		lastActionQueue[queueEndIndex] = id;
		queueEndIndex = (queueEndIndex + 1) % QUEUE_SIZE;
	}

	protected void addRewardToLastActions(double reward) {
		int n = (queueEndIndex - 1 + QUEUE_SIZE) % QUEUE_SIZE, i = (n - 1 + QUEUE_SIZE) % QUEUE_SIZE;
		double delta, val;
		
		if (lastActionQueue[n] < 0 || lastActionQueue[i] < 0)
			return;

		getEArray()[lastActionQueue[i]] += 1;
		delta = reward + DISCOUNT_RATE * getActionList()[lastActionQueue[n]] - getActionList()[lastActionQueue[i]];
		
		if (Double.isInfinite(delta))
			System.out.printf("FEHLER delta ist Infinity: reward: %s, discount: %s, n: %d, actionListIndex: %d, actionListValue: %s\n", Double.toString(reward), Double.toString(DISCOUNT_RATE), n, lastActionQueue[n], Double.toString(getActionList()[lastActionQueue[n]]));
		else
			System.out.println("Delta: " + Double.toString(delta));
			
		do {

			if (lastActionQueue[i] < 0) {
				break;
			}

			val = LEARN_RATE * delta * getEArray()[lastActionQueue[i]];
			
			if (Double.isInfinite(val))
				System.out.printf("val: %s, n: %d, e: %s, delta: %s, reward: %s\n", Double.toString(val), n, Double.toString(getEArray()[i]), Double.toString(delta), Double.toString(reward));
				
			
			getActionList()[lastActionQueue[i]] += val;

			if (lastActionQueue[n] == lastActionQueue[i])
				getEArray()[lastActionQueue[i]] = 1 + LAMBDA * DISCOUNT_RATE * getEArray()[lastActionQueue[i]];
			else if (getStateFromId(lastActionQueue[n]) == getStateFromId(lastActionQueue[i]))
				getEArray()[lastActionQueue[i]] = 0;
			else
				getEArray()[lastActionQueue[i]] *= LAMBDA * DISCOUNT_RATE;
			
			i = (i - 1 + QUEUE_SIZE) % QUEUE_SIZE;
		} while (i != n);
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
