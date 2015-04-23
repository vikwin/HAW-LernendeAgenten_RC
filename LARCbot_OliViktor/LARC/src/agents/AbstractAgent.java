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
	private static final int QUEUE_SIZE = 10, SAVE_TIMES = 100000;
	
	private static int actionCounter = 0;
	private static int[] fileCounter = new int[] { 0, 0 };

	protected Double[] actionList;
	protected AgentMode mode;

	private int[] lastActionQueue;
	private int queueEndIndex;
	
	private static String timestamp = null;

	protected AbstractAgent() {
		mode = AgentMode.LEARNING;

		lastActionQueue = new int[QUEUE_SIZE];
		Arrays.fill(lastActionQueue, -1);
		queueEndIndex = 0;

		if (timestamp == null) {
			timestamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
			new File("LARCAgents/" + timestamp).mkdirs();
		}
	}

	public void setMode(AgentMode newMode) {
		mode = newMode;
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
					fw = new FileWriter(filename + ".json");

					StringBuilder actionListString = new StringBuilder("["); // JSONValue.toJSONString(actionList);
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
	public void load(String filename) throws IOException, ParseException {
		FileReader reader = new FileReader(filename + ".json");
		JSONParser parser = new JSONParser();

		List<Double> obj = (List<Double>) ((JSONArray) parser.parse(reader));
		actionList = obj.toArray(new Double[0]);

		reader.close();
	}

	protected void addToLastActionQueue(int id) {
		lastActionQueue[queueEndIndex] = id;
		queueEndIndex = (queueEndIndex + 1) % QUEUE_SIZE;
	}

	protected void addRewardToLastActions(double reward) {
		if (reward == 0)
			return;
		
//		String type = (this instanceof MoveAgent) ? "Move" : "Attack";
		int i = queueEndIndex;
		double d = 1;

//		System.out.printf("Belohne %s mit %s: ", type, Double.toString(reward));
		do {
			i = (i + 1) % QUEUE_SIZE;

			if (lastActionQueue[i] < 0) {
				break;
			}

			actionList[lastActionQueue[i]] += reward * d;
			
//			System.out.printf("#%d - ", lastActionQueue[i]);

			d *= DISCOUNT_RATE;
		} while (i != queueEndIndex);
//		System.out.println();
	}

	private String getFileName() {
		if (this instanceof MoveAgent) {
			return "LARCAgents\\" + timestamp + "\\move_agent_" + fileCounter[0]++;
		} else {
			return "LARCAgents\\" + timestamp + "\\attack_agent_" + fileCounter[1]++;
		}

	}

	public Object getNextAction(int id) {
		if (++actionCounter >= SAVE_TIMES) {
			String fileName = getFileName();

			actionCounter = 0;
			this.save(fileName);
			System.out.println("Agent saved as: " + fileName + ".json");
		}

		return null;
	}

	public abstract void addReward(double reward);
}
