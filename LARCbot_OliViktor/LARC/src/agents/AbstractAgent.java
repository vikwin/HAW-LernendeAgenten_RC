package agents;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import robot.Action;

public abstract class AbstractAgent {
	private static double DISCOUNT_RATE = 0.9;
	private static int QUEUE_SIZE = 10;

	protected double[] actionList;
	protected AgentMode mode;
	
	private int[] lastActionQueue;
	private int queueEndIndex;
	
	protected AbstractAgent() {
		mode = AgentMode.RNDLEARN;
		
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
		
		String actionListString = JSONValue.toJSONString(actionList);
		fw.write(actionListString);
		
		fw.close();
	}
	
	@SuppressWarnings("unchecked")
	public void load(String filename) throws IOException, ParseException {
		FileReader reader = new FileReader(filename + ".json");
		JSONParser parser = new JSONParser();
		
//		TODO
//		JSONArray obj = (JSONArray) parser.parse(reader);
//		actionList = (ArrayList<Double>) obj;
		
		reader.close();
	}
	
	protected void addToLastActionQueue(int id) {
		lastActionQueue[queueEndIndex] = id;
		queueEndIndex = (queueEndIndex + 1) % QUEUE_SIZE;
	}
	
	protected void addRewardToLastActions(int reward) {
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
	
	public abstract Action getNextAction(int stateID);
	
	public abstract void addReward(int reward);
}
