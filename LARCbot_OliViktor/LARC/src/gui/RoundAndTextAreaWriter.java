package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class RoundAndTextAreaWriter implements Runnable {
	JLabel label;
	JTextArea textArea;
	BufferedReader reader;
	String curRound = "0";
	
	private Console parent;
	private int id;

	protected RoundAndTextAreaWriter(Console parent, int id, JLabel roundLabel, JTextArea textArea,
			InputStream stream) {
		this.label = roundLabel;
		this.textArea = textArea;

		reader = new BufferedReader(new InputStreamReader(stream));
		
		this.parent = parent;
		this.id = id;
	}

	public void run() {
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				if (line.equals("Let the games begin!")) {
					// Runde wurde gestartet, Rundenzahl aktualisieren
					label.setText(curRound);
				} else if (line.replaceAll("\\.", "").replaceAll("-", "")
						.length() > 0) {
					// line ist nicht nur Punkte oder Striche, also was
					// sinnvolles
					if (line.contains("Round")) {
						if (line.contains("initializing"))
							curRound = line.split(" ")[1];
					} else {
						// irgendwas anderes, einfach ausgeben
						textArea.append(line + "\n");
						textArea.setCaretPosition(textArea.getDocument()
								.getLength());
					}
				}
			}
			
			parent.streamFinished(id);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Error redirecting output : "
					+ ioe.getMessage());
		}
	}
}
