package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Config {
	private static final String CONFIG_FILENAME = "LARCBot.cfg";
	
	private static HashMap<String, String> values;
	
	static {
		values = new HashMap<String, String>();
		
		try {
			Scanner s = new Scanner(new File(CONFIG_FILENAME));
			
			String line = "";
			String[] splitLine;
			
			while (s.hasNextLine()) {
				line = s.nextLine();
				splitLine = line.split("=");
				if (splitLine.length == 2)
					values.put(splitLine[0], splitLine[1]);
			}
			s.close();
		} catch (FileNotFoundException e) {
		}
	}
	
	public static String getStringValue(String key) {
		if (values.containsKey(key)) {
			return values.get(key);
		} else {
			return "";
		}
	}
	
	public static int getIntValue(String key) {
		
		if (values.containsKey(key)) {
			return Integer.parseInt(values.get(key));
		} else {
			return 0;
		}
	}
	
	public static double getDoubleValue(String key) {
		if (values.containsKey(key)) {
			return Double.parseDouble(values.get(key));
		} else {
			return 0.0;
		}
	}
	
	public static boolean getBoolValue(String key) {
		if (values.containsKey(key)) {
			return values.get(key).equals("true");
		} else {
			return false;
		}
	}
	
	public static void setStringValue(String key, String value) {
		values.put(key, value);
	}
	
	public static void setIntValue(String key, int value) {
		values.put(key, Integer.toString(value));
	}
	
	public static void setDoubleValue(String key, double value) {
		values.put(key, Double.toString(value));
	}
	
	public static void setBoolValue(String key, boolean value) {
		values.put(key, value ? "true" : "false");
	}
	
	private static void saveFile(HashMap<String, String> elems, String filename) {
		try {
			FileWriter writer = new FileWriter(filename);
			
			for (String key : elems.keySet()) {
				writer.write(key + "=" + elems.get(key) + "\n");
			}
			
			writer.close();
		} catch (IOException e) {
			System.out.println(filename + " konnte nicht gespeichert werden: " + e.getMessage());
		}
	}
	
	public static void save() {
		saveFile(values, CONFIG_FILENAME);
		saveFile(values, values.get("RobocodeHome") + File.separator + CONFIG_FILENAME);
	}
	
	public static String createAndSaveBattle() {
		String dirname = values.get("RobocodeHome") + "\\battles\\", filename = "generated_battle.battle";
		
		HashMap<String, String> battleValues = new HashMap<String, String>();
		battleValues.put("robocode.battleField.width", values.get("FieldWidth"));
		battleValues.put("robocode.battleField.height", values.get("FieldHeight"));
		battleValues.put("robocode.battle.numRounds", values.get("Rounds"));
		battleValues.put("robocode.battle.gunCoolingRate", "0.1");
		battleValues.put("robocode.battle.rules.inactiveTime", "450");
		battleValues.put("robocode.battle.hideEnemyNames", "true");
		battleValues.put("robocode.battle.selectedRobots", "robot.LARCbot*," + values.get("EnemyRobot"));
		
		saveFile(battleValues, dirname + filename);
		
		return filename;
	}
}