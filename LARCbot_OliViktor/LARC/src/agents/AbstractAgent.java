package agents;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import robot.Action;

public abstract class AbstractAgent {
	private static double DISCOUNT_RATE = 0.9;
	private static int QUEUE_SIZE = 10;

	protected Map<Integer, double[]> actionList;
	protected AgentMode mode;
	
	private int[][] lastActionQueue;
	private int queueEndIndex;
	
	protected AbstractAgent() {
		actionList = new HashMap<Integer, double[]>();
		mode = AgentMode.RNDLEARN;
		
		lastActionQueue = new int[QUEUE_SIZE][2];
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
		
		JSONObject obj = (JSONObject) parser.parse(reader);
		actionList = (Map<Integer, double[]>) obj;
		
		reader.close();
	}
	
	protected void addToLastActionQueue(int stateID, int actionID) {
		lastActionQueue[queueEndIndex] = new int[] {stateID, actionID};
		queueEndIndex = (queueEndIndex + 1) % QUEUE_SIZE;
	}
	
	protected void addRewardToLastActions(int reward) {
		int i = queueEndIndex;
		double d = 1;
		
		do {
			i = (i + 1) % QUEUE_SIZE;
			
			if (lastActionQueue[i] == null) {
				break;
			}
			
			actionList.get(lastActionQueue[i][0])[lastActionQueue[i][1]] += reward * d;
			
			d *= DISCOUNT_RATE;
		} while (i != queueEndIndex);
	}
	
	public abstract Action getNextAction(int stateID);
	
	public abstract void addReward(int reward);
}
