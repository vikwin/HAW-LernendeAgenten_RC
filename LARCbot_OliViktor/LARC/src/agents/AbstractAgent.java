package agents;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class AbstractAgent {
	private static double DISCOUNT_RATE = 0.9;
	private static int QUEUE_SIZE = 10;

	protected Double[] actionList;
	protected AgentMode mode;
	
	private int[] lastActionQueue;
	private int queueEndIndex;
	
	protected AbstractAgent() {
		mode = AgentMode.LEARNING;
		
		lastActionQueue = new int[QUEUE_SIZE];
		Arrays.fill(lastActionQueue, -1);
		queueEndIndex = 0;
	}
	
	public void setMode(AgentMode newMode) {
		mode = newMode;
	}
	
	/**
	 * Speichert gelernte Informationen des Bots in einer Datei ab.
	 * @param filename Der Dateiname
	 * @throws IOException Wenn die angebene Datei nicht vorhanden ist oder nicht zugreifbar
	 */
	public void save(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename + ".json");
		
		String actionListString = "["; //JSONValue.toJSONString(actionList);
		for (double d : actionList) {
			actionListString += d + ",";
		}
		actionListString += "]";
		
		fw.write(actionListString);
		
		fw.close();
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
		int i = queueEndIndex;
		double d = 1;
		
		do {
			i = (i + 1) % QUEUE_SIZE;
			
			if (lastActionQueue[i] < 0) {
				break;
			}
			
			actionList[lastActionQueue[i]] += reward * d;
			
			d *= DISCOUNT_RATE;
		} while (i != queueEndIndex);
	}
	
	public abstract void addReward(double reward);
}
