package agents;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class AbstractAgent {
	private static final double DISCOUNT_RATE = 0.9;
	private static final int QUEUE_SIZE = 10;

	protected static final int SAVE_TIMES = 100000;
	protected static final String TIMESTAMP = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
	protected static final boolean LOAD_ON_START = true;

	protected AgentMode mode;

	private int[] lastActionQueue;
	private int queueEndIndex;
	
	static {
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
	public void save(final String filename) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				FileWriter fw = null;

				try {
					fw = new FileWriter("LARCAgents\\" + filename + ".json");

					StringBuilder actionListString = new StringBuilder("["); // JSONValue.toJSONString(actionList);
					Double[] actionList = getActionList();
					for (double d : actionList) {
						actionListString.append(d + ",");
					}
					actionListString.delete(actionListString.length() - 1,
							actionListString.length());
					actionListString.append("]");

					fw.write(actionListString.toString());
					fw.close();
				} catch (IOException e) {
					System.out.printf(
							"### Fehler beim Speichern des Agents: %s ###\n",
							e.getMessage());
				}
			}
		});

		t.start();
	}

	@SuppressWarnings("unchecked")
	public static void load(String filename) throws IOException, ParseException {
		FileReader reader = new FileReader("LARCAgents\\" + filename + ".json");
		JSONParser parser = new JSONParser();

		List<Double> obj = (List<Double>) ((JSONArray) parser.parse(reader));
		fillActionList(obj.toArray(new Double[0]));
		reader.close();
	}

	protected void addToLastActionQueue(int id) {
		lastActionQueue[queueEndIndex] = id;
		queueEndIndex = (queueEndIndex + 1) % QUEUE_SIZE;
	}

	protected void addRewardToLastActions(double reward) {
		if (reward == 0)
			return;
		
		int i = queueEndIndex;
		double d = 1;

		do {
			i = (i + 1) % QUEUE_SIZE;

			if (lastActionQueue[i] < 0) {
				break;
			}

			getActionList()[lastActionQueue[i]] += reward * d;
			
			d *= DISCOUNT_RATE;
		} while (i != queueEndIndex);
	}

	public abstract void addReward(double reward);
	
	public abstract void saveOnBattleEnd();
}
